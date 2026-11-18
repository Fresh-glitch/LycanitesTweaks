package lycanitestweaks.mixin.lycanitesmobspatches.core;

import com.lycanitesmobs.core.pets.PetEntry;
import com.lycanitesmobs.core.pets.PetManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

// Fix by Flankrat/Maximus
@Mixin(PetManager.class)
public abstract class PetManager_SavePendingEntriesMixin {

    // Live pet entries, the only ones Lycanites writes when saving
    @Shadow(remap = false) public Map<UUID, PetEntry> entries;
    // Pet entries read from the player's save, only turned into live entries on the PetManager's next onUpdate
    @Shadow(remap = false) public Map<UUID, NBTTagCompound> entryNBTs;

    /*
     * readFromNBT only stores the saved pets in entryNBTs, and writeToNBT only writes the live entries.
     * A save before the next onUpdate (player clone on death/respawn or leaving the End, logging out or an
     * autosave right after joining, or any tick where the player's LivingUpdateEvent didn't reach
     * ExtendedPlayer) therefore wrote an empty pet list and the pets were gone for good.
     * Write the still pending entries too, so they survive until a tick loads them.
     */
    @Inject(
            method = "writeToNBT",
            at = @At("TAIL"),
            remap = false
    )
    private void lycanitesTweaks_lycanitesMobsPetManager_writeToNBTPending(NBTTagCompound nbtTagCompound, CallbackInfo ci){
        if (this.entryNBTs.isEmpty()) return;

        // The list writeToNBT just stored (type 10 = compound entries)
        NBTTagList entryList = nbtTagCompound.getTagList("PetManager", 10);
        for (Map.Entry<UUID, NBTTagCompound> pending : this.entryNBTs.entrySet()) {
            // Already written from its live entry (added before the merge, e.g. a familiar), don't write it twice
            if (this.entries.containsKey(pending.getKey())) continue;
            entryList.appendTag(pending.getValue().copy());
        }
        nbtTagCompound.setTag("PetManager", entryList);
    }
}
