package lycanitestweaks;

import fermiumbooter.FermiumRegistryAPI;
import lycanitestweaks.compat.ModLoadedUtil;
import lycanitestweaks.handlers.ForgeConfigHandler;
import lycanitestweaks.handlers.ForgeConfigProvider;
import lycanitestweaks.handlers.config.EarlyConfigReader;
import lycanitestweaks.handlers.config.IntegrationConfig;
import lycanitestweaks.handlers.config.PatchConfig;
import lycanitestweaks.handlers.config.major.CreatureStatsConfig;
import lycanitestweaks.handlers.config.major.ItemTweaksConfig;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class LycanitesTweaksPlugin implements IFMLLoadingPlugin {

	public LycanitesTweaksPlugin() {
		// Indev, mixin adds ForceLoadAsMod dependencies on the classpath to the reparseable coremods, which the classpath mod scan skips
		if(FMLLaunchHandler.isDeobfuscatedEnvironment()) {
			Set<String> classpath = Launch.classLoader.getSources().stream().map(URL::getPath).map(path -> new File(path).getName()).collect(Collectors.toSet());
			CoreModManager.getReparseableCoremods().removeIf(classpath::contains);
		}

		//		FermiumRegistryAPI.enqueueMixin(true, "mixins.lycanitestweaks.client.bigchildheadall.json"); // funny but clearly broken

		// Always
		FermiumRegistryAPI.enqueueMixin(false, "mixins.lycanitestweaks.core.vanilla.json");
		FermiumRegistryAPI.enqueueMixin(true, "mixins.lycanitestweaks.client.rainbow.json");
		FermiumRegistryAPI.enqueueMixin(true, "mixins.lycanitestweaks.core.itemswithcreatureinfo.json");
		FermiumRegistryAPI.enqueueMixin(true, "mixins.lycanitestweaks.core.loaddefaultoverride.json");
		FermiumRegistryAPI.enqueueMixin(true, "mixins.lycanitestweaks.feature.debug.json");
		FermiumRegistryAPI.enqueueMixin(true, "mixins.lycanitestweaks.battletowers.json", () -> FermiumRegistryAPI.isModPresent(ModLoadedUtil.BATTLETOWERS_MODID));
		FermiumRegistryAPI.enqueueMixin(true, "mixins.lycanitestweaks.client.reachfixcrystal.json", () -> FermiumRegistryAPI.isModPresent(ModLoadedUtil.REACHFIX_MODID));

		// Conditional based on config
		FermiumRegistryAPI.enqueueMixin(true, "mixins.lycanitestweaks.feature.spawnedasbosskobold.json", () -> EarlyConfigReader.getDouble(CreatureStatsConfig.SPAWNED_AS_BOSS_NATURAL_CHANCE, ForgeConfigHandler.majorFeaturesConfig.creatureStatsConfig.spawnedAsBossNaturalSpawnChance) > 0);
		FermiumRegistryAPI.enqueueMixin(true, "mixins.lycanitestweaks.feature.spawnedasbossrngname.json", () -> EarlyConfigReader.getDouble(CreatureStatsConfig.SPAWNED_AS_BOSS_NAMES_COUNT, ForgeConfigHandler.majorFeaturesConfig.creatureStatsConfig.spawnedAsBossNaturalSpawnNames) > 0);

		if(EarlyConfigReader.getBoolean(IntegrationConfig.REMOVE_DUPLICATE_MIXINS, ForgeConfigHandler.integrationConfig.removeDuplicateMixins)){
			if(EarlyConfigReader.getBoolean(PatchConfig.FIX_HELLSHIELD_MINIONS, ForgeConfigHandler.mixinPatchesConfig.fixAsmodeusMinions))
				FermiumRegistryAPI.removeMixin("mixins.fermiummixins.late.lycanitesmobs.minion.json");
			if(EarlyConfigReader.getBoolean(PatchConfig.FIX_CUSTOM_ITEM_DESPAWN, ForgeConfigHandler.mixinPatchesConfig.customItemEntityRegistryPatch))
				FermiumRegistryAPI.removeMixin("mixins.fermiummixins.late.lycanitesmobs.customitem.json");
			if(EarlyConfigReader.getBoolean(PatchConfig.FIX_DESPAWN_SOULBOUNDS, ForgeConfigHandler.mixinPatchesConfig.fixInvalidSoulbounds))
				FermiumRegistryAPI.removeMixin("mixins.fermiummixins.late.lycanitesmobs.invalidsoulbound.json");
			if(EarlyConfigReader.getBoolean(ItemTweaksConfig.LYCANITES_EQUIPMENT_ENCHANTMENTS, ForgeConfigHandler.majorFeaturesConfig.itemTweaksConfig.equipmentEnchantments))
				FermiumRegistryAPI.removeMixin("mixins.rlmixins.late.lycanitesmobs.equipmentenchantments.json");
		}
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[0];
	}
	
	@Override
	public String getModContainerClass()
	{
		return null;
	}
	
	@Override
	public String getSetupClass()
	{
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) {
		ForgeConfigProvider.pluginInit();
		if (Boolean.FALSE.equals(data.get("runtimeDeobfuscationEnabled"))) {
			MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
			CoreModManager.getReparseableCoremods().removeIf(s -> StringUtils.containsIgnoreCase(s, "fermiumbooter"));
		}
	}
	
	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}