package lycanitestweaks.handlers.features.entity;

import com.lycanitesmobs.core.spawner.Spawner;
import com.lycanitesmobs.core.spawner.SpawnerManager;
import com.lycanitesmobs.core.spawner.trigger.PlayerSpawnTrigger;
import com.lycanitesmobs.core.spawner.trigger.SpawnTrigger;
import lycanitestweaks.mixin.lycanitesmobspatches.core.spawner.PlayerSpawnTrigger_AccessorMixin;
import lycanitestweaks.mixin.lycanitesmobspatches.core.spawner.SpawnTrigger_AccessorMixin;
import lycanitestweaks.mixin.lycanitesmobspatches.core.spawner.Spawner_AccessorMixin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpawnerPlayerLeakHandler {

    // same entity id, so only the key object changes
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event){
        for(Map<EntityPlayer, ?> map : getPlayerMaps()) replaceKey(map, event.player);
    }

    // a relog gets a new entity id
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event){
        for(Map<EntityPlayer, ?> map : getPlayerMaps()) map.remove(event.player);
    }

    // fake players never log out
    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event){
        if(event.getWorld().isRemote) return;
        for(Map<EntityPlayer, ?> map : getPlayerMaps()) map.keySet().removeIf(player -> player.world == event.getWorld());
    }

    private static <V> void replaceKey(Map<EntityPlayer, V> map, EntityPlayer player){
        V value = map.remove(player);
        if(value != null) map.put(player, value);
    }

    private static List<Map<EntityPlayer, ?>> getPlayerMaps(){
        List<Map<EntityPlayer, ?>> maps = new ArrayList<>();
        for(Spawner spawner : SpawnerManager.getInstance().spawners.values()){
            maps.add(((Spawner_AccessorMixin) spawner).lycanitesTweaks$getTriggerCounts());
            for(SpawnTrigger trigger : spawner.triggers){
                maps.add(((SpawnTrigger_AccessorMixin) trigger).lycanitesTweaks$getPlayerUsedTicks());
                if(trigger instanceof PlayerSpawnTrigger) maps.add(((PlayerSpawnTrigger_AccessorMixin) trigger).lycanitesTweaks$getLastTickPositions());
            }
        }
        return maps;
    }
}
