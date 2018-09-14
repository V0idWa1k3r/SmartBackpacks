package v0id.vsb.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import v0id.api.vsb.capability.ICraftingUpgrade;

import javax.annotation.Nonnull;

public class ContainerCraftingUpgrade extends Container
{
    public final ItemStack upgrade;
    public final int upgradeSlot;
    public final InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public final InventoryCraftResult craftResult = new InventoryCraftResult();
    public final EntityPlayer player;

    public ContainerCraftingUpgrade(InventoryPlayer inventoryPlayer, ItemStack upgrade, int upgradeSlot)
    {
        this.player = inventoryPlayer.player;
        this.upgrade = upgrade;
        this.upgradeSlot = upgradeSlot;
        ICraftingUpgrade craftingUpgrade = ICraftingUpgrade.of(upgrade);
        this.addSlotToContainer(new SlotCraftingUpgradeResult(inventoryPlayer.player, craftMatrix, 0, 116, 26));
        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new SlotCraftingUpgrade(craftingUpgrade.getInventory(), i, 44 + (i % 3) * 18, 8 + (i / 3) * 18));
        }

        this.addPlayerInventory(inventoryPlayer, 8, 68);
    }

    @Override
    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        if (listener instanceof EntityPlayerMP)
        {
            ((EntityPlayerMP) listener).connection.sendPacket(new SPacketSetSlot(this.windowId, 0, this.craftResult.getStackInSlot(0)));
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        if (!this.player.world.isRemote)
        {
            IRecipe recipe = CraftingManager.findMatchingRecipe(this.craftMatrix, this.player.world);
            if (recipe != null)
            {
                ItemStack result = recipe.getCraftingResult(this.craftMatrix);
                if (!result.isEmpty())
                {
                    this.craftResult.setInventorySlotContents(0, result.copy());
                    ((EntityPlayerMP)this.player).connection.sendPacket(new SPacketSetSlot(this.windowId, 0, result.copy()));
                }
                else
                {
                    this.craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
                    ((EntityPlayerMP)this.player).connection.sendPacket(new SPacketSetSlot(this.windowId, 0, ItemStack.EMPTY));
                }
            }
            else
            {
                this.craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
                ((EntityPlayerMP)this.player).connection.sendPacket(new SPacketSetSlot(this.windowId, 0, ItemStack.EMPTY));
            }
        }
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
                        return super.canTakeStack(playerIn) && this.getSlotIndex() != ContainerCraftingUpgrade.this.upgradeSlot;
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
                    return super.canTakeStack(playerIn) && this.getSlotIndex() != ContainerCraftingUpgrade.this.upgradeSlot;
                }
            });
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        return ItemStack.EMPTY;
    }

    private class SlotCraftingUpgradeResult extends SlotCrafting
    {
        public SlotCraftingUpgradeResult(EntityPlayer player, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition)
        {
            super(player, ContainerCraftingUpgrade.this.craftMatrix, ContainerCraftingUpgrade.this.craftResult, slotIndex, xPosition, yPosition);
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn)
        {
            return false;
        }
    }

    private class SlotCraftingUpgrade extends SlotItemHandler
    {
        public SlotCraftingUpgrade(IItemHandler itemHandler, int index, int xPosition, int yPosition)
        {
            super(itemHandler, index, xPosition, yPosition);
            ContainerCraftingUpgrade.this.craftMatrix.setInventorySlotContents(index, this.getStack());
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack)
        {
            ItemStack stack1 = stack.copy();
            stack1.setCount(1);
            this.putStack(stack1);
            ContainerCraftingUpgrade.this.craftMatrix.setInventorySlotContents(this.getSlotIndex(), stack1);
            return false;
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn)
        {
            this.putStack(ItemStack.EMPTY);
            ContainerCraftingUpgrade.this.craftMatrix.setInventorySlotContents(this.getSlotIndex(), ItemStack.EMPTY);
            return false;
        }
    }
}
