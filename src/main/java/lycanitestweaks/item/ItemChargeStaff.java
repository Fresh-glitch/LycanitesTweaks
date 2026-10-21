package lycanitestweaks.item;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import com.lycanitesmobs.core.item.ChargeItem;
import lycanitestweaks.LycanitesTweaks;
import lycanitestweaks.capability.storedcreatureinfo.IStoredCreatureInfo;
import lycanitestweaks.capability.storedcreatureinfo.ItemStoredCreatureInfo;
import lycanitestweaks.capability.storedcreatureinfo.StoredCreatureInfoHandler;
import lycanitestweaks.client.renderer.RenderContext;
import lycanitestweaks.entity.projectile.EntityChargeArrow;
import lycanitestweaks.handlers.ForgeConfigHandler;
import lycanitestweaks.handlers.ForgeConfigProvider;
import lycanitestweaks.handlers.features.item.ConfigurableItemHandler;
import lycanitestweaks.util.LycanitesEntityUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ItemChargeStaff extends ItemBow {

    protected String infinityProjectileName = "";

    public ItemChargeStaff(String itemName){
        super();
        this.setMaxDamage(ToolMaterial.DIAMOND.getMaxUses());

        this.setRegistryName(LycanitesTweaks.MODID, itemName);
        this.setTranslationKey(LycanitesTweaks.MODID + "." + itemName);

        this.addPropertyOverride(new ResourceLocation(LycanitesTweaks.MODID, "objtesr"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack itemStack, World world, EntityLivingBase entity) {
                RenderContext.currentRenderEntity = entity;
                RenderContext.currentRenderStack = itemStack;
                return 0.0F;
            }
        });
    }

    @Override
    protected boolean isArrow(ItemStack stack) {
        return stack.getItem() instanceof ChargeItem;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
        if(this.getMaxItemUseDuration(stack) - timeLeft >= 20) this.fireChargeProjectile(stack, world, entityLiving, timeLeft, 1);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair){
        return repair.getItem() == Items.END_CRYSTAL;
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

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
        if(this.findAmmo(player).isEmpty()) {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment){
        if(!ForgeConfigHandler.server.customStaffConfig.chargeStaffEnchantability) return false;
        if(ForgeConfigProvider.getChargeStaffEnchantmentBlacklist().contains(enchantment)) return false;
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    public SoundEvent getFireSound(){
        return SoundEvents.EVOCATION_ILLAGER_CAST_SPELL;
    }

    public float getSoundVolume() {
        return 1F;
    }

    // The shooting velocity of the Charge, 3.0 is how much the Vanilla Bow gives to an Arrow
    public float getFireVelocityModifier(){
        return 3F;
    }

    public String getInfinityProjectileName(){
        return this.infinityProjectileName;
    }

    protected void setInfinityProjectileName(String projectileName){
        this.infinityProjectileName = projectileName;
    }

    protected List<BaseProjectileEntity> createChargeProjectileList(ItemStack itemStack, World world, EntityPlayer player, ChargeItem chargeItem) {
        return LycanitesEntityUtil.createProjectilesFromItem(itemStack, world, player, chargeItem);
    }

    protected List<BaseProjectileEntity> createInfinityProjectileList(World world, EntityPlayer player) {
        BaseProjectileEntity baseProjectileEntity = createInfinityProjectile(world, player);
        if(baseProjectileEntity != null) {
            return LycanitesEntityUtil.createRapidFireProjectile(baseProjectileEntity, null);
        }
        return Collections.emptyList();
    }

    protected BaseProjectileEntity createInfinityProjectile(World world, EntityPlayer player) {
        BaseProjectileEntity projectile = null;
        if(this.getInfinityProjectileName().isEmpty()) return projectile;

        ProjectileManager projectileManager = ProjectileManager.getInstance();
        if(projectileManager.oldModelProjectiles.containsKey(this.getInfinityProjectileName())){
            try {
                projectile = (BaseProjectileEntity) projectileManager.oldModelProjectiles.get(this.getInfinityProjectileName()).getConstructor(World.class, EntityLivingBase.class).newInstance(world, player);
            }
            catch (Exception e) {
                LycanitesTweaks.LOGGER.warn("Unable to create a oldModelProjectiles from the class: {}", this.getInfinityProjectileName());
            }
        }
        else if(projectileManager.oldSpriteProjectiles.containsKey(this.getInfinityProjectileName())){
            try {
                projectile = (BaseProjectileEntity) projectileManager.oldSpriteProjectiles.get(this.getInfinityProjectileName()).getConstructor(World.class, EntityLivingBase.class).newInstance(world, player);
            }
            catch (Exception e) {
                LycanitesTweaks.LOGGER.warn("Unable to create a oldSpriteProjectiles from the class: {}", this.getInfinityProjectileName());
            }
        }
        else if(projectileManager.getProjectile(this.getInfinityProjectileName()) != null){
            projectile = projectileManager.getProjectile(this.getInfinityProjectileName()).createProjectile(world, player);
        }
        return projectile;
    }

    public void fireChargeProjectile(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft, int projectileCount) {
        this.fireChargeProjectile(stack, world, entityLiving, timeLeft, 0, 0, 1F, projectileCount, true);
    }

    public void fireChargeProjectile(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft, float pitchOffset, float yawOffset, float inaccuracy, int projectileCount, boolean consumeAmmo) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entityLiving;
            boolean hasInfinity = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack itemStack = this.findAmmo(player);

            int charge = this.getMaxItemUseDuration(stack) - timeLeft;
            charge = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, world, player, charge, !itemStack.isEmpty() || hasInfinity);
            if (charge < 0) return;

            float launchVelocity = getArrowVelocity(charge);
            if (launchVelocity >= 0.1F) {
                if (!world.isRemote) {
                    List<BaseProjectileEntity> projectiles = null;

                    for(int i = 0; i < projectileCount; i++) {
                        if (!itemStack.isEmpty()) {
                            ChargeItem chargeItem = (ChargeItem) itemStack.getItem();
                            projectiles = this.createChargeProjectileList(itemStack, world, player, chargeItem);

                            this.setInfinityProjectileName(chargeItem.projectileInfo.getName());
                        } else if (hasInfinity) {
                            projectiles = this.createInfinityProjectileList(world, player);
                        }
                        if (projectiles == null) return;

                        // Enchantments handled by lycanitestweaks.handlers.features.entity.ChargeEnchantmentsHandler
                        // Cope compatibility for how mods handle "speed = more damage", shoot an arrow and test velocity
                        float onSpawnVelocity = launchVelocity * this.getFireVelocityModifier();
                        int onSpawnDamage = 0;
                        for(int j = 0; j < projectiles.size(); j++) {
                            BaseProjectileEntity projectile = projectiles.get(j);
                            projectile.shoot(player, player.rotationPitch + pitchOffset, player.rotationYaw + yawOffset, 0.0F, launchVelocity * this.getFireVelocityModifier(), inaccuracy);
                            world.spawnEntity(projectile);
                            this.customizeProjectile(projectile, entityLiving);

                            if(j == 0) {
                                projectile.playSound(this.getFireSound(), this.getSoundVolume(), 1.0F / (player.getRNG().nextFloat() * 0.4F + 0.8F));
                                projectile.playSound(projectile.getLaunchSound(), this.getSoundVolume(), 1.0F / (player.getRNG().nextFloat() * 0.4F + 0.8F));

                                EntityArrow entityarrow = new EntityChargeArrow(world, player, projectile);
                                entityarrow = this.customizeArrow(entityarrow);
                                entityarrow.shoot(player, player.rotationPitch + pitchOffset, player.rotationYaw + yawOffset, 0.0F, onSpawnVelocity, inaccuracy);
                                entityarrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
                                world.spawnEntity(entityarrow);

                                float arrowVelocity = MathHelper.sqrt(entityarrow.motionX * entityarrow.motionX + entityarrow.motionY * entityarrow.motionY + entityarrow.motionZ * entityarrow.motionZ);
                                onSpawnDamage = MathHelper.ceil((double) arrowVelocity / onSpawnVelocity * entityarrow.getDamage());
                                entityarrow.setDead();
                            }
                            projectile.setDamage(onSpawnDamage); // Vanilla arrow hit damage
                        }
                    }

                    if(projectiles != null && !projectiles.isEmpty() && consumeAmmo)
                        stack.damageItem(1, player);
                }

                if (!player.capabilities.isCreativeMode && !hasInfinity && consumeAmmo) {
                    itemStack.shrink(1);

                    if (itemStack.isEmpty()) {
                        player.inventory.deleteStack(itemStack);
                    }
                }

                player.addStat(StatList.getObjectUseStats(this));
            }
        }
    }

    protected void customizeProjectile(BaseProjectileEntity projectile, EntityLivingBase entityLivingBase) {
        if(projectile == null) return;
        ItemStack itemStack = entityLivingBase.getActiveItemStack();

        ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(itemStack);
        if(storedCreatureInfo != null) {
            ConfigurableItemHandler.ItemStats stats = storedCreatureInfo.getOrCreateStats();
            if (stats.damage >= 0) projectile.setDamage((int) (projectile.damage * stats.damage / 2D));
            if (stats.pierce >= 0) projectile.pierce = (int) stats.pierce;
        }
    }

    @Override
    public int getItemEnchantability() {
        return ToolMaterial.GOLD.getEnchantability();
    }
}
