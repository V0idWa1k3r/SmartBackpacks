package v0id.vsb.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import v0id.api.vsb.capability.IFilter;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.api.vsb.item.IBackpackWrapper;
import v0id.api.vsb.item.IUpgradeWrapper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UpgradeMagnet extends UpgradeFiltered
{
    public UpgradeMagnet()
    {
        super(VSBRegistryNames.itemUpgradeMagnet);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("vsb.txt.upgrade.magnet.desc").split("\\|")));
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker)
    {
    }

    @Override
    public void onPulse(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity pulsar)
    {
        if (!pulsar.isSneaking() && pulsar instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) pulsar;
            IFilter filter = IFilter.of(self.getSelf().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0));
            List<EntityItem> items = pulsar.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pulsar.posX - 12, pulsar.posY - 6, pulsar.posZ - 12, pulsar.posX + 12, pulsar.posY + 6, pulsar.posZ + 12), e -> filter == null || filter.accepts(e.getItem()));
            items.forEach(e -> e.onCollideWithPlayer(player));
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
