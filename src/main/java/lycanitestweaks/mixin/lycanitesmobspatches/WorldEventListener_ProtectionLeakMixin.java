package lycanitestweaks.mixin.lycanitesmobspatches;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.lycanitesmobs.WorldEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(WorldEventListener.class)
public abstract class WorldEventListener_ProtectionLeakMixin {

    @WrapWithCondition(
            method = "onEntityAdded",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", remap = false)
    )
    private boolean lycanitesTweaks_lycanitesMobsWorldEventListener_onEntityAddedSkipList(List<Object> list, Object entity){
        // nothing reads this list
        return false;
    }
}
