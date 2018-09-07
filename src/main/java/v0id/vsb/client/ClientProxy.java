package v0id.vsb.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import v0id.vsb.client.gui.GuiBackpack;
import v0id.vsb.client.gui.GuiFilter;
import v0id.vsb.client.gui.GuiUpgradeEnderStorage;
import v0id.vsb.client.gui.GuiUpgradeFiltered;
import v0id.vsb.container.ContainerBackpack;
import v0id.vsb.util.EnumGuiType;
import v0id.vsb.util.IProxy;

public class ClientProxy implements IProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        ClientRegistry.onPreInit();
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        ClientRegistry.onInit();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        ClientRegistry.onPostInit();
    }

    @Override
    public void openModGui(ItemStack stack, EnumGuiType guiType, int backpackSlot)
    {
        GuiScreen gui = null;
        switch (guiType)
        {
            case WORN_BACKPACK:
            case BACKPACK_NESTED:
            case BACKPACK:
            {
                gui = new GuiBackpack(new ContainerBackpack.ContainerBackpackInventory(stack, this.getClientPlayer().inventory, backpackSlot, -1), stack);
                break;
            }

            case WORN_BACKPACK_UPGRADES:
            case BACKPACK_UPGRADES:
            {
                gui = new GuiBackpack(new ContainerBackpack.ContainerBackpackUpgrades(stack, this.getClientPlayer().inventory, backpackSlot, this.getClientPlayer().openContainer instanceof ContainerBackpack.ContainerBackpackInventory ? (ContainerBackpack) this.getClientPlayer().openContainer : null, -1), stack);
                break;
            }

            case FILTER:
            {
                gui = new GuiFilter(stack, backpackSlot);
                break;
            }

            case UPGRADE:
            {
                gui = new GuiUpgradeFiltered(stack, backpackSlot);
                break;
            }

            case UPGRADE_ENDER_STORAGE:
            {
                gui = new GuiUpgradeEnderStorage(stack, backpackSlot);
                break;
            }
        }

        Minecraft.getMinecraft().displayGuiScreen(gui);
    }

    @Override
    public IThreadListener getClientListener()
    {
        return Minecraft.getMinecraft();
    }

    @Override
    public EntityPlayer getClientPlayer()
    {
        return Minecraft.getMinecraft().player;
    }
}
