package lycanitestweaks.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import lycanitestweaks.util.LycanitesMobsWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(World.class)
public abstract class World_ExtinguishLycanitesFireMixin implements IBlockAccess {

    @Shadow
    public abstract void playEvent(@Nullable EntityPlayer player, int type, BlockPos pos, int data);
    @Shadow
    public abstract boolean setBlockToAir(BlockPos pos);

    @ModifyReturnValue(
            method = "extinguishFire",
            at = @At(value = "RETURN", ordinal = 1)
    )
    public boolean lycanitesTweaks_vanillaWorld_extinguishFireLycanites(boolean original, @Local(argsOnly = true) EntityPlayer player, @Local(argsOnly = true) BlockPos pos){
        if(LycanitesMobsWrapper.isLycanitesFire(this, pos)){
            this.playEvent(player, 1009, pos, 0);
            this.setBlockToAir(pos);
            return true;
        }
        return original;
    }
}
