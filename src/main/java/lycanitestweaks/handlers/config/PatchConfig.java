package lycanitestweaks.handlers.config;

import fermiumbooter.annotations.MixinConfig;
import lycanitestweaks.LycanitesTweaks;
import net.minecraftforge.common.config.Config;

@MixinConfig(name = LycanitesTweaks.MODID)
public class PatchConfig {
    /*
     *
     * Temporary Mixin Patches, intended to become PR in LycanitesMobs
     *
     */

    @Config.Comment({
            "Lycanites Mobs a few config map errors and therefore always uses a hidden default.",
            "This will allow the missing configs to generate and be changed.",
            "\tCreatureConfig -> \"Elemental Fusion Enabled\"",
            "\tCreatureConfig -> \"Elemental Fusion Mix Bonus\"",
            "The following are default misspellings that would need to be regenerated or manually corrected.",
            "\tItemConfig -> \"prismarine_crystals\" (Max Sharpness repair item)",
            "The following configs are incorrectly processed in-game and are fixed."
    })
    @Config.Name("0. Fix Lycanites Config Errors")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.configerrors.json")
    public boolean fixLycanitesConfigErrors = true;

    @Config.Comment({
            "Most Lycanites Asset Managers are safe to reload and need late loading.",
            "Those that are already safe to reload are done so by LycanitesTweaks.",
            "Late loading fixes any silent errors involving biomes/entities that are loaded after Lycanites mobs",
            "If enabled, the following unsafe cases are fixed:",
            "Creature Manager",
            "\tEntity Item Drops being duplicated",
            "Equipment Part Manager",
            "\tItem Drops being duplicated",
            "Affects hard coded reloads and using the debug reload command."
    })
    @Config.Name("0. Fix Lycanites Asset Manager Reloading")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.assetmanagerreload.json")
    public boolean fixAssetManagerReloading = true;

    @Config.Comment({
            "Fixes Lycanites Entities sometimes receiving a minimum of 1.0 point of damage.",
            "Affects the following known cases:",
            "\tAn incoming attack's base damage is under 1.0 points of damage",
            "\tDamage is reduced under 1.0 by Armor Points",
            "\tDamage is reduced under 1.0 by Potion Effects",
            "\tDamage is reduced under 1.0 by Enchantments"
    })
    @Config.Name("Fix Lycanites Entities Minimum Damage Received")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.defmindmg.json")
    public boolean fixLycanitesMinimumDamageTaken = true;

    @Config.Comment({
            "Fixes Lycanites Pierce damage ignoring iframes when dealing pierce damage.",
            "Before: Save iframes -> deal pierce -> reduce vanilla damage -> reset iframes -> deal vanilla damage.",
            "After: Deal pierce -> deal vanilla damage.",
            "Will address the following issues:",
            "\tFinal IFrames being incorrectly set to a small value as the piece value approached the vanilla damage value",
            "\tDouble attacks when pierce and vanilla damage should be equal but float vs double values are different.",
            "\tArmor points having increased effectiveness as the vanilla damage portion was reduced by pierce damage value."
    })
    @Config.Name("Fix Lycanites Pierce Modifying IFrames")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.pierceiframes.json")
    public boolean fixPierceIFrameHandling = true;

    @Config.Comment({
            "Fixes Lycanites Projectiles and Mounted Abilities posting Forge AttackEntityEvent.",
            "Vanilla uses the event only for player's melee attacks and does not allow checking the Damage Source and Amount.",
            "\tLycanites Projectiles were already using ProjectileImpactEvent.",
            "\tMount Abilities were already using PotionAddedEvent."
    })
    @Config.Name("Fix Projectiles and Tames Firing Melee Events")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.playerattackevents.json")
    public boolean fixNonPlayerAttackEntityEvents = true;

    @Config.Comment("Fixes the transparent rendering that is usually only seen by Invisible mobs while in Spectator Mode")
    @Config.Name("Fix Transparent Lycanites Rendering")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.renderinvisspectator.json")
    public boolean fixRenderInvisCreature = true;

    @Config.Comment("Renders the mount transparent when the descending keybind is pressed")
    @Config.Name("Fix Transparent Lycanites Rendering - Transparent Mount While Descending")
    public boolean renderInvisMountDescend = false;

    @Config.Comment("Renders the mount transparent when the client player is sneaking")
    @Config.Name("Fix Transparent Lycanites Rendering - Transparent Mount While Sneaking")
    public boolean renderInvisMountSneak = false;

