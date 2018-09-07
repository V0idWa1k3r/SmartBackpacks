package v0id.api.vsb.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;

public interface IFilter extends INBTSerializable<NBTTagCompound>
{
    IItemHandler getItems();

    boolean accepts(ItemStack is);

    boolean isOreDictionary();

    void setOreDictionary(boolean b);

    boolean ignoresMetadata();

    void setIgnoresMeta(boolean b);

    boolean ignoresNBT();

    void setIgnoresNBT(boolean b);

    boolean isWhitelist();

    void setWhitelist(boolean b);

    static IFilter of(ItemStack is)
    {
        return is.getCapability(VSBCaps.FILTER_CAPABILITY, null);
    }
}
