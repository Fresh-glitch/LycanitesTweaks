package lycanitestweaks.capability.storedcreatureinfo;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import lycanitestweaks.LycanitesTweaks;
import lycanitestweaks.compat.BaublesHandler;
import lycanitestweaks.compat.ModLoadedUtil;
import lycanitestweaks.handlers.ForgeConfigHandler;
import lycanitestweaks.handlers.features.entity.AttributesHandler;
import lycanitestweaks.item.interfaces.IItemWithCreatureInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StoredCreatureInfoHandler {

    public static final ResourceLocation STORED_CREATURE_INFO_KEY = new ResourceLocation(LycanitesTweaks.MODID, "storedcreatureinfo");

    @CapabilityInject(ItemStoredCreatureInfo.class)
    public static Capability<ItemStoredCreatureInfo> ITEM_STORED_CREATURE_INFO;

    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(ItemStoredCreatureInfo.class, new Storage(), ItemStoredCreatureInfo::new);
    }

    public static class AttachCapabilityHandler {
        @SubscribeEvent()
        public static void onAttachPlayerCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
            if(!LycanitesTweaks.completedLoading) return;
            ItemStack stack = event.getObject();

            if(stack.hasCapability(ITEM_STORED_CREATURE_INFO, null)) return;

            Item item = stack.getItem();
            if(!(item instanceof IItemWithCreatureInfo)) return;

            event.addCapability(STORED_CREATURE_INFO_KEY, new Provider(stack));

            // Generate a tag since getShareTag doesn't automatically
            NBTTagCompound tag = stack.getTagCompound();
            if(tag == null) tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
        private final ItemStoredCreatureInfo instance;

        public Provider(ItemStack itemStack) {
            this.instance = new ItemStoredCreatureInfo(itemStack);
            this.instance.migrateFromOldFormat();
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == ITEM_STORED_CREATURE_INFO;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return capability == ITEM_STORED_CREATURE_INFO ? ITEM_STORED_CREATURE_INFO.cast(instance) : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return (NBTTagCompound) ITEM_STORED_CREATURE_INFO.writeNBT(instance, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            ITEM_STORED_CREATURE_INFO.readNBT(instance, null, nbt);
        }
    }

    private static class Storage implements Capability.IStorage<ItemStoredCreatureInfo> {

        @Override
        public NBTBase writeNBT(Capability<ItemStoredCreatureInfo> capability, ItemStoredCreatureInfo instance, EnumFacing side) {
            //If capability hasn't changed, return the cached NBT
            NBTTagCompound cachedNBT = instance.getCachedNBT();
            if(cachedNBT != null)
                return cachedNBT;

            NBTTagCompound nbt = new NBTTagCompound();

            instance.writeNBT(nbt);

            instance.setCachedNBT(nbt); //cache new NBT
            return nbt;
        }

        @Override
        public void readNBT(Capability<ItemStoredCreatureInfo> capability, ItemStoredCreatureInfo instance, EnumFacing side, NBTBase nbt) {
            NBTTagCompound tags = (NBTTagCompound)nbt;

            instance.readNBT(tags);

            instance.setCachedNBT(null);
            //After reading the cached NBT is null=invalid, will be set on next itemStack comparison
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) return;
        if (!(event.getEntity() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntity();
        boolean clearOldAttributes = false;

        if (ModLoadedUtil.baubles.isLoaded()) {
            List<ItemStack> attributeBaubles = BaublesHandler.getAttributeBaubles(player);

            for(ItemStack itemStack : attributeBaubles) {
                ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(itemStack);
                if(storedCreatureInfo != null && storedCreatureInfo.shouldClearOldBaubleAttributes()) {
                    clearOldAttributes = true;
                    NBTTagCompound itemNBT = itemStack.getTagCompound();

                    if(itemNBT != null && itemNBT.hasKey("BaubleModifierUUIDs")) {
                        itemNBT.removeTag("BaubleModifierUUIDs");
                        itemStack.setTagCompound(itemNBT);
                    }
                }
            }
        }

        if(clearOldAttributes) {
            final String OLD_BAUBLE_MODIFIER_NAME = "Bauble modifier";
            clearAttributeModifiers(player, SharedMonsterAttributes.ATTACK_DAMAGE, OLD_BAUBLE_MODIFIER_NAME);
            clearAttributeModifiers(player, SharedMonsterAttributes.ATTACK_SPEED, OLD_BAUBLE_MODIFIER_NAME);
            clearAttributeModifiers(player, SharedMonsterAttributes.MAX_HEALTH, OLD_BAUBLE_MODIFIER_NAME);
            clearAttributeModifiers(player, SharedMonsterAttributes.ARMOR, OLD_BAUBLE_MODIFIER_NAME);
            clearAttributeModifiers(player, SharedMonsterAttributes.MOVEMENT_SPEED, OLD_BAUBLE_MODIFIER_NAME);
            clearAttributeModifiers(player, BaseCreatureEntity.DEFENSE, OLD_BAUBLE_MODIFIER_NAME);
            clearAttributeModifiers(player, AttributesHandler.PIERCE, OLD_BAUBLE_MODIFIER_NAME);

            if(ForgeConfigHandler.debug.debugLoggerAutomatic) {
                LycanitesTweaks.LOGGER.log(Level.DEBUG, "Cleared Old LycanitesTweaks Attribute Modifiers from: {}", player.getName());
            }
        }
    }

    private static void clearAttributeModifiers(EntityLivingBase entityLivingBase, IAttribute attribute, String modifierName) {
        IAttributeInstance attributeInstance = entityLivingBase.getEntityAttribute(attribute);
        if(attributeInstance != null) {
            List<AttributeModifier> toRemove = new ArrayList<>();
            attributeInstance.getModifiers().forEach(attributeModifier -> {
                if(attributeModifier.getName().equals(modifierName))
                    toRemove.add(attributeModifier);
            });
            toRemove.forEach(attributeInstance::removeModifier);
        }
    }
}
