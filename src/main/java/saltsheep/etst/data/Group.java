package saltsheep.etst.data;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class Group {

	protected final String groupName;
	protected List<ChainNode> allChainsRootNode = Lists.newLinkedList();
	
	public Group(String groupName) {
		this.groupName = groupName;
	}
	
	public boolean addChainRootNode(ChainNode newChain) {
		if(this.getAllChainsRootNode().contains(newChain))
			return false;
		for(ChainNode root:this.getAllChainsRootNode())
			if(root.getRecipe().recipeName.equals(newChain.getRecipe().recipeName))
				return false;
		this.getAllChainsRootNode().add(newChain);
		return true;
	}
	
	public boolean removeChainRootNode(String chainRootName) {
		for(ChainNode root:this.getAllChainsRootNode())
			if(root.getRecipe().recipeName.equals(chainRootName)) {
				this.getAllChainsRootNode().remove(root);
				return true;
			}
		return false;
	}
	
	public List<ChainNode> getAllChainsRootNode() {
		return allChainsRootNode;
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("groupName", this.groupName);
		NBTTagList allChains = new NBTTagList();
		for(ChainNode root:getAllChainsRootNode())
			allChains.appendTag(root.toNBT());
		nbt.setTag("allChainsRootNode", allChains);
		return nbt;
	}
	
	public static Group fromNBT(NBTTagCompound nbt) throws UnbindableException, InformationNotsameException {
		Group group = new Group(nbt.getString("groupName"));
		List<ChainNode> allChainsRootNode = Lists.newLinkedList();
		NBTTagList allChains = nbt.getTagList("allChainsRootNode", 10);
		for(int i=0;i<allChains.tagCount();i++)
			allChainsRootNode.add(ChainNode.fromNBT(allChains.getCompoundTagAt(i)));
		group.allChainsRootNode = allChainsRootNode;
		return group;
	}
	
}
