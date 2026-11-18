package lycanitestweaks.handlers.config;

import fermiumbooter.annotations.MixinConfig;
import lycanitestweaks.LycanitesTweaks;
import net.minecraftforge.common.config.Config;

@MixinConfig(name = LycanitesTweaks.MODID)
public class ClientFeaturesConfig {

    @Config.Comment("Allows the vanilla Boss Bars to show additional properties related to the exact Boss.")
    @Config.Name("Lycanites Boss Info Overlay")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(
            defaultValue = true,
            earlyMixin = "mixins.lycanitestweaks.vanilla.lycanitesbossinfo.json",
            lateMixin = "mixins.lycanitestweaks.client.lycanitesbossinfo.json"
    )
    public boolean lycanitesBossInfoOverlay = true;

    @Config.Comment({
            "Right side of the Boss Bar shows Icon and Health.",
            "Left side shows % of Hellfire Energy and current type of Hellfire Minions."
    })
    @Config.Name("Lycanites Boss Info Overlay - Rahovart")
    @Config.RequiresMcRestart
    public boolean rahovartInfoOverlay = true;

    @Config.Comment({
            "Rendering controls for the position of the Event Art, if empty the render is disabled:",
            "\tSize Scale",
            "\tX Position",
            "\tY Position",
            "\tZ Position"
    })
    @Config.Name("Lycanites Boss Info Overlay - Rahovart Event")
    public float[] rahovartEventOverlay = {
            -0.75F,
            0F,
            -80F,
            -50F
    };

    @Config.Comment({
            "Right side of the Boss Bar shows Icon and Health.",
            "Left side shows when the Astaroth Hellshield or Healing is active."
    })
    @Config.Name("Lycanites Boss Info Overlay - Asmodeus")
    @Config.RequiresMcRestart
    public boolean asmodeusInfoOverlay = true;

    @Config.Comment({
            "Rendering controls for the position of the Event Art, if empty the render is disabled:",
            "\tSize Scale",
            "\tX Position",
            "\tY Position",
            "\tZ Position"
    })
    @Config.Name("Lycanites Boss Info Overlay - Asmodeus Event")
    public float[] asmodeusEventOverlay = {
            0.75F,
            0F,
            70F,
            -50F
    };

    @Config.Comment({
            "Right side of the Boss Bar shows Icon and Health.",
            "Left side shows when the Consumption is winding up or active."
    })
    @Config.Name("Lycanites Boss Info Overlay - Amalgalich")
    @Config.RequiresMcRestart
    public boolean amalgalichInfoOverlay = true;

    @Config.Comment({
            "Rendering controls for the position of the Event Art, if empty the render is disabled:",
            "\tSize Scale",
            "\tX Position",
            "\tY Position",
            "\tZ Position"
    })
    @Config.Name("Lycanites Boss Info Overlay - Amalgalich Event")
    public float[] amalgalichEventOverlay = {
            0.25F,
            -111F,
            1F,
            -50F
    };

    @Config.Comment({
            "A basic display of Creature Icon and Health on the right side of the Boss Bar.",
            "\tSpawned As Boss - Red Color and an entity NBT property.",
            "\tCustom Name Boss - Green Color and must have a Custom Name set."
    })
    @Config.Name("Lycanites Boss Info Overlay - Other")
    @Config.RequiresMcRestart
    public boolean otherBossInfoOverlay = true;

    @Config.Comment({
            "Enable various visual easter eggs.",
            "Vanilla Lycanites",
            "\tExperiment 69R - When Attacking",
            "RLCraft",
            "\tKobold - When Damaged",
            "\tKrake - When Attacking"
    })
    @Config.Name("Lycanites Boss Info Overlay - Easter Eggs")
    public boolean easterEggBossInfoOverlay = true;

    @Config.Comment("Dependency for adding new/hiding Beastiary information. Required for server-side to know what Creature players have selected.")
    @Config.Name("0. Modify Beastiary Information")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.featureclientbeastiarylt.json")
    public boolean beastiaryGUILT = true;

    @Config.Comment("Adds a tab for Lycanites Altar renders and block counts")
    @Config.Name("0.a Lycanites Mobs Altar Beastiary Tab")
    public boolean beastiaryGUIAltars = true;

    @Config.Comment("Adds a tab to show Player Mob Levels information")
    @Config.Name("0.a LycanitesTweaks Player Mob Levels Beastiary Tab")
    public boolean beastiaryGUIPML = true;

