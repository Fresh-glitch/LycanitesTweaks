package lycanitestweaks.mixin.lycanitesmobspatches;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lycanitesmobs.WorldEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(WorldEventListener.class)
public abstract class WorldEventListener_ProtectionLeakMixin {

    @WrapOperation(
            method = "onEntityAdded",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", remap = false)
    )
    private boolean lycanitesTweaks_lycanitesMobsWorldEventListener_onEntityAddedSkipList(List<Object> list, Object entity, Operation<Boolean> original){
        // nothing reads customBlockProtectionEntities, it only kept every added entity and its world
        return false;
    }
}
