package v0id.vsb.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import v0id.api.vsb.capability.IFilter;
import v0id.api.vsb.data.VSBTextures;
import v0id.vsb.capability.FilterRegex;
import v0id.vsb.config.VSBCfg;
import v0id.vsb.container.ContainerFilterRegex;
import v0id.vsb.net.VSBNet;
import v0id.vsb.util.VSBUtils;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class GuiFilterRegex extends GuiContainer
{
    private final ItemStack filter;
    private GuiTextField input;
    private String lastException;
    private boolean regexCorrect = true;

    public GuiFilterRegex(ItemStack filter, int slotIndex)
    {
        super(new ContainerFilterRegex(Minecraft.getMinecraft().player.inventory, filter, slotIndex));
        this.filter = filter;
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
        this.addButton(new FilterButton(3, i + 135, j + 30, 20, 20, StringUtils.EMPTY));
        this.input = new GuiTextField(4, Minecraft.getMinecraft().fontRenderer, i + 10, j + 8, 136, 20);
        FilterRegex filter = (FilterRegex)IFilter.of(this.filter);
        if (filter != null)
        {
            this.compilePattern(filter.getPattern());
            this.input.setText(filter.getPattern());
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        super.actionPerformed(button);
        IFilter filter = IFilter.of(this.filter);
        switch (button.id)
        {
            case 0:
            {
                filter.setOreDictionary(!filter.isOreDictionary());
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
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.input.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (!this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
        {
            super.keyTyped(typedChar, keyCode);
        }

        String current = this.input.getText();
        this.input.textboxKeyTyped(typedChar, keyCode);
        if (!VSBUtils.areStringsEqual(current, this.input.getText()))
        {
            this.compilePattern(this.input.getText());
            FilterRegex filter = (FilterRegex)IFilter.of(this.filter);
            if (filter != null)
            {
                filter.setPattern(this.input.getText());
            }

            VSBNet.sendChangeFilterPattern(this.input.getText());
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        Minecraft.getMinecraft().renderEngine.bindTexture(VSBCfg.useLightUI ? VSBTextures.FILTER_REGEX_LIGHT : VSBTextures.FILTER_REGEX);
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(i + 149, j + 9, 176, this.regexCorrect ? 0 : 18, 18, 18);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.input.drawTextBox();
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
                        postfix = Boolean.toString(IFilter.of(this.filter).isOreDictionary());
                        break;
                    }

                    case 3:
                    {
                        postfix = Boolean.toString(IFilter.of(this.filter).isWhitelist());
                        break;
                    }
                }

                this.drawHoveringText(I18n.format("vsb.filter.state." + btn.id + "." + postfix), mouseX, mouseY);
            }
        }

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        if (mouseX >= i + 149 && mouseX <= i + 167 && mouseY >= j + 9 && mouseY <= j + 27)
        {
            this.drawHoveringText(this.regexCorrect ? I18n.format("vsb.filter.regex.ok") : this.lastException, mouseX, mouseY);
        }
    }

    private void compilePattern(String s)
    {
        try
        {
            Pattern.compile(s);
            this.regexCorrect = true;
        }
        catch (PatternSyntaxException ex)
        {
            this.regexCorrect = false;
            this.lastException = ex.getMessage();
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
                        iconY = IFilter.of(GuiFilterRegex.this.filter).isOreDictionary() ? 20 : 40;
                        break;
                    }

                    case 3:
                    {
                        iconY = IFilter.of(GuiFilterRegex.this.filter).isWhitelist() ? 20 : 40;
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
