package v0id.vsb.capability;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import v0id.api.vsb.capability.IVSBPlayer;
import v0id.vsb.net.VSBNet;

import java.util.List;
import java.util.stream.StreamSupport;

public class Player implements IVSBPlayer
{
    private ItemStack backpack = ItemStack.EMPTY;
    private final List<EntityPlayer> listeners = Lists.newArrayList();
    private final List<ItemStack> savedBackpacks = Lists.newArrayList();
    private boolean wasTicked;
    private EntityPlayer owner;

    public Player()
    {
    }

    public Player(EntityPlayer owner)
    {
        this.listeners.add(owner);
        this.owner = owner;
    }

    @Override
    public ItemStack getCurrentBackpack()
    {
        return this.backpack;
    }

    @Override
    public void setCurrentBackpack(ItemStack newStack)
    {
        this.backpack = newStack;
    }

    @Override
    public List<EntityPlayer> getListeners()
    {
        return this.listeners;
    }

    @Override
    public void addListener(EntityPlayer player)
    {
        this.listeners.add(player);
    }

    @Override
    public void removeListener(EntityPlayer player)
    {
        this.listeners.remove(player);
    }

    @Override
    public void sync()
    {
        if (this.owner instanceof EntityPlayerMP)
        {
            this.listeners.forEach(l -> VSBNet.sendPlayerDataSync((EntityPlayerMP) this.owner, (EntityPlayerMP) l));
        }
    }

    @Override
    public void syncTo(EntityPlayer to)
    {
        if (this.owner instanceof EntityPlayerMP)
        {
            VSBNet.sendPlayerDataSync((EntityPlayerMP) this.owner, (EntityPlayerMP) to);
        }
    }

    @Override
    public void copyFrom(IVSBPlayer from)
    {
        this.backpack = from.getCurrentBackpack();
        this.listeners.addAll(from.getListeners());
        this.savedBackpacks.addAll(from.getSavedBackpacks());
    }

    @Override
    public boolean wasTicked()
    {
        return this.wasTicked;
    }

    @Override
    public void setWasTicked()
    {
        this.wasTicked = true;
    }

    @Override
    public List<ItemStack> getSavedBackpacks()
    {
        return this.savedBackpacks;
    }

    @Override
    public void addSavedBackpack(ItemStack is)
    {
        this.savedBackpacks.add(is);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setTag("backpack", this.backpack.serializeNBT());
        NBTTagList savedPacks = new NBTTagList();
        for (ItemStack is : this.savedBackpacks)
        {
            savedPacks.appendTag(is.serializeNBT());
        }

        ret.setTag("savedBackpacks", savedPacks);
        return ret;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        this.backpack = new ItemStack(nbt.getCompoundTag("backpack"));
        NBTTagList savedPacks = nbt.getTagList("savedBackpacks", Constants.NBT.TAG_COMPOUND);
        this.savedBackpacks.clear();
        StreamSupport.stream(savedPacks.spliterator(), false).map(n -> new ItemStack((NBTTagCompound) n)).forEach(this.savedBackpacks::add);
    }
}
