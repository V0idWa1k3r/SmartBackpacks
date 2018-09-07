package v0id.vsb.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import v0id.api.vsb.capability.IBackpack;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.api.vsb.item.IBackpackWrapper;
import v0id.api.vsb.item.IUpgrade;
import v0id.api.vsb.item.IUpgradeWrapper;
import v0id.vsb.item.ItemSimple;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UpgradeNesting extends ItemSimple implements IUpgrade
{
    public UpgradeNesting()
    {
        super(VSBRegistryNames.itemUpgradeNesting, 1);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("vsb.txt.upgrade.nesting.desc").split("\\|")));
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker)
    {
        for (ItemStack stack : backpack.getReadonlyInventory())
        {
            IBackpack backpackCap = IBackpack.of(stack);
            if (backpackCap != null)
            {
                for (IUpgradeWrapper upgradeWrapper : backpackCap.createWrapper().getReadonlyUpdatesArray())
                {
                    if (upgradeWrapper != null)
                    {
                        upgradeWrapper.getUpgrade().onTick(backpack, backpackCap.createWrapper(), upgradeWrapper, ticker);
                    }
                }
            }
        }
    }

    @Override
    public void onPulse(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity pulsar)
    {
        for (ItemStack stack : backpack.getReadonlyInventory())
        {
            IBackpack backpackCap = IBackpack.of(stack);
            if (backpackCap != null)
            {
                for (IUpgradeWrapper upgradeWrapper : backpackCap.createWrapper().getReadonlyUpdatesArray())
                {
                    if (upgradeWrapper != null)
                    {
                        upgradeWrapper.getUpgrade().onPulse(backpack, backpackCap.createWrapper(), upgradeWrapper, pulsar);
                    }
                }
            }
        }
    }

    @Override
    public boolean onItemPickup(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, EntityItem is, Entity picker)
    {
        for (ItemStack stack : backpack.getReadonlyInventory())
        {
            IBackpack backpackCap = IBackpack.of(stack);
            if (backpackCap != null)
            {
                for (IUpgradeWrapper upgradeWrapper : backpackCap.createWrapper().getReadonlyUpdatesArray())
                {
                    if (upgradeWrapper != null)
                    {
                        if (upgradeWrapper.getUpgrade().onItemPickup(backpack, backpackCap.createWrapper(), upgradeWrapper, is, picker))
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public EnumActionResult onBlockClick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, EntityPlayer player, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        for (ItemStack is : backpack.getReadonlyInventory())
        {
            IBackpack isBackpack = IBackpack.of(is);
            if (isBackpack != null)
            {
                for (IUpgradeWrapper upgradeWrapper : isBackpack.createWrapper().getReadonlyUpdatesArray())
                {
                    if (upgradeWrapper != null)
                    {
                        EnumActionResult result = upgradeWrapper.getUpgrade().onBlockClick(backpack, isBackpack.createWrapper(), upgradeWrapper, player, pos, side, hitX, hitY, hitZ);
                        if (result != null)
                        {
                            return result;
                        }
                    }
                }
            }
        }

        return null;
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
