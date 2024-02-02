package saltsheep.etst.data;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class StrengtheningRecipe {

	protected final String recipeName;
	private final ItemStack craftTarget;
	private final ItemStack originalItem;
	private final List<ItemStack> need;
	protected final double successChance;
	protected ChainNode node;
	
	public StrengtheningRecipe(String recipeName,ItemStack craftTarget,ItemStack originalItem,List<ItemStack> need,double successChance,ChainNode node) throws UnbindableException {
		this(recipeName, craftTarget, originalItem, need, successChance);
		if(!node.isBinded())
			this.bind(node);
		else
			throw new UnbindableException();
	}
	
	public StrengtheningRecipe(String recipeName,ItemStack craftTarget,ItemStack originalItem,List<ItemStack> need,double successChance) {
		this.recipeName = recipeName;
		this.craftTarget = craftTarget;
		this.originalItem = originalItem;
		this.need = need;
		/*if(need!=null)
			for(int i=0;i<need.size();i++)
				need.set(i, ItemStack.EMPTY);*/
		this.successChance = successChance;
	}
	
	public boolean isBinded() {
		return this.getNode()!=null;
	}
	
	public boolean bind(ChainNode node) {
		if(this.getNode()!=null)
			return false;
		if(node.getRecipe()!=null)
			return false;
		this.node = node;
		node.recipe = this;
		return true;
	}

	public ChainNode getNode() {
		return this.node;
	}
	
	public String getRecipeName() {
		return recipeName;
	}
	
	public ItemStack getOriginalItem() {
		return originalItem;
	}

	public ItemStack getCraftTarget() {
		return craftTarget;
	}

	public List<ItemStack> getNeed() {
		return need;
	}

	public double getFinalChance(ItemStack extra) {
		return this.successChance*(1+ExtraProbability.getExtraProbability(extra, this.getNode().group.groupName, this.recipeName, false))+ExtraProbability.getExtraProbability(extra, this.getNode().group.groupName, this.recipeName, true);
	}
	
	public boolean test(ItemStack mainItem) {
		return ItemStack.areItemStacksEqualUsingNBTShareTag(mainItem, this.getOriginalItem());
	}
	
	public boolean test(ItemStack mainItem,List<ItemStack> inputNeed) {
		if(!ItemStack.areItemStacksEqualUsingNBTShareTag(mainItem, this.getOriginalItem()))
			return false;
		if(getNeed().size()!=inputNeed.size())
			return false;
		for(int i=0;i<getNeed().size();i++)
			if(!ItemStack.areItemStacksEqualUsingNBTShareTag(getNeed().get(i),inputNeed.get(i)))
				return false;
		return true;
	}
	
	//*Return craftTarget,if both "test" return true,and random is less than chance.
	public ItemStack apply(ItemStack mainItem,List<ItemStack> inputNeed,ItemStack extraProbability) {
		if(this.test(mainItem,inputNeed)&&Math.random()<=this.getFinalChance(extraProbability))
			return this.getCraftTarget().copy();
		return ItemStack.EMPTY;
	}
	
	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("recipeName", this.recipeName);
		nbt.setTag("craftTarget", this.getCraftTarget().serializeNBT());
		nbt.setTag("originalItem", this.getOriginalItem().serializeNBT());
		nbt.setDouble("successChance", this.successChance);
		NBTTagList needAsList = new NBTTagList();
		for(int i=0;i<this.getNeed().size();i++)
			needAsList.appendTag(this.getNeed().get(i).serializeNBT());
		nbt.setTag("need", needAsList);
		return nbt;
	}
	
	public static StrengtheningRecipe fromNBT(NBTTagCompound nbt) {
		String recipeName = nbt.getString("recipeName");
		ItemStack craftTarget = new ItemStack(nbt.getCompoundTag("craftTarget"));
		ItemStack originalItem = new ItemStack(nbt.getCompoundTag("originalItem"));
		List<ItemStack> need = Lists.newArrayList();
		NBTTagList needAsList = nbt.getTagList("need", 10);
		for(int i=0;i<needAsList.tagCount();i++)
			need.add(new ItemStack(needAsList.getCompoundTagAt(i)));
		double successChance = nbt.getDouble("successChance");
		return new StrengtheningRecipe(recipeName, craftTarget, originalItem, need, successChance);
	}
	
}
