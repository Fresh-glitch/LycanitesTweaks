package lycanitestweaks.mixin.lycanitesmobspatches.core.spawner;

import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.WeakHashMap;

@Mixin(Spawner.class)
public abstract class Spawner_PlayerLeakMixin {

    @Shadow(remap = false)
    protected Map<EntityPlayer, Integer> triggerCounts;

    @Inject(
            method = "<init>",
            at = @At("TAIL"),
            remap = false
    )
    private void lycanitesTweaks_lycanitesMobsSpawner_initWeakPlayers(CallbackInfo ci){
        // weak like SpawnerEventListener.playerUpdateTicks, every respawn and relog is a new player object
        this.triggerCounts = new WeakHashMap<>();
    }
}