    @Config.Comment({
            "Enables the ability for the Equipment Infuser and Station to display progress bars for additional items",
            "Ex. LycanitesTweaks Enchanted Soulkeys and Modified Summoning Staffs."
    })
    @Config.Name("1. Infuser and Station Display Additional Items")
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.client.infuserstationdisplaycustom.json")
    public boolean lycanitesTilesCustomItems = true;

    @Config.Comment({
            "PML Beastiary Render order is determined by the order of this list.",
            "\tcategoryName - Spelling must match 'Bonus Categories' entries else hidden",
            "This will be compared to the existence of 'Bonus Categories' entries in the PML config"
    })
    @Config.Name("Player Mob Levels Category Display Order")
    public String[] pmlBeastiaryOrder = {
            "AltarBossMain",
            "AltarBossMini",
            "DungeonBoss",
            "SpawnerNatural",
            "SpawnerTile",
            "SpawnerTrigger_",
            "EncounterEvent_",
            "SoulboundTame_",
            "SummonMinion",
            "SummonMinionInstant"
    };

    @Config.Comment("Adds stats on Summoning tab that shows Imperfect Summoning information")
    @Config.Name("LycanitesTweaks Imperfect Summoning Tab")
    public boolean beastiaryGUIImperfectSummon = true;

    @Config.Comment("Case sensitive blacklist for hiding any Altar by name. Does not affect gameplay.")
    @Config.Name("Altar Display Blacklist")
    public String[] altarInfoBeastiaryBlacklist = {

    };

    @Config.Comment({
            "Case sensitive blacklist for hiding any Creature by name. Does not affect gameplay.",
            "Used by Lycanites Tweaks to hide easter eggs."
    })
    @Config.Name("Creature Display Blacklist")
    public String[] creatureInfoBeastiaryBlacklist = {
            "sonofamalgalich"
    };

    @Config.Comment({
            "Case sensitive blacklist for hiding any Creature Subspecies by name. Does not affect gameplay.",
            "Used by Lycanites Tweaks to hide easter eggs.",
            "\tFormat: [creatureName: subspeciesIndex]"
    })
    @Config.Name("Creature Subspecies Display Blacklist")
    public String[] creatureSubspeciesInfoBeastiaryBlacklist = {
            "darkling: 111"
    };

    @Config.Comment("Case sensitive blacklist for hiding any Element by name. Does not affect gameplay.")
    @Config.Name("Element Display Blacklist")
    public String[] elementInfoBeastiaryBlacklist = {
            "nightmare",
            "viral"
    };

    @Config.Comment({
            "Dependency for modifying how Equipment is rendered in 1st person.",
            "This will additionally mirror the render in offhand instead of having it copy the mainhand."
    })
    @Config.Name("Modify Equipment Rendering")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.client.equipmentrendering.json")
    public boolean modifyEquipmentRendering = true;

    @Config.Comment("Size Scale of Equipment")
    @Config.Name("Modify Equipment Rendering - Size Scale")
    @Config.RangeDouble(min = 0)
    public float equipmentRenderScale = 1.0F;

    @Config.Comment("Case sensitive blacklist for hiding Equipment Parts by type")
    @Config.Name("Modify Equipment Rendering - Parts To Skip")
    public String[] equipmentRenderPartNamesToSkip = {
            "base_",
            "head_",
            "blade_",
            "pike_",
            "jewel_",
            "axe_",
            "pommel_"
    };

    @Config.Comment({
            "Have long tooltips be shortened and require Shift to be held down in order to show the full description.",
            "Equipment Mob Parts - Hides weapon stats and simplifies to xp, element, and slots.",
            "Equipment - Hides individual mob parts, block harvest, and melee effects."
    })
    @Config.Name("Shorten/Expand Tooltips With Shift")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.client.tooltiphideexpand.json")
    public boolean shortenTooltips = true;

    @Config.Comment({
            "Replace the denial of sleep message mentioning monsters nearby with one that directly mentions Insomnia.",
            "Langkey is: \"tile.bed.lycanites.insomnia\""
    })
    @Config.Name("Unique Insomnia Message")
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.client.insomniamessage.json")
    public boolean insomniaMessage = true;

    @Config.Comment("Reduces the particles that Lycanites Fire Blocks create to match Vanilla Fire.")
    @Config.Name("Fire Block Particle Reducer")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.lycanitestweaks.client.fireparticlereducer.json")
    public boolean fireBlockParticleReduce = true;
}
