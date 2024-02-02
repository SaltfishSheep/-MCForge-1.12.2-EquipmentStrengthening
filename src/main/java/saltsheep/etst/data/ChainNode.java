package saltsheep.etst.data;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ChainNode {

	@Nonnull
	protected Group group;
	@Nonnull
	protected ChainNode root;
	@Nullable
	protected ChainNode up;
	protected List<ChainNode> next = Lists.newArrayList();
	@Nonnull
	protected StrengtheningRecipe recipe;
	
	public ChainNode(@Nonnull Group group,@Nullable ChainNode up,StrengtheningRecipe recipe) throws UnbindableException, InformationNotsameException {
		this(group,up);
		if(!recipe.isBinded())
			this.bind(recipe);
		else
			throw new UnbindableException();
	}
	
	public ChainNode(@Nonnull Group group,@Nullable ChainNode up) throws InformationNotsameException {
		if(up!=null&&!up.group.groupName.equals(group.groupName))
			throw new InformationNotsameException();
		this.group = group;
		ChainNode root = null;
		if(up==null)
			root = this;
		else
			root = up.getRoot();
		this.root = root;
		this.up = up;
		if(this.up != null)
			this.up.addNext(this);
	}
	
	public boolean isBinded() {
		return this.getRecipe()!=null;
	}
	
	public boolean bind(StrengtheningRecipe recipe) {
		if(this.getRecipe()!=null)
			return false;
		if(recipe.getNode()!=null)
			return false;
		this.recipe = recipe;
		recipe.node = this;
		return true;
	}
	
	//*Because bfs,the return list will put the same level node at nearly place.
	public List<ChainNode> getSonNodesContainsItselfByBFS(){
		List<ChainNode> sons = Lists.newLinkedList();
		sons.add(this);
		LinkedList<ChainNode> queue = Lists.newLinkedList(getNext());
		while(!queue.isEmpty()) {
			ChainNode node = queue.pop();
			if(!node.getNext().isEmpty())
				queue.addAll(node.getNext());
			sons.add(node);
		}
		return sons;
	}
	
	public List<ChainNode> getAllChainNodeByBFS(){
		return this.getRoot().getSonNodesContainsItselfByBFS();
	}
	
	public StrengtheningRecipe getRecipe() {
		return recipe;
	}

	public ChainNode getRoot() {
		return root;
	}
	
	public Group getGroup() {
		return RecipeRegistry.getGroup(this.group.groupName);
	}

	public boolean addNext(ChainNode newNext) {
		for(ChainNode originalNext:getNext()) {
			if(originalNext.getRecipe().recipeName.equals(newNext.getRecipe().recipeName))
				return false;
		}
		getNext().add(newNext);
		return true;
	}
	
	public boolean removeNext(String removeName) {
		for(int i = 0;i<getNext().size();i++) {
			if(getNext().get(i).getRecipe().recipeName.equals(removeName)) {
				getNext().remove(i);
				return true;
			}
		}
		return false;
	}
	
	public List<ChainNode> getNext() {
		return next;
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("group", this.group.groupName);
		nbt.setString("root", this.getRoot().getRecipe().recipeName);
		if(this.up!=null)
			nbt.setString("up", this.up.getRecipe().recipeName);
		NBTTagList nextAsList = new NBTTagList();
		for(int i=0;i<this.getNext().size();i++)
			nextAsList.appendTag(this.getNext().get(i).toNBT());
		nbt.setTag("next", nextAsList);
		nbt.setTag("recipe", this.getRecipe().toNBT());
		return nbt;
	}
	
	public static ChainNode fromNBT(NBTTagCompound nbt) throws UnbindableException, InformationNotsameException {
		Group group = RecipeRegistry.getGroup(nbt.getString("group"));
		StrengtheningRecipe recipe = StrengtheningRecipe.fromNBT(nbt.getCompoundTag("recipe"));
		ChainNode node = new ChainNode(group, null, recipe);
		ChainNode rootNode = node;
		String root = nbt.getString("root");
		String up = nbt.getString("up");
		if(!root.equals(node.getRecipe().recipeName))
			rootNode = RecipeRegistry.getRecipe(root).getNode();
		node.root = rootNode;
		if(!up.equals(""))
			node.up = RecipeRegistry.getRecipe(up).getNode();
		List<ChainNode> next = Lists.newArrayList();
		NBTTagList nextAsList = nbt.getTagList("next", 10);
		for(int i=0;i<nextAsList.tagCount();i++)
			next.add(ChainNode.fromNBT(nextAsList.getCompoundTagAt(i)));
		node.next = next;
		return node;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj!=null&&obj instanceof ChainNode)
			return ((ChainNode)obj).recipe.recipeName.equals(this.recipe.recipeName);
		return false;
	}
	
}