    @Config.Comment("Renders the mount transparent when the client player is sprinting")
    @Config.Name("Fix Transparent Lycanites Rendering - Transparent Mount While Sprinting")
    public boolean renderInvisMountSprint = false;

    @Config.Comment("Fixes the code structure having unnecessary overrides and copied handling. Required for \"Tamed Ice and Lightning Resistance Auras\" to work properly on mounts.")
    @Config.Name("Fix Tamed Potion Cleanse and Resistance Consistency")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.tameownereffects.json")
    public boolean fixTamedCleanseConsistency = true;

    @Config.Comment("Fixes the Lycanites entities with Village wandering AI standing still instead of walking around like vanilla Zombies")
    @Config.Name("Fix Village AI Pathfinding")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.villagenav.json")
    public boolean fixVillagePathfinding = true;

    @Config.Comment("Names of creatures that will be given Village wandering AI. The defaults are the original hardcoded mobs.")
    @Config.Name("Fix Village AI Pathfinding - Affected Creatures")
    public String[] villageNavAIMobs = {
            "cryptkeeper",
            "geist",
            "ghoul",
            "jabberwock",
    };

    @Config.Comment("Fixes the Lycanites entities with Door Breaking AI never being able to target Doors")
    @Config.Name("Fix Door Break AI Pathfinding")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.doorbreakai.json")
    public boolean fixDoorBreakPathfinding = true;

    @Config.Comment("Names of creatures that will be given Door Breaking AI. The defaults are the original hardcoded mobs.")
    @Config.Name("Fix Door Break AI Pathfinding - Affected Creatures")
    public String[] doorBreakAIMobs = {
            "belph",
            "cryptkeeper",
            "ettin",
            "geist",
            "ghoul",
            "jabberwock",
            "troll",
            "wildkin"
    };

    @Config.Comment("Fix the Summoning Staff portal staying active if the Staff was swapped away from, swapping will perform the right click release.")
    @Config.Name("Fix Summoning Staff Swap Off Portal")
    public boolean summoningStaffSwapOff = true;

    @Config.Comment({
            "Fix Projectiles checking Solid Block Material instead of the more accurate Block State check.",
            "Allows projectiles to hit targets within the fire or webs they place."
    })
    @Config.Name("Fix Projectile Non Solid Block Collision")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.projectilecollideeffect.json")
    public boolean projectileSolidCollide = true;

    @Config.Comment("Fix Hostile Wraith's charge ability selecting themselves as targets and instantly exploding.")
    @Config.Name("Fix Wraith Charge Instant Explode")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.wraithchargefix.json")
    public boolean wraithChargeSelf = true;

    @Config.Comment("Fix Minions of mobs having their numerical ID saved instead of their UUID, causing reloading entities to almost always lose track of their minions.")
    @Config.Name("Fix Minion NBT List Saving")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.nbtminionuuid.json")
    public boolean minionNBTSaving = true;

    @Config.Comment("Fix Rahovart not saving Hellfire Barriers when unloaded")
    @Config.Name("Fix Rahovart Phase 3 Barrier Unloading")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.rahobarrierssave.json")
    public boolean rahoBarrierSave = true;

    @Config.Comment("Server side will send packets to update Amalgalich's Consumption animation when it changes state (start/end/client load)")
    @Config.Name("Fix Amalgalich/Force Goal Desyncs")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.forcegoalsync.json")
    public boolean forceGoalSync = true;

    @Config.Comment("Fix name tagging pets being inconsistent as other right click actions taking priority")
    @Config.Name("Fix Name Tagging Pets")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.nametaguse.json")
    public boolean fixNametaggingPets = true;

    @Config.Comment({
            "Fix being able to use Soulstones on temporary minions, such as when enabling tameable and summonable creatures properties.",
            "Also makes breeding temporary minions also produce a child with the same limitations instead of being free to be Soulbound."
    })
    @Config.Name("Fix Soulstone Use On Minions")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.soulbindminion.json")
    public boolean fixSoulbindMinions = true;

    public static final String FIX_DESPAWN_SOULBOUNDS = "Fix Despawning Invalid Soulbound Pets";
    @Config.Comment("Fix unloaded Soulbound Pets being removed from the world improperly")
    @Config.Name(FIX_DESPAWN_SOULBOUNDS)
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.invalidsoulbound.json")
    public boolean fixInvalidSoulbounds = true;

