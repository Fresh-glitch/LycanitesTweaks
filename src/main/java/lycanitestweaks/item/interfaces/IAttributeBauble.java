package lycanitestweaks.item.interfaces;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import lycanitestweaks.LycanitesTweaks;
import lycanitestweaks.capability.storedcreatureinfo.ItemStoredCreatureInfo;
import lycanitestweaks.compat.BaublesHandler;
import lycanitestweaks.compat.ModLoadedUtil;
import lycanitestweaks.handlers.features.entity.AttributesHandler;
import lycanitestweaks.handlers.features.item.ConfigurableItemHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "baubles.api.IBauble", modid = ModLoadedUtil.BAUBLES_MODID, striprefs = true)
public interface IAttributeBauble extends IBauble {

    String BAUBLE_MODIFIER_NAME = LycanitesTweaks.MODID + ".baubleModifier";

    // Above bounds = All Slots
    // Below bounds = Not a bauble
    @Optional.Method(modid = ModLoadedUtil.BAUBLES_MODID)
    default BaubleType getBaubleType(ItemStack itemStack) {
        ConfigurableItemHandler.EquipmentSlot slots = ConfigurableItemHandler.getItemSlot(itemStack);
        if(slots != null) {
            if(slots.baubleTypeOrdinal < BaubleType.values().length)
                return BaubleType.values()[slots.baubleTypeOrdinal];
        }
        return BaubleType.TRINKET;
    }

    @Optional.Method(modid = ModLoadedUtil.BAUBLES_MODID)
    default void onEquipped(ItemStack itemstack, EntityLivingBase player) {
        if (player.world.isRemote) return;

        if(player instanceof EntityPlayer) {
            ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(itemstack);
            if(storedCreatureInfo != null) {
                ConfigurableItemHandler.EquipmentSlot slots = ConfigurableItemHandler.getItemSlot(itemstack);
                if(slots != null && slots.baubleAttributes) {
                    Multimap<String, AttributeModifier> multimap = ConfigurableItemHandler.getBaubleAttributeModifiers(storedCreatureInfo, BAUBLE_MODIFIER_NAME);
                    player.getAttributeMap().applyAttributeModifiers(multimap);
                }
            }
        }

        if(player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    // Some mods like Bountiful Baubles reforger doesn't fire this
    @Optional.Method(modid = ModLoadedUtil.BAUBLES_MODID)
    default void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        if (player.world.isRemote) return;

        if(player instanceof EntityPlayer) {
            ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(itemstack);
            ConfigurableItemHandler.EquipmentSlot slots = ConfigurableItemHandler.getItemSlot(itemstack);
            if(slots != null) {
                Multimap<String, AttributeModifier> multimap = ConfigurableItemHandler.getBaubleAttributeModifiers(storedCreatureInfo, BAUBLE_MODIFIER_NAME);
                player.getAttributeMap().removeAttributeModifiers(multimap);
            }
        }

        if(player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    // Actual check where below bounds = not a bauble
    @Optional.Method(modid = ModLoadedUtil.BAUBLES_MODID)
    default boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        ConfigurableItemHandler.EquipmentSlot slots = ConfigurableItemHandler.getItemSlot(itemstack);
        if(slots != null) {
            if(slots.baubleTypeOrdinal >= BaubleType.values().length)
                return false;

            if(player instanceof EntityPlayer) {
                if(BaublesHandler.getBaubleCount((EntityPlayer) player, itemstack.getItem()) >= slots.baubleCountLimit)
                    return false;
            }
        }

        return true;
    }
}
