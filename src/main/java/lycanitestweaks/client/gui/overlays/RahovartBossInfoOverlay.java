package lycanitestweaks.client.gui.overlays;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityBehemoth;
import com.lycanitesmobs.core.entity.creature.EntityBelph;
import com.lycanitesmobs.core.entity.creature.EntityRahovart;
import com.lycanitesmobs.core.entity.creature.EntityWraith;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireOrb;
import lycanitestweaks.handlers.ForgeConfigHandler;
import lycanitestweaks.network.PacketHandler;
import lycanitestweaks.network.PacketLycanitesBossInfo;
import lycanitestweaks.util.IBossInfo_LycanitesBossMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RahovartBossInfoOverlay extends LycanitesBossInfoOverlay {

    private static EntityHellfireOrb dummyOrb = null;
    private static EntityBelph dummyBelph = null;
    private static EntityBehemoth dummyBehemoth = null;
    private static EntityWraith dummyWraith = null;

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if(!event.getWorld().isRemote) return;
        if(event.getEntity() instanceof BaseCreatureEntity) {
            BaseCreatureEntity creature = (BaseCreatureEntity) event.getEntity();
            if(creature.creatureInfo.getName().equals("rahovart"))
                PacketHandler.instance.sendToServer(new PacketLycanitesBossInfo(creature));
        }
    }

    // the dummies keep their world
    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if(!event.getWorld().isRemote) return;
        dummyOrb = null;
        dummyBelph = null;
        dummyBehemoth = null;
        dummyWraith = null;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void bossBarRenderPre(RenderGameOverlayEvent.BossInfo event) {
        if(event.isCanceled()) return;
        if(!(event.getBossInfo() instanceof IBossInfo_LycanitesBossMixin)) return;
        IBossInfo_LycanitesBossMixin lycanitesBossInfo = (IBossInfo_LycanitesBossMixin) event.getBossInfo();

        if(!lycanitesBossInfo.lycanitesTweaks$isLycanitesBoss()) return;
        if(!(lycanitesBossInfo.lycanitesTweaks$getLycanitesEntity() instanceof EntityRahovart)) return;
        EntityRahovart rahovart = (EntityRahovart) lycanitesBossInfo.lycanitesTweaks$getLycanitesEntity();
        World world = rahovart.world;

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        int lhs = event.getX();
        int rhs = event.getX() + 182;
        int drawY = (int) (event.getY() - ((float) event.getIncrement() / 2));

        // Client Sync Properties and what I wish were
        EntityHellfireOrb orb = rahovart.hellfireOrbs.isEmpty() ? dummyOrb : rahovart.hellfireOrbs.get(0);
        if(dummyOrb == null) dummyOrb = new EntityHellfireOrb(world);
        if(dummyBelph == null) dummyBelph = new EntityBelph(world);
        if(dummyBehemoth == null) dummyBehemoth = new EntityBehemoth(world);
        if(dummyWraith == null) dummyWraith = new EntityWraith(world);

        // Mob Event
        drawTopCenteredConfigurableTexture(
                RAHOVART_EVENT_TEXTURE,
                event.getResolution(),
                ForgeConfigHandler.clientFeaturesMixinConfig.rahovartEventOverlay
        );

        // Right Hand Side
        drawRHS(rahovart, rhs, drawY, fontRenderer);

        // Left Hand Side
        // Hellfire Energy %
        lhs -= 18;
        if(orb != null) {
            float minV = (float)orb.animationFrame / (float)orb.animationFrameMax;
            float maxV = minV + 1F / (float)orb.animationFrameMax;
            drawTexture(
                    dummyOrb.getTexture(),
                    lhs,
                    drawY,
                    0,
                    0,
                    minV,
                    1,
                    maxV,
                    16,
                    16
            );
        }
        String energyString = rahovart.hellfireEnergy + "%";
        fontRenderer.drawStringWithShadow(energyString, lhs + 9 - ((float) fontRenderer.getStringWidth(energyString) / 2), drawY + 2, 0xFFFFFF);
        // Belph
        if(rahovart.getBattlePhase() != 1) {
            drawTexture(
                    dummyBelph.creatureInfo.getIcon(),
                    lhs -= 18,
                    drawY,
                    0,
                    0,
                    0,
                    1,
                    1,
                    16,
                    16
            );
        }
        // Behemoth
        if(rahovart.getBattlePhase() != 0) {
            drawTexture(
                    dummyBehemoth.creatureInfo.getIcon(),
                    lhs -= 18,
                    drawY,
                    0,
                    0,
                    0,
                    1,
                    1,
                    16,
                    16
            );
        }
        // Wraith
        if(rahovart.getBattlePhase() == 2) {
            drawTexture(
                    dummyWraith.creatureInfo.getIcon(),
                    lhs -= 18,
                    drawY,
                    0,
                    0,
                    0,
                    1,
                    1,
                    16,
                    16
            );
        }
    }
}
