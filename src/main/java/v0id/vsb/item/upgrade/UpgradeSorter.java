package v0id.vsb.item.upgrade;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.api.vsb.item.IBackpackWrapper;
import v0id.api.vsb.item.IUpgrade;
import v0id.api.vsb.item.IUpgradeWrapper;
import v0id.vsb.container.ContainerBackpack;
import v0id.vsb.item.ItemSimple;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class UpgradeSorter extends ItemSimple implements IUpgrade
{
    public UpgradeSorter()
    {
        super(VSBRegistryNames.itemUpgradeSorting, 1);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("vsb.txt.upgrade.sorting.desc").split("\\|")));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if (playerIn.isSneaking() && !worldIn.isRemote)
        {
            ItemStack is = playerIn.getHeldItem(handIn);
            if (!is.hasTagCompound())
            {
                is.setTagCompound(new NBTTagCompound());
            }

            is.getTagCompound().setBoolean("light", !is.getTagCompound().getBoolean("light"));
            worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1, 1);
            return new ActionResult<>(EnumActionResult.SUCCESS, is);
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker)
    {
    }

    @Override
    public void onPulse(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity pulsar)
    {
        int current = self.getSelf().hasTagCompound() ? self.getSelf().getTagCompound().getByte("pulses") : 0;
        if (++current >= 10)
        {
            current = 0;
            List<ItemStack> allItems = Lists.newArrayList();
            for (int i = 0; i < backpack.getInventory().getSlots(); ++i)
            {
                ItemStack is = backpack.getInventory().getStackInSlot(i).copy();
                if (!is.isEmpty())
                {
                    backpack.getInventory().extractItem(i, Integer.MAX_VALUE, false);
                    allItems.add(is);
                }
            }

            if (!(self.getSelf().hasTagCompound() && self.getSelf().getTagCompound().getBoolean("light")))
            {
                allItems.sort(new ComparatorItemStack());
            }

            for (ItemStack is : allItems)
            {
                ItemHandlerHelper.insertItemStacked(backpack.getInventory(), is, false);
            }

            if (pulsar instanceof EntityPlayer)
            {
                Container openContainer = ((EntityPlayer) pulsar).openContainer;
                if (container instanceof ContainerBackpack)
                {
                    for (Slot s : ((ContainerBackpack) container).inventorySlots)
                    {
                        s.onSlotChanged();
                    }
                }
            }
        }

        if (!self.getSelf().hasTagCompound())
        {
            self.getSelf().setTagCompound(new NBTTagCompound());
        }

        self.getSelf().getTagCompound().setByte("pulses", (byte) current);
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

    /**
     * Comparing algorithm courtesy of CPW (https://github.com/cpw/inventorysorter/blob/master/src/main/java/cpw/mods/inventorysorter/InventoryHandler.java#L159)
     * To make sure the result is desired by the end user.
     */
    private static class ComparatorItemStack implements Comparator<ItemStack>
    {
        @Override
        public int compare(ItemStack o1, ItemStack o2)
        {
            if (o1 == o2)
            {
                return 0;
            }

            if (o1.getItem() != o2.getItem())
            {
                return String.valueOf(o1.getItem().getRegistryName()).compareTo(String.valueOf(o2.getItem().getRegistryName()));
            }

            if (o1.getMetadata() != o2.getMetadata())
            {
                return Ints.compare(o1.getMetadata(), o2.getMetadata());
            }

            if (ItemStack.areItemStackTagsEqual(o1, o2))
            {
                return 0;
            }

            return Ints.compare(System.identityHashCode(o1), System.identityHashCode(o2));
        }
    }
}
