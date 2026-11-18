package lycanitestweaks.mixin.lycanitesmobspatches.core.jsonobj;

import com.llamalad7.mixinextras.sugar.Local;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.info.projectile.behaviours.ProjectileBehaviourPlaceBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileBehaviourPlaceBlocks.class)
public abstract class ProjectileBehaviourPlaceBlocks_SideFixMixin {

    @Shadow(remap = false) public double chance;
    @Shadow(remap = false) public int height;

    @Inject(
            method = "onProjectileImpact",
            at = @At(value = "FIELD", target = "Lcom/lycanitesmobs/core/info/projectile/behaviours/ProjectileBehaviourPlaceBlocks;radius:I", ordinal = 0),
            remap = false
    )
    private void lycanitesTweaks_lycanitesMobsProjectileBehaviourPlaceBlocks_onProjectileImpactVerticalFire(BaseProjectileEntity projectile, World world, BlockPos pos, CallbackInfo ci, @Local Block block, @Local IBlockState blockState){
        if(block instanceof BlockFire || block instanceof BlockFireBase) {
            for(int x = -this.height; x < this.height + 1; x++) {
                for(int y = -this.height; y < this.height + 1; y++) {
                    for(int z = -this.height; z < this.height + 1; z++) {
                        BlockPos placePos = pos.add(x, y, z);
                        if (projectile.canDestroyBlock(placePos) && (this.chance >= 1 || this.chance >= world.rand.nextDouble()) && block.canPlaceBlockAt(world, placePos)) {
                            world.setBlockState(placePos, blockState);
                        }
                    }
                }
            }
        }
    }
}
