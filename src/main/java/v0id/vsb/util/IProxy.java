package v0id.vsb.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import v0id.api.vsb.util.ILifecycleListener;

public interface IProxy extends ILifecycleListener
{
    void openModGui(ItemStack stack, EnumGuiType guiType, int backpackSlot);

    void setGuiExperience(int exp);

    IThreadListener getClientListener();

    EntityPlayer getClientPlayer();
}
