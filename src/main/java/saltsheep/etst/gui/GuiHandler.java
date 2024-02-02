package saltsheep.etst.gui;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import saltsheep.etst.EquipmentStrengthening;
import saltsheep.etst.data.ChainNode;
import saltsheep.etst.data.RecipeRegistry;

public class GuiHandler implements IGuiHandler {
	
	public static final int NORMAL_CRAFT = 0;
	public static final int CRAFT_VIEW = 1;
	public static final int CREATE = 2;
	public static final int GROUP_VIEW = 3;
	public static final int CHAIN_VIEW = 4;
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID==0)
			return new ETSTContainerRecipes(player);
		else if(ID==1) {
			ETSTContainerRecipes containerView = new ETSTContainerRecipes(player);
			containerView.isView=true;
			return containerView;
		}
		else if(ID==2)
			return new ETSTContainerRecipesCreate(player);
		else if(ID==3)
			return new ETSTContainerView(player, TEMP_GROUP_NAME, 3, ETSTContainerView.GROUP, TEMP_GROUP_DISPLAY);
		else if(ID==4)
			return new ETSTContainerView(player, TEMP_CHAIN_NAME, 6, ETSTContainerView.CHAIN, TEMP_CHAIN_DISPLAY);
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID==0)
			return new ETSTGuiContainerRecipes(new ETSTContainerRecipes(player));
		else if(ID==1) {
			ETSTContainerRecipes containerView = new ETSTContainerRecipes(player);
			containerView.isView=true;
			return new ETSTGuiContainerRecipesView(containerView);
		}
		else if(ID==2)
			return new ETSTGuiContainerRecipesCreate(new ETSTContainerRecipesCreate(player));
		else if(ID==3)
			return new ETSTGuiContainerView(player, TEMP_GROUP_NAME, 3, ETSTContainerView.GROUP);
		else if(ID==4)
			return new ETSTGuiContainerView(player, TEMP_CHAIN_NAME, 6, ETSTContainerView.CHAIN);
		return null;
	}
	
	private static String TEMP_GROUP_NAME = null;
	private static List<ChainNode> TEMP_GROUP_DISPLAY = null;
	private static String TEMP_CHAIN_NAME = null;
	private static List<ChainNode> TEMP_CHAIN_DISPLAY = null;
	
	public static synchronized void openGui(EntityPlayer player,int ID,Object... extraParams) {
		if(player==null)
			return;
		if(ID < CREATE)
			player.openGui(EquipmentStrengthening.instance, ID, player.world, 0, 0, 0);
		if(ID==CREATE) {
			player.openGui(EquipmentStrengthening.instance, ID, player.world, 0, 0, 0);
			if(extraParams.length>=3 && extraParams.length<=4 && extraParams[0] instanceof String && extraParams[1] instanceof Double && extraParams[2] instanceof String && (extraParams.length==3||(extraParams.length>=4&&extraParams[3] instanceof String))) {
				//*0-name;1-chance;2-group;3-up
				ETSTContainerRecipesCreate containerCreate = (ETSTContainerRecipesCreate) player.openContainer;
				containerCreate.recipeName = (String) extraParams[0];
				containerCreate.chance = (double) extraParams[1];
				containerCreate.groupName = (String) extraParams[2];
				if(extraParams.length>=4&&extraParams[3] instanceof String)
					containerCreate.up = (String) extraParams[3];
			}else
				EquipmentStrengthening.getLogger().warn("Warning!The params you input to create recipe is illegal.You must input (String)recipeName (double)chance (String)groupName [(String)up]");
		}else if(ID==GROUP_VIEW) {
			if(extraParams.length==1 && extraParams[0] instanceof String) {
				TEMP_GROUP_NAME = (String) extraParams[0];
				TEMP_GROUP_DISPLAY = RecipeRegistry.getGroup((String) extraParams[0]).getAllChainsRootNode();
				player.openGui(EquipmentStrengthening.instance, ID, player.world, 0, 0, 0);
			}else
				EquipmentStrengthening.getLogger().warn("Warning!The params you input to view the group's chains is illegal.You must input (String)groupName");
		}else if(ID==CHAIN_VIEW) {
			if(extraParams.length==2 && extraParams[0] instanceof String && extraParams[1] instanceof String) {
				TEMP_CHAIN_NAME = (String) extraParams[1];
				if(RecipeRegistry.getRecipe((String) extraParams[0])!=null) {
					TEMP_CHAIN_DISPLAY = RecipeRegistry.getRecipe((String) extraParams[0]).getNode().getAllChainNodeByBFS();
				}else {
					EquipmentStrengthening.getLogger().warn("Warning!You want to view an unknown chain!How can that be?Is you use my method illegal?Plz check saltsheep.etst.gui.GuiHandler.openGui.");
					return;
				}player.openGui(EquipmentStrengthening.instance, ID, player.world, 0, 0, 0);
			}else
				EquipmentStrengthening.getLogger().warn("Warning!The params you input to view the group's chains is illegal.You must input (String)chainRootName (String)showName");
		}
	}

}
