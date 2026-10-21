package lycanitestweaks.compat;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.lycanitesmobs.ObjectManager;
import lycanitestweaks.capability.toggleableitem.IToggleableItem;
import lycanitestweaks.capability.toggleableitem.ToggleableItem;
import lycanitestweaks.item.interfaces.IAttributeBauble;
import lycanitestweaks.network.PacketHandler;
import lycanitestweaks.network.PacketToggleableBauble;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class BaublesHandler {

    public static boolean hasSoulgazerBauble(EntityPlayer player){
        int baubleFound = BaublesApi.isBaubleEquipped(player, ObjectManager.getItem("soulgazer"));
        return (baubleFound != -1);
    }

    public static boolean hasItemAsBauble(EntityPlayer player, Item item){
        int baubleFound = BaublesApi.isBaubleEquipped(player, item);
        return (baubleFound != -1);
    }

    public static int getBaubleCount(EntityPlayer player, Item item) {
        int count = 0;

        IBaublesItemHandler baubleItems = BaublesApi.getBaublesHandler(player);
        if(baubleItems != null) {
            for (int i = 0; i < baubleItems.getSlots(); i++) {
                ItemStack bauble = baubleItems.getStackInSlot(i);
                if(bauble.getItem() == item)
                    count++;
            }
        }

        return count;
    }

    public static List<ItemStack> getAttributeBaubles(EntityPlayer player) {
        List<ItemStack> list = new ArrayList<>();
        IBaublesItemHandler baubleItems = BaublesApi.getBaublesHandler(player);
        if(baubleItems != null) {
            for (int i = 0; i < baubleItems.getSlots(); i++) {
                ItemStack bauble = baubleItems.getStackInSlot(i);
                if(bauble.getItem() instanceof IAttributeBauble)
                    list.add(bauble);
            }
        }
        return list;
    }

    // Order should be (Keybind) -> Server -> Sync -> Client
    public static void toggleBaublePassive(EntityPlayer player, int slot) {
        if(player.world.isRemote) return;

        IBaublesItemHandler baubleItems = BaublesApi.getBaublesHandler(player);
        if(baubleItems != null) {
            int count = 0;
            if(slot == -1) {
                boolean toggle = false;
                for (int i = 0; i < baubleItems.getSlots(); i++) {
                    ItemStack bauble = baubleItems.getStackInSlot(i);
                    IToggleableItem toggleableItem = ToggleableItem.getForItemStack(bauble);
                    if(toggleableItem != null) {
                        count++;
                        if(count == 1) toggle = !toggleableItem.isAbilityToggled();
                        toggleableItem.toggleAbility(toggle, bauble, player);
                        syncBauble(player, i, toggle);
                    }
                }
                player.sendStatusMessage(new TextComponentTranslation("message.bauble.toggle.all", String.valueOf(count), toggle ? "I" : "O"), true);
            }
            else {
                ItemStack bauble = baubleItems.getStackInSlot(slot);
                IToggleableItem toggleableItem = ToggleableItem.getForItemStack(bauble);
                if(toggleableItem != null) {
                    toggleableItem.toggleAbility(!toggleableItem.isAbilityToggled(), bauble, player);
                    syncBauble(player, slot, toggleableItem.isAbilityToggled());
                    player.sendStatusMessage(new TextComponentTranslation("message.bauble.toggle.specific", toggleableItem.isAbilityToggled() ? "I" : "O", bauble.getDisplayName()), true);
                }
            }
        }
    }

    public static void setClientToggle(EntityPlayer player, int slot, boolean toggle) {
        if(!player.world.isRemote) return;
        if(slot < 0) return;

        IBaublesItemHandler baubleItems = BaublesApi.getBaublesHandler(player);
        if(baubleItems != null && slot < baubleItems.getSlots()) {
            ItemStack bauble = baubleItems.getStackInSlot(slot);
            IToggleableItem toggleableItem = ToggleableItem.getForItemStack(bauble);
            if(toggleableItem != null) {
                toggleableItem.toggleAbility(toggle);
            }
        }
    }

    private static void syncBauble(EntityPlayer player, int slot, boolean toggle) {
        PacketToggleableBauble packet = new PacketToggleableBauble(slot, toggle);
        if(player instanceof EntityPlayerMP) PacketHandler.instance.sendTo(packet, (EntityPlayerMP) player);
        PacketHandler.instance.sendToAllTracking(packet, player);
    }

    // Catch anything that doesn't fire onUnequipped, I wish it didn't have to be this way
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if(event.getEntityLiving().world.isRemote) return;

        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            // Can afford a slower cycle as primarily handled by onEquipped and onUnequipped
            if (player.ticksExisted % 40 == 0) {
                Multimap<String, AttributeModifier> modifiersToRemove = HashMultimap.create();
                for(IAttributeInstance attribute : player.getAttributeMap().getAllAttributes()) {
                    for(AttributeModifier modifier : attribute.getModifiers()) {
                        if (modifier.getName().equals(IAttributeBauble.BAUBLE_MODIFIER_NAME)) {
                            modifiersToRemove.put(attribute.getAttribute().getName(), modifier);
                        }
                    }
                }
                player.getAttributeMap().removeAttributeModifiers(modifiersToRemove);

                IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
                if(baubles != null) {
                    for (int i = 0; i < baubles.getSlots(); i++) {
                        ItemStack bauble = baubles.getStackInSlot(i);
                        if(bauble.getItem() instanceof IAttributeBauble) {
                            ((IAttributeBauble) bauble.getItem()).onEquipped(bauble, player);
                        }
                    }
                }
            }
        }
    }
}
