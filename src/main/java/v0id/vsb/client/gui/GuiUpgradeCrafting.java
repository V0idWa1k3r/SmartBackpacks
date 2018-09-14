package v0id.vsb.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import v0id.api.vsb.capability.ICraftingUpgrade;
import v0id.api.vsb.data.VSBTextures;
import v0id.vsb.config.VSBCfg;
import v0id.vsb.container.ContainerCraftingUpgrade;
import v0id.vsb.net.VSBNet;

import java.io.IOException;

public class GuiUpgradeCrafting extends GuiContainer
{
    public GuiUpgradeCrafting(ItemStack upgrade, int slotIndex)
    {
        super(new ContainerCraftingUpgrade(Minecraft.getMinecraft().player.inventory, upgrade, slotIndex));
        this.xSize = 176;
        this.ySize = 150;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        for (int k = 0; k < 9; ++k)
        {
            this.addButton(new OreDictButton(k, i + 43 + (k % 3) * 18, j + 7 + (k / 3) * 18));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        super.actionPerformed(button);
        if (button.id < 9)
        {
            ItemStack upgrade = ((ContainerCraftingUpgrade) this.inventorySlots).upgrade;
            ICraftingUpgrade craftingUpgrade = ICraftingUpgrade.of(upgrade);
            if (craftingUpgrade != null)
            {
                craftingUpgrade.getOreDictFlags()[button.id] = !craftingUpgrade.getOreDictFlags()[button.id];
                VSBNet.sendChangeOreDictParam(button.id);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        Slot s = this.getSlotUnderMouse();
        if (s != null && mouseButton <= 2)
        {
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            int sx = i + s.xPos;
            int sy = j + s.yPos;
            int dx = mouseX - sx;
            int dy = mouseY - sy;
            if (dx <= 6 && dy <= 6)
            {
                GuiButton guiButton = this.buttonList.stream().filter(b -> b.enabled && b.visible && b.x <= mouseX && b.x + b.width >= mouseX && b.y <= mouseY && b.y + b.height >= mouseY).findAny().orElse(null);
                if (guiButton != null)
                {
                    guiButton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
                    this.actionPerformed(guiButton);
                }

                return;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        Minecraft.getMinecraft().renderEngine.bindTexture(VSBCfg.useLightUI ? VSBTextures.UPGRADE_CRAFTING_LIGHT : VSBTextures.UPGRADE_CRAFTING);
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    private class OreDictButton extends GuiButton
    {
        public OreDictButton(int buttonId, int x, int y)
        {
            super(buttonId, x, y, 6, 6, StringUtils.EMPTY);
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
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                this.drawTexturedModalRect(this.x, this.y, 100, 0, 6, 6);
                int offsetX = i == 0 ? 6 : 0;
                int offsetY = i == 2 ? 12 : 6;
                this.drawTexturedModalRect(this.x, this.y, 100 + offsetX, offsetY, 6, 6);
                boolean oreDict = ICraftingUpgrade.of(((ContainerCraftingUpgrade)GuiUpgradeCrafting.this.inventorySlots).upgrade).getOreDictFlags()[this.id];
                this.drawCenteredString(Minecraft.getMinecraft().fontRenderer, I18n.format("vsb.crafting.oredict." + oreDict), this.x + 3, this.y - 1, -1);
                this.mouseDragged(mc, mouseX, mouseY);
            }
        }
    }
}
