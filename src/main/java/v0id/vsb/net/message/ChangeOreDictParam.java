package v0id.vsb.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import v0id.api.vsb.capability.ICraftingUpgrade;
import v0id.vsb.container.ContainerCraftingUpgrade;

public class ChangeOreDictParam implements IMessage
{
    private byte slotID;

    public ChangeOreDictParam(byte slotID)
    {
        this.slotID = slotID;
    }

    public ChangeOreDictParam()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.slotID = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(this.slotID);
    }

    public static class Handler implements IMessageHandler<ChangeOreDictParam, IMessage>
    {
        @Override
        public IMessage onMessage(ChangeOreDictParam message, MessageContext ctx)
        {
            EntityPlayerMP sender = ctx.getServerHandler().player;
            sender.getServerWorld().addScheduledTask(() ->
            {
                Container openContainer = sender.openContainer;
                if (openContainer instanceof ContainerCraftingUpgrade)
                {
                    ItemStack upgrade = ((ContainerCraftingUpgrade) openContainer).upgrade;
                    ICraftingUpgrade craftingUpgrade = ICraftingUpgrade.of(upgrade);
                    if (craftingUpgrade != null)
                    {
                        craftingUpgrade.getOreDictFlags()[message.slotID] = !craftingUpgrade.getOreDictFlags()[message.slotID];
                    }
                }
            });

            return null;
        }
    }
}
