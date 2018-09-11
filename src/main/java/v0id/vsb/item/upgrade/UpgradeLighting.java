package v0id.vsb.item.upgrade;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
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

public class UpgradeLighting extends ItemSimple implements IUpgrade
{
    public UpgradeLighting()
    {
        super(VSBRegistryNames.itemUpgradeLighting, 1);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("vsb.txt.upgrade.lighting.desc").split("\\|")));
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker)
    {
        BlockPos playerPos = ticker.getPosition();
        int light = ticker.world.getLight(playerPos);
        if (light <= 7 && ticker.world.isAirBlock(playerPos))
        {
            int torchSlot = -1;
            for (int i = 0; i < backpack.getInventory().getSlots(); ++i)
            {
                ItemStack is = backpack.getInventory().getStackInSlot(i);
                if (!is.isEmpty() && is.getItem() instanceof ItemBlock && ((ItemBlock) is.getItem()).getBlock() instanceof BlockTorch)
                {
                    torchSlot = i;
                    break;
                }
            }

            if (torchSlot != -1)
            {
                ItemStack torch = backpack.getInventory().getStackInSlot(torchSlot);
                BlockTorch torchBlock = (BlockTorch) ((ItemBlock)torch.getItem()).getBlock();
                List<EnumFacing> possibleFacings = Lists.newArrayList();
                for (EnumFacing facing : EnumFacing.values())
                {
                    if (facing == EnumFacing.UP)
                    {
                        continue;
                    }

                    BlockPos at = playerPos.offset(facing);
                    IBlockState blockAt = ticker.world.getBlockState(at);
                    if (facing == EnumFacing.DOWN)
                    {
                        if (blockAt.getBlock().canPlaceTorchOnTop(blockAt, ticker.world, at))
                        {
                            possibleFacings.add(facing);
                        }
                    }
                    else
                    {
                        if (blockAt.getBlockFaceShape(ticker.world, at, facing.getOpposite()) == BlockFaceShape.SOLID)
                        {
                            possibleFacings.add(facing);
                        }
                    }
                }

                if (!possibleFacings.isEmpty())
                {
                    EnumFacing randomFacing = possibleFacings.get(ticker.world.rand.nextInt(possibleFacings.size()));
                    ticker.world.setBlockState(playerPos, torchBlock.getStateForPlacement(ticker.world, playerPos, randomFacing, 0, 0, 0, 0, (EntityLivingBase) ticker));
                    backpack.getInventory().extractItem(torchSlot, 1, false);
                }
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
