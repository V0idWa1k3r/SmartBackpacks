package v0id.api.vsb.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class VSBCaps
{
    @CapabilityInject(IBackpack.class)
    public static final Capability<IBackpack> BACKPACK_CAPABILITY = null;

    @CapabilityInject(IVSBPlayer.class)
    public static final Capability<IVSBPlayer> PLAYER_CAPABILITY = null;

    @CapabilityInject(IFilter.class)
    public static final Capability<IFilter> FILTER_CAPABILITY = null;
}
