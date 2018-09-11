package v0id.vsb.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import v0id.api.vsb.capability.IBackpack;
import v0id.api.vsb.capability.IVSBPlayer;
import v0id.vsb.VSB;
import v0id.vsb.util.EnumGuiType;

public class OpenWornBackpackOther implements IMessage
{
    private int entID;
    private int windowID;

    public OpenWornBackpackOther(int entID, int windowID)
    {
        this.entID = entID;
        this.windowID = windowID;
    }

    public OpenWornBackpackOther()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entID = buf.readInt();
        this.windowID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entID);
        buf.writeInt(this.windowID);
    }

    public static class Handler implements IMessageHandler<OpenWornBackpackOther, IMessage>
    {
        @Override
        public IMessage onMessage(OpenWornBackpackOther message, MessageContext ctx)
        {
            VSB.proxy.getClientListener().addScheduledTask(() ->
            {
                EntityPlayer player = VSB.proxy.getClientPlayer();
                Entity other = player.world.getEntityByID(message.entID);
                if (other instanceof EntityPlayer)
                {
                    IVSBPlayer ofOther = IVSBPlayer.of((EntityPlayer) other);
                    IBackpack backpack = IBackpack.of(ofOther.getCurrentBackpack());
                    if (backpack != null)
                    {
                        VSB.proxy.openModGui(ofOther.getCurrentBackpack(), EnumGuiType.WORN_BACKPACK, -2);
                        VSB.proxy.getClientPlayer().openContainer.windowId = message.windowID;
                    }
                }
            });

            return null;
        }
    }
}
