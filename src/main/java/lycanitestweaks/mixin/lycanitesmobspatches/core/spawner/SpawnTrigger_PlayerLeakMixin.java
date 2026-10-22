package lycanitestweaks.mixin.lycanitesmobspatches.core.spawner;

import com.lycanitesmobs.core.spawner.trigger.SpawnTrigger;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.WeakHashMap;

@Mixin(SpawnTrigger.class)
public abstract class SpawnTrigger_PlayerLeakMixin {

    @Shadow(remap = false)
    protected Map<EntityPlayer, Long> playerUsedTicks;

    @Inject(
            method = "<init>",
            at = @At("TAIL"),
            remap = false
    )
    private void lycanitesTweaks_lycanitesMobsSpawnTrigger_initWeakPlayers(CallbackInfo ci){
        this.playerUsedTicks = new WeakHashMap<>();
    }
}
