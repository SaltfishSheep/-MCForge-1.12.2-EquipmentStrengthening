package saltsheep.etst.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import saltsheep.etst.EquipmentStrengthening;
import saltsheep.etst.data.RecipeRegistry;
import saltsheep.lib.list.ListHelper;
import saltsheep.lib.player.PlayerHelper;

public class ETSTContainerRecipesCreate extends Container {

	public Slot originalItem;
	public Slot[] need = new Slot[5];
	public Slot result;
	
	public String recipeName;
	public String groupName;
	public String up;
	public double chance;
	protected EntityPlayer playerIn;
	protected IInventory items;
	
	public ETSTContainerRecipesCreate(EntityPlayer playerIn) {
		super();
		this.items = new InventoryBasic("EquipmentStrengthening", false, 10);
		this.playerIn = playerIn;
		this.addSlotToContainer(originalItem = new Slot(items, 0, 43, 40-18));
		this.addSlotToContainer(result = new Slot(items, 1, 122, 40-18));
        for(int i = 0;i<5;i++) {
        	this.addSlotToContainer(need[i] = new Slot(items, 2+i, 47+i*18, 81-25));
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
	
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	//*Same as register.
	public void create() {
		this.register();
	}
	
	//*Don't use at client,it will has no useful.Plz use at the handler server.
	public void register() {
		try {
			if(RecipeRegistry.registerRecipe(this.recipeName, this.groupName, this.up, this.result.getStack().copy(),this.originalItem.getStack().copy(), ListHelper.getListInner(this.need, (slot)->((Slot)slot).getStack().copy(),ItemStack.class), this.chance))
				this.playerIn.sendMessage(new TextComponentString("Register recipe successful!"));
			else
				this.playerIn.sendMessage(new TextComponentString("Warning!Failed to register a new recipe.\nIs you put no item at originalSlot,craftSlot,needSlot?\nOr you have already register this recipe."));
		} catch (Throwable e) {
			EquipmentStrengthening.printError(e);
		}
	}
	
	@Override
    public void onContainerClosed(EntityPlayer playerIn){
        super.onContainerClosed(playerIn);
        PlayerHelper.giveOrDropItem(playerIn, this.originalItem.getStack());
    	for(Slot eachNeedSlot:need)
    		PlayerHelper.giveOrDropItem(playerIn, eachNeedSlot.getStack());
    	PlayerHelper.giveOrDropItem(playerIn, this.result.getStack());
    }
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index){
	    return ItemStack.EMPTY;
	}

}
