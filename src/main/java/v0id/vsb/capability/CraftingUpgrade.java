package v0id.vsb.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import v0id.api.vsb.capability.ICraftingUpgrade;

public class CraftingUpgrade implements ICraftingUpgrade
{
    private final ItemStackHandler inventory = new ItemStackHandler(9);
    private final boolean[] oreDict = new boolean[9];

    @Override
    public IItemHandler getInventory()
    {
        return this.inventory;
    }

    @Override
    public boolean[] getOreDictFlags()
    {
        return this.oreDict;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setTag("inventory", this.inventory.serializeNBT());
        NBTTagList lst = new NBTTagList();
        for (int i = 0; i < 9; ++i)
        {
            lst.appendTag(new NBTTagByte((byte) (this.oreDict[i] ? 1 : 0)));
        }

        ret.setTag("oreDict", lst);
        return ret;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        this.inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
        NBTTagList lst = nbt.getTagList("oreDict", Constants.NBT.TAG_BYTE);
        int index = 0;
        for (NBTBase base : lst)
        {
            this.oreDict[index++] = ((NBTTagByte)base).getByte() == 1;
        }
    }
}
