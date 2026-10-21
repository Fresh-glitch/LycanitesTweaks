package lycanitestweaks.capability.storedcreatureinfo;

import lycanitestweaks.LycanitesTweaks;
import lycanitestweaks.handlers.ForgeConfigHandler;
import lycanitestweaks.handlers.features.item.ConfigurableItemHandler;
import lycanitestweaks.item.ItemChallengeSoulStaff;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemStoredCreatureInfo implements IStoredCreatureInfo {

    public static final String BAUBLE_MODIFIERS_TAG = "BaubleModifierUUIDs";
    public static final String CREATURE_INFO_TAG = "CreatureInfo";


    private static final String ITEM_STATS_UUID_NBT = LycanitesTweaks.MODID + ".ItemStatsUUID";
    private static final String VILE_MATTER_UUID_NBT = "VileMatterUUID";

    private String creatureType = ""; // Creature Lookup Key
    private String customName = ""; // Skin by CustomName
    private int mobLevel = 0;
    private int subspecies = 0;
    private int variant = 0;
    private String rarity = "COMMON"; // Rarity Lookup Key
    private boolean spawnedAsBoss = false;

    private ItemStack itemStack = ItemStack.EMPTY;
    private NBTTagCompound cachedNBT;
    private ConfigurableItemHandler.ItemStats cachedStats;
    private final Map<String, UUID> cachedBaubleModifierIDs = new HashMap<>();

    private boolean clearOldAttributes = false;

    public ItemStoredCreatureInfo() {}
    public ItemStoredCreatureInfo(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /** Creative players on dedicated servers will often desnyc with inventory open as server replaces with clientside copy **/
    public static ItemStoredCreatureInfo getForItemStack(ItemStack itemStack) {
        ItemStoredCreatureInfo toggleableItem = itemStack.getCapability(StoredCreatureInfoHandler.ITEM_STORED_CREATURE_INFO, null);

        return toggleableItem;
    }

    @Nonnull
    public ConfigurableItemHandler.ItemStats getOrCreateStats() {
        if(this.cachedStats == null) {
            this.cachedStats = new ConfigurableItemHandler.ItemStats(this.itemStack);
        }
        return this.cachedStats;
    }

    @Nonnull
    public UUID getOrCreateBaubleModifierUUID(String attributeName){
        return this.cachedBaubleModifierIDs.computeIfAbsent(attributeName, key -> {
            this.setCachedNBT(null);
            return UUID.randomUUID();
        });
    }

    // Required as old format does not consistently save UUIDs to item NBT, need to flag a full wipe
    public boolean shouldClearOldBaubleAttributes(){
        boolean value = this.clearOldAttributes;
        clearOldAttributes = false;
        return value;
    }

    @Override
    public String getCreatureTypeName(){
        return this.creatureType;
    }

    @Override
    public String getCustomName(){
        return this.customName;
    }

    @Override
    public int getLevel(){
        return this.mobLevel;
    }

    @Override
    public int getEntitySubspecies(){
        return this.subspecies;
    }

    @Override
    public int getEntityVariant(){
        return this.variant;
    }

    @Override
    public String getEntityVariantRarity(){
        return this.rarity;
    }

    @Override
    public boolean getSpawnedAsBoss() {
        return this.spawnedAsBoss;
    }

    @Override
    public void setCreatureTypeName(String type){
        if(this.itemStack.getItem() instanceof ItemChallengeSoulStaff) {
            // Temp way to give Lobber something as it isn't tameable
            if(type.equals("lobber")) {
                type = "volcan";
                this.setSpawnedAsBoss(true);
            }
        }
        this.creatureType = type;
        this.setCachedNBT(null);
    }

    @Override
    public void setCustomName(String name){
        this.customName = name;
        this.setCachedNBT(null);
    }

    @Override
    public void setLevel(int level){
        this.mobLevel = level;
        this.setCachedNBT(null);
    }

    @Override
    public void setEntitySubspecies(int index){
        this.subspecies = index;
        this.setCachedNBT(null);
    }

    @Override
    public void setEntityVariant(int index){
        this.variant = index;
        this.setCachedNBT(null);
    }

    @Override
    public void setEntityVariantRarity(String rarity){
        this.rarity = rarity;
        this.setCachedNBT(null);
    }

    @Override
    public void setSpawnedAsBoss(boolean spawnedAsBoss) {
        this.spawnedAsBoss = spawnedAsBoss;
    }

    @Override
    public void readNBT(NBTTagCompound nbtTagCompound) {
        NBTTagCompound tagCompound = nbtTagCompound.getCompoundTag(CREATURE_INFO_TAG);

        if(tagCompound.hasKey(CREATURE_TYPE_NBT))
            this.setCreatureTypeName(tagCompound.getString(CREATURE_TYPE_NBT));
        if(tagCompound.hasKey(CUSTOM_NAME_NBT))
            this.setCustomName(tagCompound.getString(CUSTOM_NAME_NBT));
        if(tagCompound.hasKey(MOB_LEVEL_NBT))
            this.setLevel(tagCompound.getInteger(MOB_LEVEL_NBT));
        if(tagCompound.hasKey(SUBSPECIES_NBT))
            this.setEntitySubspecies(tagCompound.getInteger(SUBSPECIES_NBT));
        if(tagCompound.hasKey(VARIANT_NBT))
            this.setEntityVariant(tagCompound.getInteger(VARIANT_NBT));
        if(tagCompound.hasKey(VARIANT_RARITY_NBT))
            this.setEntityVariantRarity(tagCompound.getString(VARIANT_RARITY_NBT));
        if(tagCompound.hasKey(SPAWNED_AS_BOSS_NBT))
            this.setSpawnedAsBoss(tagCompound.getBoolean(SPAWNED_AS_BOSS_NBT));

        if(nbtTagCompound.hasKey(BAUBLE_MODIFIERS_TAG)) {
            NBTTagCompound baubleModifiers = nbtTagCompound.getCompoundTag(BAUBLE_MODIFIERS_TAG);
            for(String attributeName : baubleModifiers.getKeySet()) {
                if(baubleModifiers.getCompoundTag(attributeName).hasUniqueId("UUID")) {
                    this.cachedBaubleModifierIDs.put(
                            attributeName,
                            baubleModifiers.getCompoundTag(attributeName).getUniqueId("UUID")
                    );
                }
            }
        }
    }

    @Override
    public void writeNBT(NBTTagCompound nbtTagCompound) {
        NBTTagCompound creatureInfoTag = new NBTTagCompound();

        creatureInfoTag.setString(CREATURE_TYPE_NBT, this.getCreatureTypeName());
        creatureInfoTag.setString(CUSTOM_NAME_NBT, this.getCustomName());
        creatureInfoTag.setInteger(MOB_LEVEL_NBT, this.getLevel());
        creatureInfoTag.setInteger(SUBSPECIES_NBT, this.getEntitySubspecies());
        creatureInfoTag.setInteger(VARIANT_NBT, this.getEntityVariant());
        creatureInfoTag.setString(VARIANT_RARITY_NBT, this.getEntityVariantRarity());
        creatureInfoTag.setBoolean(SPAWNED_AS_BOSS_NBT, this.getSpawnedAsBoss());

        nbtTagCompound.setTag(CREATURE_INFO_TAG, creatureInfoTag);

        if(!this.cachedBaubleModifierIDs.isEmpty()) {
            NBTTagCompound baubleModifiersTag = new NBTTagCompound();

            this.cachedBaubleModifierIDs.forEach((attributeName, uuid) -> {
                NBTTagCompound uuidTag = new NBTTagCompound();
                uuidTag.setUniqueId("UUID", uuid);
                baubleModifiersTag.setTag(attributeName, uuidTag);
            });

            nbtTagCompound.setTag(BAUBLE_MODIFIERS_TAG, baubleModifiersTag);
        }
    }

    @Override
    @Nullable
    public NBTTagCompound getCachedNBT() {
        return this.cachedNBT;
    }

    @Override
    public void setCachedNBT(@Nullable NBTTagCompound nbt) {
        this.cachedNBT = nbt;
        this.cachedStats = null;
    }

    protected void migrateFromOldFormat() {
        NBTTagCompound nbt = this.itemStack.getTagCompound();
        if(nbt != null) {
            boolean changed = false;

            if(nbt.hasKey(CREATURE_TYPE_NBT)) {
                this.setCreatureTypeName(nbt.getString(CREATURE_TYPE_NBT));
                nbt.removeTag(CREATURE_TYPE_NBT);
                changed = true;
            }

            if(nbt.hasKey(CUSTOM_NAME_NBT)) {
                this.setCustomName(nbt.getString(CUSTOM_NAME_NBT));
                nbt.removeTag(CUSTOM_NAME_NBT);
                changed = true;
            }

            if(nbt.hasKey(MOB_LEVEL_NBT)) {
                this.setLevel(nbt.getInteger(MOB_LEVEL_NBT));
                nbt.removeTag(MOB_LEVEL_NBT);
                changed = true;
            }

            if(nbt.hasKey(SUBSPECIES_NBT)) {
                this.setEntitySubspecies(nbt.getInteger(SUBSPECIES_NBT));
                nbt.removeTag(SUBSPECIES_NBT);
                changed = true;
            }

            if(nbt.hasKey(VARIANT_NBT)) {
                this.setEntityVariant(nbt.getInteger(VARIANT_NBT));
                nbt.removeTag(VARIANT_NBT);
                changed = true;
            }

            if(nbt.hasKey(VARIANT_RARITY_NBT)) {
                this.setEntityVariantRarity(nbt.getString(VARIANT_RARITY_NBT));
                nbt.removeTag(VARIANT_RARITY_NBT);
                changed = true;
            }

            if(nbt.hasKey(SPAWNED_AS_BOSS_NBT)) {
                this.setSpawnedAsBoss(nbt.getBoolean(SPAWNED_AS_BOSS_NBT));
                nbt.removeTag(SPAWNED_AS_BOSS_NBT);
                changed = true;
            }

            if(nbt.hasUniqueId(ITEM_STATS_UUID_NBT)) {
                nbt.removeTag(ITEM_STATS_UUID_NBT + "Most");
                nbt.removeTag(ITEM_STATS_UUID_NBT + "Least");
                changed = true;
            }

            if(nbt.hasUniqueId(VILE_MATTER_UUID_NBT)) {
                nbt.removeTag(VILE_MATTER_UUID_NBT + "Most");
                nbt.removeTag(VILE_MATTER_UUID_NBT + "Least");
                changed = true;
            }

            if(changed) {
                this.clearOldAttributes = true;
                this.itemStack.setTagCompound(nbt);
                if(ForgeConfigHandler.debug.debugLoggerAutomatic) {
                    LycanitesTweaks.LOGGER.log(Level.DEBUG, "Migrated Old ItemWithCreatureInfo to Forge Capability: {}", this.itemStack);
                }
            }
        }
    }
}
