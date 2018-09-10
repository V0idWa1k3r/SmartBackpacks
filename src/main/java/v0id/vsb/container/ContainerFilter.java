package v0id.vsb.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import v0id.api.vsb.capability.IFilter;

import javax.annotation.Nonnull;

public class ContainerFilter extends Container
{
    public final ItemStack filter;
    public final int filterSlot;

    public ContainerFilter(InventoryPlayer inventoryPlayer, ItemStack filter, int filterSlot)
    {
        this.filter = filter;
        this.filterSlot = filterSlot;
        IFilter filter1 = IFilter.of(filter);
        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new SlotFilter(filter1.getItems(), i, 8 + i * 18, 8));
        }

        this.addPlayerInventory(inventoryPlayer, 8, 58);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
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
                        return super.canTakeStack(playerIn) && this.getSlotIndex() != ContainerFilter.this.filterSlot;
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
                    return super.canTakeStack(playerIn) && this.getSlotIndex() != ContainerFilter.this.filterSlot;
                }
            });
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        return ItemStack.EMPTY;
    }

    private class SlotFilter extends SlotItemHandler
    {
        public SlotFilter(IItemHandler itemHandler, int index, int xPosition, int yPosition)
        {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack)
        {
            this.putStack(stack.copy());
            return false;
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn)
        {
            this.putStack(ItemStack.EMPTY);
            return false;
        }
    }
}
