package saltsheep.etst.data;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.item.ItemStack;
import saltsheep.etst.data.ChainNode;
import saltsheep.etst.data.Group;
import saltsheep.etst.data.InformationNotsameException;
import saltsheep.etst.data.StrengtheningRecipe;
import saltsheep.etst.data.UnbindableException;

public class RecipeRegistry {

	//*The biggest str,include chains(chain include node,node bind to recipe).
	protected static Map<String,Group> groups = Maps.newHashMap();
	//*The recipe is bind to a node,so you can use one of them to search any information.
	protected static Map<String,StrengtheningRecipe> recipes = Maps.newHashMap();
	
	@Nonnull
	public static Group getGroup(String groupName) {
		if(!groups.containsKey(groupName))
			groups.put(groupName, new Group(groupName));
		return groups.get(groupName);
	}
	
	public static boolean registerRecipe(String recipeName,String group,@Nullable String up,@Nonnull ItemStack craftTarget,@Nonnull ItemStack originalItem,@Nonnull List<ItemStack> need,double successChance) throws UnbindableException, InformationNotsameException {
		if(recipes.containsKey(recipeName))
			return false;
		if(craftTarget.isEmpty()||originalItem.isEmpty()||need.isEmpty())
			return false;
		boolean isAllNeedEmpty = true;
		for(ItemStack eachNeed:need)
			if(!eachNeed.isEmpty()) {
				isAllNeedEmpty = false;
				break;
			}
		if(isAllNeedEmpty)
			return false;
		StrengtheningRecipe recipe = new StrengtheningRecipe(recipeName, craftTarget, originalItem, need, successChance);
		ChainNode upNode = null;
		if(up!=null)
			upNode = recipes.get(up).getNode();
		ChainNode node = new ChainNode(getGroup(group), upNode, recipe);
		if(upNode==null) 
			getGroup(group).addChainRootNode(node);
		recipes.put(recipeName, recipe);
		EquipmentSaveData.getData().save();
		return true;
	}
	
	public static boolean deleteRecipe(String recipeName) {
		if(recipes.containsKey(recipeName)) {
			ChainNode itself = recipes.get(recipeName).node;
			if(itself.up!=null) {
				itself.up.next.remove(itself);
			}else {
				RecipeRegistry.getGroup(itself.group.groupName).getAllChainsRootNode().remove(itself);
			}
			if(!itself.next.isEmpty())
				for(int i=itself.next.size()-1;i>=0;i--)
					deleteRecipe(itself.next.get(i).recipe.recipeName);
			recipes.remove(recipeName);
			EquipmentSaveData.getData().save();
			return true;
		}else
			return false;
	}
	
	//*Only use for data load.
	protected static void setGroupForcibly(String groupName,Group group) {
		groups.put(groupName, group);
		registerGroupRecipes(group);
	}
	
	//*Only use for data load
	private static void registerGroupRecipes(Group group) {
		for(ChainNode root:group.getAllChainsRootNode())
			for(ChainNode node:root.getAllChainNodeByBFS()) {
				recipes.put(node.getRecipe().recipeName, node.getRecipe());
			}
	}
	
	@Nullable
	public static StrengtheningRecipe getRecipe(String recipeName) {
		return recipes.get(recipeName);
	}
	
	public static boolean hasRecipe(String recipeName) {
		return recipes.containsKey(recipeName);
	}
	
	public static List<StrengtheningRecipe> getRecipe(ItemStack originalItem) {
		List<StrengtheningRecipe> recipeList = Lists.newArrayList();
		for(StrengtheningRecipe recipe:recipes.values()) {
			if(recipe.test(originalItem))
				recipeList.add(recipe);
		}
		return recipeList;
	}
	
}
