package saltsheep.etst.gui;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import saltsheep.etst.data.ChainNode;

public class ETSTContainerView extends ContainerChest{
	
	public static int GROUP = 0;
	public static int CHAIN = 1;

	protected int displayLevel;
	protected List<ChainNode> nodes;
	protected EntityPlayer player;
	protected String TRANSFER_DATA = null;
	
	public ETSTContainerView(EntityPlayer player, String name, int line, int displayLevel, List<ChainNode> nodes) {
		this(player, name, line, displayLevel);
		this.setDisplay(nodes);
	}
	
	protected ETSTContainerView(EntityPlayer player, String name, int line, int displayLevel) {
		this(player.inventory,  new InventoryBasic(name, true, line*9), player, displayLevel);
	}
	
	protected ETSTContainerView(IInventory upperInv, IInventory lowerInv, EntityPlayer player, int displayLevel) {
		super(upperInv, lowerInv , player);
		this.player = player;
		this.displayLevel = displayLevel;
	}

	public List<ChainNode> getNodes() {
		return nodes;
	}
	
	public int getDisplayLevel() {
		return displayLevel;
	}

	public void setDisplay(List<ChainNode> nodes) {
		this.getLowerChestInventory().clear();
		this.nodes = nodes;
		for(int i=0;i<nodes.size();i++) {
			if(this.displayLevel == GROUP)
				this.getLowerChestInventory().setInventorySlotContents(i, nodes.get(i).getRecipe().getOriginalItem());
			else if(this.displayLevel == CHAIN)
				this.getLowerChestInventory().setInventorySlotContents(i, nodes.get(i).getRecipe().getCraftTarget());
		}
	}
	
	@Nullable
	public ChainNode getNode(int slotIndex) {
		if(slotIndex<nodes.size())
			return nodes.get(slotIndex);
		return null;
	}
	
	@Nullable
	public Container getNextLevelInformation(int slotIndex) {
		if(this.getNode(slotIndex)!=null) {
			if(this.displayLevel == GROUP) {
				ChainNode selectNode = this.getNode(slotIndex);
				List<ChainNode> lists = Lists.newLinkedList();
				lists.addAll(selectNode.getAllChainNodeByBFS());
				ETSTContainerView chainView = new ETSTContainerView(this.player, selectNode.getRecipe().getOriginalItem().getDisplayName(), 6, CHAIN, lists);
				chainView.TRANSFER_DATA = selectNode.getRecipe().getRecipeName();
				return chainView;
			}else if(this.displayLevel == CHAIN) {
				ChainNode selectNode = this.getNode(slotIndex);
				ETSTContainerRecipes recipeView = new ETSTContainerRecipes(this.player);
				recipeView.isView = true;
				recipeView.setItemStacks(selectNode.getRecipe().getOriginalItem(), selectNode.getRecipe().getCraftTarget(), selectNode.getRecipe().getNeed());
				return recipeView;
			}
		}
		return null;
	}

}
