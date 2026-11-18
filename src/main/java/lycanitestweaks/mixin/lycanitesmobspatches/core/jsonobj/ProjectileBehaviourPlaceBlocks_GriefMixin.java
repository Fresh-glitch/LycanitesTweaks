package lycanitestweaks.mixin.lycanitesmobspatches.core.jsonobj;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.info.projectile.behaviours.ProjectileBehaviourPlaceBlocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileBehaviourPlaceBlocks.class)
public abstract class ProjectileBehaviourPlaceBlocks_GriefMixin {

    @Inject(
            method = "onProjectileImpact",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void lycanitesTweaks_lycanitesMobsProjectileBehaviourPlaceBlocks_onProjectileImpactNoGrief(BaseProjectileEntity projectile, World world, BlockPos pos, CallbackInfo ci){
        if(!ForgeEventFactory.getMobGriefingEvent(world, projectile.getThrower()))
            ci.cancel();
    }
}
