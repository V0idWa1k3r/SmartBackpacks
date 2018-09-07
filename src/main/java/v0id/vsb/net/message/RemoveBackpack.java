package v0id.vsb.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import v0id.api.vsb.capability.IVSBPlayer;
import v0id.api.vsb.capability.VSBCaps;
import v0id.vsb.util.VSBUtils;

public class RemoveBackpack implements IMessage
{
    @Override
    public void fromBytes(ByteBuf buf)
    {
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    }

    public static class Handler implements IMessageHandler<RemoveBackpack, IMessage>
    {
        @Override
        public IMessage onMessage(RemoveBackpack message, MessageContext ctx)
        {
            EntityPlayerMP sender = ctx.getServerHandler().player;
            sender.getServerWorld().addScheduledTask(() ->
            {
                IVSBPlayer player = IVSBPlayer.of(sender);
                ItemStack stack = VSBUtils.firstMatch(i -> !i.isEmpty() && i.hasCapability(VSBCaps.BACKPACK_CAPABILITY, null), sender.getHeldItem(EnumHand.MAIN_HAND), sender.getHeldItem(EnumHand.OFF_HAND));
                if (stack != null)
                {
                    EnumHand hand = stack == sender.getHeldItem(EnumHand.MAIN_HAND) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
                    ItemStack backpack = player.getCurrentBackpack();
                    player.setCurrentBackpack(stack.copy());
                    sender.setItemStackToSlot(hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, backpack);
                    sender.world.playSound(null, sender.posX, sender.posY, sender.posZ, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1, 1);
                    player.sync();
                }
                else
                {
                    EnumHand hand = VSBUtils.firstMatch(h -> sender.getHeldItem(h).isEmpty(), EnumHand.MAIN_HAND, EnumHand.OFF_HAND);
                    if (hand != null)
                    {
                        ItemStack backpack = player.getCurrentBackpack();
                        sender.setItemStackToSlot(hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, backpack);
                        player.setCurrentBackpack(ItemStack.EMPTY);
                        sender.world.playSound(null, sender.posX, sender.posY, sender.posZ, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1, 1);
                        player.sync();
                    }
                }
            });

            return null;
        }
    }
}
