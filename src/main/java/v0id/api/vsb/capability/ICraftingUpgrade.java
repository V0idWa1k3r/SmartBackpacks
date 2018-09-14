package v0id.api.vsb.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;

public interface ICraftingUpgrade extends INBTSerializable<NBTTagCompound>
{
    IItemHandler getInventory();

    boolean[] getOreDictFlags();

    static ICraftingUpgrade of(ItemStack is)
    {
        return is.getCapability(VSBCaps.CRAFTING_UPGRADE_CAPABILITY, null);
    }
}
