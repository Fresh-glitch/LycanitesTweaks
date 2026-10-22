package lycanitestweaks.client.gui.overlays;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityAsmodeus;
import com.lycanitesmobs.core.entity.creature.EntityAstaroth;
import com.lycanitesmobs.core.entity.projectile.EntityHellShield;
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

public class AsmodeusBossInfoOverlay extends LycanitesBossInfoOverlay {

    private static EntityAstaroth dummyAstaroth = null;
    private static EntityHellShield dummyShield = null;

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if(!event.getWorld().isRemote) return;
        if(event.getEntity() instanceof BaseCreatureEntity) {
            BaseCreatureEntity creature = (BaseCreatureEntity) event.getEntity();
            if(creature.creatureInfo.getName().equals("asmodeus"))
                PacketHandler.instance.sendToServer(new PacketLycanitesBossInfo(creature));
        }
    }

    // the dummies keep their world
    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if(!event.getWorld().isRemote) return;
        dummyShield = null;
        dummyAstaroth = null;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void bossBarRenderPre(RenderGameOverlayEvent.BossInfo event) {
        if(event.isCanceled()) return;
        if(!(event.getBossInfo() instanceof IBossInfo_LycanitesBossMixin)) return;
        IBossInfo_LycanitesBossMixin lycanitesBossInfo = (IBossInfo_LycanitesBossMixin) event.getBossInfo();

        if(!lycanitesBossInfo.lycanitesTweaks$isLycanitesBoss()) return;
        if(!(lycanitesBossInfo.lycanitesTweaks$getLycanitesEntity() instanceof EntityAsmodeus)) return;
        EntityAsmodeus asmodeus = (EntityAsmodeus) lycanitesBossInfo.lycanitesTweaks$getLycanitesEntity();
        World world = asmodeus.world;

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        int lhs = event.getX();
        int rhs = event.getX() + 182;
        int drawY = (int) (event.getY() - ((float) event.getIncrement() / 2));

        // Client Sync Properties and what I wish were
        if(dummyShield == null) dummyShield = new EntityHellShield(world);
        if(dummyAstaroth == null) dummyAstaroth = new EntityAstaroth(world);

        // Mob Event
        drawTopCenteredConfigurableTexture(
                ASMODEUS_EVENT_TEXTURE,
                event.getResolution(),
                ForgeConfigHandler.clientFeaturesMixinConfig.asmodeusEventOverlay
        );

        // Right Hand Side
        drawRHS(asmodeus, rhs, drawY, fontRenderer);

        // Left Hand Side
        // Hellshield
        if(asmodeus.isBlocking()) {
            drawTexture(
                    dummyShield.getTexture(),
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
            drawTexture(
                    dummyAstaroth.creatureInfo.getIcon(),
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
