package lycanitestweaks.capability.storedcreatureinfo;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import lycanitestweaks.LycanitesTweaks;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public interface IStoredCreatureInfo {

    String FORGE_CAP_NBT = LycanitesTweaks.MODID + "StoredCreatureInfo";

    // Written to support more than just Items, but not used atm
    // This version is migrated from a Mixin Interface

    String CREATURE_TYPE_NBT = "creatureTypeName";
    String CUSTOM_NAME_NBT = "CustomName";
    String MOB_LEVEL_NBT = "MobLevel";
    String SUBSPECIES_NBT = "Subspecies";
    String VARIANT_NBT = "Variant";
    String VARIANT_RARITY_NBT = "VariantRarity";
    String SPAWNED_AS_BOSS_NBT = "SpawnedAsBoss";

    String getCreatureTypeName();
    String getCustomName();
    int getLevel();
    int getEntitySubspecies();
    int getEntityVariant();
    String getEntityVariantRarity();
    boolean getSpawnedAsBoss();

    default void setAllFromCreature(BaseCreatureEntity creature){
        this.setCreatureTypeName(creature.creatureInfo.getName());
        if(creature.hasCustomName()) this.setCustomName(creature.getCustomNameTag());
        this.setLevel(creature.getLevel());
        this.setEntitySubspecies(creature.getSubspeciesIndex());
        this.setEntityVariant(creature.getVariantIndex());
        if(creature.getVariant() != null) {
            this.setEntityVariantRarity(creature.getVariant().rarity.toUpperCase());
        }
        this.setSpawnedAsBoss(creature.spawnedAsBoss);
    }
    void setCreatureTypeName(String type);
    void setCustomName(String name);
    void setLevel(int level);
    void setEntitySubspecies(int index);
    void setEntityVariant(int index);
    void setEntityVariantRarity(String rarity);
    void setSpawnedAsBoss(boolean spawnedAsBoss);

    void readNBT(NBTTagCompound nbtTagCompound);
    void writeNBT(NBTTagCompound nbtTagCompound);

    @Nullable
    NBTTagCompound getCachedNBT();
    void setCachedNBT(@Nullable NBTTagCompound nbt);
}
