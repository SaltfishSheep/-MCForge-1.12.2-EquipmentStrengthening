package saltsheep.etst.command;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import saltsheep.etst.EquipmentStrengthening;
import saltsheep.etst.data.ChainNode;
import saltsheep.etst.data.RecipeRegistry;
import saltsheep.etst.gui.GuiHandler;
import saltsheep.lib.command.CommandFather;
import saltsheep.lib.command.CommandSon;
import saltsheep.lib.common.BaseType;
import saltsheep.lib.exception.InformationUnsameException;
import saltsheep.lib.exception.InputIllegalException;

public class CommandETST extends CommandFather {

	@Override
	public String getName() {
		return "etst";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "§c/etst open (可选)<玩家名> 打开强化界面\n§c/etst create <配方名> <几率(1=100%)> <从属组名> (可选)<父配方> 创建配方\n§c/etst delete <配方名>\n§c/etst view <组名> (可选)玩家名 预览某组下的配方\n§c/etst listg <组名> 列出组内所有根节点（首配方）\n§c/etst listc <配方名> 列出配方即其所有的子配方";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		this.executeIn(server, sender, args);
	}

	@Override
	protected void initCommandSons() {
		this.sons = Lists.newLinkedList();
		try {
			this.sons.add(new CommandSon(true, 1, new String[] {"open"}, new BaseType[0], 
				(objs)->GuiHandler.openGui((EntityPlayer)objs[0], GuiHandler.NORMAL_CRAFT))
			);
			this.sons.add(new CommandSon(false, 2, new String[] {"open"}, new BaseType[] {BaseType.STRING}, 
				(objs)->GuiHandler.openGui(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername((String) objs[1]), GuiHandler.NORMAL_CRAFT))
			);
			this.sons.add(new CommandSon(true, 4, new String[] {"create"}, new BaseType[] {BaseType.STRING,BaseType.DOUBLE,BaseType.STRING}, 
				(objs)->{
					if(!RecipeRegistry.hasRecipe((String) objs[1]))
						GuiHandler.openGui((EntityPlayer)objs[0], GuiHandler.CREATE,objs[1],objs[2],objs[3]);
					else
						((ICommandSender)objs[0]).sendMessage(new TextComponentString("Warning!You have already register this recipe."));
				}
			));
			this.sons.add(new CommandSon(true, 5, new String[] {"create"}, new BaseType[] {BaseType.STRING,BaseType.DOUBLE,BaseType.STRING,BaseType.STRING}, 
				(objs)->{
					if(!RecipeRegistry.hasRecipe((String) objs[1]))
						GuiHandler.openGui((EntityPlayer)objs[0], GuiHandler.CREATE,objs[1],objs[2],objs[3],objs[4]);
					else
						((ICommandSender)objs[0]).sendMessage(new TextComponentString("Warning!You have already register this recipe."));
				}
			));
			this.sons.add(new CommandSon(false, 2, new String[] {"delete"}, new BaseType[] {BaseType.STRING}, 
				(objs)->{
					if(RecipeRegistry.deleteRecipe((String) objs[1]))
						((ICommandSender)objs[0]).sendMessage(new TextComponentString("Delete recipe successful."));
					else
						((ICommandSender)objs[0]).sendMessage(new TextComponentString("Warning!There's no such recipe"));
				}
			));
			this.sons.add(new CommandSon(true, 2, new String[] {"view"}, new BaseType[] {BaseType.STRING}, 
				(objs)->GuiHandler.openGui((EntityPlayer)objs[0], GuiHandler.GROUP_VIEW, objs[1]))
			);
			this.sons.add(new CommandSon(false, 3, new String[] {"view"}, new BaseType[] {BaseType.STRING,BaseType.STRING}, 
				(objs)->GuiHandler.openGui(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername((String) objs[2]), GuiHandler.GROUP_VIEW, objs[1]))
			);
			this.sons.add(new CommandSon(false, 2, new String[] {"listg"}, new BaseType[] {BaseType.STRING}, 
				(objs)->{
					StringBuilder builder = new StringBuilder();
					for(ChainNode eachRecipe:RecipeRegistry.getGroup((String) objs[1]).getAllChainsRootNode()) {
						builder.append(eachRecipe.getRecipe().getRecipeName());
						builder.append(',');
					}
					((ICommandSender)objs[0]).sendMessage(new TextComponentString(builder.toString()));
				}
			));
			this.sons.add(new CommandSon(false, 2, new String[] {"listc"}, new BaseType[] {BaseType.STRING}, 
				(objs)->{
					StringBuilder builder = new StringBuilder();
					for(ChainNode eachRecipe:RecipeRegistry.getRecipe((String) objs[1]).getNode().getSonNodesContainsItselfByBFS()) {
						builder.append(eachRecipe.getRecipe().getRecipeName());
						builder.append(',');
					}
					((ICommandSender)objs[0]).sendMessage(new TextComponentString(builder.toString()));
				}
			));
		} catch (InformationUnsameException | InputIllegalException e) {
			EquipmentStrengthening.printError(e);
		}
	}

}
