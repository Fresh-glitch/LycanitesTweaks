package lycanitestweaks.handlers.config;

import fermiumbooter.annotations.MixinConfig;
import lycanitestweaks.LycanitesTweaks;
import net.minecraftforge.common.config.Config;

@MixinConfig(name = LycanitesTweaks.MODID)
public class MinorFeaturesConfig {

    @Config.Comment("Adds modded Ice and Lightning Resistance auras to pets to match the Fire Resistance aura")
    @Config.Name("Tamed Ice and Lightning Resistance Auras")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.petelemresistance.json")
    public boolean tameIceAndLightningResistance = true;

    @Config.Comment("The id for the Ice Resistance potion effect for a tame with any element with the \"canFreeze\" property set to false.")
    @Config.Name("Tamed Ice Resistance - ID")
    public String tameIceResistanceID = "xat:ice_resistance";

    @Config.Comment("The id for the Lightning Resistance potion effect for a tame that has the \"lightning\" element")
    @Config.Name("Tamed Lightning Resistance - ID")
    public String tameLightningResistanceID = "xat:lightning_resistance";

    /*
     *
     * Features, no plans on porting Vanilla Mixins into Lycanites
     *
     */

    @Config.Comment({
            "Tamed Lycanites damage credit will be like vanilla Wolves, affects loot and damage bonus behaviors.",
            "    XP Drop credit will be enabled for any tamed attacked.",
            "    Item Drop credit will consider the tamed entity instead of the player.",
            "    Damage credit will consider only the tamed entity instead of involving the player."
    })
    @Config.Name("Vanilla-like Tame-able Damage Credit (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, earlyMixin = "mixins.lycanitestweaks.feature.ownercreditvanillalike.vanilla.json", lateMixin = "mixins.lycanitestweaks.feature.ownercreditvanillalike.json")
    public boolean vanillaTameableDmgCredit = true;

    @Config.Comment({
            "Additionally applies the changes to all own-able entities instead of only tame-able.",
            "    Would include own-able hostiles, such as other mod's summons"
    })
    @Config.Name("Vanilla-like Tameable Damage Credit - Generic Own-able")
    public boolean vanillaTameableDmgCreditAll = true;

    @Config.Comment({
            "Improves hit detection of very large Lycanites Entities by skipping the sub chunk filter in AABB searches.",
            "    Includes Rahovart, Asmodeus, and Amalgalich.",
            "    Includes any Lycanites entity with a size scale over the max random size defined in the config.",
            "    More performant than modifying World.MAX_ENTITY_RADIUS and creating MultiPart Entities."
    })
    @Config.Name("Improved Large Entity AABB Searching (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, earlyMixin = "mixins.lycanitestweaks.feature.largeentitysearch.vanilla.json")
    public boolean improveLargeHurtboxes = true;

    @Config.Comment("List of Entity IDs for other large entities. These entities should never commonly spawn as this will affect performance")
    @Config.Name("Improved Large Entity AABB Searching - Additional Entities")
    public String[] improveLargeOtherEntityIDs = {

    };

    @Config.Comment({
            "Enforce an earlier check of the \"Disable Sneak Dismount\" option for Lycanites entities",
            "This will retain the current sneaking state instead of resetting it",
            "Provides more consistent behavior for any sneaking checks done simultaneously on both client and server, such as with items."
    })
    @Config.Name("Fix Sneaking Flag When Dismounting (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, earlyMixin = "mixins.lycanitestweaks.feature.vanilla.dismountsneakflag.json")
    public boolean fixSneakDismountFlag = true;

    @Config.Comment("Adds automatic despawning of nearby Event spawned mobs when the Beacon provides buffs. The effect's range matches the Player buffing range.")
    @Config.Name("Beacon Clear Event Mobs (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, earlyMixin = "mixins.lycanitestweaks.feature.vanilla.beaconeventmobs.json")
    public boolean beaconClearsEvent = true;

    @Config.Comment("Fix Withers and their side heads attacking Tremors")
    @Config.Name("Fix Withers Attacking Tremors (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, earlyMixin = "mixins.lycanitestweaks.vanilla.withertargettremor.json")
    public boolean fixWitherTargetTremor = true;

    @Config.Comment("Makes all vanilla Entities (and all modded Entities that don't have a specified Creature Attribute) an Undead creature while the Smited effect is active. This will for example allow the Smite enchant to work on them.")
    @Config.Name("Most Smited Are Undead (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = false, earlyMixin = "mixins.lycanitestweaks.vanilla.smitedundeadlivingbase.json")
    public boolean smitedMakesMostUndead = false;

    /*
     *
     * Features that I feel are otherwise out of scope of LycanitesMobs, can change of course
     *
     */

    @Config.Comment("Overrides the Lycanites entities which have Solid Collision with other entities")
    @Config.Name("Override Solid Collision")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.overridesolidcollision.json")
    public boolean overrideSolidCollision = true;

    @Config.Comment("Names of creatures that will be given Solid Collision. The defaults are the original hardcoded mobs.")
    @Config.Name("Override Solid Collision - Affected Creatures")
    public String[] solidCollisionMobs = {
            "asmodeus",
            "beholder",
            "ettin",
            "khalk",
            "rahovart",
            "treant",
            "troll",
            "vespidqueen"
    };

    @Config.Comment({
            "Potion Effects provided from eating Lycanites Foods will be immune from targeted cleansing.",
            "    Cleansing based on checking potions properties, such as negative/positive, WILL BE prevented.",
            "    Purging based on removing every potion, such as with Totems of Undying, WILL NOT BE prevented.",
            "    The trait is lost if replaced with a higher amplifier or longer duration."
    })
    @Config.Name("Potion Effects From Food Cleanse Immune")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.foodcleanseimmune.json")
    public boolean foodPotionNoRemove = true;

    @Config.Comment({
            "Allows Players to force their pets/mounts to lift off or land:",
            "\tMount Ability Keybind - Fly Immediately",
            "\tMount Dismount Keybind - Land Immediately",
            "Flying Mounts will actively try to switch states instead of being stuck in the Flying/Landed state:",
            "\tOwner is flying - Fly Immediately",
            "\tOwner is riding another flying mount - Fly Immediately",
            "\tOwner is sneaking on the ground - Land Immediately",
            "Additionally taming the following will reduce their AI wander chance to match other entities (31x less likely):",
            "\tGrigori, Morock, Quetzodracl, Raiko, Roc, Wisp, Wraith"
    })
    @Config.Name("Control Flying Pet/Mount Landing")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.petcontrollanding.json")
    public boolean petControlLanding = true;

    @Config.Comment({
            "Register Attributes and Handlers to replicate certain Attributes exclusive to Lycanites entities",
            "\tDefence - Flat damage reduction calculated after Armor and Protection",
            "\tPierce - Flat damage as an additional attack that ignores Armor and Protection",
            "\tRanged Speed - Not used as an attribute, treated as the rate specific items can be used",
            "Additionally makes Lycanites entities use the attributes as they were not using them already."
    })
    @Config.Name("Lycanites Creature Attributes For All Mobs")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.lycanitesuseunusedattributes.json")
    public boolean lycanitesAttributesForAll = true;

    @Config.Comment({
            "Remove various Fear counters/immunities that don't make sense.",
            "These were copied from the living entity pickup mechanic:",
            "\tHardcoded \"Heavy\" Entities Group",
            "\tEntities with height or width greater than 4.0",
            "\tTargets riding a modded vehicle",
            "\tTargets with the weight potion effect",
            "\tTargets with the repulsion potion effect",
            "Additionally makes entities without gravity (such as Vexes) use flying movement instead of acting grounded."
    })
    @Config.Name("Remove Odd Fear Counters/Immunities")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.fearmorethings.json")
    public boolean fearMoreThings = true;

    @Config.Comment("When a player logs out, the current Mob Event and remaining duration is saved to that player.\n" +
            "This will allow Mob Event mobs to keep spawning if the player logs out.\n" +
            "Logging back in will show a message with the remaining duration.\n" +
            "Only one event can be saved at a time.\n" +
            "Changing dimensions will clear the save as the property isn't saved.")
    @Config.Name("Player Logout Saves Mob Event")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.savedmobevents.json")
    public boolean logoutSavesMobEvents = true;

    @Config.Comment("Multiplier on the remaining duration that will be saved.")
    @Config.Name("Player Logout Saves Mob Event - Remaining Duration")
    public float savedMobEventDurationAmount = 0.1F;

    @Config.Comment("Naturally spawned Aegis can pickup and drop Iron Golems when specifically defending them.\n" +
            "Aegis will try to move Iron Golems out of water and very rarely do the same for Villagers.")
    @Config.Name("Aegis Pickup Iron Golems")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.aegispickupgolems.json")
    public boolean aegisPickupGolem = true;

    @Config.Comment("Adds parity with Iron Golem AI for naturally spawned Aegis\n" +
            "\tWill patrol the village at night.\n" +
            "\tWill be hostile to the same mobs as Iron Golems.\n" +
            "\tWill set a home position at the village.\n" +
            "\tWill penalize village reputation on death.")
    @Config.Name("Aegis Iron Golem AI")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.aegisirongolemai.json")
    public boolean aegisIronGolemAI = true;

    @Config.Comment("AI move speed modifier when patroling the village at night. Vanilla uses 0.6 for Iron Golems.")
    @Config.Name("Aegis Iron Golem AI - Village Patrol Speed")
    @Config.RequiresMcRestart
    public float aegisVillagePatrolSpeed = 0.2F;

    @Config.Comment("The Dungeon Boss Room exit will have a single layer of fence/wall blocks")
    @Config.Name("Dungeon Boss Room Exit Wall")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.dungeonbossexitwall.json")
    public boolean dungeonBossExitWall = true;

    @Config.Comment({
            "Makes the Melee Chasing AI minimum range increase with the size of the mob.",
            "Allows mobs with both ranged and melee attacks to swap to melee attacks when their size is large.",
            "This has no affect on their damage dealt conditions.",
            "\tEx. Large Astaroths can swap to their melee as a flat 8.0 range is within their hitbox."
    })
    @Config.Name("Minimum Melee Chase Range Increases With Size")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.meleeminimumrangeplus.json")
    public boolean meleeRangeMinPlus = true;

    @Config.Comment({
            "Make the Ranged AI maximum range increase with the size of the mob.",
            "Allows mobs with large sizes to attack in cases where their width covers the maximum flat range restrictions.",
            "Specifically a mob will be able shoot their projectiles from within the flat range + scaled melee attack range.",
            "\tEx. Large Iorays can attack further than their flat 8.0 range restriction."
    })
    @Config.Name("Maximum Ranged Attack Range Increases With Size")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.rangedmaximumrangeplus.json")
    public boolean rangedRangeMaxPlus = true;

    @Config.Comment("Adds additional Pet Commands.\n" +
            "Fight Boss - Whether the pet can target Bosses, Lycanite Bosses generally have a -75% damage reduction from non players.\n" +
            "Grief - Whether pets such as Beholder, Cacodemon, Troll, and Wraith can break blocks.")
    @Config.Name("Additional Pet Commands")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.petflags.json")
    public boolean additionalPetCommands = true;

    @Config.Comment("Bleed damage uses setDamageIsAbsolute ontop of Magic=Armor ignoring, making it ignore Resistance and other potion effects that reduce damage, as well as Protection enchantments.")
    @Config.Name("Bleed Pierces")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.featurebleedpierces.json")
    public boolean bleedPierces = true;

    @Config.Comment("Killing Boss Minions will directly mend armor. No XP Orb is dropped. Intended to encourage staying in Boss Arenas.")
    @Config.Name("Boss Minion Kills Provide Instant Mending")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.bossminionmend.json")
    public boolean bossMinionMending = true;

    @Config.Comment("Set to true to kill associated minions and projectiles when a Lycanites Mobs boss entity dies")
    @Config.Name("Boss Death Kills Minions and Projectiles")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.featurebossdeathminionprojectiles.json")
    public boolean bossDeathKillMinionProjectile = true;

    @Config.Comment({
            "Amalgalich, Asmodeus, and Rahovart will no longer take damage from players under the arena."
    })
    @Config.Name("Bosses Immune to Players Under Arena")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.bossunderarena.json")
    public boolean bossUnderArenaAntiCheese = true;

    @Config.Comment({
            "Amalgalich, Asmodeus, and Rahovart will no longer take damage from tamed entities based on the following:",
            "\tBoss Anti Flight Measures - The tame's owner is within the area specified in the vanilla Lycanites config.",
            "\tLine of Sight - The tame's owner must have line of sight if below or above the boss."
    })
    @Config.Name("Bosses Immune to Tames With Far Owners")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.bossfarpetdmg.json")
    public boolean bossPreventFarPetDamage = true;

    @Config.Comment({
            "Move the calculations of the Boss DPS Limit to a later point in the order, After [LivingAttackEvent] -> After [LivingDamageEvent].",
            "\tFixes one-shot exploits which drop mob loot early.",
            "\tFixes other damage bonuses ignoring the limit and dealing additional damage."
    })
    @Config.Name("Boss DPS Limit Recalc")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.bossdpsrecalc.json")
    public boolean bossDPSLimitRecalc = true;

    @Config.Comment("Allows the visual tracking range for Boss Projectile and Portal Sprites to be modified.\n" +
            "Rahovart's Hellfire Barriers are affected the most as ones on the other side of the arena did not render at all.")
    @Config.Name("Boss Projectile Modify Tracking Range")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.features.oldprojectiletracking.json")
    public boolean bossProjectileTracking = true;

    @Config.Comment("Lycanites uses 40")
    @Config.Name("Boss Projectile Modify Tracking Range - Value")
    @Config.RequiresMcRestart
    @Config.RangeInt(min = 40)
    public int bossProjectileTrackingRange = 80;

    public static final String ADD_DIAMOND_PAXEL = "Diamond Paxel";
    @Config.Comment("Adds a craftable \"Diamond Paxel\", a tier above the \"Iron Paxel\"")
    @Config.Name(ADD_DIAMOND_PAXEL)
    @Config.RequiresMcRestart
    public boolean addDiamondPaxel = true;

    @Config.Comment("Minimum Level override, can be set above 1 to have crafted Diamond Paxels use LycanitesTweaks equipment enchanting")
    @Config.Name("Diamond Paxel - Level")
    @Config.RangeInt(min = 1)
    public int diamondPaxelLevel = 3;

    @Config.Comment("When reading familiars from URL, Set Spawning Active to false to not automatically spawn them on login")
    @Config.Name("Familiars Inactive On Join")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.featurefamiliarsinactiveonjoin.json")
    public boolean familiarsInactiveOnJoin = true;

    @Config.Comment("Enable customizable biome list for Arisaurs with the custom name Flowersaur. Flowersaurs have a custom texture that is unused in base LycanitesMobs")
    @Config.Name("Flowersaurs Naturally Spawn")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.flowersaurspawning.json")
    public boolean flowersaurNaturalSpawning = true;

    @Config.Comment("List of biomes (modid:biomename) where custom name Arisaurs will spawn in")
    @Config.Name("Flowersaurs Naturally Spawn - Biomes")
    public String[] flowersaurSpawningBiomes = {
            "minecraft:mutated_forest",
            "biomesoplenty:mystic_grove",
            "twilightforest:enchanted_forest"
    };

    @Config.Comment({
            "Adjusts the handling of Lycanites Fire to be like Vanilla Fire",
            "    Fix explosion damage being reduced to 1 (as if it was a full block).",
            "    Uses fire punch-out handling instead of treating the fire as a full block.",
            "    Registers unused \"block side on fire\" visual variants."
    })
    @Config.Name("Lycanites Fire Vanilla Like")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, earlyMixin = "mixins.lycanitestweaks.vanilla.extinguishmoddedfire.json", lateMixin = "mixins.lycanitestweaks.feature.lycanitesfirepassable.json")
    public boolean lycanitesFiresNoBreakCollision = true;

    @Config.Comment("The Lycanites \"fixate target\" property is not used in vanilla, however LycanitesTweaks uses it to have mini bosses focus on players.\n" +
            "This modifies the behavior so mobs with this property can retaliate properly against other attackers.")
    @Config.Name("Lycanites Fixate Target Tweaks")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.fixaterevengetarget.json")
    public boolean lycanitesFixateTargetRevenge = true;

    @Config.Comment("Disable player tames from trying avoid an attacker when damaged to improve responsiveness and feel more controllable.")
    @Config.Name("Disable Tamed Avoid Target AI")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.tamedavoidtarget.json")
    public boolean disableTamedAvoidTarget = true;

    @Config.Comment("Lycanites grants a +2 Explosion Power to explosions caused by Rare variants, increasing damage by around 3x.\n" +
            "This will remove said bonus and no longer grant the large damage bonus as it is far above the intended Rare damage boost.")
    @Config.Name("Remove Projectile Explosion Radius Rare Bonus")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.features.projectileexplosionradius.json")
    public boolean removeBonusExplosionRadius = true;

    @Config.Comment("Adds more parity to Repulsion and Weight, repulsion gains weights benefits. This will make Roas, Spectres and Threshers unable to pull an entity with repulsion, as well as disallowing picking up an entity with Repulsion (Behemophet/Fear)")
    @Config.Name("Repulsion Weight Benefits")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.featurerepulsionweight.json")
    public boolean repulsionWeight = true;

    @Config.Comment("Whether a Lycanite Mob should be considered undead when the Smited effect is active. This will for example allow the Smite enchant to work on them.")
    @Config.Name("Lycanites Smited Are Undead")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = false, lateMixin = "mixins.lycanitestweaks.featuresmitedundeadbasecreature.json")
    public boolean smitedMakesBaseCreatureUndead = false;

    @Config.Comment("Adds the \"watermonster\" spawner type for JSON configs.\n" +
            "Functions exactly as the \"monster\" spawner type, however it also assigns spawning in Water Blocks.\n" +
            "The vanilla spawner is much faster than the lycanites spawner, however it will interact with other mods such as \"Bloodmoon\" increased spawn rates.\n" +
            "Warning: Overrides the vanilla ground spawning required for the \"monster\" spawner type when both are specified.\n" +
            "This will not automatically update JSONs.")
    @Config.Name("Water Monster Spawning")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.feature.watermonsterspawning.json")
    public boolean waterMonsterSpawning = true;

    @Config.Comment("The Vanilla Lycanites \"Use Surface Light Level\" spawn checks will go up to the Sea Surface instead of only up to 24 blocks up")
    @Config.Name("Water Monster Spawning - Check Sea Level Light")
    public boolean waterMonsterCheckSeaTop = true;

    @Config.Comment("1 in n chance to spawn. 40 is a Vanilla Ocean Drowned.")
    @Config.Name("Water Monster Spawning - Ocean Spawn Rate")
    @Config.RangeInt(min = 1)
    public int waterMonsterSpawnRateOcean = 40;

    @Config.Comment("1 in n chance to spawn. 15 is a Vanilla River Drowned.")
    @Config.Name("Water Monster Spawning - Other Spawn Rate")
    @Config.RangeInt(min = 1)
    public int waterMonsterSpawnRateOther = 15;

    @Config.Comment("Specifying the \"water\" spawner in Creature JSON configs will automatically assign the vanilla water spawning behavior.\n" +
            "This will not automatically disable the original \"water\" JSON spawner.\n" +
            "Warning: This will have issues with any mob with the \"monster\" spawner type also specified.\n" +
            "This is an automatic alternative compared to manually changing configs.")
    @Config.Name("Water Monster Spawning - Modify Loading")
    @Config.RequiresMcRestart
    public boolean waterMonsterSpawningAuto = false;

    public static final String REPLACE_WATER_MONSTER = "Water Monster Spawning - JSON Replacements";
    @Config.Comment("Load default JSON replacements which will swap most usages of the \"water\" spawner type with \"watermonster\".\n" +
            "The usage of the \"waterfloor\" type will not be changed.\n" +
            "If the \"monster\" spawner type is specified, then the mob will keep the original \"water\" spawner type.\n" +
            "This is an automatic alternative compared to manually changing configs.")
    @Config.Name(REPLACE_WATER_MONSTER)
    @Config.RequiresMcRestart
    public boolean waterMonsterSpawningConfigs = true;

    @Config.Comment({
            "Provides and option for Sky spawning using the vanilla spawner, however it requires more intrusive changes compared to \"Water Monster Spawning\"",
            "The \"spawners\" field in the Creature Config will parse for the following:",
            "\tmonster - Will assign the creature to EnumCreatureType.MONSTER",
            "\tsky - Broadly matches all the varieties of sky json spawners",
            "The first entry with both in the Creature Config will check for an associated json for yMin and yMax checks",
            "\"skyhighmonster\" would check the skyhigh.json for example",
            "JSON configs must be manually changed"
    })
    @Config.Name("Sky Monster Spawning")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = false, earlyMixin = "mixins.lycanitestweaks.feature.skymonsterspawningvanilla.json", lateMixin = "mixins.lycanitestweaks.feature.skymonsterspawning.json")
    public boolean skyMonsterSpawning = false;

    @Config.Comment({
            "1 in n chance to replace the upper bound of the randomly chosen y position with the max world height.",
            "This will affect grounded spawning by providing many more (air) blocks to check.",
            "The vanilla spawner normally only selects the highest block for the upper bound."
    })
    @Config.Name("Sky Monster Spawning - Vanilla Replace Rate")
    @Config.RangeInt(min = 1)
    public int skyMonsterReplaceRate = 3200;

    @Config.Comment("1 in n chance to spawn. Should be set while mindful of the \"Vanilla Replace Rate\"")
    @Config.Name("Sky Monster Spawning - Spawn Rate")
    @Config.RangeInt(min = 1)
    public int skyMonsterSpawnRate = 1;
}
