package lycanitestweaks.handlers.features.entity;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.damagesources.MinionEntityDamageSource;
import com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal;
import com.lycanitesmobs.core.entity.goals.actions.MoveVillageGoal;
import com.lycanitesmobs.core.entity.navigate.CreaturePathNavigate;
import com.lycanitesmobs.core.info.CreatureManager;
import lycanitestweaks.LycanitesTweaks;
import lycanitestweaks.capability.playermoblevel.IPlayerMobLevelCapability;
import lycanitestweaks.capability.playermoblevel.PlayerMobLevelCapability;
import lycanitestweaks.entity.item.EntityEncounterSummonCrystal;
import lycanitestweaks.handlers.ForgeConfigHandler;
import lycanitestweaks.handlers.config.major.PlayerMobLevelsConfig;
import lycanitestweaks.util.EquipmentUtil;
import lycanitestweaks.util.Helpers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

import java.util.HashSet;
import java.util.Set;

public class EntityLivingHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDummyTargeted(LivingSetAttackTargetEvent event) {
        if(!ForgeConfigHandler.server.fearAttackTargetEvent) return;
        if(event.getTarget() instanceof BaseCreatureEntity && event.getEntityLiving() instanceof EntityLiving){
            if(((BaseCreatureEntity) event.getTarget()).creatureInfo.dummy)
                ((EntityLiving) event.getEntityLiving()).setAttackTarget(null);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityMount(EntityMountEvent event) {
        if(ForgeConfigHandler.mixinPatchesConfig.fixPickupRange) {
            // Living Base Mount or Dismount, update Pickup State
            if (event.getEntityMounting() instanceof EntityLivingBase) {
                ExtendedEntity extendedMounter = ExtendedEntity.getForEntity((EntityLivingBase) event.getEntityMounting());
                if (extendedMounter != null && extendedMounter.isPickedUp()) {
                    if (extendedMounter.pickedUpByEntity instanceof BaseCreatureEntity) {
                        ((BaseCreatureEntity) extendedMounter.pickedUpByEntity).dropPickupEntity();
                    } else {
                        extendedMounter.setPickedUpByEntity(null);
                    }
                }
            }
        }
    }

    // Equipment applies burning in hitEntity, after the damage, so a killing blow drops loot before the victim is on fire
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEquipmentBurningKill(LivingDeathEvent event) {
        if(!ForgeConfigHandler.mixinPatchesConfig.fixEquipmentBurningKill) return;
        EntityLivingBase victim = event.getEntityLiving();
        if(victim.world.isRemote || victim.isBurning()) return;

        // Melee only, RLCombat swaps an offhand weapon into the mainhand during the attack
        DamageSource source = event.getSource();
        if(!"player".equals(source.getDamageType()) || !(source.getTrueSource() instanceof EntityLivingBase) || source.getImmediateSource() != source.getTrueSource()) return;

        ItemStack weapon = ((EntityLivingBase) source.getTrueSource()).getHeldItemMainhand();
        int seconds = EquipmentUtil.getBurningSeconds(weapon);
        if(seconds > 0) victim.setFire(seconds);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        EntityLivingBase victim = event.getEntityLiving();
        if(ForgeConfigHandler.server.chargeExpConfig.tameDamageExperience) {
            Entity attacker = event.getSource().getTrueSource();
            if(!(attacker instanceof BaseCreatureEntity) && event.getSource() instanceof MinionEntityDamageSource)
                attacker = ((MinionEntityDamageSource) event.getSource()).getMinion();

            // Dealing Damage XP
            if(attacker instanceof BaseCreatureEntity) {
                BaseCreatureEntity attackerTame = (BaseCreatureEntity) attacker;
                if(canGainDamageXP(attackerTame)) {
                    float incomingDamage = event.getAmount();
                    float modifier = ForgeConfigHandler.server.chargeExpConfig.tameDamageDealtXP;
                    incomingDamage = Math.min(incomingDamage, victim.getHealth());

                    if(victim instanceof BaseCreatureEntity) {
                        BaseCreatureEntity victimCreature = (BaseCreatureEntity) victim;
                        if(victimCreature.damageLimit > 0)
                            incomingDamage = Math.min(incomingDamage, victimCreature.damageTakenThisSec);
                    }

                    if(incomingDamage >= 1F
                            && modifier != 0
                            && canGrantDamageXP(victim)
                            && attackerTame.getRNG().nextFloat() < ForgeConfigHandler.server.chargeExpConfig.tameDamageDealtXPChance)
                        attackerTame.addExperience((int) Math.max(1.0F, incomingDamage * modifier));
                }
            }

            // Taking Damage XP
            if(victim instanceof BaseCreatureEntity) {
                BaseCreatureEntity victimTame = (BaseCreatureEntity) victim;
                if(canGainDamageXP(victimTame)) {
                    float incomingDamage = event.getAmount();
                    float modifier = ForgeConfigHandler.server.chargeExpConfig.tameDamageTakenXP;
                    incomingDamage = Math.min(incomingDamage, victimTame.getHealth());

                    if(incomingDamage >= 1F
                            && modifier != 0
                            && canGrantDamageXP(attacker)
                            && victimTame.getRNG().nextFloat() < ForgeConfigHandler.server.chargeExpConfig.tameDamageTakenXPChance)
                        victimTame.addExperience((int) Math.max(1.0F, incomingDamage * modifier));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerPickupXp(PlayerPickupXpEvent event) {
        if(!ForgeConfigHandler.server.chargeExpConfig.vanillaKillExperience) return;
        if (event.isCanceled()
                || event.getEntityPlayer() == null
                || event.getEntityPlayer().world.isRemote) {
            return;
        }

        EntityPlayer player = event.getEntityPlayer();
        ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
        if(extendedPlayer == null) return;
        if(ForgeConfigHandler.server.chargeExpConfig.killXPSoulgazer && !Helpers.hasSoulgazerEquiped(player)) return;

        int totalXP = event.getOrb().getXpValue();
        totalXP *= (int) ForgeConfigHandler.server.chargeExpConfig.killXPModifier;
        if(totalXP <= 0) return;

        Set<BaseCreatureEntity> pets = new HashSet<>(player.getEntityWorld().getEntitiesWithinAABB(
                TameableCreatureEntity.class,
                player.getEntityBoundingBox().grow(ForgeConfigHandler.server.chargeExpConfig.killXPRange),
                creature -> creature.isTamed() && creature.getPlayerOwner() == extendedPlayer.getPlayer()));

        // Summons and Soulbounds are both minions
        pets.removeIf(creature -> creature.isTemporary || creature.isPetType("familiar"));
        if(!ForgeConfigHandler.server.chargeExpConfig.killXPSoulbound) pets.removeIf(BaseCreatureEntity::isBoundPet);
        if(pets.isEmpty()) return;

        int tameCount = pets.size();
        if(ForgeConfigHandler.server.chargeExpConfig.killXPCountPlayer) tameCount++;
        if(ForgeConfigHandler.server.chargeExpConfig.killXPReducePlayer) event.getOrb().xpValue -= event.getOrb().xpValue / tameCount;
        int splitXP = Math.max(1, totalXP / tameCount);
        pets.forEach(pet -> pet.addExperience(splitXP));
    }

    @SubscribeEvent
    public static void onBlockBreak(LivingDestroyBlockEvent event) {
        if (!ForgeConfigHandler.server.blockProtectionLivingEvent) return;
        if (event.getState() == null
                || event.isCanceled()
                || event.getEntityLiving() == null
                || event.getEntityLiving().getEntityWorld().isRemote) {
            return;
        }

        ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(event.getEntity().getEntityWorld());
        if (extendedWorld.isBossNearby(new Vec3d(event.getPos()))) {
            event.setCanceled(true);
            event.setResult(Event.Result.DENY);
            if (ForgeConfigHandler.debug.debugLoggerTick)
                LycanitesTweaks.LOGGER.log(Level.DEBUG, "Boss prevented block at {}, from being broke by {}", event.getPos(), event.getEntityLiving());
        }
    }

    @SubscribeEvent
    public static void onCreatureSpecialSpawn(LivingSpawnEvent.SpecialSpawn event) {
        if(event.isCanceled()) return;
        if(event.getWorld().isRemote) return;
        if(!(event.getEntityLiving() instanceof BaseCreatureEntity)) return;
        BaseCreatureEntity creature = (BaseCreatureEntity) event.getEntityLiving();

        // Random SpawnedAsBoss
        if(event.getWorld().rand.nextFloat() < ForgeConfigHandler.majorFeaturesConfig.creatureStatsConfig.spawnedAsBossNaturalSpawnChance && event.getSpawner() == null) {
            byte minLight = ForgeConfigHandler.majorFeaturesConfig.creatureStatsConfig.spawnedAsBossNaturalMinimumLight;
            if(minLight == -1 || minLight >= creature.testLightLevel()) {
                if (!creature.isBossAlways() && !creature.isTamed() && !creature.isMinion() && !CreatureManager.getInstance().creatureGroups.get("animal").hasEntity(creature)) {
                    creature.onFirstSpawn();
                    if (ForgeConfigHandler.majorFeaturesConfig.creatureStatsConfig.spawnedAsBossNaturalSpawnCrystal) {
                        if (EntityEncounterSummonCrystal.trySpawnEncounterCrystal(event.getWorld(), creature)) {
                            creature.setDead(); // Remove Original Entity, not preferred but catches all check spawn results
                            event.setCanceled(true); // Despite the docs, this does not cancel the spawn
                            return;
                        }
                    } else {
                        creature.spawnedAsBoss = true;
                        creature.damageLimit = BaseCreatureEntity.BOSS_DAMAGE_LIMIT;
                        creature.damageMax = BaseCreatureEntity.BOSS_DAMAGE_LIMIT;
                        creature.refreshAttributes();
                    }
                }
            }
        }

        // Player Mob Levels
        PlayerMobLevelsConfig.BonusCategory category = null;
        if(event.getSpawner() == null && PlayerMobLevelsConfig.getPmlBonusCategories().containsKey(PlayerMobLevelsConfig.BonusCategory.SpawnerNatural)){
            category = PlayerMobLevelsConfig.BonusCategory.SpawnerNatural;
        }
        else if(event.getSpawner() != null && PlayerMobLevelsConfig.getPmlBonusCategories().containsKey(PlayerMobLevelsConfig.BonusCategory.SpawnerTile)){
            category = PlayerMobLevelsConfig.BonusCategory.SpawnerTile;
        }
        if(category == null) return;

        EntityPlayer player = event.getWorld().getClosestPlayerToEntity(event.getEntityLiving(), 128);
        IPlayerMobLevelCapability pml = PlayerMobLevelCapability.getForPlayer(player);

        if(pml == null || !creature.firstSpawn) return;
        if(!PlayerMobLevelsConfig.getPmlBonusCategorySoulgazer().contains(category) || Helpers.hasSoulgazerEquiped(player)){
            creature.onFirstSpawn();
            creature.addLevel(pml.getTotalLevelsForCategory(category, creature));
            if(ForgeConfigHandler.debug.debugLoggerTick) LycanitesTweaks.LOGGER.log(Level.DEBUG, "{} Spawning: {}", category.name(), creature);
        }
    }

    @SubscribeEvent
    public static void onCreatureJoinWorld(EntityJoinWorldEvent event) {
        if(event.getWorld().isRemote) return;
        if(!(event.getEntity() instanceof BaseCreatureEntity)) return;
        BaseCreatureEntity creature = (BaseCreatureEntity) event.getEntity();

        if(ForgeConfigHandler.mixinPatchesConfig.fixVillagePathfinding) {
            for(String creatureName : ForgeConfigHandler.mixinPatchesConfig.villageNavAIMobs) {
                if(creature.creatureInfo.getName().equals(creatureName)) {
                    creature.tasks.addTask(creature.nextTravelGoalIndex++, new MoveVillageGoal(creature).setSpeed(1.0).setNocturnal(false));
                    break;
                }
            }
        }

        if(ForgeConfigHandler.mixinPatchesConfig.fixDoorBreakPathfinding) {
            for(String creatureName : ForgeConfigHandler.mixinPatchesConfig.doorBreakAIMobs) {
                if(creature.creatureInfo.getName().equals(creatureName)) {
                    creature.tasks.addTask(creature.nextDistractionGoalIndex++, new BreakDoorGoal(creature));
                    if(creature.getNavigator() instanceof CreaturePathNavigate) {
                        CreaturePathNavigate creaturePathNavigate = (CreaturePathNavigate)creature.getNavigator();
                        creaturePathNavigate.setCanOpenDoors(true);
                    }
                    break;
                }
            }
        }
    }

    private static boolean canGainDamageXP(BaseCreatureEntity creature) {
        return creature.isTamed()
                && !creature.isTemporary
                && !creature.isPetType("familiar");
    }

    private static boolean canGrantDamageXP(Entity entity) {
        if(entity == null) return false;

        if(entity instanceof IEntityOwnable && ((IEntityOwnable) entity).getOwner() != null) {
            return false;
        }

        if(entity instanceof BaseCreatureEntity && ((BaseCreatureEntity) entity).isMinion()) {
            return false;
        }

        return true;
    }
}