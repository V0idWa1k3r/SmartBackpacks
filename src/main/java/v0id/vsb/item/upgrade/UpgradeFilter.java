package v0id.vsb.item.upgrade;

import com.google.common.base.Strings;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import v0id.api.vsb.capability.IFilter;
import v0id.api.vsb.capability.VSBCaps;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.vsb.capability.Filter;
import v0id.vsb.container.ContainerFilter;
import v0id.vsb.item.ItemSimple;
import v0id.vsb.net.VSBNet;
import v0id.vsb.util.EnumGuiType;
import v0id.vsb.util.VSBUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class UpgradeFilter extends ItemSimple
{
    public UpgradeFilter()
    {
        super(VSBRegistryNames.itemUpgradeFilter, 1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if (playerIn instanceof EntityPlayerMP)
        {
            ItemStack is = playerIn.getHeldItem(handIn);
            int slot = handIn == EnumHand.MAIN_HAND ? playerIn.inventory.currentItem : -1;
            VSBUtils.openContainer((EntityPlayerMP) playerIn, new ContainerFilter(playerIn.inventory, is, slot));
            VSBNet.sendOpenGUI(playerIn, handIn == EnumHand.MAIN_HAND ? playerIn.inventory.currentItem : 40, true, slot, EnumGuiType.FILTER);
            return new ActionResult<>(EnumActionResult.SUCCESS, is);
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("vsb.txt.upgrade.filter.desc").split("\\|")));
        for (int i = 0; i < 6; ++i)
        {
            tooltip.add(Strings.repeat(" ", 20));
        }
    }

    @Nullable
    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack)
    {
        NBTTagCompound tag = super.getNBTShareTag(stack);
        IFilter filter = IFilter.of(stack);
        if (filter != null)
        {
            if (tag == null)
            {
                tag = new NBTTagCompound();
                tag.setTag("filter", filter.serializeNBT());
            }
        }

        return tag;
    }

    @Override
    public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        super.readNBTShareTag(stack, nbt);
        IFilter filter = IFilter.of(stack);
        if (nbt != null && filter != null && nbt.hasKey("filter", Constants.NBT.TAG_COMPOUND))
        {
            filter.deserializeNBT(nbt.getCompoundTag("filter"));
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new ICapabilitySerializable<NBTTagCompound>()
        {
            private final Filter cap = new Filter();

            @Override
            public NBTTagCompound serializeNBT()
            {
                return cap.serializeNBT();
            }

            @Override
            public void deserializeNBT(NBTTagCompound nbt)
            {
                cap.deserializeNBT(nbt);
            }

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
            {
                return capability == VSBCaps.FILTER_CAPABILITY;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
            {
                return capability == VSBCaps.FILTER_CAPABILITY ? VSBCaps.FILTER_CAPABILITY.cast(this.cap) : null;
            }
        };
    }
}