    @Config.Comment("Fix /kill not working on Bosses and for any other usage of the kill command, such as with the Bloodmoons mod.")
    @Config.Name("Fix Boss Kill Command")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.bosskillcommand.json")
    public boolean fixBossKillCommand = true;

    @Config.Comment({
            "Fix divide by zero crash in FireProjectilesGoal and high RangedSpeed preventing attacks.",
            "Also fixes high level Quetzodracl divide by zero with a pickup victim."
    })
    @Config.Name("Fix Creature Attack Speed Crashes")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.attackspeeddividebyzero.json")
    public boolean fixRangedSpeedDivideZero = true;

    @Config.Comment("Fix the Damage Source used by minions not copying properties of their original attack. Most apparent with ranged attacks not being set as projectiles.")
    @Config.Name("Fix Minion Damage Source Properties")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.miniondamagesource.json")
    public boolean fixMinionDamageSource = true;

    @Config.Comment("Sometimes when Soulbound Pets are unloaded, they can not be teleported to their owner. This fixes it by respawning them.")
    @Config.Name("Respawn Unloaded Soulbound Pets")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.soulboundteleport.json")
    public boolean respawnUnloadedSoulboundPet = true;

    @Config.Comment("Equipment post LivingDestroyBlockEvent for every block broken. This fixes custom structure cheeses.")
    @Config.Name("Equipment Block Breaking Posts Forge Event")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.equipmentharvestforgeevent.json")
    public boolean equipmentHarvestPostsForgeEvent = true;

    public static final String FIX_CUSTOM_ITEM_DESPAWN = "Fix Missing Custom Item Entity Registry (LycanitesMobs)";
    @Config.Comment("Fixes the missing registry entry that causes lycanites item drops to always be removed when unloaded")
    @Config.Name(FIX_CUSTOM_ITEM_DESPAWN)
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.customitementityregistry.json")
    public boolean customItemEntityRegistryPatch = true;

    @Config.Comment("Fixes Withers attacking Tremors as the handling was broken. Additionally prevents Iron Golems from attacking tamed mobs.")
    @Config.Name("Fix canBeTargetedBy Handling")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.canbetargetedby.json")
    public boolean fixCanBeTargeted = true;

    @Config.Comment({
            "Lycanites Mobs intended invisibility to be super buffed, fully negating aggro except for mobs with true sight and night vision.",
            "This is disabled by default due to how powerful it is."
    })
    @Config.Name("Fix canBeTargetedBy Handling - Legacy Invisibility")
    public boolean fixCanBeTargetedLegacy = true;

    @Config.Comment({
            "Mounted Lycanites are not pushed around by other entities, however they will store velocity changes.",
            "This will fix damage and dismounting applying spontaneous velocity"
    })
    @Config.Name("Fix Spontaneous Mounted Velocity")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.mountedvelocityfix.json")
    public boolean fixRidingVelocity = true;

    @Config.Comment("Most tamed Lycanites do not clear their blacklisted entity targets, this will allowed tamed mobs to attack any mob.")
    @Config.Name("Fix Tamed Entity Can Attack Target")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.tamedcanattack.json")
    public boolean fixTamedCanAttack = true;

    @Config.Comment("Fix Amalgalich and Archvile overhead projectiles being offset from the center of their target and missing.")
    @Config.Name("Fix Overhead Projectile Offset")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.overheadprojectile.json")
    public boolean fixOverheadOffset = true;

    @Config.Comment({
            "Some Lycanites entity properties are not synced to client, this will fix:",
            "* Random Rare Variant boss bars not being shown upon spawning.",
            "* The Extra Mob Behavior NBT never being reloaded to client after being set.",
            "* A current health desync with Variants affecting HUD and Overlay mods."
    })
    @Config.Name("Sync Missing Properties")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.syncmissingproperties.json")
    public boolean syncMissingProperties = true;

    @Config.Comment({
            "Some Lycanites JSON configs have inconsistencies/missing functionalities that are expected.",
            "\tAllows \"crop\", \"ore\", and \"tree\" to use block whitelist/blacklist."
    })
    @Config.Name("Fix Lycanites JSON Consistency")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.jsoninheritance.json")
    public boolean fixJSONInheritance = true;

