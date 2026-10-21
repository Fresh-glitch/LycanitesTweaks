package lycanitestweaks.item.base;

import com.google.common.collect.Multimap;
import lycanitestweaks.LycanitesTweaks;
import lycanitestweaks.capability.storedcreatureinfo.ItemStoredCreatureInfo;
import lycanitestweaks.compat.ModLoadedUtil;
import lycanitestweaks.handlers.features.item.ConfigurableItemHandler;
import lycanitestweaks.item.interfaces.IAttributeBauble;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

public abstract class ItemPassive extends ItemBase implements IAttributeBauble {

    public static final String TOGGLE_CAP = LycanitesTweaks.MODID + "ToggleableSync";

    public ItemPassive(String modid, String name) {
        super(modid, name);
    }

    public ItemPassive(String name) {
        this(LycanitesTweaks.MODID, name);
    }

    public boolean isToggleable() {
        return false;
    }

    public void tickAbility(ItemStack itemstack, EntityLivingBase entity) {
        this.tickAbility(entity);
    }

    public void tickAbility(EntityLivingBase entity) {

    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

        ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(stack);
        if(storedCreatureInfo != null) {
            ConfigurableItemHandler.ItemStats stats = storedCreatureInfo.getOrCreateStats();
            String modifierName = "Item modifier";
            if (slot == this.getEquipmentSlot(stack)) {
                if(slot == EntityEquipmentSlot.MAINHAND) {
                    if (stats.damage != 0)
                        multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, modifierName, stats.damage - 1, 0));
                    if (stats.attackSpeed != 0)
                        multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, modifierName, stats.attackSpeed - 4D, 0));
                    ConfigurableItemHandler.getMainHandAttributeModifiers(stats, modifierName, multimap);
                }
                else {
                    ConfigurableItemHandler.getOtherSlotAttributeModifiers(stats, modifierName, multimap);
                }
            }
        }

        return multimap;
    }

    @Override
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
        ConfigurableItemHandler.EquipmentSlot slots = ConfigurableItemHandler.getItemSlot(stack);
        if(slots != null) {
            return slots.equipmentSlot;
        }
        return super.getEquipmentSlot(stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int inventorySlot, boolean isCurrentItem) {
        if(entityIn instanceof EntityLivingBase) {
            this.tickAbility(stack, (EntityLivingBase) entityIn);
        }
    }

    @Optional.Method(modid = ModLoadedUtil.BAUBLES_MODID)
    public void onWornTick(ItemStack itemStack, EntityLivingBase player) {
        this.tickAbility(itemStack, player);
    }
}
