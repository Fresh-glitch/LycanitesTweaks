package lycanitestweaks.item;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.Variant;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.pets.PetEntry;
import lycanitestweaks.capability.storedcreatureinfo.ItemStoredCreatureInfo;
import lycanitestweaks.capability.toggleableitem.IToggleableItem;
import lycanitestweaks.capability.toggleableitem.ToggleableItem;
import lycanitestweaks.compat.ModLoadedUtil;
import lycanitestweaks.handlers.ForgeConfigHandler;
import lycanitestweaks.handlers.features.item.ConfigurableItemHandler;
import lycanitestweaks.item.base.ItemPassive;
import lycanitestweaks.item.interfaces.IItemWithCreatureInfo;
import lycanitestweaks.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ItemHellShield extends ItemPassive implements IItemWithCreatureInfo {

    public static final DamageSource LIFE_LINK = new DamageSource("lifeLink").setDamageBypassesArmor();

    private static final Cache<Entity, Pair<Integer, Double>> STATUS_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build();
    
    public ItemHellShield(String name) {
        super(name);
    }

    /**
     * Client -> Render Layer
     * Server -> Shield Effect
     * <p> 
     * int variant
     * double defense
     */
    public static Pair<Integer, Double> getShieldState(EntityLivingBase entityLivingBase) {
        Pair<Integer, Double> status = STATUS_CACHE.getIfPresent(entityLivingBase);
        return status == null ? DISABLED_SHIELD : status;
    }
    public static final Pair<Integer, Double> DISABLED_SHIELD = new Pair<>(0, 0D);

    @Override
    public boolean isEnabled() {
        return ForgeConfigHandler.server.customStaffConfig.registerSpecialBossDrops;
    }

    @Override
    public boolean hasSubscriber() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull ItemStack itemStack, World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag tooltipFlag) {
        super.addInformation(itemStack, world, tooltip, tooltipFlag);

        StringBuilder rawStrings = new StringBuilder();
        rawStrings.append(I18n.format("item.lycanitestweaks.hellshield.description"));

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
            rawStrings.append(I18n.format("lycanitestweaks.ability.hellshield.tooltip0")).append("\n");

            if(storedCreatureInfo != null) {
                ConfigurableItemHandler.ItemStats stats = storedCreatureInfo.getOrCreateStats();
                rawStrings.append(I18n.format("lycanitestweaks.ability.hellshield.tooltip1", stats.defense)).append("\n");
            }
        }
        else {
            rawStrings.append("\n").append(I18n.format("item.lycanitestweaks.tooltip.expand", "SHIFT"));
        }

        List<String> formattedDescriptionList = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(rawStrings.toString(), ItemBase.DESCRIPTION_WIDTH);
        tooltip.addAll(formattedDescriptionList);
    }

    @Override
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
        EntityEquipmentSlot equipmentSlot = super.getEquipmentSlot(stack);
        return equipmentSlot != null ? equipmentSlot : EntityEquipmentSlot.OFFHAND;
    }

    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == Items.NETHER_STAR) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }

    @Override
    public boolean isToggleable() {
        return true;
    }

    @Override
    public void tickAbility(ItemStack stack, EntityLivingBase entity) {
        super.tickAbility(stack, entity);

        IToggleableItem toggleableItem = ToggleableItem.getForItemStack(stack);
        if(toggleableItem != null) {
            if(toggleableItem.isAbilityToggled()) {
                ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(stack);
                if(storedCreatureInfo != null) {
                    ConfigurableItemHandler.ItemStats stats = storedCreatureInfo.getOrCreateStats();
                    int variant = storedCreatureInfo.getEntityVariant();
                    Pair<Integer, Double> shieldState = getShieldState(entity);
                    if(shieldState == DISABLED_SHIELD) {
                        shieldState = new Pair<>(variant, stats.defense);
                    }
                    else if(stats.defense > shieldState.getRight()) {
                        shieldState = new Pair<>(variant, stats.defense);
                    }
                    else if(variant > shieldState.getLeft()) {
                        shieldState = new Pair<>(variant, stats.defense);
                    }
                    STATUS_CACHE.put(entity, shieldState);
                }
            }
        }
    }


    // ==================================================
    //                    Item Use
    // ==================================================
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);

        if(!player.world.isRemote) {
            IToggleableItem toggleableItem = ToggleableItem.getForItemStack(itemStack);
            if(toggleableItem != null) {
                toggleableItem.toggleAbility(!toggleableItem.isAbilityToggled(), itemStack, player);
            }
        }

        return new ActionResult(EnumActionResult.SUCCESS, itemStack);
    }

    // Server
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        STATUS_CACHE.invalidate(event.getEntityLiving());
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event){
        if(event.isCanceled()) return;
        if(event.getSource() == LIFE_LINK) return;
        if(event.getSource().isUnblockable()) return;
        EntityLivingBase victim = event.getEntityLiving();

        List<EntityLivingBase> linkedEntities = new ArrayList<>();
        // Add player (victim) -> add bound pets
        if(victim instanceof EntityPlayer) {
            if(getShieldState(victim) == DISABLED_SHIELD) return;

            ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer((EntityPlayer) victim);
            if (extendedPlayer != null) {
                linkedEntities.add(victim);
                for (PetEntry petEntry : extendedPlayer.petManager.entries.values()) {
                    if (petEntry.spawningActive && petEntry.active) {
                        if (petEntry.entity instanceof EntityLivingBase && petEntry.entity.isEntityAlive()) {
                            linkedEntities.add((EntityLivingBase) petEntry.entity);
                        }
                    }
                }
            }
        }
        // Add owner -> add bound pets (includes victim)
        else if(victim instanceof BaseCreatureEntity) {
            BaseCreatureEntity creature = (BaseCreatureEntity) victim;
            if(creature.isBoundPet()) {
                PetEntry boundPet = creature.getPetEntry();
                if(boundPet.host instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) boundPet.host;
                    if(getShieldState(player) == DISABLED_SHIELD) return;

                    ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
                    if (extendedPlayer != null) {
                        linkedEntities.add(player);
                        for (PetEntry petEntry : extendedPlayer.petManager.entries.values()) {
                            if (petEntry.spawningActive && petEntry.active) {
                                if (petEntry.entity instanceof EntityLivingBase && petEntry.entity.isEntityAlive()) {
                                    linkedEntities.add((EntityLivingBase) petEntry.entity);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (linkedEntities.size() > 1) {
            // Divide between
            float lifeLinkDamage = event.getAmount() / linkedEntities.size();
            // Original damage source to victim
            event.setAmount(lifeLinkDamage);
            linkedEntities.remove(victim);
            // Life Link damage source to others
            linkedEntities.forEach(linkedEntity -> linkedEntity.attackEntityFrom(LIFE_LINK, lifeLinkDamage));
        }
    }

    // When Defence Attribute for all entities is not enabled
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if(ForgeConfigHandler.minorFeaturesConfig.lycanitesAttributesForAll) return;
        EntityLivingBase victim = event.getEntityLiving();
        if(getShieldState(victim) == DISABLED_SHIELD) return;

        if(event.getAmount() > 1F) {
            float damageReduction = 0F;
            float defense = getShieldState(victim).getRight().floatValue();
            if(victim.isActiveItemStackBlocking()) {
                defense = Math.max(1, defense);
                defense *= 4F;
            }
            damageReduction = Math.max(damageReduction, defense);
            event.setAmount(Math.max(1F, event.getAmount() - damageReduction));
        }
    }
}
