package lycanitestweaks.item;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.Variant;
import com.lycanitesmobs.core.item.ChargeItem;
import com.lycanitesmobs.core.item.ItemBase;
import lycanitestweaks.capability.storedcreatureinfo.ItemStoredCreatureInfo;
import lycanitestweaks.compat.ModLoadedUtil;
import lycanitestweaks.handlers.ForgeConfigHandler;
import lycanitestweaks.handlers.features.item.ConfigurableItemHandler;
import lycanitestweaks.item.base.ItemBossRangedWeapon;
import lycanitestweaks.util.LycanitesEntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class ItemDevilGatlingGun extends ItemBossRangedWeapon {

    public ItemDevilGatlingGun(String itemName) {
        super(itemName);
        this.infinityProjectileName = "devilgatling";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull ItemStack itemStack, World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag tooltipFlag) {
        super.addInformation(itemStack, world, tooltip, tooltipFlag);

        StringBuilder rawStrings = new StringBuilder();
        rawStrings.append(I18n.format("item.lycanitestweaks.devilgatlinggun.description"));

        ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(itemStack);
        CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature("asmodeus");
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
            rawStrings.append(I18n.format("lycanitestweaks.ability.devilgatlinggun.tooltip0")).append("\n");
            rawStrings.append(I18n.format("lycanitestweaks.ability.devilgatlinggun.tooltip1")).append("\n");
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
        if(stack.getItem() instanceof ChargeItem) {
            ChargeItem item = (ChargeItem) stack.getItem();
            for(String chargeName : ForgeConfigHandler.server.customStaffConfig.gatlingAmmoBlacklist) {
                if(item.projectileInfo.getName().equals(chargeName))
                    return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entityLiving;
            player.getCooldownTracker().setCooldown(this, 20);
        }
    }

    @Override
    public SoundEvent getFireSound(){
        return AssetManager.getSound( "asmodeus_attack");
    }

    public float getFireVelocityModifier(){
        return 4F;
    }

    @Override
    protected void setInfinityProjectileName(String projectileName) {}

    @Override
    protected List<BaseProjectileEntity> createChargeProjectileList(ItemStack itemStack, World world, EntityPlayer player, ChargeItem chargeItem) {
        return Collections.singletonList(LycanitesEntityUtil.createProjectileFromItem(itemStack, world, player, chargeItem));
    }

    @Override
    protected List<BaseProjectileEntity> createInfinityProjectileList(World world, EntityPlayer player) {
        BaseProjectileEntity baseProjectileEntity = createInfinityProjectile(world, player);
        return Collections.singletonList(baseProjectileEntity);
    }

    @Override
    protected void customizeProjectile(BaseProjectileEntity projectile, EntityLivingBase entityLivingBase) {
        super.customizeProjectile(projectile, entityLivingBase);
        if(projectile == null) return;
        // Less screen flashing
        projectile.setPosition(
                projectile.posX + projectile.motionX / this.getFireVelocityModifier(),
                projectile.posY + projectile.motionY / this.getFireVelocityModifier(),
                projectile.posZ + projectile.motionZ / this.getFireVelocityModifier()
        );
    }

    @SubscribeEvent
    public static void onLivingEntityUseItemTick(LivingEntityUseItemEvent.Tick event) {
        ItemStack stack = event.getItem();
        EntityLivingBase entityLivingBase = event.getEntityLiving();
        if(!(stack.getItem() instanceof ItemDevilGatlingGun)) return;
        ItemDevilGatlingGun gatlingGun = (ItemDevilGatlingGun) stack.getItem();

        int count = event.getDuration();
        int useTime = gatlingGun.getMaxItemUseDuration(stack) - count;
        if(useTime < 20) return;
        useTime -= 20;

        int useRate = 10;
        ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(event.getItem());
        if(storedCreatureInfo != null) {
            useRate = storedCreatureInfo.getOrCreateStats().ticksPerUse;
        }
        if(useRate <= 0) useRate = 1;

        // Up to +2 bonus attacks
        for(int i = 0; i < 3; i++) {
            if(useTime >= useRate) {
                useTime -= useRate;
                event.setDuration(count + useRate);

                gatlingGun.fireChargeProjectile(
                        stack,
                        entityLivingBase.getEntityWorld(),
                        entityLivingBase,
                        count,
                        0,
                        0,
                        8F,
                        5,
                        i == 0
                );
            }
            else
                break;
        }
    }
}
