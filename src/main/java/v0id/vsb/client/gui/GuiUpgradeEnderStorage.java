package v0id.vsb.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import v0id.api.vsb.data.VSBTextures;
import v0id.vsb.config.VSBCfg;
import v0id.vsb.container.ContainerUpgradeEnderStorage;

public class GuiUpgradeEnderStorage extends GuiContainer
{
    public GuiUpgradeEnderStorage(ItemStack filter, int slotIndex)
    {
        super(new ContainerUpgradeEnderStorage(Minecraft.getMinecraft().player.inventory, filter, slotIndex));
        this.xSize = 176;
        this.ySize = 132;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        Minecraft.getMinecraft().renderEngine.bindTexture(VSBCfg.useLightUI ? VSBTextures.UPGRADE_ENDER_STORAGE_LIGHT : VSBTextures.UPGRADE_ENDER_STORAGE);
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        for (int k = 0; k < 3; ++k)
        {
            Slot s = this.inventorySlots.inventorySlots.get(k);
            if (!s.getHasStack())
            {
                this.drawTexturedModalRect(i + 61 + k * 18, j + 7, 176 + k * 18, 0, 18, 18);
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
