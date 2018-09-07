package v0id.vsb.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.oredict.ShapedOreRecipe;
import v0id.api.vsb.capability.IBackpack;

import javax.annotation.Nonnull;

public class RecipeUpgradeBackpack extends ShapedOreRecipe
{
    public RecipeUpgradeBackpack(ResourceLocation group, @Nonnull ItemStack result, CraftingHelper.ShapedPrimer primer)
    {
        super(group, result, primer);
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1)
    {
        ItemStack result = super.getCraftingResult(var1);
        ItemStack backpack = ItemStack.EMPTY;
        for (int i = 0; i < var1.getWidth() * var1.getHeight(); ++i)
        {
            ItemStack is = var1.getStackInSlot(i);
            if (!is.isEmpty() && IBackpack.of(is) != null)
            {
                backpack = is;
                break;
            }
        }

        if (!result.isEmpty() && !backpack.isEmpty())
        {
            IBackpack ofResult = IBackpack.of(result);
            IBackpack ofPack = IBackpack.of(backpack);
            if (ofResult != null)
            {
                ofResult.copyAllDataFrom(ofPack);
            }
        }

        return result;
    }
}