    @Config.Comment("Fix Ignibus and Serpix firing one of their projectiles with 10 times the intended offset")
    @Config.Name("Fix Entity Projectile Offset")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.rapidfireprojectileoffsetfix.json")
    public boolean entityRapidFireProjectileOffset = true;

    /*
     * Addressed
     * Block onEntityCollision
     * EntityIgnibus riderEffects
     * EntityIoray riderEffects onDismounted
     * EntityRoa riderEffects onDismounted
     * EntitySalamander riderEffects onDismounted
     * EntityThresher riderEffects onDismounted
     * GenericFoodItem onFoodEaten
     */
    @Config.Comment({
            "Lycanites Mobs has multiple instances of applying potion effects on client.",
            "This will fix known cases such as fire blocks, eating foods, and mounting interactions."
    })
    @Config.Name("Cancel Applying Potions on Client")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patchescancelclientpotionadd.json")
    public boolean cancelClientPotionAdding = true;

    @Config.Comment({
            "Fix an inconsistency in Rare/Boss entity audio where firing projectile, was 1/2 and 1/4 the range of other sound effects.",
            "Affects audible distance instead of actual audio volume.",
            "For example Rahovart's Hellfire Balls were muted at the edge of his arena while Asmodeus' Primary Attack could be heard at further distances.",
            "Asmodeus' Gatling [projectile.devilgatling] may need audio balancing since it is harsher than the deeper attack sfx and was rarely audible."
    })
    @Config.Name("Fix Boss Entity Projectile Volume")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patchesbossprojectilevolume.json")
    public boolean bossProjectileVolume = true;

    @Config.Comment({
            "Enable various unused sound files with obvious intent that can not be fixed with sounds.json",
            "Ignibus melee/range attack",
            "Rahovart's hell fire wave launch/activation",
            "All rapid fire projectiles (Cinder, Serpix, Ignibus)"
    })
    @Config.Name("Enable Unused Sounds")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.soundfixes.json")
    public boolean enableUnusedSounds = true;

    @Config.Comment({
            "Prevent bosses from having an avoid target to skip pathfinding and save on performance.",
            "Stationary bosses should not be pathfinding and their large sizes produces large lag spikes if they try to do so."
    })
    @Config.Name("Optimize Stationary Bosses Trying to Pathfind")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.bossavoidtarget.json")
    public boolean stationaryBossNullAvoidTarget = true;

    @Config.Comment({
            "Prevent explosions from affecting invincible large projectiles to save on performance.",
            "For example two or more Wraiths exploding in Rahovart's flamewalls produced large lag spikes."
    })
    @Config.Name("Optimize Explosions On Invincible Projectiles")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.bossprojectileexplosionimmunity.json")
    public boolean stationaryProjectileExplosions = true;

    @Config.Comment("Removes the floor/ceiling rounding with Rejuvenation and Decay. Rejuv will not have a minimum healing boost and Decay will not nullify low healing.")
    @Config.Name("Remove Healing Rejuv/Decay Rounding")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.featurerejuvdecayroundingbegone.json")
    public boolean removeHealEffectsRounding = true;

    public static final String FIX_HELLSHIELD_MINIONS = "Fix Asmodeus' Hellshield Minions";
    @Config.Comment("Fixes a bug where minions despawn and don't get properly cleared from boss mechanics")
    @Config.Name(FIX_HELLSHIELD_MINIONS)
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.asmodeusminions.json")
    public boolean fixAsmodeusMinions = true;

    @Config.Comment("Fix Lycanites Entities spawning their minions in walls. If collision is detected, spawn on top of host instead.")
    @Config.Name("Fix Minions Spawning in Walls")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patcheswallminions.json")
    public boolean fixWallMinion = true;

    // Preferring this approach as injecting pickup behavior into the vanilla bucket is silly
    @Config.Comment({
            "Register Forge's fluid buckets for Lycanites fluids. Fixes dispensers not being able to pick up Lycanites fluids.",
            "Keeps the original buckets loaded, the forge bucket item will appear where Lycanites custom bucket has no handling."
    })
    @Config.Name("Add Forge Universal Fluid Buckets")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patchesforgebucket.json")
    public boolean forgeFluidBuckets = true;

