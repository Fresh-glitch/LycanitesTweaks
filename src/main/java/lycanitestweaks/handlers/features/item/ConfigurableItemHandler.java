package lycanitestweaks.handlers.features.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.Variant;
import lycanitestweaks.LycanitesTweaks;
import lycanitestweaks.capability.storedcreatureinfo.ItemStoredCreatureInfo;
import lycanitestweaks.handlers.ForgeConfigHandler;
import lycanitestweaks.handlers.ForgeConfigProvider;
import lycanitestweaks.handlers.features.entity.AttributesHandler;
import lycanitestweaks.util.Triple;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Manages the stats of an EntityCreature. This applies difficulty multipliers, subspecies, levels, etc also. **/
public abstract class ConfigurableItemHandler {

	// UUIDs used for modifiers for specified equipment slot
	// Different from Vanilla Item
	public static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("0fed920d-ca20-4f62-825a-55634fc374ea");
	public static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("01c7af86-7c74-404f-8ec0-deb64120394b");

	public static final UUID MAX_HEALTH_MODIFIER = UUID.fromString("d11b4e33-9c69-416c-979f-54e16e7cd65b");
	public static final UUID ARMOR_MODIFIER = UUID.fromString("e5237402-bcca-4ed6-9789-23c0a30be076");
	public static final UUID GROUND_SPEED_MODIFIER = UUID.fromString("d1d87003-0344-40ad-8d35-d3e3567ae457");

	public static final UUID DEFENSE_MODIFIER = UUID.fromString("a1ac4755-0218-44eb-be84-b7ae77eefc19");
	public static final UUID PIERCE_MODIFIER = UUID.fromString("203b3bd0-7d63-40be-929d-76bead42e929");

	// Bauble UUIDs are random and stored in item's nbt

	private static final Map<STAT, Double> STAT_OVERRIDES = new HashMap<>();
	private static final Map<ItemStoredCreatureInfo, EquipmentSlot> ITEM_SLOT_CONFIG = new HashMap<>();

	public enum STAT {
		health,
		defense,
		armor,
		speed,
		damage,
		attackspeed,
		rangedspeed, // item use speed
		effect,
		amplifier,
		pierce
	}

	public static @Nullable EquipmentSlot getItemSlot(ItemStack itemStack) {
		ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(itemStack);
		if(storedCreatureInfo == null) {
			return null;
		}

		if(ITEM_SLOT_CONFIG.get(storedCreatureInfo) != null) {
			return ITEM_SLOT_CONFIG.get(storedCreatureInfo);
		}

		return ITEM_SLOT_CONFIG.computeIfAbsent(storedCreatureInfo, (item -> new EquipmentSlot(itemStack)));
	}

