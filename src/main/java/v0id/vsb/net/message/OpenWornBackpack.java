package v0id.vsb.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import v0id.api.vsb.capability.IVSBPlayer;
import v0id.vsb.container.ContainerBackpack;
import v0id.vsb.net.VSBNet;
import v0id.vsb.util.EnumGuiType;
import v0id.vsb.util.VSBUtils;

public class OpenWornBackpack implements IMessage
{
    @Override
    public void fromBytes(ByteBuf buf)
    {
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    }

    public static class Handler implements IMessageHandler<OpenWornBackpack, IMessage>
    {
        @Override
        public IMessage onMessage(OpenWornBackpack message, MessageContext ctx)
        {
            EntityPlayerMP sender = ctx.getServerHandler().player;
            sender.getServerWorld().addScheduledTask(() ->
            {
                IVSBPlayer player = IVSBPlayer.of(sender);
                if (!player.getCurrentBackpack().isEmpty() && (sender.openContainer == sender.inventoryContainer || sender.openContainer == null))
                {
                    ItemStack backpack = player.getCurrentBackpack();
                    VSBUtils.openContainer(sender, new ContainerBackpack.ContainerBackpackInventory(backpack, sender.inventory, -1, -1));
                    VSBNet.sendPlayerDataSync(sender, sender);
                    VSBNet.sendOpenGUI(sender, -1, false, -1, EnumGuiType.WORN_BACKPACK);
                }
            });

            return null;
        }
    }
}
