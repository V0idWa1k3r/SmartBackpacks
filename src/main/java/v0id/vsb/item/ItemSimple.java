package v0id.vsb.item;

import net.minecraft.item.Item;
import v0id.api.vsb.data.VSBCreativeTabs;
import v0id.api.vsb.data.VSBRegistryNames;

public class ItemSimple extends Item
{
    public ItemSimple(String name)
    {
        this(name, 64);
    }

    public ItemSimple(String name, int maxStack)
    {
        super();
        this.setRegistryName(VSBRegistryNames.asLocation(name));
        this.setUnlocalizedName(this.getRegistryName().toString().replace(':', '.'));
        this.setMaxStackSize(maxStack);
        this.setCreativeTab(VSBCreativeTabs.TAB_VSB);
    }
}
