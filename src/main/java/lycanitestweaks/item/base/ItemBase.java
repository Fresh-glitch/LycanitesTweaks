package lycanitestweaks.item.base;

import lycanitestweaks.LycanitesTweaks;
import lycanitestweaks.capability.storedcreatureinfo.IStoredCreatureInfo;
import lycanitestweaks.capability.storedcreatureinfo.ItemStoredCreatureInfo;
import lycanitestweaks.capability.storedcreatureinfo.StoredCreatureInfoHandler;
import lycanitestweaks.capability.toggleableitem.ToggleableItem;
import lycanitestweaks.capability.toggleableitem.ToggleableItemHandler;
import lycanitestweaks.client.renderer.RenderContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public abstract class ItemBase extends Item {

    public ItemBase(String modid, String name) {
        super();
        this.setRegistryName(modid, name);
        this.setTranslationKey(modid + "." + name);

        this.setMaxStackSize(1);

        this.addPropertyOverride(new ResourceLocation(LycanitesTweaks.MODID, "objtesr"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack itemStack, World world, EntityLivingBase entity) {
                RenderContext.currentRenderEntity = entity;
                RenderContext.currentRenderStack = itemStack;
                return 0.0F;
            }
        });
    }


    public ItemBase(String name) {
        this(LycanitesTweaks.MODID, name);
    }

    /**
     * @return if the item is enabled in the config
     */
    public abstract boolean isEnabled();

    /**
     * @return if the item class has an event subscriber
     */
    public boolean hasSubscriber() {
        return false;
    }

    @Override
    @Nullable
    public NBTTagCompound getNBTShareTag(ItemStack stack) {
        NBTTagCompound nbt = super.getNBTShareTag(stack);
        if(nbt != null) {
            NBTTagCompound newTag = nbt.copy();

            ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(stack);
            if (storedCreatureInfo != null) {
                NBTBase capTag = StoredCreatureInfoHandler.ITEM_STORED_CREATURE_INFO.writeNBT(storedCreatureInfo, null);
                if (capTag != null) {
                    newTag.setTag(IStoredCreatureInfo.FORGE_CAP_NBT, capTag);
                }
            }

            ToggleableItem toggleableItem = ToggleableItem.getForItemStack(stack);
            if (toggleableItem != null) {
                NBTBase capTag = ToggleableItemHandler.TOGGLE_ITEM.writeNBT(toggleableItem, null);
                if (capTag != null) {
                    newTag.setTag(ItemPassive.TOGGLE_CAP, capTag);
                }
            }

            return newTag;
        }
        return nbt;
    }

    @Override
    public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt) {
        if(nbt != null) {
            if(nbt.hasKey(IStoredCreatureInfo.FORGE_CAP_NBT)) {
                NBTTagCompound capTag = nbt.getCompoundTag(IStoredCreatureInfo.FORGE_CAP_NBT);
                ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(stack);
                if(storedCreatureInfo != null) {
                    StoredCreatureInfoHandler.ITEM_STORED_CREATURE_INFO.readNBT(storedCreatureInfo, null, capTag);
                    nbt.removeTag(IStoredCreatureInfo.FORGE_CAP_NBT);
                }
            }
            if(nbt.hasKey(ItemPassive.TOGGLE_CAP)) {
                NBTTagCompound capTag = nbt.getCompoundTag(ItemPassive.TOGGLE_CAP);
                ToggleableItem toggleableItem = ToggleableItem.getForItemStack(stack);
                if(toggleableItem != null) {
                    ToggleableItemHandler.TOGGLE_ITEM.readNBT(toggleableItem, null, capTag);
                    nbt.removeTag(ItemPassive.TOGGLE_CAP);
                }
            }
        }
        super.readNBTShareTag(stack, nbt);
    }

    public NBTTagCompound getTagCompound(ItemStack itemStack) {
        if(itemStack.hasTagCompound()) {
            return itemStack.getTagCompound();
        }
        return new NBTTagCompound();
    }

    public boolean getTagBoolean(ItemStack itemStack, String key) {
        NBTTagCompound nbt = this.getTagCompound(itemStack);
        if(nbt.hasKey(key)) return nbt.getBoolean(key);
        return false;
    }

    public int getTagInt(ItemStack itemStack, String key) {
        NBTTagCompound nbt = this.getTagCompound(itemStack);
        if(nbt.hasKey(key)) return nbt.getInteger(key);
        return 0;
    }

    public String getTagString(ItemStack itemStack, String key) {
        NBTTagCompound nbt = this.getTagCompound(itemStack);
        if(nbt.hasKey(key)) return nbt.getString(key);
        return "";
    }

    public void setTagBoolean(ItemStack itemStack, String key, boolean value){
        NBTTagCompound nbt = this.getTagCompound(itemStack);
        nbt.setBoolean(key, value);
        itemStack.setTagCompound(nbt);
    }

    public void setTagInt(ItemStack itemStack, String key, int value){
        NBTTagCompound nbt = this.getTagCompound(itemStack);
        nbt.setInteger(key, value);
        itemStack.setTagCompound(nbt);
    }

    public void setTagString(ItemStack itemStack, String key, String value){
        NBTTagCompound nbt = this.getTagCompound(itemStack);
        nbt.setString(key, value);
        itemStack.setTagCompound(nbt);
    }
}
