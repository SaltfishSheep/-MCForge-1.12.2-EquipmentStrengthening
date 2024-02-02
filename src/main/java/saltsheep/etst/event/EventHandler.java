package saltsheep.etst.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import saltsheep.etst.EquipmentStrengthening;
import saltsheep.etst.data.EquipmentSaveData;
import saltsheep.etst.gui.NetworkForRecipe;

@Mod.EventBusSubscriber
public class EventHandler {

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		if(!event.getWorld().isRemote)
			EquipmentSaveData.getData();
	}
	
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		try {
			if(!event.player.world.isRemote) {
				NetworkForRecipe.giveCraftChanceByServer((EntityPlayerMP) event.player);
			}
		}catch(Throwable error) {
			EquipmentStrengthening.printError(error);
		}
	}
	
}
