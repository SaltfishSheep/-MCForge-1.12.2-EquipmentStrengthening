package saltsheep.etst;

import org.apache.logging.log4j.Logger;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import saltsheep.etst.command.CommandManager;
import saltsheep.etst.gui.GuiHandler;
import saltsheep.etst.gui.NetworkForRecipe;
import saltsheep.lib.SheepLib;

@Mod(modid = EquipmentStrengthening.MODID, name = EquipmentStrengthening.NAME, version = EquipmentStrengthening.VERSION, useMetadata = true, dependencies="required-after:sheeplib;")
public class EquipmentStrengthening
{
    public static final String MODID = "equipmentstrengthening";
    public static final String NAME = "EquipmentStrengthening";
    public static final String VERSION = "1.0";
    public static EquipmentStrengthening instance;

    private static Logger logger;

    public EquipmentStrengthening() {
    	instance = this;
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        logger = event.getModLog();
        NetworkForRecipe.register();
        new SheepLib();
    }

    @EventHandler
    public void init(FMLInitializationEvent event){
    	NetworkRegistry.INSTANCE.registerGuiHandler(EquipmentStrengthening.instance, new GuiHandler());
    }
    
    @EventHandler
    public static void onServerStarting(FMLServerStartingEvent event){
		CommandManager.register(event);
	}
    
    public static Logger getLogger() {
    	return logger;
    }
    
    public static MinecraftServer getMCServer() {
    	return FMLCommonHandler.instance().getMinecraftServerInstance();
    }
    
    public static void printError(Throwable error) {
    	String messages = "";
    	for(StackTraceElement stackTrace : error.getStackTrace()) {
    		messages = messages+stackTrace.toString()+"\n";
		}
    	EquipmentStrengthening.getLogger().error("警告！在咸羊我的mod里出现了一些错误，信息如下：\n"+messages+"出现错误类型:"+error.getClass()+"-"+error.getMessage());
    }
    
    public static void info(String str) {
    	logger.info(str);
    }
    
    public static void info(Object obj) {
    	if(obj == null)
    		logger.info("null has such obj.");
    	else
    		logger.info(obj.toString());
    }
    
    
}
