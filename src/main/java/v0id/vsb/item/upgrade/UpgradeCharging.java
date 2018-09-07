package v0id.vsb.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.api.vsb.item.IBackpackWrapper;
import v0id.api.vsb.item.IUpgrade;
import v0id.api.vsb.item.IUpgradeWrapper;
import v0id.vsb.item.ItemSimple;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UpgradeCharging extends ItemSimple implements IUpgrade
{
    public UpgradeCharging()
    {
        super(VSBRegistryNames.itemUpgradeCharging, 1);
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
        tooltip.addAll(Arrays.asList(I18n.format("vsb.txt.upgrade.charging.desc").split("\\|")));
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
        IEnergyStorage energyStorage = is.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage != null)
        {
            int currentItem = energyStorage.getEnergyStored();
            int maxItem = energyStorage.getMaxEnergyStored();
            int currentSelf = backpack.getSelfAsCapability().getEnergyStorage().getEnergyStored();
            int needed = Math.min(maxItem - currentItem, currentSelf);
            if (needed > 0)
            {
                backpack.getSelfAsCapability().getEnergyStorage().extractEnergy(energyStorage.receiveEnergy(needed, false), false);
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
        return backpack.getMaxEnergy() > 0 && !Arrays.stream(backpack.getReadonlyUpdatesArray()).filter(Objects::nonNull).map(IUpgradeWrapper::getSelf).anyMatch(i -> i.getItem() == self.getSelf().getItem());
    }

    @Override
    public boolean hasSyncTag()
    {
        return false;
    }
}
