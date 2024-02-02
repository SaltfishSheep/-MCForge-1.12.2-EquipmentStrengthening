package saltsheep.etst;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import saltsheep.etst.data.EquipmentSaveData;

@EventBusSubscriber
public class InitWorldData {

	@SubscribeEvent
	public static void onWorldOpen(WorldEvent.Load event) {
		EquipmentSaveData.getData();
	}
	
}
