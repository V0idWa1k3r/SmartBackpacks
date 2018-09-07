package v0id.vsb.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import v0id.api.vsb.capability.IFilter;
import v0id.api.vsb.data.VSBTextures;
import v0id.vsb.container.ContainerFilter;
import v0id.vsb.net.VSBNet;

import java.io.IOException;

public class GuiFilter extends GuiContainer
{
    public GuiFilter(ItemStack filter, int slotIndex)
    {
        super(new ContainerFilter(Minecraft.getMinecraft().player.inventory, filter, slotIndex));
        this.xSize = 176;
        this.ySize = 140;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.addButton(new FilterButton(0, i + 21, j + 30, 20, 20, StringUtils.EMPTY));
        this.addButton(new FilterButton(1, i + 49, j + 30, 20, 20, StringUtils.EMPTY));
        this.addButton(new FilterButton(2, i + 107, j + 30, 20, 20, StringUtils.EMPTY));
        this.addButton(new FilterButton(3, i + 135, j + 30, 20, 20, StringUtils.EMPTY));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        super.actionPerformed(button);
        IFilter filter = IFilter.of(((ContainerFilter)this.inventorySlots).filter);
        switch (button.id)
        {
            case 0:
            {
                filter.setOreDictionary(!filter.isOreDictionary());
                break;
            }

            case 1:
            {
                filter.setIgnoresMeta(!filter.ignoresMetadata());
                break;
            }

            case 2:
            {
                filter.setIgnoresNBT(!filter.ignoresNBT());
                break;
            }

            case 3:
            {
                filter.setWhitelist(!filter.isWhitelist());
                break;
            }
        }

        if (button.id < 4)
        {
            VSBNet.sendChangeFilterParam(button.id);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        Minecraft.getMinecraft().renderEngine.bindTexture(VSBTextures.FILTER);
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        for (GuiButton btn : this.buttonList)
        {
            if (btn.isMouseOver())
            {
                String postfix = "";
                switch (btn.id)
                {
                    case 0:
                    {
                        postfix = Boolean.toString(IFilter.of(((ContainerFilter)this.inventorySlots).filter).isOreDictionary());
                        break;
                    }

                    case 1:
                    {
                        postfix = Boolean.toString(IFilter.of(((ContainerFilter)this.inventorySlots).filter).ignoresMetadata());
                        break;
                    }

                    case 2:
                    {
                        postfix = Boolean.toString(IFilter.of(((ContainerFilter)this.inventorySlots).filter).ignoresNBT());
                        break;
                    }

                    case 3:
                    {
                        postfix = Boolean.toString(IFilter.of(((ContainerFilter)this.inventorySlots).filter).isWhitelist());
                        break;
                    }
                }

                this.drawHoveringText(I18n.format("vsb.filter.state." + btn.id + "." + postfix), mouseX, mouseY);
            }
        }
    }

    private class FilterButton extends GuiButton
    {
        public FilterButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText)
        {
            super(buttonId, x, y, widthIn, heightIn, buttonText);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
        {
            if (this.visible)
            {
                mc.getTextureManager().bindTexture(VSBTextures.WIDGETS);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                int i = this.getHoverState(this.hovered);
                int offsetX = i == 0 ? 60 : i == 2 ? 40 : 20;
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                this.drawTexturedModalRect(this.x, this.y, 0, 0, 20, 20);
                int iconY = 20;
                switch (this.id)
                {
                    case 0:
                    {
                        iconY = IFilter.of(((ContainerFilter)GuiFilter.this.inventorySlots).filter).isOreDictionary() ? 20 : 40;
                        break;
                    }

                    case 1:
                    {
                        iconY = !IFilter.of(((ContainerFilter)GuiFilter.this.inventorySlots).filter).ignoresMetadata() ? 20 : 40;
                        break;
                    }

                    case 2:
                    {
                        iconY = !IFilter.of(((ContainerFilter)GuiFilter.this.inventorySlots).filter).ignoresNBT() ? 20 : 40;
                        break;
                    }

                    case 3:
                    {
                        iconY = IFilter.of(((ContainerFilter)GuiFilter.this.inventorySlots).filter).isWhitelist() ? 20 : 40;
                        break;
                    }
                }

                this.drawTexturedModalRect(this.x, this.y, this.id == 0 ? 0 : this.id == 1 ? 40 : this.id == 2 ? 20 : this.id == 3 ? 60 : 0, iconY, 20, 20);
                this.drawTexturedModalRect(this.x, this.y, offsetX, 0, 20, 20);
                this.mouseDragged(mc, mouseX, mouseY);
            }
        }
    }
}
