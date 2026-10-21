package lycanitestweaks.item;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.info.ItemConfig;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.pets.SummonSet;
import lycanitestweaks.capability.storedcreatureinfo.ItemStoredCreatureInfo;
import lycanitestweaks.capability.toggleableitem.IToggleableItem;
import lycanitestweaks.capability.toggleableitem.ToggleableItem;
import lycanitestweaks.handlers.ForgeConfigHandler;
import lycanitestweaks.item.base.ItemPassive;
import lycanitestweaks.item.interfaces.IItemWithCreatureInfo;
import lycanitestweaks.mixin.vanilla.Entity_AccessorMixin;
import lycanitestweaks.util.Helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ItemVileMatter extends ItemPassive implements IItemWithCreatureInfo {

    private static final Cache<ItemStoredCreatureInfo, Status> STATUS_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    private static class Status {
        public boolean burning = false;
        public final Collection<ElementInfo> ELEMENTS = new ArrayList<>();
        public final Collection<Potion> POTIONS = new ArrayList<>();
    }

    public ItemVileMatter(String name) {
        super(name);
        this.addPropertyOverride(new ResourceLocation("active"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack itemStack, World world, EntityLivingBase entity) {
                IToggleableItem toggleableItem = ToggleableItem.getForItemStack(itemStack);
                if(toggleableItem != null && toggleableItem.isAbilityToggled()) {
                    return 1.0F;
                }
                return 0.0F;
            }
        });
    }

    @Override
    public boolean isEnabled() {
        return ForgeConfigHandler.server.customStaffConfig.registerVileMatter;
    }

    @Override
    public boolean hasSubscriber() {
        return true;
    }

    @Override
    public boolean isToggleable() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack,  World world, List<String> tooltip, ITooltipFlag tooltipFlag) {
        super.addInformation(itemStack, world, tooltip, tooltipFlag);

        boolean beastiary = ForgeConfigHandler.clientFeaturesMixinConfig.beastiaryGUILT;
        String storedCreature = "";
        StringBuilder rawStrings = new StringBuilder();
        rawStrings.append(I18n.format("item.lycanitestweaks.vilematter.description"));
        rawStrings.append("\n").append(I18n.format("lycanitestweaks.ability.vileaura.tooltip0"));

        ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(itemStack);
        if(storedCreatureInfo != null) {
            storedCreature = storedCreatureInfo.getCreatureTypeName();
        }

        if(CreatureManager.getInstance().getCreature(storedCreature) != null) {
            storedCreature = CreatureManager.getInstance().getCreature(storedCreature).getTitle();
        }

        if(storedCreature.isEmpty()) {
            if(beastiary)
                rawStrings.append("\n").append(I18n.format("item.lycanitestweaks.vilematter.vileaura.beastiary"));
            else
                rawStrings.append("\n").append(I18n.format("item.lycanitestweaks.vilematter.vileaura.summon"));
        }
        else {
            rawStrings.append("\n").append(I18n.format("lycanitestweaks.ability.vileaura.tooltip1", storedCreature));
            if(GuiScreen.isShiftKeyDown()) {
                if(beastiary)
                    rawStrings.append("\n").append(I18n.format("item.lycanitestweaks.vilematter.vileaura.beastiary"));
                else
                    rawStrings.append("\n").append(I18n.format("item.lycanitestweaks.vilematter.vileaura.summon"));
            }
            else {
                rawStrings.append("\n").append(I18n.format("item.lycanitestweaks.tooltip.expand", "SHIFT"));
            }
        }

        List<String> formattedDescriptionList = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(rawStrings.toString(), ItemBase.DESCRIPTION_WIDTH);
        tooltip.addAll(formattedDescriptionList);
    }

    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(ForgeConfigHandler.server.customStaffConfig.vileMatterRepairables) {
            ResourceLocation resourceLocation = repairStack.getItem().getRegistryName();
            if (resourceLocation != null && ItemConfig.mediumEquipmentManaItems.contains(repairStack.getItem().getRegistryName().toString())) {
                return true;
            }
        }
        return super.getIsRepairable(itemStack, repairStack);
    }

    @Override
    public void tickAbility(ItemStack stack, EntityLivingBase entity) {
        super.tickAbility(stack, entity);
        if (entity.world.isRemote) return;

        // Tick Rate
        if(entity.ticksExisted % ForgeConfigHandler.server.customStaffConfig.vileAuraTickRate != 0) {
            return;
        }

        ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(stack);
        if(storedCreatureInfo == null) return;

        // Check toggle
        IToggleableItem toggleableItem = ToggleableItem.getForItemStack(stack);
        if(toggleableItem == null) return;
        if(!toggleableItem.isAbilityToggled()) {
            STATUS_CACHE.invalidate(storedCreatureInfo);
            return;
        }

        Status status = STATUS_CACHE.getIfPresent(storedCreatureInfo);
        if(status == null) {
            CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(storedCreatureInfo.getCreatureTypeName());
            if (creatureInfo != null) {
                int subspecies = storedCreatureInfo.getEntitySubspecies();
                List<ElementInfo> elementInfos = (subspecies == 0)
                        ? creatureInfo.elements
                        : creatureInfo.getSubspecies(subspecies).elements;
                this.setDebuffs(elementInfos, storedCreatureInfo);
                status = STATUS_CACHE.getIfPresent(storedCreatureInfo);
            }
        }

        if(status == null) return;

        if(status.ELEMENTS.isEmpty()) return;

        // Get Range
        double range = entity instanceof EntityPlayer
                ? Helpers.getPlayerInteractionReach((EntityPlayer) entity, EnumHand.MAIN_HAND) * ForgeConfigHandler.server.customStaffConfig.vileAuraRangePlayer
                : ForgeConfigHandler.server.customStaffConfig.vileAuraRangeOther;
        List<EntityLivingBase> aoeTargets = entity.getEntityWorld().getEntitiesWithinAABB(
                EntityLivingBase.class,
                entity.getEntityBoundingBox().grow(range),
                target -> target != entity && entity.canEntityBeSeen(target)
        );

        // Spread Burning
        if (status.burning && entity.isBurning() && entity instanceof Entity_AccessorMixin) {
            aoeTargets.forEach(entityLivingBase -> entityLivingBase.setFire(((Entity_AccessorMixin) entity).lycanitesTweaks$getFireTicks() / 20));
        }

        // Spread Debuffs
        status.POTIONS.stream().filter(entity::isPotionActive).forEach(potion -> {
            PotionEffect potionEffect = entity.getActivePotionEffect(potion);
            aoeTargets.forEach(target -> {
                // Try not to reset cycle dependent potions
                if(!target.isPotionActive(potion) || target.getActivePotionEffect(potion).getAmplifier() < potionEffect.getAmplifier())
                    target.addPotionEffect(new PotionEffect(potionEffect));
            });
        });

        // Self Debuff
        status.ELEMENTS.forEach(elementInfo ->
                elementInfo.debuffEntity(entity, 20 * ForgeConfigHandler.server.customStaffConfig.vileAuraDuration, 0));
    }

    // ==================================================
    //                    Item Use
    // ==================================================
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);

        if(!player.world.isRemote) {
            if(player.isSneaking()) {
                this.setDebuffsFromPlayer(player, itemStack);
            }
            else {
                IToggleableItem toggleableItem = ToggleableItem.getForItemStack(itemStack);
                if(toggleableItem != null) {
                    toggleableItem.toggleAbility(!toggleableItem.isAbilityToggled(), itemStack, player);
                }
            }
        }

        return new ActionResult(EnumActionResult.SUCCESS, itemStack);
    }

    private void setDebuffs(Collection<ElementInfo> elementInfos, ItemStoredCreatureInfo storedCreatureInfo) {
        if(storedCreatureInfo == null) return;

        Status status = STATUS_CACHE.getIfPresent(storedCreatureInfo);
        if(status == null) {
            status = new Status();
            STATUS_CACHE.put(storedCreatureInfo, status);
        }

        status.ELEMENTS.clear();
        status.ELEMENTS.addAll(elementInfos);

        status.burning = false;
        status.POTIONS.clear();
        for(ElementInfo elementInfo : elementInfos) {
            for(String debuff : elementInfo.debuffs) {
                if(debuff.equalsIgnoreCase("burning")) {
                    status.burning = true;
                }
                else {
                    Potion potion = GameRegistry.findRegistry(Potion.class).getValue(new ResourceLocation(debuff));
                    if (potion != null && !status.POTIONS.contains(potion)) status.POTIONS.add(potion);
                }
            }
        }
    }

    private void setDebuffsFromPlayer(EntityPlayer player, ItemStack itemStack) {
        ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
        if (playerExt != null) {
            String title = "";
            ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(itemStack);
            if(storedCreatureInfo != null) {
                CreatureInfo creatureInfo = playerExt.selectedCreature;
                if(creatureInfo != null) {
                    if((creatureInfo.isTameable() || creatureInfo.isSummonable() || player.isCreative())
                            && playerExt.getBeastiary().hasKnowledgeRank(creatureInfo.getName(), 2)) {
                        List<ElementInfo> elementInfos = (playerExt.selectedSubspecies == 0)
                                ? creatureInfo.elements
                                : creatureInfo.getSubspecies(playerExt.selectedSubspecies).elements;
                        this.setDebuffs(elementInfos, storedCreatureInfo);
                        storedCreatureInfo.setCreatureTypeName(creatureInfo.getName());
                        storedCreatureInfo.setEntitySubspecies(playerExt.selectedSubspecies);
                        title = creatureInfo.getTitle();
                    }
                }

                // Fallback if Client doesn't update Server
                String storedCreatureType = storedCreatureInfo.getCreatureTypeName();
                if(storedCreatureType.isEmpty()) {
                    SummonSet summonSet = playerExt.getSelectedSummonSet();
                    creatureInfo = summonSet.getCreatureInfo();
                    if (summonSet.isUseable() && !summonSet.summonType.equals(storedCreatureType)) {
                        this.setDebuffs(summonSet.getCreatureInfo().elements, storedCreatureInfo);
                        storedCreatureInfo.setCreatureTypeName(summonSet.summonType);
                        storedCreatureInfo.setEntitySubspecies(summonSet.subspecies);
                        title = creatureInfo.getTitle();
                    }
                }
            }

            if(title.isEmpty()) {
                player.sendStatusMessage(new TextComponentTranslation("message.passiveability.vileaura.linkfail"), true);
            }
            else {
                player.sendStatusMessage(new TextComponentTranslation("message.passiveability.vileaura.linkset", title), true);
            }
        }
    }
}
