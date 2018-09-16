package v0id.vsb.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.util.Strings;
import v0id.api.vsb.capability.IFilter;
import v0id.vsb.util.VSBUtils;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FilterRegex implements IFilter
{
    private final ItemStackHandler dummyInventory = new ItemStackHandler(0);
    private String pattern = Strings.EMPTY;
    private Pattern regex;
    private boolean oreDict;
    private boolean whitelist;

    @Override
    public IItemHandler getItems()
    {
        return dummyInventory;
    }

    @Override
    public boolean accepts(ItemStack is)
    {
        boolean test = this.regex == null ? !this.whitelist : this.oreDict ? VSBUtils.anyMatch(VSBUtils.getOreNames(is), s -> this.regex.matcher(s).matches()) : this.regex.matcher(is.getItem().getRegistryName().toString()).matches();
        return this.whitelist == test;
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
        return false;
    }

    @Override
    public void setIgnoresMeta(boolean b)
    {
    }

    @Override
    public boolean ignoresNBT()
    {
        return false;
    }

    @Override
    public void setIgnoresNBT(boolean b)
    {
    }

    @Override
    public boolean isWhitelist()
    {
        return this.whitelist;
    }

    @Override
    public void setWhitelist(boolean b)
    {
        this.whitelist = b;
    }

    public void setPattern(String pattern)
    {
        this.pattern = pattern;
        try
        {
            this.regex = Pattern.compile(this.pattern);
        }
        catch (PatternSyntaxException ex)
        {
            // Silent catch, the user might have inputted invalid pattern
        }
    }

    public String getPattern()
    {
        return this.pattern;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setString("regex", this.pattern);
        ret.setBoolean("whitelist", this.whitelist);
        ret.setBoolean("oreDict", this.oreDict);
        return ret;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        this.pattern = nbt.getString("regex");
        try
        {
            this.regex = Pattern.compile(this.pattern);
        }
        catch (PatternSyntaxException ex)
        {
            // Silent catch, the user might have inputted invalid pattern
        }

        this.whitelist = nbt.getBoolean("whitelist");
        this.oreDict = nbt.getBoolean("oreDict");
    }
}
