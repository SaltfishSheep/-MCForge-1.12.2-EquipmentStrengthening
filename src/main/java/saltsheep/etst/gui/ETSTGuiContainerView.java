package saltsheep.etst.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import saltsheep.lib.reflect.ReflectHelper;

public class ETSTGuiContainerView extends GuiContainer {

    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("minecraft:textures/gui/container/generic_54.png");
    private final IInventory lowerChestInventory;
    private final int inventoryRows;

    public ETSTGuiContainerView(EntityPlayer player, String name, int line, int displayLevel) {
		this(player.inventory, new InventoryBasic(new TextComponentString(name), line*9), displayLevel);
	}
    
    private ETSTGuiContainerView(IInventory upperInv, IInventory lowerInv, int displayLevel){
        super(new ETSTContainerView(upperInv, lowerInv, Minecraft.getMinecraft().player, displayLevel));
        this.lowerChestInventory = lowerInv;
        this.allowUserInput = false;
        this.inventoryRows = lowerInv.getSizeInventory() / 9;
        this.ySize = 114 + this.inventoryRows * 18;
    }
    
    //*mouseButton- 1 leftClick 2 rightClick
    @Override
    protected void mouseClicked(int mouseX, int mouseY,int mouseButton) {
    	ReflectHelper.invokeMCParentsMethod(GuiScreen.class, GuiScreen.class, void.class, this, "mouseClicked", "func_73864_a", mouseX, mouseY, mouseButton);
    	boolean flag = this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100);
        Slot slot = this.getSlotAtPosition(mouseX, mouseY);

        if (mouseButton == 0 || mouseButton == 1 || flag){
            int j = this.guiLeft;
            int k = this.guiTop;
            boolean flag1 = this.hasClickedOutside(mouseX, mouseY, j, k);
            if (slot != null) flag1 = false;
            int l = -1;
            if (slot != null){
                l = slot.slotNumber;
            }
            if (flag1){
                l = -999;
            }
            if (this.mc.gameSettings.touchscreen && flag1 && this.mc.player.inventory.getItemStack().isEmpty()){
                this.mc.displayGuiScreen((GuiScreen)null);
                return;
            }
            if (l != -1){
                if (!this.dragSplitting){
                    if (this.mc.player.inventory.getItemStack().isEmpty()){
                    	if(!this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100)){
                    		if(slot!=null)
                    			NetworkForRecipe.clickViewSlotByClient(slot.getSlotIndex());
                        }
                    }
                }
            }
        }
    }
    
    private Slot getSlotAtPosition(int x, int y){
        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i){
            Slot slot = this.inventorySlots.inventorySlots.get(i);
            if (this.isMouseOverSlot(slot, x, y) && slot.isEnabled()){
                return slot;
            }
        }
        return null;
    }
    
    private boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY){
        return this.isPointInRegion(slotIn.xPos, slotIn.yPos, 16, 16, mouseX, mouseY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
        this.fontRenderer.drawString(this.lowerChestInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }

}
