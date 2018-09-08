package v0id.vsb.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Pair;
import v0id.api.vsb.capability.IVSBPlayer;
import v0id.api.vsb.capability.VSBCaps;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.vsb.VSB;
import v0id.vsb.net.VSBNet;
import v0id.vsb.util.VSBUtils;

@Mod.EventBusSubscriber(modid = VSBRegistryNames.MODID, value = { Side.CLIENT })
public class ClientEventHandler
{
    private static int lastHotbarSwappingSlot = -1;

    @SubscribeEvent
    public static void onKeyEvent(InputEvent.KeyInputEvent event)
    {
        if (VSB.proxy.getClientPlayer() != null)
        {
            EntityPlayer entityPlayer = VSB.proxy.getClientPlayer();
            IVSBPlayer player = IVSBPlayer.of(entityPlayer);
            if (ClientRegistry.key_removeBackpack.isPressed())
            {
                if (!player.getCurrentBackpack().isEmpty() || entityPlayer.getHeldItem(EnumHand.MAIN_HAND).hasCapability(VSBCaps.BACKPACK_CAPABILITY, null) || entityPlayer.getHeldItem(EnumHand.OFF_HAND).hasCapability(VSBCaps.BACKPACK_CAPABILITY, null))
                {
                    VSBNet.sendRemoveBackpack();
                }
            }
            else
            {
                if (ClientRegistry.key_openBackpack.isPressed())
                {
                    if (!player.getCurrentBackpack().isEmpty() && Minecraft.getMinecraft().currentScreen == null)
                    {
                        VSBNet.sendOpenWornBackpack();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMouseWheelScroll(MouseEvent event)
    {
        int dWheel = event.getDwheel();
        if (dWheel != 0 || (event.getButton() == 2 && event.isButtonstate()))
        {
            if (ClientRegistry.key_changeHotbar.isKeyDown())
            {
                ItemStack backpack = getOrFindBackpack();
                if (!backpack.isEmpty())
                {
                    VSBNet.sendScrollHotbar(lastHotbarSwappingSlot, Integer.compare(dWheel, 0));
                    event.setCanceled(true);
                }
            }
        }
    }

    private static ItemStack getOrFindBackpack()
    {
        ItemStack is = VSBUtils.checkBackpackForHotbarUpgrade(VSBUtils.getBackpack(Minecraft.getMinecraft().player, lastHotbarSwappingSlot));
        if (!is.isEmpty())
        {
            return is;
        }

        Pair<Integer, ItemStack> backpackData = findBackpack();
        if (backpackData != null)
        {
            lastHotbarSwappingSlot = backpackData.getLeft();
            return backpackData.getRight();
        }

        return ItemStack.EMPTY;
    }

    private static Pair<Integer, ItemStack> findBackpack()
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        IVSBPlayer ivsbPlayer = IVSBPlayer.of(player);
        ItemStack is = ivsbPlayer.getCurrentBackpack();
        is = VSBUtils.checkBackpackForHotbarUpgrade(is);
        if (!is.isEmpty())
        {
            return Pair.of(-1, is);
        }

        for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
        {
            is = VSBUtils.checkBackpackForHotbarUpgrade(player.inventory.getStackInSlot(i));
            if (!is.isEmpty())
            {
                return Pair.of(i, is);
            }
        }

        return null;
    }
}
