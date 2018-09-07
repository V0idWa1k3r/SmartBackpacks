package v0id.vsb.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import v0id.api.vsb.capability.IBackpack;
import v0id.api.vsb.data.VSBItems;
import v0id.api.vsb.data.VSBTextures;
import v0id.api.vsb.item.IGUIOpenable;
import v0id.vsb.container.ContainerBackpack;
import v0id.vsb.net.VSBNet;
import v0id.vsb.util.Lazy;

import java.io.IOException;

public class GuiBackpack extends GuiContainer
{
    public static final Lazy<ItemStack> BACKPACK_ICON_PROVIDER = new Lazy<>(() ->
    {
        ItemStack ret = new ItemStack(VSBItems.BASIC_BACKPACK);
        IBackpack iBackpack = IBackpack.of(ret);
        iBackpack.createWrapper().setColor(0x632a00);
        return ret;
    });

    public static final Lazy<ItemStack> UPGRADE_ICON_PROVIDER = new Lazy<>(() -> new ItemStack(VSBItems.UPGRADE_BASE));

    public final ItemStack backpack;

    public GuiBackpack(Container inventorySlotsIn, ItemStack backpack)
    {
        super(inventorySlotsIn);
        this.backpack = backpack;
        IBackpack iBackpack = IBackpack.of(this.backpack);
        this.xSize = 176;
        if (inventorySlotsIn instanceof ContainerBackpack.ContainerBackpackInventory)
        {
            switch (iBackpack.createWrapper().getBackpackType())
            {
                case BASIC:
                {
                    this.ySize = 132;
                    break;
                }

                case REINFORCED:
                {
                    this.ySize = 166;
                    break;
                }

                case ADVANCED:
                {
                    this.ySize = 202;
                    break;
                }

                case ULTIMATE:
                {
                    this.xSize = 248;
                    this.ySize = 256;
                    break;
                }
            }
        }
        else
        {
            switch (iBackpack.createWrapper().getBackpackType())
            {
                case BASIC:
                case REINFORCED:
                {
                    this.ySize = 144;
                    break;
                }

                case ADVANCED:
                case ULTIMATE:
                {
                    this.ySize = 132;
                    break;
                }
            }
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.addButton(new BackpackButton(0, i - 20, j + 12, 20, 20, StringUtils.EMPTY));
        this.addButton(new BackpackButton(1, i - 20, j + 32, 20, 20, StringUtils.EMPTY));
        if (this.inventorySlots instanceof ContainerBackpack.ContainerBackpackInventory)
        {
            this.buttonList.get(0).enabled = false;
        }
        else
        {
            this.buttonList.get(1).enabled = false;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        super.actionPerformed(button);
        if (button.id == 0 || button.id == 1)
        {
            VSBNet.requestContextContainerSwitch();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 1)
        {
            Slot s = this.getSlotUnderMouse();
            if (s != null)
            {
                ItemStack is = s.getStack();
                if (!is.isEmpty() && is.getItem() instanceof IGUIOpenable)
                {
                    VSBNet.sendOpenContainer(s.getSlotIndex(), s.slotNumber);
                    return;
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        ResourceLocation backgroundTexture = null;
        IBackpack iBackpack = IBackpack.of(this.backpack);
        if (this.inventorySlots instanceof ContainerBackpack.ContainerBackpackInventory)
        {
            switch (iBackpack.createWrapper().getBackpackType())
            {
                case BASIC:
                {
                    backgroundTexture = VSBTextures.BACKPACK_BASIC;
                    break;
                }

                case REINFORCED:
                {
                    backgroundTexture = VSBTextures.BACKPACK_REINFORCED;
                    break;
                }

                case ADVANCED:
                {
                    backgroundTexture = VSBTextures.BACKPACK_ADVANCED;
                    break;
                }

                case ULTIMATE:
                {
                    backgroundTexture = VSBTextures.BACKPACK_ULTIMATE;
                    break;
                }
            }
        }
        else
        {
            switch (iBackpack.createWrapper().getBackpackType())
            {
                case BASIC:
                {
                    backgroundTexture = VSBTextures.BACKPACK_BASIC_UPGRADES;
                    break;
                }

                case REINFORCED:
                {
                    backgroundTexture = VSBTextures.BACKPACK_REINFORCED_UPGRADES;
                    break;
                }

                case ADVANCED:
                {
                    backgroundTexture = VSBTextures.BACKPACK_ADVANCED_UPGRADES;
                    break;
                }

                case ULTIMATE:
                {
                    backgroundTexture = VSBTextures.BACKPACK_ULTIMATE_UPGRADES;
                    break;
                }
            }
        }

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        Minecraft.getMinecraft().renderEngine.bindTexture(backgroundTexture);
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    private class BackpackButton extends GuiButton
    {
        public BackpackButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText)
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
                int offsetX = i == 0 ? 80 : i == 2 ? 40 : 20;
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                this.drawTexturedModalRect(this.x, this.y, 0, 0, 20, 20);
                this.drawTexturedModalRect(this.x, this.y, offsetX, 0, 20, 20);
                if (this.id == 0)
                {
                    Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(GuiBackpack.BACKPACK_ICON_PROVIDER.get(), this.x + 2, this.y + 2);
                }
                else
                {
                    Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(GuiBackpack.UPGRADE_ICON_PROVIDER.get(), this.x + 2, this.y + 2);
                }

                this.mouseDragged(mc, mouseX, mouseY);
            }
        }
    }
}
