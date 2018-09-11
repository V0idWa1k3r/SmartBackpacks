package v0id.vsb.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.api.vsb.item.IBackpackWrapper;
import v0id.api.vsb.item.IUpgrade;
import v0id.api.vsb.item.IUpgradeWrapper;
import v0id.vsb.item.ItemSimple;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UpgradeQuiver extends ItemSimple implements IUpgrade
{
    public UpgradeQuiver()
    {
        super(VSBRegistryNames.itemUpgradeQuiver, 1);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("vsb.txt.upgrade.quiver.desc").split("\\|")));
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker)
    {
    }

    @Override
    public void onPulse(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity pulsar)
    {
        if (pulsar instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) pulsar;
            if (!self.getSelf().hasTagCompound())
            {
                self.getSelf().setTagCompound(new NBTTagCompound());
            }

            int index = self.getSelf().getTagCompound().getInteger("index");
            int checkDelay = self.getSelf().getTagCompound().getInteger("checkDelay");
            if (index > player.inventory.getSizeInventory())
            {
                index = 0;
            }

            ItemStack is = ((EntityPlayer) pulsar).inventory.getStackInSlot(index);
            int max = Math.min(is.getMaxStackSize(), player.inventory.getInventoryStackLimit());
            if (!is.isEmpty() && is.getItem() instanceof ItemArrow && is.getCount() < max)
            {
                for (int i = 0; i < backpack.getInventory().getSlots(); ++i)
                {
                    ItemStack is2 = backpack.getInventory().getStackInSlot(i);
                    if (!is2.isEmpty() && is2.getItem() instanceof ItemArrow)
                    {
                        int added = Math.min(max - is.getCount(), is2.getCount());
                        if (ItemHandlerHelper.canItemStacksStack(is, is2))
                        {
                            is.grow(added);
                            backpack.getInventory().extractItem(i, added, false);
                            if (is.getCount() >= max)
                            {
                                break;
                            }
                        }
                    }
                }
            }

            if (++checkDelay >= 10)
            {
                checkDelay = 0;
                boolean hasArrow = false;
                for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
                {
                    is = player.inventory.getStackInSlot(i);
                    if (!is.isEmpty() && is.getItem() instanceof ItemArrow)
                    {
                        hasArrow = true;
                        break;
                    }
                }

                if (!hasArrow)
                {
                    int arrowSlot = -1;
                    for (int i = 0; i < backpack.getInventory().getSlots(); ++i)
                    {
                        is = backpack.getInventory().getStackInSlot(i);
                        if (!is.isEmpty() && is.getItem() instanceof ItemArrow)
                        {
                            arrowSlot = i;
                            break;
                        }
                    }

                    if (arrowSlot != -1)
                    {
                        ItemStack arrow = backpack.getInventory().getStackInSlot(arrowSlot);
                        InvWrapper invWrapper = new InvWrapper(player.inventory);
                        if (ItemHandlerHelper.insertItemStacked(invWrapper, arrow, true) != arrow)
                        {
                            ItemStack inserted = ItemHandlerHelper.insertItemStacked(invWrapper, arrow.copy(), false);
                            backpack.getInventory().extractItem(arrowSlot, inserted.isEmpty() ? arrow.getCount() : arrow.getCount() - inserted.getCount(), false);
                        }
                    }
                }
            }

            self.getSelf().getTagCompound().setInteger("index", ++index);
            self.getSelf().getTagCompound().setInteger("checkDelay", ++checkDelay);
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