	public static void getOtherSlotAttributeModifiers(@Nonnull ItemStats stats, String modifierName, Multimap<String, AttributeModifier> multimap) {
		if(stats.damage != 0) multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ConfigurableItemHandler.ATTACK_DAMAGE_MODIFIER, modifierName, stats.damage, 0));
		if(stats.attackSpeed != 0) multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ConfigurableItemHandler.ATTACK_SPEED_MODIFIER, modifierName, stats.attackSpeed, 0));

		getMainHandAttributeModifiers(stats, modifierName, multimap);
	}

	public static void getMainHandAttributeModifiers(@Nonnull ItemStats stats, String modifierName, Multimap<String, AttributeModifier> multimap) {
		if(stats.health != 0) multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(ConfigurableItemHandler.MAX_HEALTH_MODIFIER, modifierName, stats.health, 0));
		if(stats.armor != 0) multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ConfigurableItemHandler.ARMOR_MODIFIER, modifierName, stats.armor, 0));
		if(stats.speed != 0) multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(ConfigurableItemHandler.GROUND_SPEED_MODIFIER, modifierName, stats.speed, 0));

		if(stats.defense != 0) multimap.put(BaseCreatureEntity.DEFENSE.getName(), new AttributeModifier(ConfigurableItemHandler.DEFENSE_MODIFIER, modifierName, stats.defense, 0));

		if(stats.pierce != 0) multimap.put(AttributesHandler.PIERCE.getName(), new AttributeModifier(ConfigurableItemHandler.PIERCE_MODIFIER, modifierName, stats.pierce, 0));
	}

	public static Multimap<String, AttributeModifier> getBaubleAttributeModifiers(ItemStoredCreatureInfo storedCreatureInfo, String modifierName) {
		ConfigurableItemHandler.ItemStats stats = storedCreatureInfo.getOrCreateStats();
		Multimap<String, AttributeModifier> multimap = HashMultimap.create();
		if(stats.damage != 0) multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(storedCreatureInfo.getOrCreateBaubleModifierUUID(SharedMonsterAttributes.ATTACK_DAMAGE.getName()), modifierName, stats.damage, 0));
		if(stats.attackSpeed != 0) multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(storedCreatureInfo.getOrCreateBaubleModifierUUID(SharedMonsterAttributes.ATTACK_SPEED.getName()), modifierName, stats.attackSpeed, 0));

		if(stats.health != 0) multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(storedCreatureInfo.getOrCreateBaubleModifierUUID(SharedMonsterAttributes.MAX_HEALTH.getName()), modifierName, stats.health, 0));
		if(stats.armor != 0) multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(storedCreatureInfo.getOrCreateBaubleModifierUUID(SharedMonsterAttributes.ARMOR.getName()), modifierName, stats.armor, 0));
		if(stats.speed != 0) multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(storedCreatureInfo.getOrCreateBaubleModifierUUID(SharedMonsterAttributes.MOVEMENT_SPEED.getName()), modifierName, stats.speed, 0));

		if(stats.defense != 0) multimap.put(BaseCreatureEntity.DEFENSE.getName(), new AttributeModifier(storedCreatureInfo.getOrCreateBaubleModifierUUID(BaseCreatureEntity.DEFENSE.getName()), modifierName, stats.defense, 0));

		if(stats.pierce != 0) multimap.put(AttributesHandler.PIERCE.getName(), new AttributeModifier(storedCreatureInfo.getOrCreateBaubleModifierUUID(AttributesHandler.PIERCE.getName()), modifierName, stats.pierce, 0));

		return multimap;
	}

	public static void onConfigChange() {
		STAT_OVERRIDES.clear();
		ITEM_SLOT_CONFIG.clear();
	}

	private static String getConfigLine(ResourceLocation itemID, String[] configLines) {
		for(String line : configLines) {
			String[] config = line.split(",");
			if(itemID.equals(new ResourceLocation(config[0].trim()))) {
				return line.replaceFirst("^.*?,", "").toLowerCase();
			}
		}
		return "";
	}

	private static double parseStatConfig(ResourceLocation itemID, STAT stat) {
		String[] entries = getConfigLine(itemID, ForgeConfigHandler.server.customStaffConfig.customItemStats).split(",");
		for(String entry : entries) {
			String[] split = entry.split(":");
			if(split.length > 1) {
				if(stat.toString().equals(split[0].trim())) {
					try {
						return Double.parseDouble(split[1].trim());
					}
					catch (NumberFormatException e) {
						LycanitesTweaks.LOGGER.log(Level.WARN, "Failed to parse {}'s {} in {}", itemID, stat, entry);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return 0;
	}

	private static double parseStatOverrideConfig(STAT stat) {
		for(String entry : ForgeConfigHandler.server.customStaffConfig.customItemLevelMultipliers) {
			String[] split = entry.split("=");
			if(split.length > 1) {
				if(stat.toString().equals(split[0].trim().toLowerCase())) {
					try {
						return Double.parseDouble(split[1].trim());
					}
					catch (NumberFormatException e) {
						LycanitesTweaks.LOGGER.log(Level.WARN, "Failed to parse item level multiplier {} in {}", stat, entry);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return -1D;
	}

	private static EntityEquipmentSlot parseEquipmentSlotConfig(ResourceLocation itemID) {
		String[] entries = getConfigLine(itemID, ForgeConfigHandler.server.customStaffConfig.customItemSlots).split(",");
		for(String entry : entries) {
			String[] split = entry.split(":");
			if(split.length > 1) {
				if(split[0].trim().equals("equipmentslot")) {
					try {
						return EntityEquipmentSlot.fromString(split[1].trim());
					}
					catch (NumberFormatException e) {
						LycanitesTweaks.LOGGER.log(Level.WARN, "Failed to parse {}'s EquipmentSlot in {}", itemID, entry);
						break;
					}
				}
			}
		}
		return null;
	}

	private static Triple<Boolean, Integer, Integer> parseBaubleTypeConfig(ResourceLocation itemID) {
		boolean hasAttributes = true;
		int baubleSlot = Integer.MAX_VALUE;
		int baubleLimit = Integer.MAX_VALUE;
		String[] entries = getConfigLine(itemID, ForgeConfigHandler.server.customStaffConfig.customItemSlots).split(",");
		for(String entry : entries) {
			String[] split = entry.split(":");
			if(split.length > 1) {
				switch (split[0].trim()) {
					case "baubletype":
						hasAttributes = !split[1].trim().startsWith("-");
						try {
							baubleSlot = Math.abs(Integer.parseInt(split[1].trim()));
						}
						catch (NumberFormatException e) {
							LycanitesTweaks.LOGGER.log(Level.WARN, "Failed to parse {}'s BaubleType in {}", itemID, entry);
                        }
                        break;
					case "baublelimit":
						try {
							baubleLimit = Integer.parseInt(split[1].trim());
						}
						catch (NumberFormatException e) {
							LycanitesTweaks.LOGGER.log(Level.WARN, "Failed to parse {}'s BaubleLimit in {}", itemID, entry);
						}
						break;
				}
			}
		}
		return new Triple<>(hasAttributes, baubleSlot, baubleLimit);
	}

	public static class EquipmentSlot {
		public final ItemStack itemStack;
		public final EntityEquipmentSlot equipmentSlot;
		public final boolean baubleAttributes;
		public final int baubleTypeOrdinal;
		public final int baubleCountLimit;

		public EquipmentSlot(ItemStack itemStack) {
			this.itemStack = itemStack;
			this.equipmentSlot = parseEquipmentSlotConfig(itemStack.getItem().getRegistryName());

			Triple<Boolean, Integer, Integer> baubleSlot = parseBaubleTypeConfig(itemStack.getItem().getRegistryName());
			this.baubleAttributes = baubleSlot.getLeft();
			this.baubleTypeOrdinal = baubleSlot.getMiddle();
			this.baubleCountLimit = baubleSlot.getRight();
		}
	}

	public static class ItemStats {

		public final ItemStack itemStack;
		public final ItemStoredCreatureInfo storedCreatureInfo;

		public final double health;
		public final double armor;
		public final double defense;
		public final double damage;
		public final double pierce;
		public final double attackSpeed;
		public final int ticksPerUse;
		public final double effectDuration;
		public final double effectAmplifier;
		public final double speed;

		public final int baseTicksPerUse;

		public ItemStats(ItemStack itemStack) {
			this.itemStack = itemStack;
			this.storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(itemStack);

			this.health = calculateStat(STAT.health);
			this.armor = calculateStat(STAT.armor);
			this.defense = calculateStat(STAT.defense);
			this.damage = calculateStat(STAT.damage);
			this.pierce = calculateStat(STAT.pierce);
			this.attackSpeed = calculateStat(STAT.attackspeed);
			this.ticksPerUse = (int) Math.max(1D, Math.round(1D / calculateStat(STAT.rangedspeed) * 20D));
			this.effectDuration = calculateStat(STAT.effect);
			this.effectAmplifier = calculateStat(STAT.amplifier);
			this.speed = calculateStat(STAT.speed);

			this.baseTicksPerUse = (int) Math.max(1D, Math.round((1D / getBaseStat(STAT.rangedspeed) * 20D)));
		}

		private double getBaseStat(STAT stat) {
			return parseStatConfig(this.itemStack.getItem().getRegistryName(), stat);
		}

		public double calculateStat(STAT stat) {
			double value = getBaseStat(stat);
			if(value == 0) return value;

			if(stat == STAT.speed) {
				value /= 100D;
			}
			double levelMultiplier = this.getLevelMultiplier(stat);
			double ratio = ForgeConfigHandler.server.customStaffConfig.customItemStatsCap
					? ForgeConfigProvider.getStatRatioCap(stat.toString())
					: -1D;

			if(ratio >= 0) {
				value = Math.min(value * levelMultiplier, value * ratio);
			}
			else {
				value *= levelMultiplier;
			}
			value *= this.getVariantMultiplier(stat);

			return value;
		}

		/**
		 * Returns a variant stat multiplier for the provided stat name and the variant that the entity is.
		 * @param stat The name of the stat to get the multiplier for.
		 * @return The stat multiplier.
		 */
		public double getVariantMultiplier(STAT stat) {
			if(this.storedCreatureInfo == null) return 1D;

			String statKey = this.storedCreatureInfo.getEntityVariantRarity() + "-" + stat.toString().toUpperCase();
			return Variant.STAT_MULTIPLIERS.getOrDefault(statKey, 1D);
		}


		/**
		 * Returns a level stat multiplier for the provided stat name and the creature's current level.
		 * @param stat The name of the stat to get the multiplier for.
		 * @return The stat multiplier.
		 */
		public double getLevelMultiplier(STAT stat) {
			double statLevel;
			double levelMultiplier;
			if(this.storedCreatureInfo == null)
				statLevel = 0;
			else
				statLevel = Math.max(0, this.storedCreatureInfo.getLevel());

			levelMultiplier = STAT_OVERRIDES.computeIfAbsent(stat, value -> parseStatOverrideConfig(stat));

			if(levelMultiplier == -1D)
				levelMultiplier = CreatureManager.getInstance().getLevelMultiplier(stat.toString());

			return 1 + (statLevel * levelMultiplier);
		}
	}
}
