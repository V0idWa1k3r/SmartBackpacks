package v0id.vsb;

import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import v0id.api.vsb.capability.IBackpack;
import v0id.api.vsb.capability.IFilter;
import v0id.api.vsb.capability.IVSBPlayer;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.api.vsb.util.ILifecycleListener;
import v0id.vsb.capability.Backpack;
import v0id.vsb.capability.Filter;
import v0id.vsb.capability.Player;
import v0id.vsb.handler.VSBEventHandler;
import v0id.vsb.net.VSBNet;
import v0id.vsb.util.IProxy;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.List;

@Mod(modid = VSBRegistryNames.MODID, useMetadata = true, dependencies = "after:harvestcraft;after:tombmanygraves2api@[1.12-4.1.0,);after:tombmanygraves@[1.12-4.1.0,)", certificateFingerprint = "751ba7c2091ec5cc4cd1fcc6e9a4e9d5a2cace8d")
public class VSB
{
    public static List<ILifecycleListener> listeners = Lists.newArrayList();

    @SidedProxy(clientSide = "v0id.vsb.client.ClientProxy", serverSide = "v0id.vsb.server.ServerProxy")
    public static IProxy proxy;
    private static Logger modLogger = LogManager.getLogger(VSB.class);

    static
    {
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        CapabilityManager.INSTANCE.register(IBackpack.class, new Capability.IStorage<IBackpack>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IBackpack> capability, IBackpack instance, EnumFacing side)
            {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IBackpack> capability, IBackpack instance, EnumFacing side, NBTBase nbt)
            {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }, Backpack::new);

        CapabilityManager.INSTANCE.register(IVSBPlayer.class, new Capability.IStorage<IVSBPlayer>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IVSBPlayer> capability, IVSBPlayer instance, EnumFacing side)
            {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IVSBPlayer> capability, IVSBPlayer instance, EnumFacing side, NBTBase nbt)
            {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }, Player::new);

        CapabilityManager.INSTANCE.register(IFilter.class, new Capability.IStorage<IFilter>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IFilter> capability, IFilter instance, EnumFacing side)
            {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IFilter> capability, IFilter instance, EnumFacing side, NBTBase nbt)
            {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }, Filter::new);

        VSBNet.register();
        listeners.add(proxy);
        listeners.forEach(l -> l.preInit(event));
        VSBEventHandler.tableScales = LootTableList.register(VSBRegistryNames.asLocation("inject_dragon_scales"));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (Loader.isModLoaded("tombmanygraves2api"))
        {
            try
            {
                Class<?> c = Class.forName("v0id.vsb.compat.TombManyGravesCompat");
                Method m = c.getDeclaredMethod("register");
                m.invoke(null);
            }
            catch (Exception ex)
            {
                FMLCommonHandler.instance().raiseException(ex, "VSB was unable to instantinate TombManyGraves compatibility patch!", false);
            }
        }

        listeners.forEach(l -> l.init(event));
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        listeners.forEach(l -> l.postInit(event));
    }

    @Mod.EventHandler
    public static void fingerprintViolated(FMLFingerprintViolationEvent event)
    {
        if (event.isDirectory())
        {
            modLogger.warn("VSB fingerprint doesn't match but we are in a dev environment so that's okay.");
        }
        else
        {
            modLogger.error("VSB fingerprint doesn't match! Expected {}, got {}!", event.getExpectedFingerprint(), String.join(" , ", event.getFingerprints()));
        }
    }
}
