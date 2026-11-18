package lycanitestweaks.mixin.lycanitestweaksminor.customfirenobreakcollision.client;

import com.lycanitesmobs.client.ClientProxy;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraftforge.client.model.ModelLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientProxy.class)
public abstract class ClientProxy_FireModelMixin {

    @Inject(
            method = "addBlockRender",
            at = @At("HEAD"),
            remap = false
    )
    private void lycanitesTweaks_lycanitesMobsClientProxy_addBlockRenderFireVariants(ModInfo group, Block block, CallbackInfo ci){
        if(block instanceof BlockFireBase) {
            ModelLoader.setCustomStateMapper(block, (new StateMap.Builder())
                    .ignore(BlockFireBase.AGE)
                    .ignore(BlockFireBase.PERMANENT)
                    .ignore(BlockFireBase.STATIC)
                    .build());
        }
    }
}
