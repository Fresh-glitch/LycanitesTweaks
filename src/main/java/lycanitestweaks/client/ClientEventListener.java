package lycanitestweaks.client;

import com.lycanitesmobs.core.item.GenericFoodItem;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.special.ItemSoulgazer;
import com.lycanitesmobs.core.item.special.ItemSoulkey;
import lycanitestweaks.capability.lycanitestweaksplayer.ILycanitesTweaksPlayerCapability;
import lycanitestweaks.capability.lycanitestweaksplayer.LycanitesTweaksPlayerCapability;
import lycanitestweaks.capability.playermoblevel.IPlayerMobLevelCapability;
import lycanitestweaks.capability.playermoblevel.PlayerMobLevelCapability;
import lycanitestweaks.client.keybinds.KeyHandler;
import lycanitestweaks.client.renderer.RenderContext;
import lycanitestweaks.compat.ModLoadedUtil;
import lycanitestweaks.entity.item.EntityBossSummonCrystal;
import lycanitestweaks.entity.item.EntityEncounterSummonCrystal;
import lycanitestweaks.handlers.ForgeConfigHandler;
import lycanitestweaks.handlers.config.major.PlayerMobLevelsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEventListener {

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if(!event.getWorld().isRemote) return;
        RenderContext.currentRenderEntity = null;
        RenderContext.currentRenderStack = null;
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();

        if(item instanceof ItemSoulgazer) {
            if(renderFullTooltip(!ForgeConfigHandler.clientFeaturesMixinConfig.shortenTooltips)) {
                IPlayerMobLevelCapability pml = PlayerMobLevelCapability.getForPlayer(event.getEntityPlayer());
                if(pml != null) {
                    if(!PlayerMobLevelsConfig.getPmlBonusCategorySoulgazer().isEmpty()) {
                        addWordWrappedTooltip(event, "item.soulgazer.description.pmlsoulgazer", ItemBase.DESCRIPTION_WIDTH);
                    }
                }
            }

            if(renderFullTooltip(!ForgeConfigHandler.clientFeaturesMixinConfig.shortenTooltips)) {
                if(ForgeConfigHandler.server.chargeExpConfig.vanillaKillExperience && ForgeConfigHandler.server.chargeExpConfig.killXPSoulgazer) {
                    if(ForgeConfigHandler.server.chargeExpConfig.killXPSoulbound) {
                        addWordWrappedTooltip(event, "item.soulgazer.description.killxpshare.soulbound", ItemBase.DESCRIPTION_WIDTH);
                    }
                    else {
                        addWordWrappedTooltip(event, "item.soulgazer.description.killxpshare", ItemBase.DESCRIPTION_WIDTH);
                    }
                }
            }

            if(renderFullTooltip(!ForgeConfigHandler.clientFeaturesMixinConfig.shortenTooltips)) {
                if(ForgeConfigHandler.integrationConfig.soulgazerBaubleBonusRecharge != 0) {
                    addWordWrappedTooltip(event, "item.soulgazer.description.baublebonus", ItemBase.DESCRIPTION_WIDTH);
                }
            }

            if(ForgeConfigHandler.integrationConfig.soulgazerBauble && ModLoadedUtil.baubles.isLoaded()) {
                ILycanitesTweaksPlayerCapability ltp = LycanitesTweaksPlayerCapability.getForPlayer(event.getEntityPlayer());
                if(ltp != null) {
                    int autoID = ltp.getSoulgazerAutoToggle();
                    int manualID = ltp.getSoulgazerManualToggle() ? 1 : 2;
                    event.getToolTip().add(
                            I18n.format("item.soulgazer.description.keybind.auto." + autoID)
                                    + I18n.format("item.lycanitestweaks.tooltip.keybind", KeyHandler.TOGGLE_SOULGAZER_AUTO.getDisplayName())
                    );
                    event.getToolTip().add(
                            I18n.format("item.soulgazer.description.keybind.manual." + manualID)
                                    + I18n.format("item.lycanitestweaks.tooltip.keybind", KeyHandler.TOGGLE_SOULGAZER_MANUAL.getDisplayName())
                    );
                }
            }
        }

        if(ForgeConfigHandler.majorFeaturesConfig.creatureInteractConfig.babyAgeGapple){
            if(item instanceof ItemAppleGold && event.getItemStack().getMetadata() > 0){
                event.getToolTip().add(I18n.format("item.appleGold.description.baby"));
            }
        }

        if(ForgeConfigHandler.majorFeaturesConfig.creatureInteractConfig.sizeChangeFoods){
            if(item instanceof GenericFoodItem){
                if(((GenericFoodItem) item).itemName.equals("battle_burrito")){
                    event.getToolTip().add(I18n.format("item.battle_burrito.description.sizechange"));
                }
                else if (((GenericFoodItem) item).itemName.equals("explorers_risotto")) {
                    event.getToolTip().add(I18n.format("item.explorers_risotto.description.sizechange"));
                }
            }
        }

        if(ForgeConfigHandler.majorFeaturesConfig.creatureInteractConfig.soulkeysSetVariant){
            if(item instanceof ItemSoulkey){
                event.getToolTip().add(I18n.format("item.soulkey.description.setvariant"));
            }
        }
    }

    // Full tooltip for JEI to parse
    public static boolean renderFullTooltip(boolean override) {
        return Minecraft.getMinecraft().player == null || GuiScreen.isShiftKeyDown() || override;
    }

    public static void addWordWrappedTooltip(ItemTooltipEvent event, String langKey, int width){
        event.getToolTip().addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(I18n.format(langKey), width));
    }

    // Condition for Melee Raytrace and client invis
    public static boolean canBreakCrystal(EntityBossSummonCrystal crystal) {
        if(crystal instanceof EntityEncounterSummonCrystal) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            return !player.isSneaking() || player.getHeldItemMainhand().isStackable();
        }
        return true;
    }
}
