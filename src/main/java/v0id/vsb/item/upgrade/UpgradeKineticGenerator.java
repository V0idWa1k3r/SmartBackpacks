package v0id.vsb.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.api.vsb.item.IBackpackWrapper;
import v0id.api.vsb.item.IUpgrade;
import v0id.api.vsb.item.IUpgradeWrapper;
import v0id.vsb.config.VSBCfg;
import v0id.vsb.item.ItemSimple;

import javax.annotation.Nullable;
import javax.vecmath.Vector3d;
import java.util.Arrays;
import java.util.List;

public class UpgradeKineticGenerator extends ItemSimple implements IUpgrade
{
    public UpgradeKineticGenerator()
    {
        super(VSBRegistryNames.itemUpgradeKineticGenerator, 1);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("vsb.txt.upgrade.generator_kinetic.desc").split("\\|")));
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

        double prevPosX = self.getSelf().getTagCompound().hasKey("prevPosX") ? self.getSelf().getTagCompound().getDouble("prevPosX") : pulsar.posX;
        double prevPosY = self.getSelf().getTagCompound().hasKey("prevPosY") ? self.getSelf().getTagCompound().getDouble("prevPosY") : pulsar.posY;
        double prevPosZ = self.getSelf().getTagCompound().hasKey("prevPosZ") ? self.getSelf().getTagCompound().getDouble("prevPosZ") : pulsar.posZ;
        int power = (int) Math.round(new Vector3d(pulsar.posX - prevPosX, pulsar.posY - prevPosY, pulsar.posZ - prevPosZ).length() * VSBCfg.kineticUpgradeFEPerMeter);
        if (power > 0)
        {
           backpack.getSelfAsCapability().getEnergyStorage().receiveEnergy(power, false);
        }

        self.getSelf().getTagCompound().setDouble("prevPosX", pulsar.posX);
        self.getSelf().getTagCompound().setDouble("prevPosY", pulsar.posY);
        self.getSelf().getTagCompound().setDouble("prevPosZ", pulsar.posZ);
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
        return backpack.getMaxEnergy() > 0;
    }

    @Override
    public boolean hasSyncTag()
    {
        return false;
    }
}
