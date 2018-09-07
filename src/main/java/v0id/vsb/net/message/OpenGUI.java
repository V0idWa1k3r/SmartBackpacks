package v0id.vsb.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import v0id.api.vsb.capability.IVSBPlayer;
import v0id.vsb.VSB;
import v0id.vsb.container.ContainerBackpack;
import v0id.vsb.util.EnumGuiType;

public class OpenGUI implements IMessage
{
    private int slot;
    private int windowID;
    private EnumGuiType guiType;
    private int slotID;
    private boolean openedFromInventory;

    public OpenGUI(int slot, int windowID, EnumGuiType guiType, int slotID, boolean openedFromInventory)
    {
        this.slot = slot;
        this.windowID = windowID;
        this.guiType = guiType;
        this.slotID = slotID;
        this.openedFromInventory = openedFromInventory;
    }

    public OpenGUI()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.slot = buf.readInt();
        this.windowID = buf.readInt();
        this.guiType = EnumGuiType.values()[buf.readByte()];
        this.slotID = buf.readInt();
        this.openedFromInventory = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.slot);
        buf.writeInt(this.windowID);
        buf.writeByte(this.guiType.ordinal());
        buf.writeInt(this.slotID);
        buf.writeBoolean(this.openedFromInventory);
    }

    public static class Handler implements IMessageHandler<OpenGUI, IMessage>
    {
        @Override
        public IMessage onMessage(OpenGUI message, MessageContext ctx)
        {
            VSB.proxy.getClientListener().addScheduledTask(() ->
            {
                EntityPlayer player = VSB.proxy.getClientPlayer();
                Container openContainer = message.openedFromInventory ? null : player.openContainer;
                ItemStack is;
                if (message.guiType == EnumGuiType.WORN_BACKPACK || message.guiType == EnumGuiType.WORN_BACKPACK_UPGRADES)
                {
                    is = IVSBPlayer.of(player).getCurrentBackpack();
                }
                else
                {
                    if (message.guiType == EnumGuiType.BACKPACK || message.guiType == EnumGuiType.BACKPACK_UPGRADES)
                    {
                        if (openContainer instanceof ContainerBackpack)
                        {
                            if (((ContainerBackpack) openContainer).parentContainer instanceof ContainerBackpack)
                            {
                                is = ((ContainerBackpack) openContainer).parentContainer.inventorySlots.get(message.slotID).getStack();
                            }
                            else
                            {
                                is = openContainer.inventorySlots.get(message.slotID).getStack();
                            }
                        }
                        else
                        {
                            is = player.inventory.getStackInSlot(message.slotID);
                        }
                    }
                    else
                    {
                        if (openContainer != null)
                        {
                            is = openContainer.inventorySlots.get(message.slotID).getStack();
                        }
                        else
                        {
                            is = player.inventory.getStackInSlot(message.slotID);
                        }
                    }
                }

                VSB.proxy.openModGui(is, message.guiType, message.slot);
                VSB.proxy.getClientPlayer().openContainer.windowId = message.windowID;
            });

            return null;
        }
    }
}
