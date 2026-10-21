package lycanitestweaks.mixin.lycanitestweakscore.assetload;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.ItemDrop;
import lycanitestweaks.capability.storedcreatureinfo.ItemStoredCreatureInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemDrop.class)
public abstract class ItemDrop_WithCreatureInfoMixin {

    // Tried to do ItemDrop subclass, however data is forever lost after reload
    @ModifyReturnValue(
            method = "getEntityDropItemStack",
            at = @At("RETURN"),
            remap = false
    )
    public ItemStack lycanitesTweaks_lycanitesMobsItemDrop_getEntityDropItemStackWithCreatureInfo(ItemStack itemStack, EntityLivingBase entity){
        if(entity instanceof BaseCreatureEntity) {
            ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(itemStack);
            if(storedCreatureInfo != null) {
                storedCreatureInfo.setAllFromCreature((BaseCreatureEntity) entity);
            }
        }
        return itemStack;
    }
}
