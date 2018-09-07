package v0id.api.vsb.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

public interface IVSBPlayer extends INBTSerializable<NBTTagCompound>
{
    ItemStack getCurrentBackpack();

    void setCurrentBackpack(ItemStack newStack);

    List<EntityPlayer> getListeners();

    void addListener(EntityPlayer player);

    void removeListener(EntityPlayer player);

    void sync();

    void syncTo(EntityPlayer to);

    void copyFrom(IVSBPlayer from);

    boolean wasTicked();

    void setWasTicked();

    List<ItemStack> getSavedBackpacks();

    void addSavedBackpack(ItemStack is);

    static IVSBPlayer of(EntityPlayer player)
    {
        return player.getCapability(VSBCaps.PLAYER_CAPABILITY, null);
    }
}
