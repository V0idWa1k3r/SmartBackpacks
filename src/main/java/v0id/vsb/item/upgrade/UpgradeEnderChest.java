package v0id.vsb.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.api.vsb.item.IBackpackWrapper;
import v0id.api.vsb.item.IUpgrade;
import v0id.api.vsb.item.IUpgradeWrapper;
import v0id.vsb.item.ItemSimple;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UpgradeEnderChest extends ItemSimple implements IUpgrade
{
    public UpgradeEnderChest()
    {
        super(VSBRegistryNames.itemUpgradeEnderChest, 1);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("vsb.txt.upgrade.ender_chest.desc").split("\\|")));
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker)
    {
    }

    @Override
    public void onPulse(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity pulsar)
    {
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
