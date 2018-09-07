package v0id.vsb.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.ObjectUtils;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.api.vsb.item.IBackpackWrapper;
import v0id.api.vsb.item.IUpgrade;
import v0id.api.vsb.item.IUpgradeWrapper;
import v0id.vsb.item.ItemSimple;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UpgradeWaterSpring extends ItemSimple implements IUpgrade
{
    public UpgradeWaterSpring()
    {
        super(VSBRegistryNames.itemUpgradeWaterSpring, 1);
    }

    private int getSlot(ItemStack is)
    {
        return is.getTagCompound().getInteger("index");
    }

    private void setSlot(ItemStack is, int i)
    {
        is.getTagCompound().setInteger("index", i);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("vsb.txt.upgrade.water_spring.desc").split("\\|")));
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker)
    {
    }

    @Override
    public void onPulse(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity pulsar)
    {
        if (!self.getSelf().hasTagCompound())
        {
            self.getSelf().setTagCompound(new NBTTagCompound());
        }

        int index = this.getSlot(self.getSelf());
        if (index >= backpack.getInventory().getSlots())
        {
            index = 0;
        }

        ItemStack is = backpack.getInventory().getStackInSlot(index);
        if (!is.isEmpty() && is.getItem() == Items.BUCKET)
        {
            ItemStack insert = new ItemStack(Items.WATER_BUCKET, 1, 0);
            if (ItemHandlerHelper.insertItemStacked(backpack.getInventory(), insert, true).isEmpty())
            {
                backpack.getInventory().extractItem(index, 1, false);
                ItemHandlerHelper.insertItemStacked(backpack.getInventory(), insert, false);
                is = backpack.getInventory().getStackInSlot(index);
                if (!is.isEmpty() && is.getItem() == Items.BUCKET && is.getCount() >= 1)
                {
                    return;
                }
            }
            else
            {
                if (is.getCount() == 1)
                {
                    backpack.getInventory().extractItem(index, 1, false);
                    backpack.getInventory().insertItem(index, insert, false);
                }
            }
        }
        else
        {
            IFluidHandler fluidHandlerItem = ObjectUtils.firstNonNull(is.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null), is.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null));
            if (fluidHandlerItem != null)
            {
                FluidStack fs = fluidHandlerItem.drain(Integer.MAX_VALUE, false);
                boolean accepts = fs == null || fs.getFluid() == FluidRegistry.WATER;
                fluidHandlerItem.fill(new FluidStack(FluidRegistry.WATER, Integer.MAX_VALUE), true);
            }
        }

        this.setSlot(self.getSelf(), ++index);
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
