package v0id.vsb.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import v0id.api.vsb.capability.IBackpack;
import v0id.api.vsb.capability.IVSBPlayer;
import v0id.api.vsb.capability.VSBCaps;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.api.vsb.item.IBackpackWrapper;
import v0id.api.vsb.item.IUpgradeWrapper;
import v0id.vsb.capability.Player;
import v0id.vsb.config.VSBCfg;
import v0id.vsb.item.upgrade.UpgradeSoulbound;
import v0id.vsb.util.VSBUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = VSBRegistryNames.MODID)
public class VSBEventHandler
{
    @SubscribeEvent
    public static void onCapsAttach(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer)
        {
            event.addCapability(VSBRegistryNames.asLocation("vsbplayer"), new ICapabilitySerializable<NBTTagCompound>()
            {
                private final Player cap = new Player((EntityPlayer) event.getObject());

                @Override
                public NBTTagCompound serializeNBT()
                {
                    return this.cap.serializeNBT();
                }

                @Override
                public void deserializeNBT(NBTTagCompound nbt)
                {
                    this.cap.deserializeNBT(nbt);
                }

                @Override
                public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
                {
                    return capability == VSBCaps.PLAYER_CAPABILITY;
                }

                @Nullable
                @Override
                public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
                {
                    return capability == VSBCaps.PLAYER_CAPABILITY ? VSBCaps.PLAYER_CAPABILITY.cast(this.cap) : null;
                }
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerClone(PlayerEvent.Clone event)
    {
        IVSBPlayer.of(event.getEntityPlayer()).copyFrom(IVSBPlayer.of(event.getOriginal()));
        IVSBPlayer.of(event.getEntityPlayer()).sync();

        if (event.isCanceled() || !event.isWasDeath())
        {
            return;
        }

        if (event.getEntityPlayer() instanceof FakePlayer)
        {
            return;
        }

        if (event.getEntityPlayer().world.getGameRules().getBoolean("keepInventory"))
        {
            return;
        }

        for (ItemStack is : IVSBPlayer.of(event.getEntityPlayer()).getSavedBackpacks())
        {
            if (!event.getEntityPlayer().inventory.addItemStackToInventory(is))
            {
                event.getEntityPlayer().dropItem(is, false);
            }
        }

        IVSBPlayer.of(event.getEntityPlayer()).getSavedBackpacks().clear();
        for (int i = 0; i < event.getOriginal().inventory.mainInventory.size(); ++i)
        {
            ItemStack is = event.getOriginal().inventory.mainInventory.get(i);
            IBackpack backpack = IBackpack.of(is);
            if (backpack != null)
            {
                if (Arrays.stream(backpack.createWrapper().getReadonlyUpdatesArray()).filter(Objects::nonNull).anyMatch(e -> e.getSelf().getItem() instanceof UpgradeSoulbound))
                {
                    event.getOriginal().inventory.mainInventory.set(i, ItemStack.EMPTY);
                }
            }
        }

        for (int i = 0; i < event.getOriginal().inventory.offHandInventory.size(); ++i)
        {
            ItemStack is = event.getOriginal().inventory.offHandInventory.get(i);
            IBackpack backpack = IBackpack.of(is);
            if (backpack != null)
            {
                if (Arrays.stream(backpack.createWrapper().getReadonlyUpdatesArray()).filter(Objects::nonNull).anyMatch(e -> e.getSelf().getItem() instanceof UpgradeSoulbound))
                {
                    event.getOriginal().inventory.offHandInventory.set(i, ItemStack.EMPTY);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event)
    {
        if (event.getTarget() instanceof EntityPlayer)
        {
            IVSBPlayer.of((EntityPlayer) event.getTarget()).addListener(event.getEntityPlayer());
            IVSBPlayer.of((EntityPlayer) event.getTarget()).syncTo(event.getEntityPlayer());
        }
    }

    @SubscribeEvent
    public static void onStopTracking(PlayerEvent.StopTracking event)
    {
        if (event.getTarget() instanceof EntityPlayer)
        {
            IVSBPlayer.of((EntityPlayer) event.getTarget()).removeListener(event.getEntityPlayer());
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            if (event.player instanceof EntityPlayerMP)
            {
                IVSBPlayer player = IVSBPlayer.of(event.player);
                if (!player.wasTicked())
                {
                    player.sync();
                    player.setWasTicked();
                }

                if (!player.getCurrentBackpack().isEmpty())
                {
                    IBackpack backpack = IBackpack.of(player.getCurrentBackpack());
                    if (backpack != null)
                    {
                        backpack.onTick(null, event.player);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event)
    {
        if (!event.getEntityPlayer().world.isRemote)
        {
            IVSBPlayer player = IVSBPlayer.of(event.getEntityPlayer());
            IBackpack backpack = IBackpack.of(player.getCurrentBackpack());
            if (backpack != null)
            {
                if (pickupItem(event.getItem(), backpack.createWrapper(), event.getEntityPlayer()))
                {
                    event.setCanceled(true);
                    return;
                }
            }

            for (ItemStack is : VSBUtils.getPlayerInventory(event.getEntityPlayer()))
            {
                if (!is.isEmpty())
                {
                    backpack = IBackpack.of(is);
                    if (backpack != null)
                    {
                        if (pickupItem(event.getItem(), backpack.createWrapper(), event.getEntityPlayer()))
                        {
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onDeath(PlayerDropsEvent event)
    {
        if (event.isCanceled())
        {
            return;
        }

        if (event.getEntityPlayer() instanceof FakePlayer)
        {
            return;
        }

        if (event.getEntityPlayer().world.getGameRules().getBoolean("keepInventory"))
        {
            return;
        }

        IVSBPlayer player = IVSBPlayer.of(event.getEntityPlayer());
        if (!player.getCurrentBackpack().isEmpty())
        {
            boolean hasSoulbound = false;
            IBackpack backpack = IBackpack.of(player.getCurrentBackpack());
            if (backpack != null)
            {
                for (IUpgradeWrapper upgradeWrapper : backpack.createWrapper().getReadonlyUpdatesArray())
                {
                    if (upgradeWrapper != null && upgradeWrapper.getSelf().getItem() instanceof UpgradeSoulbound)
                    {
                        hasSoulbound = true;
                        break;
                    }
                }
            }

            if (!hasSoulbound)
            {
                event.getDrops().add(new EntityItem(event.getEntityPlayer().world, event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ, player.getCurrentBackpack().copy()));
                player.setCurrentBackpack(ItemStack.EMPTY);
            }
        }

        Iterator<EntityItem> iter = event.getDrops().iterator();
        while (iter.hasNext())
        {
            EntityItem item = iter.next();
            ItemStack is = item.getItem();
            IBackpack backpack = IBackpack.of(is);
            if (backpack != null)
            {
                boolean hasSoulbound = false;
                for (IUpgradeWrapper upgradeWrapper : backpack.createWrapper().getReadonlyUpdatesArray())
                {
                    if (upgradeWrapper != null && upgradeWrapper.getSelf().getItem() instanceof UpgradeSoulbound)
                    {
                        hasSoulbound = true;
                        break;
                    }
                }

                if (hasSoulbound)
                {
                    player.addSavedBackpack(is.copy());
                    iter.remove();
                }
            }
        }
    }

    public static ResourceLocation tableScales;
    @SubscribeEvent
    public static void onLootTable(LootTableLoadEvent event)
    {
        if (event.getName().toString().equals("minecraft:entities/ender_dragon") && VSBCfg.dragonDropsScales)
        {
            LootEntry entry = new LootEntryTable(tableScales, 1, 0, new LootCondition[0], "v0idssmartbackpacks_inject_dragon_scales_entry");
            LootPool pool = new LootPool(new LootEntry[] { entry }, new LootCondition[0], new RandomValueRange(1), new RandomValueRange(0, 1), "v0idssmartbackpacks_inject_dragon_scales_pool");
            event.getTable().addPool(pool);
        }
    }

    private static boolean pickupItem(EntityItem item, IBackpackWrapper backpack, EntityPlayer player)
    {
        for (IUpgradeWrapper wrapper : backpack.getReadonlyUpdatesArray())
        {
            if (wrapper != null)
            {
                if (wrapper.getUpgrade().onItemPickup(null, backpack, wrapper, item, player))
                {
                    return true;
                }
            }
        }

        return false;
    }
}
