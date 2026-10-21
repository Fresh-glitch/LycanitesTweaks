package lycanitestweaks.item.base;

import com.google.common.collect.Multimap;
import lycanitestweaks.capability.storedcreatureinfo.ItemStoredCreatureInfo;
import lycanitestweaks.handlers.features.item.ConfigurableItemHandler;
import lycanitestweaks.item.ItemChargeStaff;
import lycanitestweaks.item.interfaces.IAttributeBauble;
import lycanitestweaks.item.interfaces.IItemWithCreatureInfo;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public abstract class ItemBossRangedWeapon extends ItemChargeStaff implements IItemWithCreatureInfo, IAttributeBauble {

    public ItemBossRangedWeapon(String itemName) {
        super(itemName);
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
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair){
        if(repair.getItem() == Items.NETHER_STAR) return true;
        return super.getIsRepairable(toRepair, repair);
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
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment){
        if(enchantment == Enchantments.INFINITY) return true;
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public float getSoundVolume() {
        return 2.0F;
    }
}
