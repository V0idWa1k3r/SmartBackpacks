package v0id.api.vsb.util;

import net.minecraft.nbt.NBTTagCompound;

public interface ISyncable
{
    NBTTagCompound serializeSync();

    void deserializeSync(NBTTagCompound tag);
}
