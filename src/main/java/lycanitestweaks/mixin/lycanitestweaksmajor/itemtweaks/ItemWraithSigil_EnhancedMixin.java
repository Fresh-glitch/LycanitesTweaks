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
import lycanitestweaks.handlers.ForgeConfigHandler;
import lycanitestweaks.util.IItemBase_WithCreatureInfoMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ItemWraithSigil.class)
public abstract class ItemWraithSigil_EnhancedMixin extends ItemScepter implements IItemBase_WithCreatureInfoMixin {

    @ModifyReceiver(
            method = "rapidAttack",
            at = @At(value = "INVOKE", target = "Lcom/lycanitesmobs/core/entity/creature/EntityWraith;setMinion(Z)V"),
            remap = false
    )
    public EntityWraith lycanitesTweaks_lycanitesMobsItemWraithSigil_rapidAttackProperties(EntityWraith wraith, boolean b, @Local(argsOnly = true) ItemStack itemStack, @Local(argsOnly = true) EntityLivingBase entity){
        if(!this.getCustomName(itemStack).isEmpty()) wraith.setCustomNameTag(this.getCustomName(itemStack));
        wraith.setLevel(this.getLevel(itemStack));
        wraith.applyVariant(this.getEntityVariant(itemStack));

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
        if(creatureInfo != null && this.getLevel(stack) > 0) {
            StringBuilder rawStrings = new StringBuilder();
            Variant variant = creatureInfo.getSubspecies(0).getVariant(this.getEntityVariant(stack));
            if (variant == null) rawStrings.append(I18n.format("subspecies.normal")).append(" ");
            else rawStrings.append(variant.getTitle()).append(" ");

            rawStrings.append(creatureInfo.getTitle()).append(" ");
            rawStrings.append(I18n.format("entity.level")).append(" ").append(this.getLevel(stack));

            List<String> formattedDescriptionList = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(rawStrings.toString(), ItemBase.DESCRIPTION_WIDTH);
            tooltip.addAll(formattedDescriptionList);
        }

        return super.getDescription(stack, worldIn, tooltip, flagIn);
    }

    @Unique
    @Override
    public String lycanitesTweaks$getCreatureTypeName(ItemStack itemStack){
        return "wraith";
    }

    @Unique
    @Override
    public String lycanitesTweaks$getCustomName(ItemStack itemStack){
        NBTTagCompound nbt = this.getTagCompound(itemStack);
        if(nbt.hasKey(CUSTOM_NAME_NBT)) return nbt.getString(CUSTOM_NAME_NBT);
        return "";
    }

    @Unique
    @Override
    public int lycanitesTweaks$getLevel(ItemStack itemStack){
        NBTTagCompound nbt = this.getTagCompound(itemStack);
        if(nbt.hasKey(MOB_LEVEL_NBT)) return nbt.getInteger(MOB_LEVEL_NBT);
        return -1;
    }

    @Unique
    @Override
    public int lycanitesTweaks$getEntitySubspecies(ItemStack itemStack){
        return 0;
    }

    @Unique
    @Override
    public int lycanitesTweaks$getEntityVariant(ItemStack itemStack){
        NBTTagCompound nbt = this.getTagCompound(itemStack);
        if(nbt.hasKey(VARIANT_NBT)) return nbt.getInteger(VARIANT_NBT);
        return -1;
    }

    @Unique
    @Override
    public String lycanitesTweaks$getEntityVariantRarity(ItemStack itemStack) {
        return this.getEntityVariantRarity(itemStack);
    }

    @Unique
    @Override
    public void lycanitesTweaks$setCreatureTypeName(ItemStack itemStack, String type){

    }

    @Unique
    @Override
    public void lycanitesTweaks$setCustomName(ItemStack itemStack, String name){
        NBTTagCompound nbt = this.getTagCompound(itemStack);
        nbt.setString(CUSTOM_NAME_NBT, name);
        itemStack.setTagCompound(nbt);
    }

    @Unique
    @Override
    public void lycanitesTweaks$setLevel(ItemStack itemStack, int level){
        NBTTagCompound nbt = this.getTagCompound(itemStack);
        nbt.setInteger(MOB_LEVEL_NBT, level);
        itemStack.setTagCompound(nbt);
    }

    @Unique
    @Override
    public void lycanitesTweaks$setEntitySubspecies(ItemStack itemStack, int index){

    }

    @Unique
    @Override
    public void lycanitesTweaks$setEntityVariant(ItemStack itemStack, int index){
        NBTTagCompound nbt = this.getTagCompound(itemStack);
        nbt.setInteger(VARIANT_NBT, index);
        itemStack.setTagCompound(nbt);
    }
}
