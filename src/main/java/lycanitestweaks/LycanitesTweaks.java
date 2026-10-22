package lycanitestweaks;

import com.lycanitesmobs.core.dungeon.DungeonManager;
import com.lycanitesmobs.core.info.AltarInfo;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.lycanitesmobs.core.mobevent.effects.StructureBuilder;
import com.lycanitesmobs.core.spawner.SpawnerManager;
import lycanitestweaks.capability.entitystorecreature.EntityStoreCreatureCapabilityHandler;
import lycanitestweaks.capability.lycanitestweaksplayer.LycanitesTweaksPlayerCapabilityHandler;
import lycanitestweaks.capability.playermoblevel.PlayerMobLevelCapabilityHandler;
import lycanitestweaks.capability.storedcreatureinfo.StoredCreatureInfoHandler;
import lycanitestweaks.capability.toggleableitem.ToggleableItemHandler;
import lycanitestweaks.command.LycanitesTweaksCommand;
import lycanitestweaks.compat.BaublesHandler;
import lycanitestweaks.compat.ModLoadedUtil;
import lycanitestweaks.compat.PotionCoreHandler;
import lycanitestweaks.compat.RLCombatHandler;
import lycanitestweaks.compat.SMEHandler;
import lycanitestweaks.handlers.ForgeConfigHandler;
import lycanitestweaks.handlers.ForgeConfigProvider;
import lycanitestweaks.handlers.features.effect.CripplingEffectsHandler;
import lycanitestweaks.handlers.features.effect.CuringEffectsHandler;
import lycanitestweaks.handlers.features.entity.AttributesHandler;
import lycanitestweaks.handlers.features.entity.ChargeEnchantmentsHandler;
import lycanitestweaks.handlers.features.entity.EntityLivingHandler;
import lycanitestweaks.handlers.features.entity.EntityLootHandler;
import lycanitestweaks.handlers.features.entity.GenericBestiaryHandler;
import lycanitestweaks.handlers.features.entity.SpawnerPlayerLeakHandler;
import lycanitestweaks.handlers.features.item.ItemHandler;
import lycanitestweaks.handlers.features.item.ItemSoulgazerTweaksHandler;
import lycanitestweaks.info.altar.AltarInfoBeastiary;
import lycanitestweaks.info.altar.AltarInfoChargedCreeper;
import lycanitestweaks.info.altar.AltarInfoWitheringHeights;
import lycanitestweaks.info.altar.AltarInfoZombieHorse;
import lycanitestweaks.proxy.CommonProxy;
import lycanitestweaks.worldgen.mobevents.WitheringHeightsStructureBuilder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = LycanitesTweaks.MODID,
        version = LycanitesTweaks.VERSION,
        name = LycanitesTweaks.NAME,
        acceptableRemoteVersions = LycanitesTweaks.ACCEPTABLE_REMOTE,
        dependencies =
                "required-after:fermiumbooter@[1.5.2,);" +
                "required-after:lycanitesmobs;"
)
public class LycanitesTweaks {
    public static final String MODID = "lycanitestweaks";
    public static final String VERSION = "1.0.16.8";
    private static final String NEXT_MAIN_VERSION = "1.0.17";
    public static final String NAME = "LycanitesTweaks";
    public static final Logger LOGGER = LogManager.getLogger();
    public static boolean completedLoading = false;

    public static final String ACCEPTABLE_REMOTE = "[" + VERSION + "," + NEXT_MAIN_VERSION + ")";

    @SidedProxy(clientSide = "lycanitestweaks.proxy.ClientProxy", serverSide = "lycanitestweaks.proxy.CommonProxy")
    public static CommonProxy PROXY;
	
	@Instance(MODID)
	public static LycanitesTweaks instance;

    public static final ModInfo MOD_INFO = new ModInfo(instance, NAME, 1000);
	
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LycanitesTweaks.PROXY.preInit();

        LycanitesTweaksPlayerCapabilityHandler.registerCapability();
        MinecraftForge.EVENT_BUS.register(LycanitesTweaksPlayerCapabilityHandler.AttachCapabilityHandler.class);
        MinecraftForge.EVENT_BUS.register(LycanitesTweaksPlayerCapabilityHandler.class);
        MinecraftForge.EVENT_BUS.register(ItemSoulgazerTweaksHandler.class);

        StoredCreatureInfoHandler.registerCapability();
        MinecraftForge.EVENT_BUS.register(StoredCreatureInfoHandler.AttachCapabilityHandler.class);
        MinecraftForge.EVENT_BUS.register(StoredCreatureInfoHandler.class);

        ToggleableItemHandler.registerCapability();
        MinecraftForge.EVENT_BUS.register(ToggleableItemHandler.AttachCapabilityHandler.class);
        MinecraftForge.EVENT_BUS.register(ToggleableItemHandler.class);

