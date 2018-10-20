package v0id.vsb.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import org.apache.commons.lang3.ObjectUtils;
import v0id.api.vsb.capability.IBackpack;
import v0id.api.vsb.capability.VSBCaps;
import v0id.api.vsb.data.VSBCreativeTabs;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.api.vsb.item.EnumBackpackType;
import v0id.api.vsb.item.IGUIOpenable;
import v0id.api.vsb.item.IUpgradeWrapper;
import v0id.vsb.capability.Backpack;
import v0id.vsb.container.ContainerBackpack;
import v0id.vsb.item.upgrade.UpgradeEnderChest;
import v0id.vsb.net.VSBNet;
import v0id.vsb.util.EnumGuiType;
import v0id.vsb.util.VSBUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ItemBackpack extends Item implements IGUIOpenable
{
    public final EnumBackpackType backpackType;

    public ItemBackpack(EnumBackpackType type, String name)
    {
        super();
        this.backpackType = type;
        this.setRegistryName(VSBRegistryNames.asLocation(name));
        this.setUnlocalizedName(this.getRegistryName().toString().replace(':', '.'));
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setCreativeTab(VSBCreativeTabs.TAB_VSB);
    }

    @Nullable
    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack)
    {
        NBTTagCompound tag = ObjectUtils.firstNonNull(super.getNBTShareTag(stack), new NBTTagCompound());
        IBackpack cap = IBackpack.of(stack);
        tag.setTag("backpack_tag", cap.serializeSync());
        return tag;
    }

    @Override
    public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        super.readNBTShareTag(stack, nbt);
        if (nbt != null)
        {
            IBackpack cap = IBackpack.of(stack);
            cap.deserializeSync(nbt.getCompoundTag("backpack_tag"));
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (tab != this.getCreativeTab())
        {
            return;
        }

        for (EnumDyeColor dyeColor : EnumDyeColor.values())
        {
            float[] dyeColorValues = dyeColor.getColorComponentValues();

            int r = (int)(dyeColorValues[0] * 255.0F);
            int g = (int)(dyeColorValues[1] * 255.0F);
            int b = (int)(dyeColorValues[2] * 255.0F);

            int finalColor = ((r << 8) + g << 8) + b;

            ItemStack is = new ItemStack(this, 1, 0);
            IBackpack.of(is).createWrapper().setColor(finalColor);
            items.add(is);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if (playerIn instanceof EntityPlayerMP)
        {
            ItemStack backpack = playerIn.getHeldItem(handIn);
            if (!playerIn.isSneaking() && Arrays.stream(IBackpack.of(backpack).createWrapper().getReadonlyUpdatesArray()).anyMatch(u -> u != null && u.getUpgrade() instanceof UpgradeEnderChest))
            {
                playerIn.displayGUIChest(playerIn.getInventoryEnderChest());
            }
            else
            {
                int slot = handIn == EnumHand.MAIN_HAND ? playerIn.inventory.currentItem : -1;
                VSBUtils.openContainer((EntityPlayerMP) playerIn, new ContainerBackpack.ContainerBackpackInventory(backpack, playerIn.inventory, slot, handIn == EnumHand.MAIN_HAND ? playerIn.inventory.currentItem : 40));
                VSBNet.sendOpenGUI(playerIn, handIn == EnumHand.MAIN_HAND ? playerIn.inventory.currentItem : 40, true, slot, EnumGuiType.BACKPACK);
            }
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
        ItemStack is = player.getHeldItem(EnumHand.MAIN_HAND);
        IBackpack backpack = IBackpack.of(is);
        if (backpack == null)
        {
            backpack = IBackpack.of(player.getHeldItem(EnumHand.OFF_HAND));
        }

        for (IUpgradeWrapper upgradeWrapper : backpack.createWrapper().getReadonlyUpdatesArray())
        {
            if (upgradeWrapper != null)
            {
                EnumActionResult result = upgradeWrapper.getUpgrade().onBlockClick(null, backpack.createWrapper(), upgradeWrapper, player, pos, side, hitX, hitY, hitZ);
                if (result != null)
                {
                    return result;
                }
            }
        }

        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("vsb:upgrade_damage_bar");
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("vsb:upgrade_damage_bar") ? stack.getTagCompound().getFloat("vsb:upgrade_damage_bar") : super.getDurabilityForDisplay(stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        if (!worldIn.isRemote)
        {
            IBackpack.of(stack).onTick(null, entityIn);
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        IBackpack backpack = IBackpack.of(stack);
        if (backpack != null)
        {
            int maxEnergy = backpack.createWrapper().getMaxEnergy();
            if (maxEnergy > 0)
            {
                tooltip.add(I18n.format("vsb.txt.backpack.desc.rf", backpack.getEnergyStorage().getEnergyStored(), maxEnergy));
            }
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return oldStack.getItem() != newStack.getItem();
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new ICapabilitySerializable<NBTTagCompound>()
        {
            private final Backpack cap = new Backpack(stack, ItemBackpack.this.backpackType);

            @Override
            public NBTTagCompound serializeNBT()
            {
                return this.cap.serializeNBT();
            }

            @Override
            public void deserializeNBT(NBTTagCompound nbt)
            {
                this.cap.deserializeNBT(nbt);
            }

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
            {
                return capability == VSBCaps.BACKPACK_CAPABILITY || capability == CapabilityEnergy.ENERGY;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
            {
                return capability == VSBCaps.BACKPACK_CAPABILITY ? VSBCaps.BACKPACK_CAPABILITY.cast(this.cap) : capability == CapabilityEnergy.ENERGY ? CapabilityEnergy.ENERGY.cast(this.cap.getEnergyStorage()) : null;
            }
        };
    }

    @Override
    public void openContainer(EntityPlayerMP player, ItemStack stack, int slot, int slotID)
    {
        if (!player.isSneaking() && Arrays.stream(IBackpack.of(stack).createWrapper().getReadonlyUpdatesArray()).anyMatch(u -> u != null && u.getUpgrade() instanceof UpgradeEnderChest))
        {
            player.displayGUIChest(player.getInventoryEnderChest());
        }
        else
        {
            VSBUtils.openContainer(player, new ContainerBackpack.ContainerBackpackInventory(stack, player.inventory, slot, slotID));
            VSBNet.sendOpenGUI(player, slotID, false, slot, EnumGuiType.BACKPACK_NESTED);
        }
    }
}
