package lycanitestweaks.mixin.lycanitestweaksmajor.itemtweaks;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Local;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.creature.EntityWraith;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.Variant;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.special.ItemWraithSigil;
import com.lycanitesmobs.core.item.temp.ItemScepter;
import com.lycanitesmobs.core.pets.SummonSet;
import lycanitestweaks.capability.storedcreatureinfo.IStoredCreatureInfo;
import lycanitestweaks.capability.storedcreatureinfo.ItemStoredCreatureInfo;
import lycanitestweaks.capability.storedcreatureinfo.StoredCreatureInfoHandler;
import lycanitestweaks.handlers.ForgeConfigHandler;
import lycanitestweaks.item.interfaces.IItemWithCreatureInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ItemWraithSigil.class)
public abstract class ItemWraithSigil_EnhancedMixin extends ItemScepter implements IItemWithCreatureInfo {

    @ModifyReceiver(
            method = "rapidAttack",
            at = @At(value = "INVOKE", target = "Lcom/lycanitesmobs/core/entity/creature/EntityWraith;setMinion(Z)V"),
            remap = false
    )
    public EntityWraith lycanitesTweaks_lycanitesMobsItemWraithSigil_rapidAttackProperties(EntityWraith wraith, boolean b, @Local(argsOnly = true) ItemStack itemStack, @Local(argsOnly = true) EntityLivingBase entity){
        ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(itemStack);
        if(storedCreatureInfo != null) {
            if(!storedCreatureInfo.getCustomName().isEmpty()) wraith.setCustomNameTag(storedCreatureInfo.getCustomName());
            wraith.setLevel(storedCreatureInfo.getLevel());
            wraith.applyVariant(storedCreatureInfo.getEntityVariant());
        }

        if(ForgeConfigHandler.majorFeaturesConfig.itemTweaksConfig.wraithSigilCopyPotions){
            entity.getActivePotionEffects().forEach(effect -> wraith.addPotionEffect(new PotionEffect(effect)));
        }

        return wraith;
    }

    @ModifyReceiver(
            method = "rapidAttack",
            at = @At(value = "INVOKE", target = "Lcom/lycanitesmobs/core/entity/creature/EntityWraith;setPlayerOwner(Lnet/minecraft/entity/player/EntityPlayer;)V"),
            remap = false
    )
    public EntityWraith lycanitesTweaks_lycanitesMobsItemWraithSigil_rapidAttackMinionBehaviors(EntityWraith wraith, EntityPlayer player, @Local(argsOnly = true) ItemStack itemStack){
        if(ExtendedPlayer.getForPlayer(player) != null) {
            SummonSet summonSet = ExtendedPlayer.getForPlayer(player).getSelectedSummonSet();
            summonSet.applyBehaviour(wraith);
        }
        return wraith;
    }

    @Unique
    @SideOnly(Side.CLIENT)
    @Override
    public String getDescription(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature("wraith");
        ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(stack);
        if(creatureInfo != null && storedCreatureInfo != null) {
            int level = storedCreatureInfo.getLevel();
            if(level > 0) {
                StringBuilder rawStrings = new StringBuilder();
                Variant variant = creatureInfo.getSubspecies(0).getVariant(storedCreatureInfo.getEntityVariant());
                if (variant == null) rawStrings.append(I18n.format("subspecies.normal")).append(" ");
                else rawStrings.append(variant.getTitle()).append(" ");

                rawStrings.append(creatureInfo.getTitle()).append(" ");
                rawStrings.append(I18n.format("entity.level")).append(" ").append(level);

                List<String> formattedDescriptionList = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(rawStrings.toString(), ItemBase.DESCRIPTION_WIDTH);
                tooltip.addAll(formattedDescriptionList);
            }
        }

        return super.getDescription(stack, worldIn, tooltip, flagIn);
    }

    @Override
    @Nullable
    public NBTTagCompound getNBTShareTag(ItemStack stack) {
        NBTTagCompound nbt = super.getNBTShareTag(stack);
        if(nbt != null) {
            ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(stack);
            if (storedCreatureInfo != null) {
                NBTBase capTag = StoredCreatureInfoHandler.ITEM_STORED_CREATURE_INFO.writeNBT(storedCreatureInfo, null);
                if (capTag != null) {
                    NBTTagCompound newTag = nbt.copy();
                    newTag.setTag(IStoredCreatureInfo.FORGE_CAP_NBT, capTag);
                    return newTag;
                }
            }
        }
        return nbt;
    }

    @Override
    public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt) {
        if(nbt != null && nbt.hasKey(IStoredCreatureInfo.FORGE_CAP_NBT)) {
            NBTTagCompound capTag = nbt.getCompoundTag(IStoredCreatureInfo.FORGE_CAP_NBT);
            ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(stack);
            if(storedCreatureInfo != null) {
                StoredCreatureInfoHandler.ITEM_STORED_CREATURE_INFO.readNBT(storedCreatureInfo, null, capTag);
                nbt.removeTag(IStoredCreatureInfo.FORGE_CAP_NBT);
            }
        }
        super.readNBTShareTag(stack, nbt);
    }
}
