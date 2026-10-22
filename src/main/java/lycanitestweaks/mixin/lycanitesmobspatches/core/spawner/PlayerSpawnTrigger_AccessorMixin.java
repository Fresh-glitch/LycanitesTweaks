package lycanitestweaks.mixin.lycanitesmobspatches.core.spawner;

import com.lycanitesmobs.core.spawner.trigger.PlayerSpawnTrigger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(PlayerSpawnTrigger.class)
public interface PlayerSpawnTrigger_AccessorMixin {

    @Accessor(value = "lastTickPositions", remap = false)
    Map<EntityPlayer, BlockPos> lycanitesTweaks$getLastTickPositions();
}
