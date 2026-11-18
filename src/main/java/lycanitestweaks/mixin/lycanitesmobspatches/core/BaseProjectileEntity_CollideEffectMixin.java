package lycanitestweaks.mixin.lycanitesmobspatches.core;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BaseProjectileEntity.class)
public abstract class BaseProjectileEntity_CollideEffectMixin extends EntityThrowable {

    public BaseProjectileEntity_CollideEffectMixin(World world) {
        super(world);
    }

    @ModifyExpressionValue(
            method = "onImpact",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/material/Material;isSolid()Z")
    )
    private boolean lycanitesTweaks_lycanitesMobsBaseProjectileEntity_onImpactIsSideSolid(boolean isSolid, RayTraceResult rayTraceResult, @Local(name = "blockPos") BlockPos blockPos, @Local IBlockState blockState){
        return isSolid && !blockState.getBlock().isPassable(this.world, blockPos);
    }

    @ModifyVariable(
            method = "onImpact",
            at = @At(value = "STORE", ordinal = 2),
            name = "impactPos"
    )
    private BlockPos lycanitesTweaks_lycanitesMobsBaseProjectileEntity_onImpactBlockSideHit(BlockPos value, RayTraceResult rayTraceResult){
        return rayTraceResult.getBlockPos().offset(rayTraceResult.sideHit);
    }
}