        if(ForgeConfigHandler.majorFeaturesConfig.escConfig.entityStoreCreatureCapability){
            EntityStoreCreatureCapabilityHandler.registerCapability();
            MinecraftForge.EVENT_BUS.register(EntityStoreCreatureCapabilityHandler.AttachCapabilityHandler.class);
            MinecraftForge.EVENT_BUS.register(EntityStoreCreatureCapabilityHandler.class);
        }
        if(ForgeConfigHandler.majorFeaturesConfig.pmlConfig.playerMobLevelCapability){
            PlayerMobLevelCapabilityHandler.registerCapability();
            MinecraftForge.EVENT_BUS.register(PlayerMobLevelCapabilityHandler.AttachCapabilityHandler.class);
            MinecraftForge.EVENT_BUS.register(PlayerMobLevelCapabilityHandler.class);
        }

        if(ForgeConfigHandler.majorFeaturesConfig.itemTweaksConfig.summonStaffLevelMap){
            MinecraftForge.EVENT_BUS.register(ItemHandler.class);
        }

        MinecraftForge.EVENT_BUS.register(EntityLootHandler.class);
        MinecraftForge.EVENT_BUS.register(EntityLivingHandler.class);
        MinecraftForge.EVENT_BUS.register(GenericBestiaryHandler.class);
        MinecraftForge.EVENT_BUS.register(CuringEffectsHandler.class);
        if(ForgeConfigHandler.mixinPatchesConfig.fixSpawnerPlayerLeak)
            MinecraftForge.EVENT_BUS.register(SpawnerPlayerLeakHandler.class);

        if(ForgeConfigHandler.server.effectsConfig.registerConsumed || ForgeConfigHandler.server.effectsConfig.registerVoided)
            MinecraftForge.EVENT_BUS.register(CripplingEffectsHandler.class);

        if(ForgeConfigHandler.majorFeaturesConfig.creatureStatsConfig.applyDamageAttributeToRanged && ForgeConfigHandler.integrationConfig.potionCoreSwapTrueShot && ModLoadedUtil.potionCore.isLoaded())
            MinecraftForge.EVENT_BUS.register(PotionCoreHandler.class);
        if(ForgeConfigHandler.integrationConfig.craftedEquipmentRLCombatSweep && ModLoadedUtil.versionInRange(ModLoadedUtil.rlCombat, "[2.2.0,)"))
            MinecraftForge.EVENT_BUS.register(RLCombatHandler.class);

        if(ForgeConfigHandler.server.altarsConfig.beastiaryAltar) AltarInfo.addAltar(new AltarInfoBeastiary(LycanitesTweaks.MODID + ":beastiaryaltar"));
        if(ForgeConfigHandler.server.altarsConfig.vanillaEntityAltars) {
            AltarInfo.addAltar(new AltarInfoChargedCreeper(LycanitesTweaks.MODID + ":chargedcreeperaltar"));
            AltarInfo.addAltar(new AltarInfoZombieHorse(LycanitesTweaks.MODID + ":zombiehorsealtar"));
        }
        if(ForgeConfigHandler.server.altarsConfig.witheringHeightsAltar){
            AltarInfo.addAltar(new AltarInfoWitheringHeights(LycanitesTweaks.MODID + ":witheringheights"));
            StructureBuilder.addStructureBuilder(new WitheringHeightsStructureBuilder());
        }

        if(ForgeConfigHandler.majorFeaturesConfig.creatureInteractConfig.applyBowEnchantmentsToCharges) {
            MinecraftForge.EVENT_BUS.register(ChargeEnchantmentsHandler.class);
            if (ModLoadedUtil.versionInRange(ModLoadedUtil.sme, "[1.0.0,)") && ForgeConfigHandler.integrationConfig.smeBowEnchantmentsToCharges) {
                MinecraftForge.EVENT_BUS.register(SMEHandler.class);
            }
        }

        if(ForgeConfigHandler.minorFeaturesConfig.lycanitesAttributesForAll) {
            MinecraftForge.EVENT_BUS.register(AttributesHandler.class);
        }

        if(ModLoadedUtil.baubles.isLoaded()) {
            MinecraftForge.EVENT_BUS.register(BaublesHandler.class);
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        LycanitesTweaks.PROXY.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LycanitesTweaks.PROXY.postInit();
        // Reload these for any custom assets added
        DungeonManager.getInstance().reload();
        MobEventManager.getInstance().reload(); // Fix null Event Altars
        SpawnerManager.getInstance().reload(); // Fix null Mob Spawn
//        if(ForgeConfigHandler.mixinPatchesConfig.fixAssetManagerReloading) {
//            // If 2x drops is fixed
//            CreatureManager.getInstance().reload();
//            EquipmentPartManager.getInstance().reload();
//        }
        ForgeConfigProvider.init();
        completedLoading = true;
    }

    @Mod.EventHandler
    public void serverInit(FMLServerStartingEvent event) {
        event.registerServerCommand(new LycanitesTweaksCommand());
    }
}