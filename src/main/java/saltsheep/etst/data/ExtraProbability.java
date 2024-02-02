package saltsheep.etst.data;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ExtraProbability {

	public static final int ALL = -1;
	public static final int GROUP = 0;
	public static final int RECIPE = 1;
	
	public static void addExtraProbability(ItemStack item,double chance,boolean isAbs,int range,String rangeTargetName) {
		if(!item.hasTagCompound())
			item.setTagCompound(new NBTTagCompound());
		NBTTagList probabilityList = item.getTagCompound().getTagList("etstProbability", 10);
		NBTTagCompound newProbability = new NBTTagCompound();
		newProbability.setInteger("range", range);
		newProbability.setString("rangeTarget", rangeTargetName);
		newProbability.setDouble("chance", chance);
		newProbability.setBoolean("abs", isAbs);
		probabilityList.appendTag(newProbability);
		item.getTagCompound().setTag("etstProbability", probabilityList);
	}
	
	public static List<String> checkExtraProbability(ItemStack item){
		if(!item.hasTagCompound()||!item.getTagCompound().hasKey("etstProbability"))
			return Lists.newArrayList();
		List<String> list = Lists.newArrayList();
		NBTTagList probabilityList = item.getTagCompound().getTagList("etstProbability", 10);
		for(int i=0;i<probabilityList.tagCount();i++)
			list.add("range:"+probabilityList.getCompoundTagAt(i).getInteger("range")+" rangeTarget:"+probabilityList.getCompoundTagAt(i).getString("rangeTarget")+" chance:"+probabilityList.getCompoundTagAt(i).getDouble("chance")+" isAbs:"+probabilityList.getCompoundTagAt(i).getBoolean("abs"));
		return list;
	}
	
	public static void clearExtraProbability(ItemStack item) {
		item.getTagCompound().setTag("etstProbability", new NBTTagList());
	}
	
	public static double getExtraProbability(ItemStack item,String group,String recipe,boolean isAbs) {
		double chance = 0;
		if(item.hasDisplayName()) {
			NBTTagList probabilityList = item.getTagCompound().getTagList("etstProbability", 10);
			for(int i=0;i<probabilityList.tagCount();i++) {
				NBTTagCompound probability = probabilityList.getCompoundTagAt(i);
				if(probability.getBoolean("abs")!=isAbs)
					continue;
				if(probability.getInteger("range")==ALL) 
					chance+=probability.getDouble("chance");
				else if(probability.getInteger("range")==GROUP&&probability.getString("rangeTarget").equals(group))
					chance+=probability.getDouble("chance");
				else if(probability.getInteger("range")==RECIPE&&probability.getString("rangeTarget").equals(recipe))
					chance+=probability.getDouble("chance");
			}
		}
		return chance;
	}
	
}
