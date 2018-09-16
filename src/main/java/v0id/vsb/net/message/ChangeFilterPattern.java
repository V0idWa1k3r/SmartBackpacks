package v0id.vsb.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.lang3.StringUtils;
import v0id.api.vsb.capability.IFilter;
import v0id.vsb.capability.FilterRegex;
import v0id.vsb.container.ContainerFilterRegex;

public class ChangeFilterPattern implements IMessage
{
    private String pattern = StringUtils.EMPTY;

    public ChangeFilterPattern(String pattern)
    {
        this.pattern = pattern;
    }

    public ChangeFilterPattern()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.pattern = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.pattern);
    }

    public static class Handler implements IMessageHandler<ChangeFilterPattern, IMessage>
    {
        @Override
        public IMessage onMessage(ChangeFilterPattern message, MessageContext ctx)
        {
            EntityPlayerMP sender = ctx.getServerHandler().player;
            sender.getServerWorld().addScheduledTask(() ->
            {
                Container openContainer = sender.openContainer;
                if (openContainer instanceof ContainerFilterRegex)
                {
                    IFilter filter = IFilter.of(((ContainerFilterRegex) openContainer).filter);
                    if (filter instanceof FilterRegex)
                    {
                        ((FilterRegex) filter).setPattern(message.pattern);
                    }
                }
            });

            return null;
        }
    }
}
