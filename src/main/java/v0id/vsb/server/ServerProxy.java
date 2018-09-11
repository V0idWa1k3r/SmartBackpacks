package v0id.vsb.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import v0id.vsb.util.EnumGuiType;
import v0id.vsb.util.IProxy;

public class ServerProxy implements IProxy
{
    @Override
    public void openModGui(ItemStack stack, EnumGuiType guiType, int backpackSlot)
    {
    }

    @Override
    public void setGuiExperience(int exp)
    {
    }

    @Override
    public IThreadListener getClientListener()
    {
        return null;
    }

    @Override
    public EntityPlayer getClientPlayer()
    {
        return null;
    }
}
