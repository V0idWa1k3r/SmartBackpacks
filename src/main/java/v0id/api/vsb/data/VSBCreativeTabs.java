package v0id.api.vsb.data;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class VSBCreativeTabs
{
    public static final CreativeTabs TAB_VSB = new CreativeTabs(VSBRegistryNames.MODID)
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(VSBItems.BASIC_BACKPACK);
        }
    };
}
