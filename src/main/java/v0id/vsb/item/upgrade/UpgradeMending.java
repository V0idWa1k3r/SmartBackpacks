package v0id.vsb.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

public class UpgradeMending extends ItemSimple implements IUpgrade
{
    public UpgradeMending()
    {
        super(VSBRegistryNames.itemUpgradeMending, 1);
    }

    private int getSlot(ItemStack is)
    {
        return is.getTagCompound().getInteger("index");
    }

    private void setSlot(ItemStack is, int i)
    {
        is.getTagCompound().setInteger("index", i);
    }

    private int getPlayerXP(EntityPlayer player)
    {
        return (int) (this.getExperienceForLevel(player.experienceLevel) + (player.experience * player.xpBarCap()));
    }

    private void removeXP(EntityPlayer player)
    {
        int experience = this. getPlayerXP(player) + -2;
        if (experience < 0)
        {
            return;
        }

        player.experienceTotal = experience;
        player.experienceLevel = getLevelForExperience(experience);
        int expForLevel = getExperienceForLevel(player.experienceLevel);
        player.experience = (experience - expForLevel) / (float) player.xpBarCap();
    }

    private int getLevelForExperience(int experience)
    {
        int i = 0;
        while (this.getExperienceForLevel(i) <= experience)
        {
            i++;
        }

        return i - 1;
    }

    private int getExperienceForLevel(int level)
    {
        if (level == 0)
        {
            return 0;
        }

        if (level > 0 && level < 16)
        {
            return (int) (Math.pow(level, 2)+ 6 * level);
        }
        else
        {
            if (level > 15 && level < 32)
            {
                return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360);
            }
            else
            {
                return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220);
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("vsb.txt.upgrade.mending.desc").split("\\|")));
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker)
    {
        if (ticker instanceof EntityPlayer)
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
            if (!is.isEmpty() && is.isItemDamaged() && Enchantments.MENDING.canApply(is))
            {
                int xp = this.getPlayerXP((EntityPlayer) ticker);
                if (xp >= 2)
                {
                    is.setItemDamage(is.getItemDamage() - 1);
                    this.removeXP((EntityPlayer) ticker);
                    backpack.markInventoryDirty();
                }
            }
            else
            {
                this.setSlot(self.getSelf(), ++index);
            }
        }
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
