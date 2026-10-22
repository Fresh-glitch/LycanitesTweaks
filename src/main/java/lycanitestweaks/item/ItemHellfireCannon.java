package lycanitestweaks.item;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.creature.EntityRahovart;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrier;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireOrb;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireWave;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.Variant;
import com.lycanitesmobs.core.item.ItemBase;
import lycanitestweaks.LycanitesTweaks;
import lycanitestweaks.capability.storedcreatureinfo.ItemStoredCreatureInfo;
import lycanitestweaks.compat.ModLoadedUtil;
import lycanitestweaks.entity.projectile.EntityChargeArrow;
import lycanitestweaks.handlers.ForgeConfigHandler;
import lycanitestweaks.handlers.features.item.ConfigurableItemHandler;
import lycanitestweaks.item.base.ItemBossRangedWeapon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemHellfireCannon extends ItemBossRangedWeapon {

    private static final List<EntityHellfireOrb> clientPlayerOrbs = new ArrayList<>();

    public ItemHellfireCannon(String itemName) {
        super(itemName);
        this.infinityProjectileName = "hellfirewave";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull ItemStack itemStack, World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag tooltipFlag) {
        super.addInformation(itemStack, world, tooltip, tooltipFlag);

        StringBuilder rawStrings = new StringBuilder();
        rawStrings.append(I18n.format("item.lycanitestweaks.hellfirecannon.description"));

        ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(itemStack);
        CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature("rahovart");
        if(storedCreatureInfo != null && creatureInfo != null) {
            int level = storedCreatureInfo.getLevel();
            if(level > 0) {
                rawStrings.append("\n");
                Variant variant = creatureInfo.getSubspecies(0).getVariant(storedCreatureInfo.getEntityVariant());
                if (variant != null) rawStrings.append(variant.getTitle()).append(" ");

                rawStrings.append(creatureInfo.getTitle()).append(" ");
                rawStrings.append(I18n.format("entity.level")).append(" ").append(level);
            }
        }

        rawStrings.append("\n");
        if(GuiScreen.isShiftKeyDown()) {
            if(ModLoadedUtil.baubles.isLoaded()) {
                ConfigurableItemHandler.EquipmentSlot slots = ConfigurableItemHandler.getItemSlot(itemStack);
                if(slots != null && slots.baubleAttributes) {
                    rawStrings.append(I18n.format("lycanitestweaks.ability.attributebauble.tooltip0")).append("\n");
                }
            }
            rawStrings.append(I18n.format("lycanitestweaks.ability.hellfirecannon.tooltip0")).append("\n");
            rawStrings.append(I18n.format("lycanitestweaks.ability.hellfirecannon.tooltip1")).append("\n");
            rawStrings.append(I18n.format("lycanitestweaks.ability.hellfirecannon.tooltip2")).append("\n");
        }
        else {
            rawStrings.append("\n").append(I18n.format("item.lycanitestweaks.tooltip.expand", "SHIFT"));
        }

        if(storedCreatureInfo != null) {
            ConfigurableItemHandler.ItemStats stats = storedCreatureInfo.getOrCreateStats();
            rawStrings.append("\n");
            rawStrings.append(I18n.format("item.stats.ticksperuse.weapon.tooltip", stats.ticksPerUse / 20F));
        }

        List<String> formattedDescriptionList = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(rawStrings.toString(), ItemBase.DESCRIPTION_WIDTH);
        tooltip.addAll(formattedDescriptionList);
    }

    @Override
    protected boolean isArrow(ItemStack stack) {
        return false;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
        if(world.isRemote) return;
        int useTime = this.getMaxItemUseDuration(stack) - timeLeft;

        int useRate = 200;
        ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(stack);
        if(storedCreatureInfo != null) {
            ConfigurableItemHandler.ItemStats stats = storedCreatureInfo.getOrCreateStats();
            useRate = stats.ticksPerUse;
        }
        if(useTime < useRate) return;

        this.fireChargeProjectile(stack, world, entityLiving, timeLeft, 0F, 0F, 0F, 1, true);

        if (entityLiving instanceof EntityPlayer) {
            ((EntityPlayer) entityLiving).getCooldownTracker().setCooldown(this, useRate);
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count){
        super.onUsingTick(stack, player, count);
        if(player.world.isRemote) {
            if (LycanitesTweaks.PROXY.getClientPlayer() == player) {
                int useTime = this.getMaxItemUseDuration(stack) - count;
                int useRate = 200;
                ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(stack);
                if(storedCreatureInfo != null) {
                    ConfigurableItemHandler.ItemStats stats = storedCreatureInfo.getOrCreateStats();
                    useRate = stats.ticksPerUse;
                }
                int percent = 100 * useTime / useRate;
                EntityRahovart.updateHellfireOrbs(player, player.ticksExisted, 5, percent - 7, 1, clientPlayerOrbs);
            }
        }
    }

    @Override
    public void fireChargeProjectile(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft, float pitchOffset, float yawOffset, float inaccuracy, int projectileCount, boolean consumeAmmo) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entityLiving;

            int charge = this.getMaxItemUseDuration(stack) - timeLeft;
            charge = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, world, player, charge, true);
            if (charge < 0) return;

            float launchVelocity = getArrowVelocity(charge);
            if (launchVelocity >= 0.1F) {
                if (!world.isRemote) {
                    BaseProjectileEntity projectile;
                    boolean hasInfinity = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;

                    if (player.isSneaking()) {
                        EntityHellfireBarrier hellfireBarrier = new EntityHellfireBarrier(world, player);
                        if(hasInfinity) hellfireBarrier.timeMax *= 5; // 10s with, 2s without

                        hellfireBarrier.posX = player.posX;
                        hellfireBarrier.posY = player.posY - 10;
                        hellfireBarrier.posZ = player.posZ;
                        hellfireBarrier.rotation = player.rotationYawHead + 90F;
                        projectile = hellfireBarrier;
                    }
                    else {
                        EntityHellfireWave hellfireWave = new EntityHellfireWave(world, player);
                        if(!hasInfinity) hellfireWave.timeMax /= 5; // 10s with, 2s without

                        hellfireWave.posY = player.posY - 10;
                        hellfireWave.rotation = player.rotationYawHead + 90F;
                        projectile = hellfireWave;
                    }

                    projectile.setDamage(ForgeConfigHandler.majorFeaturesConfig.rahovartConfig.hellfireAttacksBaseDamage);
                    float onSpawnVelocity = launchVelocity * this.getFireVelocityModifier();
                    int onSpawnDamage;
                    world.spawnEntity(projectile);
                    this.customizeProjectile(projectile, entityLiving);

                    projectile.playSound(this.getFireSound(), this.getSoundVolume(), 1.0F / (player.getRNG().nextFloat() * 0.4F + 0.8F));
                    projectile.playSound(projectile.getLaunchSound(), this.getSoundVolume(), 1.0F / (player.getRNG().nextFloat() * 0.4F + 0.8F));

                    // Enchantments handled by lycanitestweaks.handlers.features.entity.ChargeEnchantmentsHandler
                    // Cope compatibility for how mods handle "speed = more damage", shoot an arrow and test velocity
                    EntityArrow entityarrow = new EntityChargeArrow(world, player, projectile);
                    entityarrow = this.customizeArrow(entityarrow);
                    entityarrow.shoot(player, player.rotationPitch + pitchOffset, player.rotationYaw + yawOffset, 0.0F, onSpawnVelocity, inaccuracy);
                    entityarrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
                    world.spawnEntity(entityarrow);

                    float arrowVelocity = MathHelper.sqrt(entityarrow.motionX * entityarrow.motionX + entityarrow.motionY * entityarrow.motionY + entityarrow.motionZ * entityarrow.motionZ);
                    onSpawnDamage = MathHelper.ceil((double) arrowVelocity / onSpawnVelocity * entityarrow.getDamage());
                    entityarrow.setDead();

                    projectile.setDamage(onSpawnDamage); // Vanilla arrow hit damage

                    if(consumeAmmo)
                        stack.damageItem(1, player);
                }
                player.addStat(StatList.getObjectUseStats(this));
            }
        }
    }

    @Override
    public SoundEvent getFireSound(){
        return AssetManager.getSound("hellfirewave");
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if(event.getWorld().isRemote) clientPlayerOrbs.clear();
    }

    @SubscribeEvent
    public static void onUseTick(LivingEntityUseItemEvent.Tick event) {
        if(event.getEntityLiving().world.isRemote) return;
        EntityLivingBase entityLivingBase = event.getEntityLiving();

        if(event.getItem().getItem() instanceof ItemHellfireCannon) {
            int useTime = event.getItem().getMaxItemUseDuration() - event.getDuration();
            int useRate = 200;
            ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(event.getItem());
            if(storedCreatureInfo != null) {
                ConfigurableItemHandler.ItemStats stats = storedCreatureInfo.getOrCreateStats();
                useRate = stats.ticksPerUse;
            }
            if(useTime >= useRate && useTime <= useRate + 20) {
                event.setDuration(event.getDuration() - 20);
                entityLivingBase.getEntityWorld().playSound(
                        null,
                        entityLivingBase.posX,
                        entityLivingBase.posY,
                        entityLivingBase.posZ,
                        AssetManager.getSound("rahovart_phase"),
                        SoundCategory.PLAYERS,
                        2F,
                        1.0F / (entityLivingBase.getRNG().nextFloat() * 0.4F + 0.8F)
                );
            }
        }
    }
}
