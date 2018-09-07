package v0id.vsb.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import v0id.api.vsb.item.IGUIOpenable;

public class OpenContainer implements IMessage
{
    private int slotIndex;
    private int slotID;

    public OpenContainer(int slotIndex, int slotID)
    {
        this.slotIndex = slotIndex;
        this.slotID = slotID;
    }

    public OpenContainer()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.slotIndex = buf.readInt();
        this.slotID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.slotIndex);
        buf.writeInt(this.slotID);
    }

    public static class Handler implements IMessageHandler<OpenContainer, IMessage>
    {
        @Override
        public IMessage onMessage(OpenContainer message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() ->
            {
                Container container = player.openContainer;
                if (message.slotID < container.inventorySlots.size())
                {
                    Slot slot = container.getSlot(message.slotID);
                    ItemStack is = slot.getStack();
                    if (is.getItem() instanceof IGUIOpenable)
                    {
                        ((IGUIOpenable) is.getItem()).openContainer(player, is, message.slotIndex, message.slotID);
                    }
                }
            });

            return null;
        }
    }
}
