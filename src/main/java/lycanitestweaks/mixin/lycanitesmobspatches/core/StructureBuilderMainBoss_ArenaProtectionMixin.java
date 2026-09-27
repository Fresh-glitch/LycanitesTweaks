package lycanitestweaks.mixin.lycanitesmobspatches.core;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.mobevent.effects.StructureBuilder;
import com.lycanitesmobs.core.worldgen.mobevents.AmalgalichStructureBuilder;
import com.lycanitesmobs.core.worldgen.mobevents.RahovartStructureBuilder;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = {
        AmalgalichStructureBuilder.class,
        RahovartStructureBuilder.class
})
public abstract class StructureBuilderMainBoss_ArenaProtectionMixin extends StructureBuilder {

    @WrapOperation(
            method = "build",
            at = @At(value = "INVOKE", target = "Lcom/lycanitesmobs/ExtendedWorld;bossUpdate(Lnet/minecraft/entity/Entity;)V"),
            remap = false
    )
    private void lycanitesTweaks_lycanitesMobsStructureBuilderMainBoss_buildArenaProtection(ExtendedWorld worldExt, Entity entity, Operation<Void> original){
        original.call(worldExt, entity);
        // same as AsmodeusStructureBuilder, otherwise the walls only protect the default 30 blocks
        CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(this.name);
        if(creatureInfo != null) worldExt.overrideBossRange(entity, creatureInfo.bossNearbyRange);
    }
}
