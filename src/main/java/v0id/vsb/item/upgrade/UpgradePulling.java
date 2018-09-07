package v0id.vsb.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import v0id.api.vsb.capability.IFilter;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.api.vsb.item.IBackpackWrapper;
import v0id.api.vsb.item.IUpgradeWrapper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UpgradePulling extends UpgradeFiltered
{
    public UpgradePulling()
    {
        super(VSBRegistryNames.itemUpgradePulling);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("vsb.txt.upgrade.pulling.desc").split("\\|")));
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
        ItemStack filter = self.getSelf().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0);
        boolean canAccept = filter.isEmpty();
        if (!canAccept)
        {
            IFilter iFilter = IFilter.of(filter);
            if (iFilter != null)
            {
                canAccept = iFilter.accepts(item.getItem());
            }
        }

        if (canAccept)
        {
            ItemStack inserted = ItemHandlerHelper.insertItemStacked(backpack.getInventory(), item.getItem(), true);
            if (inserted != item.getItem())
            {
                ItemHandlerHelper.insertItemStacked(backpack.getInventory(), item.getItem().copy(), false);
                if (inserted.isEmpty())
                {
                    item.setDead();
                    if (!picker.world.isRemote)
                    {
                        picker.world.playSound(null, picker.posX, picker.posY + 0.5, picker.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((picker.world.rand.nextFloat() - picker.world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        if (picker instanceof EntityPlayer)
                        {
                            EntityTracker entitytracker = ((WorldServer)picker.world).getEntityTracker();
                            entitytracker.sendToTracking(picker, new SPacketCollectItem(item.getEntityId(), picker.getEntityId(), item.getItem().getCount()));
                        }
                    }

                    return true;
                }
            }
        }

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
