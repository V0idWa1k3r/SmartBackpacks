package v0id.vsb.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import v0id.api.vsb.capability.IFilter;
import v0id.vsb.util.VSBUtils;

public class Filter implements IFilter
{
    private final ItemStackHandler inventory = new ItemStackHandler(18);
    private boolean oreDict;
    private boolean ignoreMeta;
    private boolean ignoreNBT;
    private boolean isWhitelist;

    @Override
    public IItemHandler getItems()
    {
        return this.inventory;
    }

    @Override
    public boolean accepts(ItemStack is)
    {
        if (is.isEmpty())
        {
            return false;
        }

        for (int i = 0; i < this.inventory.getSlots(); i++)
        {
            ItemStack stack = this.inventory.getStackInSlot(i);
            IFilter filter = IFilter.of(stack);
            if (filter != null)
            {
                boolean accepts = filter.accepts(is);
                if (accepts == filter.isWhitelist())
                {
                    return accepts == this.isWhitelist;
                }
            }
            else
            {
                if (this.oreDict)
                {
                    if (VSBUtils.isOreDictionaryMatch(is, stack))
                    {
                        return this.isWhitelist;
                    }
                }

                boolean itemEquals = is.getItem() == stack.getItem();
                boolean metaEquals = is.getItemDamage() == stack.getItemDamage() || this.ignoreMeta;
                boolean nbtEquals = ItemStack.areItemStackTagsEqual(is, stack) || this.ignoreNBT;
                if (itemEquals && metaEquals && nbtEquals)
                {
                    return this.isWhitelist;
                }
            }
        }

        return !this.isWhitelist;
    }

    @Override
    public boolean isOreDictionary()
    {
        return this.oreDict;
    }

    @Override
    public void setOreDictionary(boolean b)
    {
        this.oreDict = b;
    }

    @Override
    public boolean ignoresMetadata()
    {
        return this.ignoreMeta;
    }

    @Override
    public void setIgnoresMeta(boolean b)
    {
        this.ignoreMeta = b;
    }

    @Override
    public boolean ignoresNBT()
    {
        return this.ignoreNBT;
    }

    @Override
    public void setIgnoresNBT(boolean b)
    {
        this.ignoreNBT = b;
    }

    @Override
    public boolean isWhitelist()
    {
        return this.isWhitelist;
    }

    @Override
    public void setWhitelist(boolean b)
    {
        this.isWhitelist = b;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setTag("inventory", this.inventory.serializeNBT());
        ret.setBoolean("ore", this.oreDict);
        ret.setBoolean("meta", this.ignoreMeta);
        ret.setBoolean("nbt", this.ignoreNBT);
        ret.setBoolean("whitelist", this.isWhitelist);
        return ret;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        this.inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
        this.checkV2();
        this.oreDict = nbt.getBoolean("ore");
        this.ignoreMeta = nbt.getBoolean("meta");
        this.ignoreNBT = nbt.getBoolean("nbt");
        this.isWhitelist = nbt.getBoolean("whitelist");
    }

    private void checkV2()
    {
        if (this.inventory.getSlots() == 9)
        {
            ItemStack[] stacks = VSBUtils.capabilityToArray(this.inventory, ItemStack.class, s -> s);
            this.inventory.setSize(18);
            for (int i = 0; i < stacks.length; ++i)
            {
                this.inventory.setStackInSlot(i, stacks[i]);
            }
        }
    }
}
