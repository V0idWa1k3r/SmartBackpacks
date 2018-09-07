package v0id.vsb.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import v0id.vsb.container.ContainerBackpack;
import v0id.vsb.net.VSBNet;
import v0id.vsb.util.EnumGuiType;
import v0id.vsb.util.VSBUtils;

public class SwitchContextContainer implements IMessage
{
    public SwitchContextContainer()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    }

    public static class Handler implements IMessageHandler<SwitchContextContainer, IMessage>
    {
        @Override
        public IMessage onMessage(SwitchContextContainer message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            ((WorldServer)player.world).addScheduledTask(() ->
            {
                Container container = player.openContainer;
                if (container instanceof ContainerBackpack)
                {
                    Container context = ((ContainerBackpack) container).contextContainer;
                    VSBUtils.openContainer(player, context);
                    VSBNet.sendOpenGUI(player, ((ContainerBackpack) container).backpackSlotID, ((ContainerBackpack) container).parentContainer instanceof ContainerPlayer, ((ContainerBackpack) container).backpackSlot, context instanceof ContainerBackpack.ContainerBackpackUpgrades ? ((ContainerBackpack) container).backpackSlotID == -1 ? EnumGuiType.WORN_BACKPACK_UPGRADES : EnumGuiType.BACKPACK_UPGRADES : ((ContainerBackpack) container).backpackSlotID == -1 ? EnumGuiType.WORN_BACKPACK : EnumGuiType.BACKPACK);
                }
            });

            return null;
        }
    }
}
