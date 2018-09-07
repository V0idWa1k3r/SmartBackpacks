package v0id.api.vsb.capability;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import v0id.api.vsb.item.IBackpackWrapper;
import v0id.api.vsb.item.IUpgradeWrapper;
import v0id.api.vsb.util.ISyncable;

import javax.annotation.Nullable;

public interface IBackpack extends INBTSerializable<NBTTagCompound>, ISyncable
{
    IItemHandler getInventory();

    IItemHandler getUpgrades();

    IEnergyStorage getEnergyStorage();

    IBackpackWrapper createWrapper();

    IUpgradeWrapper createUpgradeWrapper(ItemStack upgrade);

    void onTick(@Nullable IBackpackWrapper container, @Nullable Entity ticker);

    static IBackpack of(ItemStack is)
    {
        return is.getCapability(VSBCaps.BACKPACK_CAPABILITY, null);
    }

    void copyAllDataFrom(IBackpack backpack);
}
