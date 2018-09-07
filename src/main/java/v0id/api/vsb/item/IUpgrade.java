package v0id.api.vsb.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.util.Strings;

import javax.annotation.Nullable;

public interface IUpgrade
{
    void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker);

    void onPulse(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity pulsar);

    boolean onItemPickup(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, EntityItem item, Entity picker);

    void onInstalled(IBackpackWrapper backpack, IUpgradeWrapper self);

    void onUninstalled(IBackpackWrapper backpack, IUpgradeWrapper self);

    boolean canInstall(IBackpackWrapper backpack, IUpgradeWrapper self);

    boolean hasSyncTag();

    default NBTTagCompound getSyncTag()
    {
        return null;
    }

    default String getSyncTagName()
    {
        return Strings.EMPTY;
    }

    default EnumActionResult onBlockClick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, EntityPlayer player, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        return null;
    }
}
