package saltsheep.etst.data;

import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.FMLCommonHandler;
import saltsheep.etst.EquipmentStrengthening;

public class EquipmentSaveData extends WorldSavedData {

	private static final String NAME = "EquipmentSaveData";
	
	public EquipmentSaveData() {
		this(NAME);
	}
	
	public EquipmentSaveData(String name) {
		super(name);
	}
	
	public static EquipmentSaveData getData() {
		WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
		MapStorage storage = world.getMapStorage();
		if(storage.getOrLoadData(EquipmentSaveData.class, NAME) == null) {
			storage.setData(NAME, new EquipmentSaveData());
		}
		return (EquipmentSaveData) storage.getOrLoadData(EquipmentSaveData.class, NAME);
	}
	
	public void save() {
		this.markDirty();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagCompound recipes = nbt.getCompoundTag("equipmentRecipesSavedBySheep");
		for(String group:recipes.getKeySet())
			try {
				RecipeRegistry.setGroupForcibly(group, Group.fromNBT(recipes.getCompoundTag(group)));
			} catch (UnbindableException | InformationNotsameException e) {
				EquipmentStrengthening.printError(e);
				EquipmentStrengthening.getLogger().warn("How can that be!!!");
			}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound recipes = new NBTTagCompound();
		for(Entry<String, Group> entry:RecipeRegistry.groups.entrySet())
			recipes.setTag(entry.getKey(), entry.getValue().toNBT());
		compound.setTag("equipmentRecipesSavedBySheep", recipes);
		return compound;
	}

}
