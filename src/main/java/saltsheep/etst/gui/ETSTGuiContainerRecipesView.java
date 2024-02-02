package saltsheep.etst.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;

public class ETSTGuiContainerRecipesView extends ETSTGuiContainerRecipes {
	
	public ETSTGuiContainerRecipesView(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	@Override
    public void initGui() {
		super.initGui();
		this.buttonList.clear();
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        this.buttonList.add(new GuiButton(0, offsetX + 144, offsetY + 80-25, 24, 18, "返回"));
    }
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0) {
			if(this.buttonList.get(0).mousePressed(this.mc, mouseX, mouseY)) {
				NetworkForRecipe.backToChainByClient();
			}
		}
	}

}
