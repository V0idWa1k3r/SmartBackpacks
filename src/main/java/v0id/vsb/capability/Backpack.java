package v0id.vsb.capability;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import v0id.api.vsb.capability.IBackpack;
import v0id.api.vsb.item.EnumBackpackType;
import v0id.api.vsb.item.IBackpackWrapper;
import v0id.api.vsb.item.IUpgrade;
import v0id.api.vsb.item.IUpgradeWrapper;
import v0id.vsb.util.VSBUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Backpack implements IBackpack
{
    private final NBTItemHandler inventory;
    private final NBTItemHandler upgrades;
    private final EnergyStorageNBT energyStorage;
    private final ItemStack self;
    private final EnumBackpackType backpackType;
    private final Wrapper wrapper;
    private int color;
    private final NBTTagCompound upgradesClientTag = new NBTTagCompound();
    private int ticksLived;
    private int maxEnergy;

    public Backpack()
    {
        this.inventory = null;
        this.upgrades = null;
        this.self = null;
        this.backpackType = null;
        this.wrapper = null;
        this.energyStorage = null;
    }

    public Backpack(ItemStack owner, EnumBackpackType type)
    {
        this.inventory = new NBTItemHandler(owner, type.getInventorySize());
        this.upgrades = new NBTItemHandler(owner, type.getUpgradesSize());
        this.energyStorage = new EnergyStorageNBT(owner)
        {
            @Override
            public int getMaxEnergyStored()
            {
                return Backpack.this.maxEnergy;
            }

            @Override
            public int getEnergyStored()
            {
                return super.getEnergyStored() > this.getMaxEnergyStored() ? this.getMaxEnergyStored() : super.getEnergyStored();
            }
        };

        this.self = owner;
        this.backpackType = type;
        this.wrapper = new Wrapper();
    }

    @Override
    public IItemHandler getInventory()
    {
        return this.inventory;
    }

    @Override
    public IItemHandler getUpgrades()
    {
        return this.upgrades;
    }

    @Override
    public IBackpackWrapper createWrapper()
    {
        return this.wrapper;
    }

    @Override
    public IUpgradeWrapper createUpgradeWrapper(ItemStack upgrade)
    {
        return new UpgradeWrapper(upgrade);
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, @Nullable Entity ticker)
    {
        if (++ticksLived % 10 == 0)
        {
            for (IUpgradeWrapper upgradeWrapper : this.wrapper.getReadonlyUpdatesArray())
            {
                if (upgradeWrapper != null)
                {
                    upgradeWrapper.getUpgrade().onPulse(container, this.wrapper, upgradeWrapper, ticker);
                }
            }
        }

        for (IUpgradeWrapper upgradeWrapper : this.wrapper.getReadonlyUpdatesArray())
        {
            if (upgradeWrapper != null)
            {
                upgradeWrapper.getUpgrade().onTick(container, this.wrapper, upgradeWrapper, ticker);
            }
        }
    }

    @Override
    public void copyAllDataFrom(IBackpack backpack)
    {
        if (backpack instanceof Backpack)
        {
            Backpack back = (Backpack) backpack;
            for (int i = 0; i < back.inventory.getSlots(); ++i)
            {
                this.inventory.setStackInSlot(i, back.inventory.getStackInSlot(i));
            }

            for (int i = 0; i < back.upgrades.getSlots(); ++i)
            {
                this.upgrades.setStackInSlot(i, back.upgrades.getStackInSlot(i));
            }

            this.maxEnergy = back.maxEnergy;
            this.energyStorage.setEnergyStored(back.energyStorage.getEnergyStored());
            this.color = back.color;
        }
    }

    @Override
    public IEnergyStorage getEnergyStorage()
    {
        return this.energyStorage;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setTag("inventory", this.inventory.serializeNBT());
        ret.setTag("upgrades", this.upgrades.serializeNBT());
        ret.setInteger("color", this.color);
        ret.setInteger("maxEnergy", this.maxEnergy);
        ret.setInteger("energy", this.energyStorage.getEnergyStored());
        return ret;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        this.inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
        this.upgrades.deserializeNBT(nbt.getCompoundTag("upgrades"));
        this.color = nbt.getInteger("color");
        this.maxEnergy = nbt.getInteger("maxEnergy");
        this.energyStorage.extractEnergy(Integer.MAX_VALUE, false);
        this.energyStorage.receiveEnergy(nbt.getInteger("energy"), false);
    }

    @Override
    public NBTTagCompound serializeSync()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("color", this.color);
        tag.setTag("upgrades", this.upgrades.serializeNBT());
        tag.setInteger("energyMax", this.maxEnergy);
        tag.setInteger("energy", this.energyStorage.getEnergyStored());
        return tag;
    }

    @Override
    public void deserializeSync(NBTTagCompound tag)
    {
        this.color = tag.getInteger("color");
        this.upgrades.deserializeNBT(tag.getCompoundTag("upgrades"));
        this.maxEnergy = tag.getInteger("energyMax");
        this.energyStorage.extractEnergy(Integer.MAX_VALUE, false);
        this.energyStorage.receiveEnergy(tag.getInteger("energy"), false);
    }

    private class UpgradeWrapper implements IUpgradeWrapper
    {
        private final ItemStack upgrade;

        private UpgradeWrapper(ItemStack upgrade)
        {
            this.upgrade = upgrade;
        }

        @Override
        public IBackpackWrapper getContainer()
        {
            return Backpack.this.createWrapper();
        }

        @Override
        public IUpgrade getUpgrade()
        {
            return (IUpgrade) this.upgrade.getItem();
        }

        @Override
        public ItemStack getSelf()
        {
            return this.upgrade;
        }
    }

    private class Wrapper implements IBackpackWrapper
    {
        @Override
        public IBackpack getSelfAsCapability()
        {
            return Backpack.this;
        }

        @Override
        public IUpgradeWrapper[] getReadonlyUpdatesArray()
        {
            return VSBUtils.capabilityToArray(Backpack.this.upgrades, IUpgradeWrapper.class, i -> i.getItem() instanceof IUpgrade ? new UpgradeWrapper(i) : null);
        }

        @Override
        public ItemStack[] getReadonlyInventory()
        {
            return VSBUtils.capabilityToArray(Backpack.this.inventory, ItemStack.class, i -> i);
        }

        @Override
        public ItemStack getSelf()
        {
            return Backpack.this.self;
        }

        @Override
        public IItemHandler getInventory()
        {
            return Backpack.this.inventory;
        }

        @Override
        public IItemHandler getUpgradesInventory()
        {
            return Backpack.this.upgrades;
        }

        @Override
        public EnumBackpackType getBackpackType()
        {
            return Backpack.this.backpackType;
        }

        @Override
        public int getMaxUpgrades()
        {
            return Backpack.this.backpackType.getUpgradesSize();
        }

        @Override
        public NBTTagCompound getUpgradesSyncTag()
        {
            return Backpack.this.upgradesClientTag;
        }

        @Override
        public int getColor()
        {
            return Backpack.this.color;
        }

        @Override
        public void setColor(int newColor)
        {
            Backpack.this.color = newColor;
        }

        @Override
        public int getMaxEnergy()
        {
            return Backpack.this.maxEnergy;
        }

        @Override
        public void setMaxEnergy(int i)
        {
            Backpack.this.maxEnergy = i;
        }

        @Override
        public void markInventoryDirty()
        {
            Backpack.this.inventory.markDirty();
        }
    }

    /**
     * Sigh. I tried, forge. I really tried to use the ItemStackHandler as it is provided. Really.
     * However nearly every time I would update the inventory on the server while the GUI is closed the client would not get the new values.
     * And it would be fine and dandy. No really, who cares what values the client has?
     * Well, turns out vanilla cares. Yeah, when the player picks up the item from their inventory using left mouse button guess what happens?
     * The CLIENT is the one that determines what stack is picked up and then sends that stack to the SERVER. Yeah, that is a really big oversight.
     * This can be exploited to no end. For example if the server capability thinks that the inventory is empty but the client still thinks that there is a stack of stone in there.
     * Guess what happens when the client takes the stack from the slot, puts it back down and then opens the GUI?
     * Yep, the stack of stone is back. It's incredibly DUMB. I can't even begin to explain how dumb it is. This can be used for all sorts of dupes.
     * You could say - just sync the capability to the client, no problem.
     * Well, there is a problem. I can't easily sync it to the client if the backpack is nested.
     * And if I am sending the entire capability to the client anyway I could as well be using the system that's already in place - NBT.
     * Yeah, this bug made me store my capability in NBT of the item, write the NBT every time it changes forcing the client to re-create the stack, the capability and read the cap data from my NBT.
     * That is about the only way. I am so sorry capabilities, but you've failed me. Or I guess vanilla MC did. Doesn't matter.
     * So to whomever is reading this poem - go report this bullshit to mojang or forge so one of them fixes it. Otherwise there WILL BE duping exploits. I might just make one.
     * And don't repeat my mistakes. If your capability can change when the client isn't aware of the change - send it back to the client. Use NBT.
     * Don't spend hours trying to fix this bullshit, implementing more and more dumb solutions only to give up 4 days later like I did.
     * Just use NBT.
     * ~V0idWa1k3r
     */
    private class NBTItemHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<NBTTagCompound>
    {
        private final ItemStack stack;
        private final int slots;
        private final ItemStackHandler inventory;

        private NBTItemHandler(ItemStack stack, int slots)
        {
            this.stack = stack;
            this.slots = slots;
            this.inventory = new ItemStackHandler(slots)
            {
                @Override
                protected void onContentsChanged(int slot)
                {
                    super.onContentsChanged(slot);
                    NBTItemHandler.this.markDirty();
                }
            };

            if (!stack.hasTagCompound())
            {
                stack.setTagCompound(new NBTTagCompound());
            }

            if (stack.getTagCompound().hasKey("vsb:nbtItemHandler"))
            {
                this.inventory.deserializeNBT(stack.getTagCompound().getCompoundTag("vsb:nbtItemHandler"));
            }
        }

        @Override
        public int getSlots()
        {
            return this.slots;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot)
        {
            return this.inventory.getStackInSlot(slot);
        }

        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack)
        {
            this.inventory.setStackInSlot(slot, stack);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            return this.inventory.insertItem(slot, stack, simulate);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return this.inventory.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot)
        {
            return this.inventory.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack)
        {
            return this.inventory.isItemValid(slot, stack);
        }

        public void markDirty()
        {
            if (!this.stack.hasTagCompound())
            {
                this.stack.setTagCompound(new NBTTagCompound());
            }

            this.stack.getTagCompound().setTag("vsb:nbtItemHandler", this.inventory.serializeNBT());
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            return this.inventory.serializeNBT();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
            if (nbt.hasKey("vsb:nbtItemHandler"))
            {
                this.inventory.deserializeNBT(nbt.getCompoundTag("vsb:nbtItemHandler"));
            }
            else
            {
                this.inventory.deserializeNBT(nbt);
            }
        }
    }

    private class EnergyStorageNBT extends EnergyStorage
    {
        private final ItemStack stack;

        private EnergyStorageNBT(ItemStack stack)
        {
            super(0);
            this.stack = stack;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate)
        {
            if (this.getMaxEnergyStored() <= 0)
            {
                return 0;
            }

            int energyReceived = Math.min(this.getMaxEnergyStored() - this.getEnergyStored(), Math.min(this.getMaxEnergyStored(), maxReceive));
            if (!simulate)
            {
                this.setEnergyStored(this.getEnergyStored() + energyReceived);
            }

            return energyReceived;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate)
        {
            if (this.getMaxEnergyStored() <= 0)
            {
                return 0;
            }

            int energyExtracted = Math.min(this.getEnergyStored(), Math.min(this.maxExtract, this.getMaxEnergyStored()));
            if (!simulate)
            {
                this.setEnergyStored(this.getEnergyStored() - energyExtracted);
            }

            return energyExtracted;
        }

        @Override
        public int getEnergyStored()
        {
            return stack.hasTagCompound() ? stack.getTagCompound().getInteger("vsb:energy") : 0;
        }

        void setEnergyStored(int value)
        {
            if (!stack.hasTagCompound())
            {
                stack.setTagCompound(new NBTTagCompound());
            }

            stack.getTagCompound().setInteger("vsb:energy", value);
        }
    }
}
