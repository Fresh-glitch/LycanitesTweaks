package lycanitestweaks.capability.playermoblevel;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.pets.PetEntry;
import lycanitestweaks.LycanitesTweaks;
import lycanitestweaks.handlers.ForgeConfigHandler;
import lycanitestweaks.handlers.config.major.PlayerMobLevelsConfig;
import lycanitestweaks.network.PacketHandler;
import lycanitestweaks.network.PacketPlayerMobLevelsModifiers;
import lycanitestweaks.network.PacketPlayerMobLevelsStats;
import lycanitestweaks.util.Helpers;
import lycanitestweaks.util.LycanitesEntityUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class PlayerMobLevelCapability implements IPlayerMobLevelCapability {

    private EntityPlayer player;
    public static final int MAINHAND_CHECK_SIZE = 8;
    public boolean needsFullSync = true; // TODO This needs a better update, cleanup how client relog can reset modifiers

    private int deathCooldown = 0;
    public int[] nonMainLevels = new int[5];
    public Queue<Integer> mainHandLevels = new LinkedList<>();
    public Map<Integer, Integer> petEntryLevels = new HashMap<>();
    private Object[] petEntryLevelsCopy = null;
    public int highestActivePetLevels = 0; // cache value, only used with mixin enabled, clearHighestLevelPetActive flags dirty
    private boolean highestActivePetDirty = true;
    public HashMap<String, Float> pmlModifiers = new HashMap<>();
    public float defaultModifier = 1.0F;

    PlayerMobLevelCapability(){}

    PlayerMobLevelCapability(@Nonnull EntityPlayer player){
        this.player = player;
        Arrays.fill(nonMainLevels, 0);
        mainHandLevels.add(0);
    }

    public static IPlayerMobLevelCapability getForPlayer(EntityPlayer player) {
        if (player == null || !ForgeConfigHandler.majorFeaturesConfig.pmlConfig.playerMobLevelCapability) {
            return null;
        }
        IPlayerMobLevelCapability pml = player.getCapability(PlayerMobLevelCapabilityHandler.PLAYER_MOB_LEVEL, null);
        if (pml != null && pml.getPlayer() != player) {
            pml.setPlayer(player);
        }
        return pml;
    }

    @Override
    public EntityPlayer getPlayer() {
        return this.player;
    }

    @Override
    public void setPlayer(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void updateTick() {
        if(this.deathCooldown > 0) this.deathCooldown--;

        // Initial Network Sync:
        if(!this.player.getEntityWorld().isRemote && this.needsFullSync) {
            PacketPlayerMobLevelsModifiers packet = new PacketPlayerMobLevelsModifiers(this);
            EntityPlayerMP playerMP = (EntityPlayerMP) this.player;
            PacketHandler.instance.sendTo(packet, playerMP);

            this.needsFullSync = false;
        }
    }

    @Override
    public void sync() {
        if(!this.player.world.isRemote) {
            PacketPlayerMobLevelsStats levelPacket = new PacketPlayerMobLevelsStats(this);
            EntityPlayerMP playerMP = (EntityPlayerMP) this.player;
            PacketHandler.instance.sendTo(levelPacket, playerMP);
        }
    }

    @Override
    public void syncAllModifiers() {
        if(this.player.world.isRemote) {
            PacketPlayerMobLevelsModifiers packet = new PacketPlayerMobLevelsModifiers(this);
            PacketHandler.instance.sendToServer(packet);
        }
    }

    @Override
    public int getTotalLevelsForCategory(PlayerMobLevelsConfig.BonusCategory category) {
        return this.getTotalLevelsForCategory(category, null);
    }

    @Override
    public int getTotalLevelsForCategory(PlayerMobLevelsConfig.BonusCategory category, @Nullable BaseCreatureEntity creature) {
        return this.getTotalLevelsForCategory(category, creature, false);
    }

    @Override
    public int getTotalLevelsForCategory(PlayerMobLevelsConfig.BonusCategory category, @Nullable BaseCreatureEntity creature, boolean client) {
        double totalLevels = 0;
        double deathModifier = 0;

        if(PlayerMobLevelsConfig.getPmlBonusCategories().containsKey(category)){
            for(PlayerMobLevelsConfig.Bonus bonus : PlayerMobLevelsConfig.Bonus.values()) {
                Double modifier = null;

                switch (PlayerMobLevelsConfig.getPmlBonusCategories().get(category).getLeft()) {
                    case WILD:
                        if(PlayerMobLevelsConfig.getPmlBonusUsagesWild().containsKey(bonus)) {
                            modifier = PlayerMobLevelsConfig.getPmlBonusUsagesWild().get(bonus);
                            modifier *= this.getPMLModifierForCreature(creature);
                        }
                        break;
                    case TAMED:
                        if(PlayerMobLevelsConfig.getPmlBonusUsagesTamed().containsKey(bonus))
                            modifier = PlayerMobLevelsConfig.getPmlBonusUsagesTamed().get(bonus);
                        break;
                    case ALL:
                        if(PlayerMobLevelsConfig.getPmlBonusUsagesAll().containsKey(bonus)) {
                            modifier = PlayerMobLevelsConfig.getPmlBonusUsagesAll().get(bonus);
                            modifier *= this.getPMLModifierForCreature(creature);
                        }
                }
                if(modifier == null){
                    modifier = PlayerMobLevelsConfig.getPmlBonusUsagesAll().getOrDefault(bonus, 0.0D);
                }

                switch (bonus){
                    case ActivePet:
                        totalLevels += this.getHighestLevelPetActive() * modifier;
                        break;
                    case BestiaryCreature:
                        if(!client) totalLevels += this.getCurrentLevelBestiary(creature) * modifier;
                        break;
                    case BestiaryElement:
                        if(creature != null) totalLevels += this.getCurrentLevelBestiary(creature.getElements()) * modifier;
                        break;
                    case Enchantments:
                        totalLevels += this.getTotalEnchantmentLevels() * modifier;
                        break;
                    case PlayerDeath:
                        deathModifier = modifier;
                    default:
                }
            }
            totalLevels *= PlayerMobLevelsConfig.getPmlBonusCategories().get(category).getRight();
        }

        if(this.getDeathCooldown() > 0) {
            totalLevels *= (1D + deathModifier);
        }
        return (int)Math.round(totalLevels);
    }

    @Override
    public float getPMLModifierForCreature(BaseCreatureEntity creature) {
        if(creature != null && ForgeConfigHandler.majorFeaturesConfig.pmlConfig.setPMLModifiersBeastiary) {
            String creatureName = creature.creatureInfo.getName();
            if (this.pmlModifiers.containsKey(creatureName)) return this.pmlModifiers.get(creatureName);

            return this.defaultModifier;
        }
        return 1.0F;
    }

    @Override
    public float getPMLModifierForAll() {
        return this.defaultModifier;
    }

    @Override
    public void setPMLModifierForCreature(BaseCreatureEntity creature, float modifier) {
        if(creature != null) {
            this.pmlModifiers.put(creature.creatureInfo.getName(), MathHelper.clamp(modifier, 0F, 1F));
            this.syncAllModifiers();
        }
    }

    @Override
    public void setPMLModifierForAll(float modifier) {
        this.defaultModifier = MathHelper.clamp(modifier, 0F, 1F);
        this.pmlModifiers.clear();
        this.syncAllModifiers();
    }

    @Override
    public int getTotalEnchantmentLevels(){
        int total = 0;

        for(int lvl : nonMainLevels){
            total += lvl;
        }
        total += getHighestMainHandLevels();

        return total;
    }

    @Override
    public int getCurrentLevelBestiary(BaseCreatureEntity creature) {
        if(creature != null) {
            ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(this.player);
            if (extendedPlayer != null && extendedPlayer.getBeastiary().hasKnowledgeRank(creature.creatureInfo.getName(), 0)) {
                CreatureKnowledge knowledge = extendedPlayer.getBeastiary().getCreatureKnowledge(creature.creatureInfo.getName());
                float experience;
                if(knowledge.rank >= 2){
                    experience = 1F;
                }
                else {
                    experience = knowledge.experience / 1000F;
                }
                return Math.round(experience * LycanitesEntityUtil.getStartingLevel(creature));
            }
        }
        return 0;
    }

    /**
     * Gets the base level bonus for a list of elements, will only choose the element with the highest bonus
     * WARNING, this does not check if a given ElementInfo is in the Creature-Elements map
     *
     * @param elements List of elements to check
     * @return Base level bonus
     */
    @Override
    public int getCurrentLevelBestiary(List<ElementInfo> elements) {
        int total = 0;
        ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(this.player);
        if (extendedPlayer != null) {
            for(ElementInfo elementInfo : elements){
                int elementTotal = 0;
                if(Helpers.getCreatureElementsMap().containsKey(elementInfo.name))
                    for(String creatureName : Helpers.getCreatureElementsMap().get(elementInfo.name)){
                        if(extendedPlayer.getBeastiary().hasKnowledgeRank(creatureName, 2)) elementTotal++;
                    }
                total = Math.max(total, elementTotal);
            }
        }
        return total;
    }

    @Override
    public int getDeathCooldown() {
        return this.deathCooldown;
    }

    @Override
    public int getHighestLevelPetActive() {
        int total = 0;
        if(!this.highestActivePetDirty) return this.highestActivePetLevels;

        ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(this.player);
        if (extendedPlayer != null) {
            for(PetEntry petEntry : extendedPlayer.petManager.entries.values()){
                if(ForgeConfigHandler.debug.debugLoggerTrigger) LycanitesTweaks.LOGGER.log(Level.DEBUG, "Total:{} PetEntry:{} Cache:{}", total, petEntry.getLevel(), this.highestActivePetLevels);
                if(petEntry.spawningActive){
                    total = Math.max(total, petEntry.getLevel());
                    if(ForgeConfigHandler.majorFeaturesConfig.pmlConfig.petManagerTracksHighestLevelPet) {
                        this.highestActivePetLevels = total;
                    }
                }
            }
        }
        this.highestActivePetDirty = false;
        return total;
    }

    @Override
    public int getHighestLevelPetSoulbound() {
        if(!ForgeConfigHandler.majorFeaturesConfig.pmlConfig.pmlCalcHighestLevelPet || this.petEntryLevels.isEmpty()) return 0;
        if(this.petEntryLevelsCopy == null) {
            this.petEntryLevelsCopy = petEntryLevels.keySet().toArray(new Integer[0]);
            Arrays.sort(petEntryLevelsCopy, Comparator.comparingInt(a -> (int) a).reversed());
        }
        if(ForgeConfigHandler.debug.debugLoggerAutomatic)
            LycanitesTweaks.LOGGER.log(Level.DEBUG, "Highest: {} Level Map: {}", this.petEntryLevelsCopy[0], this.petEntryLevels);
        return (int)this.petEntryLevelsCopy[0];
    }

    @Override
    public int getHighestMainHandLevels(){
        Object[] mainHandLevelsCopy = mainHandLevels.toArray();
        Arrays.sort(mainHandLevelsCopy, Comparator.comparingInt(a -> (int)a).reversed());
        return (int)mainHandLevelsCopy[0];
    }

    @Override
    public int getItemStackLevels(ItemStack itemStack){
        int levels = 0;
        NBTTagList enchantments = itemStack.getEnchantmentTagList();
        if(enchantments.isEmpty()) return 0;
        for(int i = 0; i < enchantments.tagCount(); i++){
            Enchantment ench = Enchantment.getEnchantmentByID(enchantments.getCompoundTagAt(i).getShort("id"));
            if(ench != null) {
                int enchLvl = enchantments.getCompoundTagAt(i).getShort("lvl");
                int enchantabilityLevels = ench.getMinEnchantability(Math.max(1, ench.getMinLevel()))
                        * Math.round((float) enchLvl / Math.max(1, ench.getMaxLevel()));
                // Enchantability * (level / maxLevel)

                switch (ench.getRarity()){
                    case COMMON:
                        enchantabilityLevels /= ForgeConfigHandler.majorFeaturesConfig.pmlConfig.enchRarityDivisors[0];
                        break;
                    case UNCOMMON:
                        enchantabilityLevels /= ForgeConfigHandler.majorFeaturesConfig.pmlConfig.enchRarityDivisors[1];
                        break;
                    case RARE:
                        enchantabilityLevels /= ForgeConfigHandler.majorFeaturesConfig.pmlConfig.enchRarityDivisors[2];
                        break;
                    case VERY_RARE:
                        enchantabilityLevels /= ForgeConfigHandler.majorFeaturesConfig.pmlConfig.enchRarityDivisors[3];
                        break;
                }

                levels += enchantabilityLevels;

                if(ForgeConfigHandler.debug.debugLoggerTrigger)
                    LycanitesTweaks.LOGGER.log(Level.DEBUG, "ENCH: {}, LEVELS: {}", ench.getName(), enchantabilityLevels);
            }
        }
        return levels;
    }

    // Used by PetManager and when a new PetEntry instance is made with an existing mob
    @Override
    public void addNewPetLevels(int levels) {
        if(this.petEntryLevels.containsKey(levels)){
            this.petEntryLevels.put(levels, this.petEntryLevels.get(levels) + 1);
        }
        else{
            this.petEntryLevels.put(levels, 1);
            this.petEntryLevelsCopy = null;
        }
        this.sync();
    }

    // For adding from PetManager NBT
    @Override
    public void addPetEntryLevels(PetEntry entry) {
        this.addNewPetLevels(entry.getLevel());
    }

    @Override
    public void clearHighestLevelPetActive() {
        this.highestActivePetLevels = 0;
        this.highestActivePetDirty = true;
        this.sync();
    }

    // Removal from PetManager is accurate
    @Override
    public void removePetEntryLevels(PetEntry entry) {
        int level = entry.getLevel();
        if(this.petEntryLevels.containsKey(level)){
            if(this.petEntryLevels.get(level) > 1)
                this.petEntryLevels.put(level, this.petEntryLevels.get(level) - 1);
            else{
                this.petEntryLevels.remove(level);
                this.petEntryLevelsCopy = null;
            }
            this.sync();
        }
        else{
            if(ForgeConfigHandler.debug.debugLoggerTrigger) LycanitesTweaks.LOGGER.log(Level.DEBUG, "Warning tried to remove when petEntryLevels did not have key: {}", level);
        }
    }

    @Override
    public void setDeathCooldown(int cooldownTicks) {
        this.deathCooldown = cooldownTicks;
    }

    @Override
    public void setNonMainLevels(ItemStack itemStack, int slotIndex) {
        this.nonMainLevels[slotIndex-1] = getItemStackLevels(itemStack);
        this.sync();
    }

    @Override
    public void setMainHandLevels(ItemStack itemStack){
        this.mainHandLevels.add(getItemStackLevels(itemStack));
        while(this.mainHandLevels.size() > PlayerMobLevelCapability.MAINHAND_CHECK_SIZE) this.mainHandLevels.poll();
        this.sync();
    }

    @Override
    public void readNBT(NBTTagCompound nbtTagCompound) {
        NBTTagCompound extTagCompound = nbtTagCompound.getCompoundTag("LycanitesTweaksPML");

        if(extTagCompound.hasKey("DefaultModifier")){
            this.defaultModifier = extTagCompound.getFloat("DefaultModifier");
        }

        if(extTagCompound.hasKey("CreatureModifiers")){
            NBTTagList perCreatureModifiers = extTagCompound.getTagList("CreatureModifiers", 10);
            perCreatureModifiers.forEach((nbtEntry) -> {
                if (nbtEntry instanceof NBTTagCompound) this.pmlModifiers.put(
                        ((NBTTagCompound) nbtEntry).getString("CreatureName"),
                        ((NBTTagCompound) nbtEntry).getFloat("Modifier")
                );
            });
        }
    }

    @Override
    public void writeNBT(NBTTagCompound nbtTagCompound) {
        NBTTagCompound extTagCompound = new NBTTagCompound();

        extTagCompound.setFloat("DefaultModifier", this.defaultModifier);
        NBTTagList perCreatureModifiers = new NBTTagList();
        this.pmlModifiers.forEach((creatureName, modifier) -> {
            NBTTagCompound nbtEntry = new NBTTagCompound();
            nbtEntry.setString("CreatureName", creatureName);
            nbtEntry.setFloat("Modifier", modifier);
            perCreatureModifiers.appendTag(nbtEntry);
        });
        extTagCompound.setTag("CreatureModifiers", perCreatureModifiers);

        nbtTagCompound.setTag("LycanitesTweaksPML", extTagCompound);
    }
}