    @Config.Comment({
            "The vanilla Lycanites option for \"Variant Rare Despawning\" has an inconsistency where peaceful mode allowed (Chupacabras) entities never despawned.",
            "This will fix it and allow them to despawn to be consistent with other Rare variants."
    })
    @Config.Name("Rare Variant Despawning Consistency")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.raredespawnconsistency.json")
    public boolean rareVariantDespawnConsistency = true;

    @Config.Comment({
            "Fix Soulcubes not being instantly removed after the Boss Event starts.",
            "The intent was that the arena builder would replace it, however the delay allows the Soulcube to be collected in various cases."
    })
    @Config.Name("Main Boss Altar Instantly Removes Soulcube")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.altarmainbosssoulcube.json")
    public boolean altarMainBossConsumeSoulcube = true;

    @Config.Comment({
            "Fix Rahovart and Amalgalich arenas not being block protected until the boss spawns.",
            "The arena builder's walls use the default 30 block range instead of the boss's range, as Asmodeus's already do."
    })
    @Config.Name("Main Boss Altar Arena Protection")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.altarmainbossarenaprotection.json")
    public boolean altarMainBossArenaProtection = true;

    @Config.Comment("Fix Altar spawned bosses being able to despawn when Variant despawning is enabled")
    @Config.Name("Mini Boss Altar Persistence")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.altarminibosspersistence.json")
    public boolean altarMiniBossPersistence = true;

    @Config.Comment("Altars post LivingDestroyBlockEvent for every call to setBlockToAir. This fixes custom structure cheeses through griefing.")
    @Config.Name("Mini Boss Altar Posts Forge Event")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.altarforgeevent.json")
    public boolean altarPostsForgeEvent = true;

    @Config.Comment("PlaceBlockGoal post LivingDestroyBlockEvent for every call to canPlaceBlock. This fixes griefing protected blocks.")
    @Config.Name("Vespids Posts Forge Event")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patchesplaceblockgoalforgeevent.json")
    public boolean canPlaceBlockGoalForgeEvent = true;

    @Config.Comment("Disables Soul Bounds using portals, which would kill them and set respawn cooldown")
    @Config.Name("Disable Soul Bounds Using Portals")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patchessoulboundnoportal.json")
    public boolean soulBoundNoPortal = true;

    @Config.Comment("Fix hostile AgeableCreature babies not dropping loot")
    @Config.Name("Fix Baby Mobs Dropping No Loot")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patchesageablebabydrops.json")
    public boolean fixAgeableBabyDrops = true;

    // Rahovart is an example with oversight and can spawn infinite minions regardless of this
    @Config.Comment({
            "Add persistence to minions spawned through the summonMinion method",
            "This fixes vanilla Boss issues caused by despawning minions when mechanics depend on minions",
            "If master dies, minion will be force despawned after 1 minute",
            "Default is false as LycanitesTweaks boss enhancements directly addresses"
    })
    @Config.Name("Fix Boss Mechanics Minion Persistence")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patchesbasecreatureminionpersistence.json")
    public boolean fixBaseCreatureSummonPersistence = false;

    @Config.Comment("Add a call to super in BaseCreature's isPotionApplicable method, restores vanilla parity for Undead mobs")
    @Config.Name("Fix BaseCreature Potion Applicable")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patchesbasecreaturepotionapplicable.json")
    public boolean fixBaseCreaturePotionApplicableSuper = true;

    @Config.Comment({
            "Fix client side showing incorrect stats in the beastiary, interact gui, and other mods",
            "Additionally after server processes client side changes, send an update packet back to client"
    })
    @Config.Name("Fix Client Pet Stat Desync")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patchespetentrydesync.json")
    public boolean fixClientPetEntryVisual = true;

    @Config.Comment("Fix Ettin checking for inverted griefing flag")
    @Config.Name("Fix Ettin grief flag")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patchesettingriefflag.json")
    public boolean fixEttinBlockBreak = true;

    @Config.Comment("Fix Fear checking for creative capabilities instead of flight")
    @Config.Name("Fix Fear Survival Flying")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patchesfearsurvivalflying.json")
    public boolean fixFearSurvivalFlying = true;

    @Config.Comment("Fix HealWhenNoPlayersGoal trigger check using AND instead of OR therefore bricking in most cases")
    @Config.Name("Fix Heal Goal Check")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patcheshealgoalcheck.json")
    public boolean fixHealGoalCheck = true;

    @Config.Comment("Additional setting to have Boss Healing active when spectators are nearby")
    @Config.Name("Fix Heal Goal Check - Ignore Spectators")
    public boolean healGoalIgnoreSpectators = true;

