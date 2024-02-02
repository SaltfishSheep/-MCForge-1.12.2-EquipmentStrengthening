package saltsheep.etst.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import saltsheep.etst.EquipmentStrengthening;

public class ETSTGuiContainerRecipesCreate extends GuiContainer {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("etst:textures/gui/recipe_container.png");
	
	public ETSTGuiContainerRecipesCreate(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		this.width = 176;
		this.height = 195-25;
		this.xSize = 176;
		this.ySize = 195-25;
	}
	
	@Override
    public void initGui() {
        super.initGui();
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        this.buttonList.add(new GuiButton(0, offsetX + 146, offsetY + 80-25, 20, 18, "创建"));
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.drawDefaultBackground();
		GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        //*主背景
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.renderHoveredToolTip(mouseX-offsetX, mouseY-offsetY);
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		try {
			super.mouseClicked(mouseX, mouseY, mouseButton);
			GuiButton choose = this.selectedButton;
			if(choose!=null) {
				NetworkForRecipe.createRecipeByClient();
			}
		} catch (Throwable e) {
			EquipmentStrengthening.printError(e);
		}
	}

}
