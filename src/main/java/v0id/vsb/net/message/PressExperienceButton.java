package v0id.vsb.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import v0id.api.vsb.capability.IBackpack;
import v0id.api.vsb.item.IUpgradeWrapper;
import v0id.vsb.container.ContainerBackpack;
import v0id.vsb.item.upgrade.UpgradeExperience;
import v0id.vsb.net.VSBNet;
import v0id.vsb.util.VSBUtils;

import java.util.Arrays;

public class PressExperienceButton implements IMessage
{
    private int id;

    public PressExperienceButton(int id)
    {
        this.id = id;
    }

    public PressExperienceButton()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(this.id);
    }

    public static class Handler implements IMessageHandler<PressExperienceButton, IMessage>
    {
        @Override
        public IMessage onMessage(PressExperienceButton message, MessageContext ctx)
        {
            EntityPlayerMP sender = ctx.getServerHandler().player;
            sender.getServerWorld().addScheduledTask(() ->
            {
                Container openContainer = sender.openContainer;
                if (openContainer instanceof ContainerBackpack)
                {
                    ItemStack backpack = ((ContainerBackpack) openContainer).backpack;
                    IUpgradeWrapper wrapperExperience = Arrays.stream(IBackpack.of(backpack).createWrapper().getReadonlyUpdatesArray()).filter(u -> u != null && u.getUpgrade() instanceof UpgradeExperience).findAny().orElse(null);
                    if (wrapperExperience != null)
                    {
                        int expUpgrade = wrapperExperience.getSelf().hasTagCompound() ? wrapperExperience.getSelf().getTagCompound().getInteger("experience") : 0;
                        int playerExperience = VSBUtils.getPlayerXP(sender);
                        int levelsAdded = message.id == 0 ? 1 : message.id == 3 ? -1 : message.id == 1 ? 10 : message.id == 4 ? -10 : message.id == 2 ? Integer.MAX_VALUE : -Integer.MAX_VALUE;
                        if (levelsAdded < 0)
                        {
                            int expRemoved = 0;
                            if (message.id == 5)
                            {
                                expRemoved = playerExperience;
                                VSBUtils.addXP(sender, -playerExperience);
                            }
                            else
                            {
                                if (-levelsAdded > sender.experienceLevel)
                                {
                                    levelsAdded = sender.experienceLevel;
                                }

                                levelsAdded = Math.abs(levelsAdded);
                                if (levelsAdded == 0 && playerExperience > 0)
                                {
                                    expRemoved = playerExperience;
                                    VSBUtils.addXP(sender, -playerExperience);
                                }
                                else
                                {
                                    for (int i = 0; i < levelsAdded; ++i)
                                    {
                                        int expLevel = sender.xpBarCap();
                                        expRemoved += expLevel;
                                        VSBUtils.addXP(sender, -expLevel);
                                    }
                                }
                            }

                            if (!wrapperExperience.getSelf().hasTagCompound())
                            {
                                wrapperExperience.getSelf().setTagCompound(new NBTTagCompound());
                            }

                            wrapperExperience.getSelf().getTagCompound().setInteger("experience", expUpgrade + expRemoved);
                        }
                        else
                        {
                            int expLevel = VSBUtils.getLevelForExperience(expUpgrade);
                            if (levelsAdded > expLevel)
                            {
                                levelsAdded = expLevel;
                            }

                            if (levelsAdded > 0 || expUpgrade > 0)
                            {
                                int expAdded = levelsAdded == 0 ? expUpgrade : VSBUtils.getExperienceForLevel(levelsAdded);
                                if (expAdded > expUpgrade)
                                {
                                    expAdded = expUpgrade;
                                }

                                expAdded = message.id == 2 ? expUpgrade : expAdded;
                                VSBUtils.addXP(sender, expAdded);
                                if (!wrapperExperience.getSelf().hasTagCompound())
                                {
                                    wrapperExperience.getSelf().setTagCompound(new NBTTagCompound());
                                }

                                wrapperExperience.getSelf().getTagCompound().setInteger("experience", expAdded == expUpgrade ? 0 : expUpgrade - expAdded);
                            }
                        }

                        VSBNet.sendSyncGUIExperience(sender, wrapperExperience.getSelf().hasTagCompound() ? wrapperExperience.getSelf().getTagCompound().getInteger("experience") : 0);
                    }
                }
            });

            return null;
        }
    }
}
