package v0id.vsb.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import v0id.api.vsb.capability.IVSBPlayer;
import v0id.api.vsb.capability.VSBCaps;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.vsb.VSB;
import v0id.vsb.net.VSBNet;

@Mod.EventBusSubscriber(modid = VSBRegistryNames.MODID, value = { Side.CLIENT })
public class ClientEventHandler
{
    private static boolean isOpenPackKeyPressed;
    private static boolean isRemovePackKeyPressed;

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
}