    @Config.Comment("Additional setting to have Boss Healing active when survival mode players are under or over the boss AND have no line of sight")
    @Config.Name("Fix Heal Goal Check - Ignore Players Behind Walls")
    public boolean healGoalIgnoreWalls = true;

    @Config.Comment({
            "Lycanites allows both hands to interact with a pet with one action.",
            "Therefore the offhand can disrupt the mainhand with an inventory GUI or mounting.",
            "This is primarily aimed at fixing the case where you are healing pet with food and would be forced to always mount it."
    })
    @Config.Name("Fix Both Hands Performing Item Interactions")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.mainhandexclusiveinteract.json")
    public boolean fixMountingWithHealFood = true;


    // Mixin is not needed, original handling does not need to be canceled
    // Handle apply curing in PR, not needed here as original handling will catch active blindness
    @Config.Comment("Have NV deny Blindness from applying. This fixes visual flashing when blindness is applied every tick")
    @Config.Name("Fix Night Vision Curing Blindness")
    public boolean fixNVCuringBlindness = true;

    @Config.Comment({
            "Fix various issues related to the pickup mechanic's range checks.",
            "1. Lycanites entity auto drop distance check applying an extra square root.",
            "2. Lycanites entity desyncing when losing their pickup target from auto drop.",
            "3. Fear entity not being reset when unloaded.",
            "4. Fear entity losing track of target if it was picked up by another entity.",
            "5. Teleport location being out of range and overriding long distance travel.",
            "6. Fear entity being able to ride boats, minecarts, and other vehicles.",
            "7. Pickup being valid if attacker is riding/mounted.."
    })
    @Config.Name("Fix Pickup Range Checks")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.pickupfix.json")
    public boolean fixPickupRange = true;

    @Config.Comment({
            "Always release the picked up status whenever there is a Vanilla dismount.",
            "This behavior will match how teleporting an entity causes it to dismount anything it is riding.",
            "Will cover most cases where players could be teleported back into chunks that are unloaded."
    })
    @Config.Name("Fix Pickup Range Checks - Vanilla Teleport Parity")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, earlyMixin = "mixins.lycanitestweaks.patches.pickupfixvanilla.json")
    public boolean fixPickupVanillaDismount = true;

    @Config.Comment({
            "Override value for distance between entities to trigger auto pickup drop.",
            "Default Lycanites is 32. Can be set to -1 to use melee attack range."
    })
    @Config.Name("Fix Pickup Range Checks - Override Value")
    @Config.RangeDouble(min = -1)
    public double pickUpDistance = -1;

    @Config.Comment("Fix Pickup host entity losing track of target such as when holding inside a wall")
    @Config.Name("Fix Pickup Target")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patchespickuptarget.json")
    public boolean fixPickupTargetAlways = true;

    // Better fix https://gitlab.com/Lycanite/LycanitesMobs/-/merge_requests/461
    @Config.Comment("Fix properties, such as tamed state, being set after stat calculation. This fix adds a late additional recalculation.")
    @Config.Name("Fix Properties Set After Stat Calculation")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patches.latestatcalc.json")
    public boolean fixLateSettingProperties = true;

    @Config.Comment("Fix Serpix Blizzard projectile spawning in the ground")
    @Config.Name("Fix Serpix Blizzard Offset")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patcheserpixblizzardoffset.json")
    public boolean fixSerpixBlizzardOffset = true;

    @Config.Comment({
            "All Lyc containers (Equipment Forges, Infuser, Station, Summoning Pedestal) have no Item Quick Move implementation (via shift clicking).",
            "This fix makes them use newer mc quick move mechanics where the crafting slots are preferred over the player inventory+hotbar.",
            "Also fixes a minimal bug in Lyca Pet chest inventory where one slot (bottom right in player inventory) was not reachable via quick move."
    })
    @Config.Name("Fix Container Quick Move")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patchescontainerbettershifting.json")
    public boolean fixContainerQuickMove = true;

    @Config.Comment("Players can only interact with Lyca crafting blocks from very low distances. This fix instead makes them copy the vanilla block (crafting table, furnace etc) behavior")
    @Config.Name("Fix Tile Entity Interaction Distance")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.patchestileentityinteractiondistance.json")
    public boolean fixTileEntityInteractionDistance = true;
}
