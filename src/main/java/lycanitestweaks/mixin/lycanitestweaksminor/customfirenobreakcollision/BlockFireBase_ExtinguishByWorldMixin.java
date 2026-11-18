package lycanitestweaks.mixin.lycanitestweaksminor.customfirenobreakcollision;

import com.lycanitesmobs.core.block.BlockBase;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.info.ModInfo;
import lycanitestweaks.LycanitesTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockFireBase.class)
public abstract class BlockFireBase_ExtinguishByWorldMixin extends BlockBase {

    @Shadow(remap = false)
    protected abstract boolean canNeighborCatchFire(World worldIn, BlockPos pos);

    public BlockFireBase_ExtinguishByWorldMixin(Material material, ModInfo group, String name) {
        super(material, group, name);
    }

    // Intended to fix explosion damage calc treating custom fire as a full explosion damage blocker
    // Highly advised to pair with lycanitestweaks.mixin.vanilla.WorldExtinguishLycanitesFire
    //      AKA Mixin 'Lycanites Fire Extinguish (Vanilla)'
    @Inject(
            method = "<init>",
            at = @At("RETURN"),
            remap = false
    )
    public void lycanitesTweaks_lycanitesMobsBlockFireBase_init(Material material, ModInfo group, String name, CallbackInfo ci){
        this.noBreakCollision = true;
    }

    // Restore this so bushes can be broken to clear fire
    @Inject(
            method = "neighborChanged",
            at = @At("HEAD")
    )
    public void lycanitesTweaks_lycanitesMobsBlockFireBase_neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos triggerPos, CallbackInfo ci){
//        if(!world.isRemote) return;
        try {
            if (!world.getBlockState(pos.down()).isSideSolid(world, pos, EnumFacing.UP) && !this.canNeighborCatchFire(world, pos)) {
                world.setBlockToAir(pos);
            }
        }
        // I Assume this error is why Lycanites commented this exact code out
        // Fix Attempt 1: Don't run on client
        // Fix Attempt 2: IDK
        catch (NoClassDefFoundError error){
            LycanitesTweaks.LOGGER.error("Caught Exception with Lycanites BlockFireBase on LycanitesTweaks Mixin neighborChanged: {}", error.toString());
        }
    }
}
