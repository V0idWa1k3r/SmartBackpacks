package v0id.vsb.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import v0id.vsb.VSB;

public class SyncExperienceToGUI implements IMessage
{
    private int newExperience;

    public SyncExperienceToGUI(int newExperience)
    {
        this.newExperience = newExperience;
    }

    public SyncExperienceToGUI()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.newExperience = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(newExperience);
    }

    public static class Handler implements IMessageHandler<SyncExperienceToGUI, IMessage>
    {
        @Override
        public IMessage onMessage(SyncExperienceToGUI message, MessageContext ctx)
        {
            VSB.proxy.getClientListener().addScheduledTask(() ->
            {
                VSB.proxy.setGuiExperience(message.newExperience);
            });

            return null;
        }
    }
}
