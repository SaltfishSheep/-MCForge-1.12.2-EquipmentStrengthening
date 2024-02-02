package saltsheep.etst.gui;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import saltsheep.etst.EquipmentStrengthening;
import saltsheep.etst.data.StrengtheningRecipe;
import saltsheep.lib.list.ListHelper;

public class NetworkForRecipe {
	
	private static final String NAME = "ETSTDATA";
	private static final FMLEventChannel CHANNEL = NetworkRegistry.INSTANCE.newEventDrivenChannel(NAME);
	
	public static final int CLIENT_CRAFTAPPLY = 0;
	public static final int CLIENT_FINALAPPLY = 1;
	public static final int CLIENT_CREATE = 2;
	
	public static final int CLIENT_VIEW_CLICK_SLOT = 3;
	
	public static final int CLIENT_BACK_CHAIN = 4;
	
	public static final int SERVER_ANIMATION = 0;
	public static final int SERVER_CRAFT_CHANCE = 1;

	public static void register() {
		CHANNEL.register(NetworkForRecipe.class);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void getPacketByClient(FMLNetworkEvent.ClientCustomPacketEvent event) {
		if(event.getPacket().channel().equals(NAME)) {
			ByteBuf buf = event.getPacket().payload();
			int type = buf.readInt();
			if(type == SERVER_ANIMATION) 
				NetworkForRecipe.playCraftAnimationByClient(event);
			else if(type == SERVER_CRAFT_CHANCE)
				NetworkForRecipe.setCraftChanceByClient(event);
		}
	}
	
	@SubscribeEvent
	public static void getPacketByServer(FMLNetworkEvent.ServerCustomPacketEvent event) {
		if(event.getPacket().channel().equals(NAME)) {
			ByteBuf buf = event.getPacket().payload();
			int type = buf.readInt();
			if(type == CLIENT_CRAFTAPPLY) 
				NetworkForRecipe.getCraftApplyByServer(event);
			else if(type == CLIENT_FINALAPPLY)
				NetworkForRecipe.finalApplyByServer(event);
			else if(type == CLIENT_CREATE)
				NetworkForRecipe.createRecipeByServer(event);
			else if(type == CLIENT_VIEW_CLICK_SLOT)
				NetworkForRecipe.clickViewSlotByServer(event);
			else if(type == CLIENT_BACK_CHAIN)
				NetworkForRecipe.backToChainByServer(event);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void craftByClient() {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		buf.writeInt(0);
		char[] nameToArray = Minecraft.getMinecraft().player.getName().toCharArray();
		buf.writeInt(nameToArray.length);
		for(char each : nameToArray)
			buf.writeChar(each);
		//*写入玩家名称
		CHANNEL.sendToServer(new FMLProxyPacket(buf,NAME));
	}
	
	private static void getCraftApplyByServer(FMLNetworkEvent.ServerCustomPacketEvent event) {
		MinecraftServer MCServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		ByteBuf buf = event.getPacket().payload();
		char[] nameAsArray = new char[buf.readInt()];
		for(int each = 0;each < nameAsArray.length;each++)
			nameAsArray[each] = buf.readChar();
		String playerName = new String(nameAsArray);
		EntityPlayerMP player = MCServer.getPlayerList().getPlayerByUsername(playerName);
		if(player != null&&player instanceof EntityPlayerMP) {
			MCServer.addScheduledTask(()->{
				try {
					if(player.openContainer!=null&&player.openContainer instanceof ETSTContainerRecipes) {
						ETSTContainerRecipes container = ((ETSTContainerRecipes) player.openContainer);
						if(!container.isApplying) {
							container.isApplying = true;
							if(container.apply())
								playCraftAnimationByServer(player);
							else
								container.isApplying = false;
						}
					}
				}catch(Throwable error) {
					EquipmentStrengthening.printError(error);
				}
			});
		}
	}
	
	private static void playCraftAnimationByServer(EntityPlayerMP player) {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		buf.writeInt(0);
		CHANNEL.sendTo(new FMLProxyPacket(buf,NAME),player);
	}
	
	@SideOnly(Side.CLIENT)
	private static void playCraftAnimationByClient(FMLNetworkEvent.ClientCustomPacketEvent event) {
		new Thread(()->{
			for(int state=1;state<=60;state++) {
				try {
					if(Minecraft.getMinecraft().player.openContainer!=null&&Minecraft.getMinecraft().player.openContainer instanceof ETSTContainerRecipes) {
						if(((ETSTContainerRecipes)Minecraft.getMinecraft().player.openContainer).chance<=0)
							break;
						((ETSTContainerRecipes)Minecraft.getMinecraft().player.openContainer).state = state;
						Thread.sleep(50);
					}else
						break;
				} catch (InterruptedException e) {
					EquipmentStrengthening.printError(e);;
				}
			}
			if(Minecraft.getMinecraft().player.openContainer!=null&&Minecraft.getMinecraft().player.openContainer instanceof ETSTContainerRecipes) {
				((ETSTContainerRecipes)Minecraft.getMinecraft().player.openContainer).state = 0;
			}
			//*You must do this,else will cause uncraftable.
			finalApplyByClient();
		}).start();
	}
	
	@SideOnly(Side.CLIENT)
	private static void finalApplyByClient() {
		if(Minecraft.getMinecraft().player.openContainer!=null&&Minecraft.getMinecraft().player.openContainer instanceof ETSTContainerRecipes) {
			PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
			buf.writeInt(1);
			char[] nameToArray = Minecraft.getMinecraft().player.getName().toCharArray();
			buf.writeInt(nameToArray.length);
			for(char each : nameToArray)
				buf.writeChar(each);
			//*写入玩家名称
			CHANNEL.sendToServer(new FMLProxyPacket(buf,NAME));
		}
	}
	
	private static void finalApplyByServer(FMLNetworkEvent.ServerCustomPacketEvent event) {
		MinecraftServer MCServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		ByteBuf buf = event.getPacket().payload();
		char[] nameAsArray = new char[buf.readInt()];
		for(int each = 0;each < nameAsArray.length;each++)
			nameAsArray[each] = buf.readChar();
		String playerName = new String(nameAsArray);
		EntityPlayerMP player = MCServer.getPlayerList().getPlayerByUsername(playerName);
		if(player != null&&player instanceof EntityPlayerMP) {
			MCServer.addScheduledTask(()->{
				try {
					if(player.openContainer!=null&&player.openContainer instanceof ETSTContainerRecipes) {
						ETSTContainerRecipes container = ((ETSTContainerRecipes) player.openContainer);
						container.isApplying = false;
						if(container.apply())
							container.craft();
					}
				}catch(Throwable error) {
					EquipmentStrengthening.printError(error);
				}
			});
		}
	}
	
	public static void giveCraftChanceByServer(EntityPlayerMP player) {
		if(player.openContainer!=null&&player.openContainer instanceof ETSTContainerRecipes) {
			ETSTContainerRecipes container = ((ETSTContainerRecipes) player.openContainer);
			PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
			buf.writeInt(1);
			buf.writeDouble(container.getCraftProbability());
			CHANNEL.sendTo(new FMLProxyPacket(buf,NAME),player);
		}
	}
	
	@SideOnly(Side.CLIENT)
	private static void setCraftChanceByClient(FMLNetworkEvent.ClientCustomPacketEvent event) {
		Minecraft.getMinecraft().addScheduledTask(()->{
			if(Minecraft.getMinecraft().player.openContainer!=null&&Minecraft.getMinecraft().player.openContainer instanceof ETSTContainerRecipes) {
				ETSTContainerRecipes container = ((ETSTContainerRecipes) Minecraft.getMinecraft().player.openContainer);
				double chance = event.getPacket().payload().readDouble();
				container.chance = chance;
			}
		});
	}

	@SideOnly(Side.CLIENT)
	public static void createRecipeByClient() {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		buf.writeInt(CLIENT_CREATE);
		char[] nameToArray = Minecraft.getMinecraft().player.getName().toCharArray();
		buf.writeInt(nameToArray.length);
		for(char each : nameToArray)
			buf.writeChar(each);
		//*写入玩家名称
		CHANNEL.sendToServer(new FMLProxyPacket(buf,NAME));
	}
	
	private static void createRecipeByServer(FMLNetworkEvent.ServerCustomPacketEvent event) {
		MinecraftServer MCServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		ByteBuf buf = event.getPacket().payload();
		char[] nameAsArray = new char[buf.readInt()];
		for(int each = 0;each < nameAsArray.length;each++)
			nameAsArray[each] = buf.readChar();
		String playerName = new String(nameAsArray);
		EntityPlayerMP player = MCServer.getPlayerList().getPlayerByUsername(playerName);
		if(player != null&&player instanceof EntityPlayerMP) {
			MCServer.addScheduledTask(()->{
				try {
					if(player.openContainer!=null&&player.openContainer instanceof ETSTContainerRecipesCreate) {
						ETSTContainerRecipesCreate container = ((ETSTContainerRecipesCreate) player.openContainer);
						container.create();
						player.closeScreen();
					}
				}catch(Throwable error) {
					EquipmentStrengthening.printError(error);
				}
			});
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void clickViewSlotByClient(int slotIndex) {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		buf.writeInt(CLIENT_VIEW_CLICK_SLOT);
		char[] nameToArray = Minecraft.getMinecraft().player.getName().toCharArray();
		buf.writeInt(nameToArray.length);
		for(char each : nameToArray)
			buf.writeChar(each);
		//*写入玩家名称
		buf.writeInt(slotIndex);
		//*写入格子索引
		CHANNEL.sendToServer(new FMLProxyPacket(buf,NAME));
	}
	
	private static void clickViewSlotByServer(FMLNetworkEvent.ServerCustomPacketEvent event) {
		MinecraftServer MCServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		ByteBuf buf = event.getPacket().payload();
		char[] nameAsArray = new char[buf.readInt()];
		for(int each = 0;each < nameAsArray.length;each++)
			nameAsArray[each] = buf.readChar();
		String playerName = new String(nameAsArray);
		EntityPlayerMP player = MCServer.getPlayerList().getPlayerByUsername(playerName);
		int slotIndex = buf.readInt();
		if(player != null&&player instanceof EntityPlayerMP) {
			MCServer.addScheduledTask(()->{
				try {
					if(player.openContainer!=null&&player.openContainer instanceof ETSTContainerView) {
						ETSTContainerView container = ((ETSTContainerView) player.openContainer);
						Container nextLevel = container.getNextLevelInformation(slotIndex);
						if(nextLevel !=null) {
							if(nextLevel instanceof ETSTContainerView) {
								ETSTContainerView next = (ETSTContainerView) nextLevel;
								GuiHandler.openGui(player, GuiHandler.CHAIN_VIEW, next.TRANSFER_DATA, next.getLowerChestInventory().getName());
							}else if(nextLevel instanceof ETSTContainerRecipes) {
								ETSTContainerRecipes next = (ETSTContainerRecipes) nextLevel;
								GuiHandler.openGui(player, GuiHandler.CRAFT_VIEW);
								ETSTContainerRecipes view = (ETSTContainerRecipes) player.openContainer;
								view.setItemStacks(next.originalItem.getStack(), next.result.getStack(), ListHelper.getListInner(next.need, obj->((Slot)obj).getStack(), ItemStack.class));
							}
						}
					}
				}catch(Throwable error) {
					EquipmentStrengthening.printError(error);
				}
			});
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void backToChainByClient() {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		buf.writeInt(CLIENT_BACK_CHAIN);
		char[] nameToArray = Minecraft.getMinecraft().player.getName().toCharArray();
		buf.writeInt(nameToArray.length);
		for(char each : nameToArray)
			buf.writeChar(each);
		//*写入玩家名称
		CHANNEL.sendToServer(new FMLProxyPacket(buf,NAME));
	}
	
	private static void backToChainByServer(FMLNetworkEvent.ServerCustomPacketEvent event) {
		MinecraftServer MCServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		ByteBuf buf = event.getPacket().payload();
		char[] nameAsArray = new char[buf.readInt()];
		for(int each = 0;each < nameAsArray.length;each++)
			nameAsArray[each] = buf.readChar();
		String playerName = new String(nameAsArray);
		EntityPlayerMP player = MCServer.getPlayerList().getPlayerByUsername(playerName);
		if(player != null&&player instanceof EntityPlayerMP) {
			MCServer.addScheduledTask(()->{
				try {
					StrengtheningRecipe recipe = null;
					if(player.openContainer!=null&&player.openContainer instanceof ETSTContainerRecipes) {
						ETSTContainerRecipes container = ((ETSTContainerRecipes) player.openContainer);
						if(container.isView)
							recipe = container.findMatchRecipe();
					}
					if(recipe==null)
						return;
					GuiHandler.openGui(player, GuiHandler.CHAIN_VIEW, recipe.getNode().getRoot().getRecipe().getRecipeName(), recipe.getNode().getRoot().getRecipe().getOriginalItem().getDisplayName());
				}catch(Throwable error) {
					EquipmentStrengthening.printError(error);
				}
			});
		}
	}
	
}
