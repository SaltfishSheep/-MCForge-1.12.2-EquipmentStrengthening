package saltsheep.etst.command;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class CommandManager {

	public static void register(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandETST());
	}

}
