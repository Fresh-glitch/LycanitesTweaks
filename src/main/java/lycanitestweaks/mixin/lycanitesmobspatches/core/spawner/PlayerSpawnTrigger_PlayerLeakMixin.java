package lycanitestweaks.mixin.lycanitesmobspatches.core.spawner;

import com.lycanitesmobs.core.spawner.trigger.PlayerSpawnTrigger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.WeakHashMap;

@Mixin(PlayerSpawnTrigger.class)
public abstract class PlayerSpawnTrigger_PlayerLeakMixin {

    @Shadow(remap = false)
    protected Map<EntityPlayer, BlockPos> lastTickPositions;

    @Inject(
            method = "<init>",
            at = @At("TAIL"),
            remap = false
    )
    private void lycanitesTweaks_lycanitesMobsPlayerSpawnTrigger_initWeakPlayers(CallbackInfo ci){
        this.lastTickPositions = new WeakHashMap<>();
    }
}
