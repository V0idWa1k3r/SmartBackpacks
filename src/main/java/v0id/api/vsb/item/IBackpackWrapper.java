package v0id.api.vsb.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import v0id.api.vsb.capability.IBackpack;

public interface IBackpackWrapper
{
    IBackpack getSelfAsCapability();

    IUpgradeWrapper[] getReadonlyUpdatesArray();

    ItemStack[] getReadonlyInventory();

    ItemStack getSelf();

    IItemHandler getInventory();

    IItemHandler getUpgradesInventory();

    EnumBackpackType getBackpackType();

    int getMaxUpgrades();

    /**
     * Client-only
     */
    NBTTagCompound getUpgradesSyncTag();

    int getColor();

    void setColor(int newColor);

    default int getCurrentUpdatesAmt()
    {
        return getReadonlyUpdatesArray().length;
    }

    int getMaxEnergy();

    void setMaxEnergy(int i);

    /**
     * Invoke if you have to use ItemStack.shrink/grow/setCount
     */
    void markInventoryDirty();
}
