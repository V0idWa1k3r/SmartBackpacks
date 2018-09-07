package v0id.vsb.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import v0id.api.vsb.capability.IFilter;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.api.vsb.item.IBackpackWrapper;
import v0id.api.vsb.item.IUpgradeWrapper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UpgradeCompressor extends UpgradeFiltered
{
    private InventoryCrafting crafting_3x3;
    private InventoryCrafting crafting_2x2;

    public UpgradeCompressor()
    {
        super(VSBRegistryNames.itemUpgradeCompressor);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("vsb.txt.upgrade.compressor.desc").split("\\|")));
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker)
    {
    }

    @Override
    public void onPulse(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity pulsar)
    {
        this.crafting_3x3 = new InventoryCrafting(new ContainerWorkbench(((EntityPlayer)pulsar).inventory, pulsar.world, pulsar.getPosition()), 3, 3);
        this.crafting_2x2 = new InventoryCrafting(new ContainerWorkbench(((EntityPlayer)pulsar).inventory, pulsar.world, pulsar.getPosition()), 3, 3);
        int index = self.getSelf().hasTagCompound() ? self.getSelf().getTagCompound().getInteger("index") : 0;
        if (index >= backpack.getInventory().getSlots())
        {
            index = 0;
        }

        if (!self.getSelf().hasTagCompound())
        {
            self.getSelf().setTagCompound(new NBTTagCompound());
        }

        ItemStack is = backpack.getInventory().getStackInSlot(index);
        boolean accepts = true;
        IFilter filter = IFilter.of(self.getSelf().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0));
        if (filter != null)
        {
            accepts = filter.accepts(is);
        }

        if (accepts)
        {
            int amt = is.getCount() >= 9 ? 9 : 4;
            if (amt == 9)
            {
                for (int i = 0; i < 9; ++i)
                {
                    this.crafting_3x3.setInventorySlotContents(i, is.copy());
                }

                IRecipe recipe9x9 = CraftingManager.findMatchingRecipe(this.crafting_3x3, pulsar.world);
                if (recipe9x9 != null)
                {
                    this.compress(recipe9x9, backpack.getInventory(), is, 9);
                }
                else
                {
                    amt = 4;
                }
            }

            if (amt == 4)
            {
                for (int i = 0; i < 4; ++i)
                {
                    this.crafting_2x2.setInventorySlotContents(i, is.copy());
                }

                IRecipe recipe2x2 = CraftingManager.findMatchingRecipe(this.crafting_2x2, pulsar.world);
                if (recipe2x2 != null)
                {
                    this.compress(recipe2x2, backpack.getInventory(), is, 4);
                }
            }
        }

        ++index;
        if (index >= backpack.getReadonlyInventory().length)
        {
            index = 0;
        }

        self.getSelf().getTagCompound().setInteger("index", index);

    }

    private void compress(IRecipe recipe, IItemHandler to, ItemStack from, int amt)
    {
        int craftsNum = from.getCount() / amt;
        ItemStack result = recipe.getCraftingResult(amt == 4 ? this.crafting_2x2 : this.crafting_3x3).copy();
        result.setCount(result.getCount() * craftsNum);
        if (!result.isEmpty() && ItemHandlerHelper.insertItemStacked(to, result, true).isEmpty())
        {
            from.setCount(from.getCount() - craftsNum * amt);
            ItemHandlerHelper.insertItemStacked(to, result, false);
        }
    }

    @Override
    public boolean onItemPickup(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, EntityItem item, Entity picker)
    {
        return false;
    }

    @Override
    public void onInstalled(IBackpackWrapper backpack, IUpgradeWrapper self)
    {

    }

    @Override
    public void onUninstalled(IBackpackWrapper backpack, IUpgradeWrapper self)
    {

    }

    @Override
    public boolean canInstall(IBackpackWrapper backpack, IUpgradeWrapper self)
    {
        return !Arrays.stream(backpack.getReadonlyUpdatesArray()).filter(Objects::nonNull).map(IUpgradeWrapper::getSelf).anyMatch(i -> i.getItem() == self.getSelf().getItem());
    }

    @Override
    public boolean hasSyncTag()
    {
        return false;
    }
}
