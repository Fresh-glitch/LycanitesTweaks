package lycanitestweaks.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import lycanitestweaks.LycanitesTweaks;
import lycanitestweaks.capability.storedcreatureinfo.ItemStoredCreatureInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

import java.util.Random;

/*

    Alternative Loot Function to creature json config

    "functions": [
        {
            "function": "lycanitestweaks:item_with_creature_info"
        }
    ]

 */
public class ItemWithCreatureInfo extends LootFunction {

    public ItemWithCreatureInfo(LootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
        if(context.getLootedEntity() instanceof BaseCreatureEntity) {
            ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(stack);
            if(storedCreatureInfo != null) {
                storedCreatureInfo.setAllFromCreature((BaseCreatureEntity) context.getLootedEntity());
            }
        }

        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<ItemWithCreatureInfo> {

        public Serializer() {
            super(new ResourceLocation(LycanitesTweaks.MODID + ":" + "item_with_creature_info"), ItemWithCreatureInfo.class);
        }

        public void serialize(JsonObject object, ItemWithCreatureInfo functionClazz, JsonSerializationContext serializationContext) {

        }

        public ItemWithCreatureInfo deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
            return new ItemWithCreatureInfo(conditionsIn);
        }
    }
}
