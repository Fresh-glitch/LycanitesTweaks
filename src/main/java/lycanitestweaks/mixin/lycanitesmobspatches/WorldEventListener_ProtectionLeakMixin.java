package lycanitestweaks.mixin.lycanitesmobspatches;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.WorldEventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(WorldEventListener.class)
public abstract class WorldEventListener_ProtectionLeakMixin {

    @WrapOperation(
            method = "onEntityAdded",
            at = @At(value = "INVOKE", target = "Lcom/lycanitesmobs/ExtendedWorld;getForWorld(Lnet/minecraft/world/World;)Lcom/lycanitesmobs/ExtendedWorld;", remap = false)
    )
    private ExtendedWorld lycanitesTweaks_lycanitesMobsWorldEventListener_onEntityAddedSkipUnkeyed(World world, Operation<ExtendedWorld> original, Entity entity){
        // a player changing dimension names the old world
        if(EntityList.getKey(entity) == null) return null;
        return original.call(world);
    }

    @WrapWithCondition(
            method = "onEntityAdded",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", remap = false)
    )
    private boolean lycanitesTweaks_lycanitesMobsWorldEventListener_onEntityAddedSkipList(List<Object> list, Object entity){
        // nothing reads this list
        return false;
    }
}
