package v0id.vsb.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.ArrayUtils;
import v0id.api.vsb.capability.ICraftingUpgrade;
import v0id.api.vsb.capability.VSBCaps;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.api.vsb.item.IBackpackWrapper;
import v0id.api.vsb.item.IGUIOpenable;
import v0id.api.vsb.item.IUpgrade;
import v0id.api.vsb.item.IUpgradeWrapper;
import v0id.vsb.capability.CraftingUpgrade;
import v0id.vsb.container.ContainerCraftingUpgrade;
import v0id.vsb.item.ItemSimple;
import v0id.vsb.net.VSBNet;
import v0id.vsb.util.EnumGuiType;
import v0id.vsb.util.VSBUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class UpgradeCrafting extends ItemSimple implements IUpgrade, IGUIOpenable
{
    private InventoryCrafting crafting;

    public UpgradeCrafting()
    {
        super(VSBRegistryNames.itemUpgradeCrafting, 1);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("vsb.txt.upgrade.crafting.desc").split("\\|")));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if (playerIn instanceof EntityPlayerMP)
        {
            ItemStack is = playerIn.getHeldItem(handIn);
            int slot = handIn == EnumHand.MAIN_HAND ? playerIn.inventory.currentItem : -1;
            VSBUtils.openContainer((EntityPlayerMP) playerIn, new ContainerCraftingUpgrade(playerIn.inventory, is, slot));
            VSBNet.sendOpenGUI(playerIn, handIn == EnumHand.MAIN_HAND ? playerIn.inventory.currentItem : 40, true, slot, EnumGuiType.UPGRADE_CRAFTING);
            return new ActionResult<>(EnumActionResult.SUCCESS, is);
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new ICapabilitySerializable<NBTTagCompound>()
        {
            private final CraftingUpgrade cap = new CraftingUpgrade();

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
                return capability == VSBCaps.CRAFTING_UPGRADE_CAPABILITY;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
            {
                return capability == VSBCaps.CRAFTING_UPGRADE_CAPABILITY ? VSBCaps.CRAFTING_UPGRADE_CAPABILITY.cast(this.cap) : null;
            }
        };
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker)
    {
    }

    @Override
    public void onPulse(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity pulsar)
    {
        if (pulsar instanceof EntityPlayerMP)
        {
            ICraftingUpgrade craftingUpgrade = ICraftingUpgrade.of(self.getSelf());
            if (craftingUpgrade != null)
            {
                InventoryCrafting inventoryCrafting = new InventoryCrafting(new ContainerWorkbench(((EntityPlayerMP) pulsar).inventory, pulsar.world, pulsar.getPosition()), 3, 3);
                for (int i = 0; i < craftingUpgrade.getInventory().getSlots(); ++i)
                {
                    inventoryCrafting.setInventorySlotContents(i, craftingUpgrade.getInventory().getStackInSlot(i));
                }

                IRecipe recipe = CraftingManager.findMatchingRecipe(inventoryCrafting, pulsar.world);
                if (recipe != null)
                {
                    int[] slotIndices = VSBUtils.createDefaultArray(9, -2);
                    c: for (int i = 0; i < craftingUpgrade.getInventory().getSlots(); ++i)
                    {
                        ItemStack compareTo = craftingUpgrade.getInventory().getStackInSlot(i);
                        if (compareTo.isEmpty())
                        {
                            slotIndices[i] = -1;
                            continue;
                        }

                        boolean oreDict = craftingUpgrade.getOreDictFlags()[i];
                        for (int j = 0; j < backpack.getInventory().getSlots(); ++j)
                        {
                            ItemStack is = backpack.getInventory().getStackInSlot(j);
                            if (!is.isEmpty())
                            {
                                if (oreDict)
                                {
                                    if (VSBUtils.isOreDictionaryMatch(compareTo, is))
                                    {
                                        slotIndices[i] = j;
                                        continue c;
                                    }
                                }
                                else
                                {
                                    if (ItemHandlerHelper.canItemStacksStack(is, compareTo))
                                    {
                                        slotIndices[i] = j;
                                        continue c;
                                    }
                                }
                            }
                        }
                    }

                    if (ArrayUtils.contains(slotIndices, -2))
                    {
                        return;
                    }

                    for (int i = 0; i < 9; ++i)
                    {
                        ItemStack stack = slotIndices[i] == -1 ? ItemStack.EMPTY : backpack.getInventory().getStackInSlot(slotIndices[i]);
                        inventoryCrafting.setInventorySlotContents(i, stack);
                    }

                    recipe = CraftingManager.findMatchingRecipe(inventoryCrafting, pulsar.world);
                    if (recipe != null)
                    {
                        ItemStack result = recipe.getCraftingResult(inventoryCrafting);
                        if (!result.isEmpty())
                        {
                            while (true)
                            {
                                if (ItemHandlerHelper.insertItemStacked(backpack.getInventory(), result, true) != ItemStack.EMPTY)
                                {
                                    return;
                                }

                                for (int i = 0; i < 9; ++i)
                                {
                                    int id = slotIndices[i];
                                    if (id != -1)
                                    {
                                        inventoryCrafting.setInventorySlotContents(i, backpack.getInventory().getStackInSlot(id));
                                        if (backpack.getInventory().getStackInSlot(id).isEmpty())
                                        {
                                            return;
                                        }
                                    }
                                }

                                if (!recipe.matches(inventoryCrafting, pulsar.world))
                                {
                                    return;
                                }

                                for (int id : slotIndices)
                                {
                                    if (id != -1)
                                    {
                                        backpack.getInventory().extractItem(id, 1, false);
                                    }
                                }

                                ItemHandlerHelper.insertItemStacked(backpack.getInventory(), result.copy(), false);
                            }
                        }
                    }
                }
            }
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
        return true;
    }

    @Override
    public boolean hasSyncTag()
    {
        return false;
    }

    @Override
    public void openContainer(EntityPlayerMP player, ItemStack stack, int slot, int slotID)
    {
        VSBUtils.openContainer(player, new ContainerCraftingUpgrade(player.inventory, stack, slot));
        VSBNet.sendOpenGUI(player, slotID, true, slot, EnumGuiType.UPGRADE_CRAFTING);
    }
}
