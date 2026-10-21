package lycanitestweaks.item;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.PortalEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.item.temp.ItemStaffSummoning;
import lycanitestweaks.LycanitesTweaks;
import lycanitestweaks.capability.storedcreatureinfo.IStoredCreatureInfo;
import lycanitestweaks.capability.storedcreatureinfo.ItemStoredCreatureInfo;
import lycanitestweaks.capability.storedcreatureinfo.StoredCreatureInfoHandler;
import lycanitestweaks.item.interfaces.IItemWithCreatureInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCreatureInfoStaff extends ItemStaffSummoning implements IItemWithCreatureInfo {

    // Used to pass to Vanilla Lycanites methods without an ItemStack param
    protected ItemStoredCreatureInfo storedCreatureInfoRef = null;

    public ItemCreatureInfoStaff(String itemName, String textureName) {
        super(itemName, textureName);

        this.setRegistryName(LycanitesTweaks.MODID, this.itemName);
        this.setTranslationKey(LycanitesTweaks.MODID + "." + this.itemName);
    }

    @Override
    public void setup() {}

    @SideOnly(Side.CLIENT)
    @Override
    public String getDescription(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(stack);
        if(storedCreatureInfo != null) {
            if(storedCreatureInfo.getCreatureTypeName().isEmpty()) return I18n.format("item.lycanitestweaks.creatureinfostaff.null");
            return I18n.format(this.getTranslationKey() + ".description", storedCreatureInfo.getLevel(), storedCreatureInfo.getCreatureTypeName());
        }
        return I18n.format("item.lycanitestweaks.creatureinfostaff.null");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // For creative use and NBT reference
        if(playerIn.capabilities.isCreativeMode && playerIn.isSneaking()) {
            ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(playerIn.getHeldItem(hand));
            if(storedCreatureInfo != null && storedCreatureInfo.getCreatureTypeName().isEmpty()) {
                storedCreatureInfo.setCreatureTypeName("kobold");
                storedCreatureInfo.setCustomName("Krill Issue");
                storedCreatureInfo.setLevel(0);
                storedCreatureInfo.setEntitySubspecies(1);
                storedCreatureInfo.setEntityVariant(2);
            }
        }
        return super.onItemUse(playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    // ========== Minion Behaviour ==========
    @Override
    public void applyMinionBehaviour(TameableCreatureEntity minion, EntityPlayer player) {
        super.applyMinionBehaviour(minion, player);
        if(this.storedCreatureInfoRef != null) {
            if(!this.storedCreatureInfoRef.getCustomName().isEmpty()) minion.setCustomNameTag(this.storedCreatureInfoRef.getCustomName());
            minion.setSubspecies(this.storedCreatureInfoRef.getEntitySubspecies());
            minion.setVariant(this.storedCreatureInfoRef.getEntityVariant());
            if(minion.isRareVariant()) minion.setVariant(0); // Only Challenge Soul should summon
            minion.addLevel(this.storedCreatureInfoRef.getLevel() - 1);
        }
        minion.getRandomSize();
    }


    // ==================================================
    //                      Attack
    // ==================================================
    // ========== Start ==========
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (!CreatureManager.getInstance().config.isSummoningAllowed(world)) {
            return new ActionResult<>(EnumActionResult.FAIL, itemStack);
        }
        ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
        if (playerExt != null) {
            ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(itemStack);
            if(storedCreatureInfo != null) {
                // Summon Selected Mob:
                CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(storedCreatureInfo.getCreatureTypeName());
                if (creatureInfo != null) {
                    if (!player.getEntityWorld().isRemote) {
                        playerExt.staffPortal = new PortalEntity(world, player, creatureInfo.getEntityClass(), this);
                        playerExt.staffPortal.setLocationAndAngles(player.posX, player.posY, player.posZ, world.rand.nextFloat() * 360.0F, 0.0F);
                        world.spawnEntity(playerExt.staffPortal);
                    }
                }
                else {
                    return new ActionResult<>(EnumActionResult.FAIL, itemStack);
                }
            }
        }
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }

    // ========== Charged ==========
    @Override
    public boolean chargedAttack(ItemStack itemStack, World world, EntityLivingBase entity, float power) {
        // Reference to pass to methods without an ItemStack param
        ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(itemStack);
        if(storedCreatureInfo != null) {
            this.storedCreatureInfoRef = storedCreatureInfo;
        }
        boolean success = super.chargedAttack(itemStack, world, entity, power);
        if(success && entity instanceof EntityPlayer){
            // Consume Soulstone:
            if (!((EntityPlayer) entity).capabilities.isCreativeMode && this.getMaxDamage(itemStack) == 1)
                itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
        }
        return success;
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
