package v0id.vsb.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;
import v0id.api.vsb.capability.IBackpack;
import v0id.api.vsb.item.IUpgrade;
import v0id.api.vsb.item.IUpgradeWrapper;
import v0id.vsb.item.upgrade.UpgradeNesting;

import javax.annotation.Nonnull;

public abstract class ContainerBackpack extends Container
{
    public final ItemStack backpack;
    public final int backpackSlot;
    public ContainerBackpack contextContainer;
    public Container parentContainer;
    public int backpackSlotID;

    public ContainerBackpack(ItemStack backpack, int backpackSlot, int backpackSlotID, Container parentContainer, ContainerBackpack contextContainer)
    {
        this.backpack = backpack;
        this.backpackSlot = backpackSlot;
        this.contextContainer = contextContainer;
        this.parentContainer = parentContainer;
        this.backpackSlotID = backpackSlotID;
    }

    @Override
    public void addListener(IContainerListener listener)
    {
        if (!this.listeners.contains(listener))
        {
            super.addListener(listener);
        }
    }

    public void addPlayerInventory(InventoryPlayer playerInventory, int offsetX, int offsetY)
    {
        for (int l = 0; l < 3; ++l)
        {
            for (int j1 = 0; j1 < 9; ++j1)
            {
                this.addSlotToContainer(new Slot(playerInventory, 9 + j1 + l * 9, offsetX + j1 * 18, offsetY + l * 18)
                {
                    @Override
                    public boolean canTakeStack(EntityPlayer playerIn)
                    {
                        return super.canTakeStack(playerIn) && this.getSlotIndex() != ContainerBackpack.this.backpackSlot;
                    }
                });
            }
        }

        for (int i1 = 0; i1 < 9; ++i1)
        {
            this.addSlotToContainer(new Slot(playerInventory, i1, offsetX + i1 * 18, offsetY + 58)
            {
                @Override
                public boolean canTakeStack(EntityPlayer playerIn)
                {
                    return super.canTakeStack(playerIn) && this.getSlotIndex() != ContainerBackpack.this.backpackSlot;
                }
            });
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.inventorySlots.size() - 36)
            {
                if (!this.mergeItemStack(itemstack1, this.inventorySlots.size() - 36, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.inventorySlots.size() - 36, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (!ItemHandlerHelper.canItemStacksStack(itemstack1, itemstack))
            {
                slot.onTake(playerIn, itemstack);
            }
        }

        return itemstack;
    }

    public static class ContainerBackpackInventory extends ContainerBackpack
    {
        public ContainerBackpackInventory(ItemStack backpack, InventoryPlayer inventoryPlayer, int slotIndex, int slotID)
        {
            super(backpack, slotIndex, slotID, inventoryPlayer.player.openContainer instanceof ContainerBackpackUpgrades ? ((ContainerBackpackUpgrades) inventoryPlayer.player.openContainer).parentContainer : inventoryPlayer.player.openContainer, null);
            this.contextContainer = new ContainerBackpackUpgrades(backpack, inventoryPlayer, slotIndex, this, slotID);
            int offsetX = 8;
            int offsetY = 0;
            int slotsPerRow = 9;
            int rows = 2;
            IBackpack iBackpack = IBackpack.of(backpack);
            switch (iBackpack.createWrapper().getBackpackType())
            {
                case BASIC:
                {
                    offsetY = 50;
                    break;
                }

                case REINFORCED:
                {
                    rows = 4;
                    offsetY = 84;
                    break;
                }

                case ADVANCED:
                {
                    rows = 6;
                    offsetY = 120;
                    break;
                }

                case ULTIMATE:
                {
                    slotsPerRow = 13;
                    rows = 9;
                    offsetX = 44;
                    offsetY = 174;
                    break;
                }
            }

            for (int y = 0; y < rows; ++y)
            {
                for (int x = 0; x < slotsPerRow; ++x)
                {
                    this.addSlotToContainer(new SlotItemHandlerBackpack(iBackpack.getInventory(), x + y * slotsPerRow, 8 + x * 18, 8 + y * 18));
                }
            }

            this.addPlayerInventory(inventoryPlayer, offsetX, offsetY);
        }

        private class SlotItemHandlerBackpack extends SlotItemHandler
        {
            public SlotItemHandlerBackpack(IItemHandler itemHandler, int index, int xPosition, int yPosition)
            {
                super(itemHandler, index, xPosition, yPosition);
            }

            @Override
            public boolean isItemValid(@Nonnull ItemStack stack)
            {
                IBackpack backpack_test = IBackpack.of(stack);
                if (backpack_test != null)
                {
                    IBackpack backpack = IBackpack.of(ContainerBackpackInventory.this.backpack);
                    boolean allowNesting = false;
                    for (IUpgradeWrapper upgradeWrapper : backpack.createWrapper().getReadonlyUpdatesArray())
                    {
                        if (upgradeWrapper != null)
                        {
                            if (upgradeWrapper.getSelf().getItem() instanceof UpgradeNesting)
                            {
                                allowNesting = true;
                                break;
                            }
                        }
                    }

                    if (allowNesting && backpack_test.createWrapper().getBackpackType().ordinal() < backpack.createWrapper().getBackpackType().ordinal())
                    {
                        return super.isItemValid(stack);
                    }

                    return false;
                }

                return super.isItemValid(stack);
            }
        }
    }

    public static class ContainerBackpackUpgrades extends ContainerBackpack
    {
        public ContainerBackpackUpgrades(ItemStack backpack, InventoryPlayer inventoryPlayer, int backpackSlot, ContainerBackpack context, int slotID)
        {
            super(backpack, backpackSlot, slotID, context != null ? context.parentContainer : inventoryPlayer.player.openContainer, context);
            int offsetX = 8;
            int offsetY = 0;
            IBackpack iBackpack = IBackpack.of(backpack);
            switch (iBackpack.createWrapper().getBackpackType())
            {
                case BASIC:
                {
                    for (int i = 0; i < 5; ++i)
                    {
                        this.addSlotToContainer(new SlotUpgrade(iBackpack.getUpgrades(), i, 44 + i * 18, 8));
                    }

                    offsetY = 32;
                    break;
                }

                case REINFORCED:
                {
                    for (int i = 0; i < 9; ++i)
                    {
                        this.addSlotToContainer(new SlotUpgrade(iBackpack.getUpgrades(), i, 8 + i * 18, 8));
                    }

                    offsetY = 32;
                    break;
                }

                case ADVANCED:
                {
                    for (int i = 0; i < 9; ++i)
                    {
                        this.addSlotToContainer(new SlotUpgrade(iBackpack.getUpgrades(), i, 8 + i * 18, 8));
                    }

                    for (int i = 0; i < 5; ++i)
                    {
                        this.addSlotToContainer(new SlotUpgrade(iBackpack.getUpgrades(), 9 + i, 44 + i * 18, 26));
                    }

                    offsetY = 50;
                    break;
                }

                case ULTIMATE:
                {
                    for (int i = 0; i < 18; ++i)
                    {
                        this.addSlotToContainer(new SlotUpgrade(iBackpack.getUpgrades(), i, 8 + (i % 9) * 18, 8 + (i / 9) * 18));
                    }

                    offsetY = 50;
                    break;
                }
            }

            this.addPlayerInventory(inventoryPlayer, offsetX, offsetY);
        }

        private class SlotUpgrade extends SlotItemHandler
        {
            public SlotUpgrade(IItemHandler itemHandler, int index, int xPosition, int yPosition)
            {
                super(itemHandler, index, xPosition, yPosition);
            }

            @Override
            public boolean isItemValid(@Nonnull ItemStack stack)
            {
                return super.isItemValid(stack) && stack.getItem() instanceof IUpgrade && ((IUpgrade) stack.getItem()).canInstall(IBackpack.of(ContainerBackpackUpgrades.this.backpack).createWrapper(), IBackpack.of(ContainerBackpackUpgrades.this.backpack).createUpgradeWrapper(stack));
            }

            @Override
            public void putStack(@Nonnull ItemStack stack)
            {
                ItemStack oldStack = this.getStack();
                super.putStack(stack);
                if (stack.getItem() instanceof IUpgrade)
                {
                    ((IUpgrade) stack.getItem()).onInstalled(IBackpack.of(ContainerBackpackUpgrades.this.backpack).createWrapper(), IBackpack.of(ContainerBackpackUpgrades.this.backpack).createUpgradeWrapper(stack));
                }

                if (oldStack.getItem() instanceof IUpgrade)
                {
                    ((IUpgrade) oldStack.getItem()).onUninstalled(IBackpack.of(ContainerBackpackUpgrades.this.backpack).createWrapper(), IBackpack.of(ContainerBackpackUpgrades.this.backpack).createUpgradeWrapper(oldStack));
                }
            }

            @Override
            public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack)
            {
                ItemStack ret = super.onTake(thePlayer, stack);
                if (stack.getItem() instanceof IUpgrade)
                {
                    ((IUpgrade) stack.getItem()).onUninstalled(IBackpack.of(ContainerBackpackUpgrades.this.backpack).createWrapper(), IBackpack.of(ContainerBackpackUpgrades.this.backpack).createUpgradeWrapper(stack));
                }

                return ret;
            }
        }
    }
}
