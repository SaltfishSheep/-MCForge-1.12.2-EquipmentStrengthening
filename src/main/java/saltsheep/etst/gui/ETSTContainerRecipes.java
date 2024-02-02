package saltsheep.etst.gui;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import saltsheep.etst.data.RecipeRegistry;
import saltsheep.etst.data.StrengtheningRecipe;
import saltsheep.lib.player.PlayerHelper;

public class ETSTContainerRecipes extends Container {

	public Slot originalItem;
	public Slot extraProbability;
	public Slot[] need = new Slot[5];
	public Slot result;
	
	public boolean isView = false;
	public boolean isApplying = false;
	public double state = 0;
	public double chance = 0;
	protected EntityPlayer playerIn;
	protected IInventory items;
	
	public ETSTContainerRecipes(EntityPlayer playerIn) {
		super();
		this.items = new InventoryBasic("EquipmentStrengthening", false, 10);
		this.playerIn = playerIn;
		this.addSlotToContainer(originalItem = new Slot(items, 0, 43, 40-18));
		this.addSlotToContainer(result = new Slot(items, 1, 122, 40-18) {
			@Override
			public boolean isItemValid(ItemStack stack){
		        return false;
		    }
		});
		this.addSlotToContainer(extraProbability = new Slot(items, 2, 20, 81-25));
        for(int i = 0;i<5;i++) {
        	this.addSlotToContainer(need[i] = new Slot(items, 3+i, 47+i*18, 81-25));
        }
        for (int k = 0; k < 3; ++k){
            for (int i1 = 0; i1 < 9; ++i1){
                this.addSlotToContainer(new Slot(playerIn.inventory, i1 + k * 9 + 9, 8 + i1 * 18, 113 + k * 18-25));
            }
        }
        for (int l = 0; l < 9; ++l){
            this.addSlotToContainer(new Slot(playerIn.inventory, l, 8 + l * 18, 171-25));
        }
	}
	
	//*Use for view recipe.
	public void setItemStacks(ItemStack originalItem,ItemStack result,List<ItemStack> need) {
		if(need.size()!=5)
			return;
		for(int i=0;i<need.size();i++)
			this.need[i].putStack(need.get(i));
		this.originalItem.putStack(originalItem);
		this.result.putStack(result);
	}
	
	@Nullable
	public StrengtheningRecipe findMatchRecipe() {
		if((!this.isView&&this.result.getHasStack())||!this.originalItem.getHasStack())
			return null;
		List<StrengtheningRecipe> findRecipes = RecipeRegistry.getRecipe(this.originalItem.getStack());
		if(findRecipes.isEmpty())
			return null;
		List<ItemStack> need = Lists.newArrayList();
		for(Slot eachNeed:this.need)
			need.add(eachNeed.getStack());
		for(StrengtheningRecipe recipe:findRecipes)
			if(recipe.test(this.originalItem.getStack(), need))
				return recipe;
		return null;
	}
	
	public double getCraftProbability() {
		StrengtheningRecipe recipe = this.findMatchRecipe();
		if(recipe != null) {
			return recipe.getFinalChance(this.extraProbability.getStack());
		}else
			return 0;
	}
	
	public boolean apply() {
		if(this.result.getHasStack())
			return false;
		if(this.findMatchRecipe()!=null)
				return true;
		return false;
	}
	
	public void craft() {
		if(this.result.getHasStack())
			return;
		StrengtheningRecipe recipe = this.findMatchRecipe();
		if(recipe!=null) {
			List<ItemStack> needItems = Lists.newArrayList();
			for(Slot eachNeed:this.need)
				needItems.add(eachNeed.getStack());
			ItemStack result = recipe.apply(this.originalItem.getStack(), needItems, this.extraProbability.getStack());
			if(!result.isEmpty())
				this.originalItem.putStack(ItemStack.EMPTY);
			for(Slot eachNeed:this.need)
				eachNeed.putStack(ItemStack.EMPTY);
			this.extraProbability.getStack().setCount(this.extraProbability.getStack().getCount()-1);
			this.result.putStack(result);
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	@Override
    public void onContainerClosed(EntityPlayer playerIn){
        super.onContainerClosed(playerIn);
        if(this.isView)
        	return;
    	PlayerHelper.giveOrDropItem(playerIn, this.originalItem.getStack());
    	PlayerHelper.giveOrDropItem(playerIn, this.extraProbability.getStack());
    	for(Slot eachNeedSlot:need)
    		PlayerHelper.giveOrDropItem(playerIn, eachNeedSlot.getStack());
    	PlayerHelper.giveOrDropItem(playerIn, this.result.getStack());
    }
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index){
	    return ItemStack.EMPTY;
	}

}
