package saltsheep.etst.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import saltsheep.etst.EquipmentStrengthening;

public class ETSTGuiContainerRecipes extends GuiContainer {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("etst:textures/gui/recipe_container.png");
	
	public ETSTGuiContainerRecipes(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		this.width = 176;
		this.height = 195-25;
		this.xSize = 176;
		this.ySize = 195-25;
	}
	
	@Override
    public void initGui() {
        super.initGui();
        ETSTGuiContainerRecipes gui = this;
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        this.buttonList.add(new GuiButton(0, offsetX + 146, offsetY + 80-25, 20, 18, I18n.format("saltsheep.gui.etstcraft")) {
        	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
            {
                if (this.visible)
                {
                    mc.getTextureManager().bindTexture(TEXTURE);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                    //*0按下，1正常，2在上
                    int i = this.getHoverState(this.hovered);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    if(i==0)
                    	this.drawTexturedModalRect(this.x, this.y, 176, 0, this.width, this.height);
                    else if(i==1)
                    	this.drawTexturedModalRect(this.x, this.y, 176, 36, this.width, this.height);
                    else if(i==2) {
                    	this.drawTexturedModalRect(this.x, this.y, 176, 18, this.width, this.height);
                    	ItemStack message = new ItemStack(Items.DIAMOND);
                    	message.setStackDisplayName(I18n.format("saltsheep.gui.etstcraft"));
                    	gui.renderToolTip(message, mouseX, mouseY);
                    }
                    this.mouseDragged(mc, mouseX, mouseY);
                }
            }
        });
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
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(offsetX+77, offsetY+50-25, 176, 54, (int) ((((ETSTContainerRecipes)this.inventorySlots).state)/60*24), 17);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		int fontWidth = this.mc.fontRenderer.getStringWidth("几率-"+String.valueOf(((int)(((ETSTContainerRecipes)this.inventorySlots).chance*10)/10)));
		this.mc.fontRenderer.drawString("几率-"+String.valueOf((int)(((ETSTContainerRecipes)this.inventorySlots).chance*100))+"%", 176/2-fontWidth/2, 40-25, 0x000000);
		this.renderHoveredToolTip(mouseX-offsetX, mouseY-offsetY);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		try {
			super.mouseClicked(mouseX, mouseY, mouseButton);
			GuiButton choose = this.selectedButton;
			if(choose!=null) {
				NetworkForRecipe.craftByClient();
			}
		} catch (IOException e) {
			EquipmentStrengthening.printError(e);
		}
	}

}
