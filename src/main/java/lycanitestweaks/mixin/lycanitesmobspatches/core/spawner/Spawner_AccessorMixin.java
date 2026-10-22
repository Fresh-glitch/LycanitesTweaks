package lycanitestweaks.mixin.lycanitesmobspatches.core.spawner;

import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Spawner.class)
public interface Spawner_AccessorMixin {

    @Accessor(value = "triggerCounts", remap = false)
    Map<EntityPlayer, Integer> lycanitesTweaks$getTriggerCounts();
}
