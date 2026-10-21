package lycanitestweaks.item;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.pets.SummonSet;
import lycanitestweaks.handlers.ForgeConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemChallengeSoulStaff extends ItemCreatureInfoStaff {

    public ItemChallengeSoulStaff(String itemName, String textureName) {
        super(itemName, textureName);
    }

    // ==================================================
    //                       Use
    // ==================================================
    // ========== Durability ==========
    @Override
    public int getDurability() {
        return 1;
    }

    // ========== Summon Cost ==========
    @Override
    public float getSummonCostMod() {
        return 0F;
    }

    // ========== Summon Duration ==========
    @Override
    public int getSummonDuration() {
        return 20 * ForgeConfigHandler.server.customStaffConfig.challengeSoulStaffDuration;
    }

    // I don't like this but it works
    @Override
    public boolean getAdditionalCosts(EntityPlayer player) {
        ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
        boolean oneSpawn = false;
        if(extendedPlayer != null) {
            oneSpawn = extendedPlayer.summonFocus >= extendedPlayer.summonFocusCharge;
            extendedPlayer.summonFocus = 0;
        }

        return oneSpawn && super.getAdditionalCosts(player);
    }


    // ========== Minion Behaviour ==========
    // Override to avoid my own mixins
    @Override
    public void applyMinionBehaviour(TameableCreatureEntity minion, EntityPlayer player) {
        SummonSet summonSet = ExtendedPlayer.getForPlayer(player).getSelectedSummonSet();
        summonSet.applyBehaviour(minion);
        // Ensure properties are synced from stack nbt
        if(this.storedCreatureInfoRef != null) {
            minion.spawnedAsBoss = this.storedCreatureInfoRef.getSpawnedAsBoss();
            if(!this.storedCreatureInfoRef.getCustomName().isEmpty()) minion.setCustomNameTag(this.storedCreatureInfoRef.getCustomName());
            minion.setSubspecies(this.storedCreatureInfoRef.getEntitySubspecies());
            minion.setVariant(this.storedCreatureInfoRef.getEntityVariant());
            minion.addLevel(this.storedCreatureInfoRef.getLevel() - 1);
            minion.firstSpawn = false;
        }
        minion.getRandomSize();
    }

    // ==================================================
    //                     Repairs
    // ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        return false;
    }
}
