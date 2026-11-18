package lycanitestweaks.mixin.lycanitestweaksclient;

import com.lycanitesmobs.core.block.effect.BlockDoomfire;
import com.lycanitesmobs.core.block.effect.BlockFrostCloud;
import com.lycanitesmobs.core.block.effect.BlockFrostfire;
import com.lycanitesmobs.core.block.effect.BlockFrostweb;
import com.lycanitesmobs.core.block.effect.BlockHellfire;
import com.lycanitesmobs.core.block.effect.BlockIcefire;
import com.lycanitesmobs.core.block.effect.BlockPoisonCloud;
import com.lycanitesmobs.core.block.effect.BlockPoopCloud;
import com.lycanitesmobs.core.block.effect.BlockPrimefire;
import com.lycanitesmobs.core.block.effect.BlockScorchfire;
import com.lycanitesmobs.core.block.effect.BlockShadowfire;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = {
        BlockDoomfire.class,
        BlockFrostCloud.class,
        BlockFrostfire.class,
        BlockFrostweb.class,
        BlockHellfire.class,
        BlockIcefire.class,
        BlockPoisonCloud.class,
        BlockPoopCloud.class,
        BlockPrimefire.class,
        BlockScorchfire.class,
        BlockShadowfire.class
})
public abstract class BlockFireBases_ParticleReducerMixin {

    @ModifyConstant(
            method = "randomDisplayTick",
            constant = @Constant(intValue = 12)
    )
    private int ddddd(int constant){
        return 2;
    }
}
