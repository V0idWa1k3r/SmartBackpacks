package v0id.vsb.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import v0id.api.vsb.capability.IBackpack;
import v0id.api.vsb.item.IUpgradeWrapper;
import v0id.vsb.item.upgrade.UpgradeHotbarSwapper;
import v0id.vsb.util.VSBUtils;

public class ScrollHotbar implements IMessage
{
    private int slotID;
    private int direction;

    public ScrollHotbar(int slotID, int direction)
    {
        this.slotID = slotID;
        this.direction = direction;
    }

    public ScrollHotbar()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.slotID = buf.readInt();
        this.direction = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.slotID);
        buf.writeInt(this.direction);
    }

    public static class Handler implements IMessageHandler<ScrollHotbar, IMessage>
    {
        @Override
        public IMessage onMessage(ScrollHotbar message, MessageContext ctx)
        {
            EntityPlayerMP sender = ctx.getServerHandler().player;
            if (message.slotID < sender.inventory.getSizeInventory())
            {
                sender.getServerWorld().addScheduledTask(() ->
                {
                    ItemStack backpack = VSBUtils.checkBackpackForHotbarUpgrade(VSBUtils.getBackpack(sender, message.slotID));
                    if (!backpack.isEmpty())
                    {
                        IBackpack iBackpack = IBackpack.of(backpack);
                        ItemStack upgrade = ItemStack.EMPTY;
                        for (IUpgradeWrapper wrapper : iBackpack.createWrapper().getReadonlyUpdatesArray())
                        {
                            if (wrapper != null && wrapper.getUpgrade() instanceof UpgradeHotbarSwapper)
                            {
                                upgrade = wrapper.getSelf();
                                break;
                            }
                        }

                        if (upgrade != ItemStack.EMPTY)
                        {
                            int index = upgrade.hasTagCompound() ? upgrade.getTagCompound().getInteger("index") : 0;
                            index = index < 0 ? VSBUtils.getBackpackRows(iBackpack.createWrapper().getBackpackType()) - 1 : index >= VSBUtils.getBackpackRows(iBackpack.createWrapper().getBackpackType()) ? 0 : index;
                            ItemStack[] playerHotbar = new ItemStack[9];
                            for (int i = 0; i < 9; ++i)
                            {
                                playerHotbar[i] = sender.inventory.getStackInSlot(i).copy();
                            }

                            for (int i = 0; i < 9; ++i)
                            {
                                int ai = index * 9 + i;
                                // Impossible but just in case something wonky happens
                                if (ai < 0 || ai >= iBackpack.getInventory().getSlots())
                                {
                                    return;
                                }

                                if (IBackpack.of(sender.inventory.getStackInSlot(i)) != null)
                                {
                                    continue;
                                }

                                sender.inventory.setInventorySlotContents(i, iBackpack.getInventory().getStackInSlot(ai).copy());
                                iBackpack.getInventory().extractItem(ai, Integer.MAX_VALUE, false);
                                iBackpack.getInventory().insertItem(ai, playerHotbar[i], false);
                            }

                            sender.getEntityWorld().playSound(null, sender.posX, sender.posY, sender.posZ, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1, 1);
                            index = index + message.direction;
                            index = index < 0 ? VSBUtils.getBackpackRows(iBackpack.createWrapper().getBackpackType()) - 1 : index >= VSBUtils.getBackpackRows(iBackpack.createWrapper().getBackpackType()) ? 0 : index;
                            if (!upgrade.hasTagCompound())
                            {
                                upgrade.setTagCompound(new NBTTagCompound());
                            }

                            upgrade.getTagCompound().setInteger("index", index);
                        }
                    }
                });
            }

            return null;
        }
    }
}
