package lycanitestweaks.mixin.lycanitesmobspatches.core.spawner;

import com.lycanitesmobs.core.spawner.trigger.SpawnTrigger;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SpawnTrigger.class)
public interface SpawnTrigger_AccessorMixin {

    @Accessor(value = "playerUsedTicks", remap = false)
    Map<EntityPlayer, Long> lycanitesTweaks$getPlayerUsedTicks();
}
