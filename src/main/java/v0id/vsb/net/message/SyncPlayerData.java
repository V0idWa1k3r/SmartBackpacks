package v0id.vsb.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import v0id.api.vsb.capability.IVSBPlayer;
import v0id.vsb.VSB;

public class SyncPlayerData implements IMessage
{
    private ItemStack backpack;
    private int entID;

    public SyncPlayerData(int id, ItemStack backpack)
    {
        this.entID = id;
        this.backpack = backpack;
    }

    public SyncPlayerData()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entID = buf.readInt();
        this.backpack = new ItemStack(ByteBufUtils.readTag(buf));
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entID);
        ByteBufUtils.writeTag(buf, this.backpack.serializeNBT());
    }

    public static class Handler implements IMessageHandler<SyncPlayerData, IMessage>
    {
        @Override
        public IMessage onMessage(SyncPlayerData message, MessageContext ctx)
        {
            VSB.proxy.getClientListener().addScheduledTask(() ->
            {
                Entity entity = VSB.proxy.getClientPlayer().world.getEntityByID(message.entID);
                if (entity instanceof EntityPlayer)
                {
                    IVSBPlayer.of((EntityPlayer) entity).setCurrentBackpack(message.backpack);
                }
            });

            return null;
        }
    }
}
