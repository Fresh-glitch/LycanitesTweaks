package lycanitestweaks.mixin.lycanitestweaksmajor.itemtweaks.equipment;

import com.lycanitesmobs.core.info.ItemConfig;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEquipment.class)
public abstract class ItemEquipment_MendingMixin extends ItemBase {

    @Shadow(remap = false)
    public abstract boolean addSharpness(ItemStack equipmentStack, int sharpness);
    @Shadow(remap = false)
    public abstract int getSharpness(ItemStack equipmentStack);
    @Shadow(remap = false)
    public abstract boolean removeSharpness(ItemStack equipmentStack, int sharpness);

    @Inject(
            method = "<init>",
            at = @At("RETURN"),
            remap = false
    )
    public void lycanitesTweaks_lycanitesMobsItemEquipment_init(CallbackInfo ci){
        this.setMaxDamage(ItemEquipment.SHARPNESS_MAX);
    }

    @Unique
    @Override
    public boolean isDamaged(ItemStack stack) {
        return this.getSharpness(stack) < ItemEquipment.SHARPNESS_MAX;
    }

    @Unique
    @Override
    public int getDamage(ItemStack stack) {
        return ItemEquipment.SHARPNESS_MAX - this.getSharpness(stack);
    }

    @Unique
    @Override
    public void setDamage(ItemStack stack, int damage){
        int repair = this.getDamage(stack) - damage;
        if(repair > 0) this.addSharpness(stack, repair);
        else if(repair < 0) this.removeSharpness(stack, -repair);
    }

    @Unique
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        return ItemConfig.maxEquipmentSharpnessItems.contains(repairStack.getItem().getRegistryName().toString())
                || super.getIsRepairable(itemStack, repairStack);
    }
}
