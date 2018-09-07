package v0id.api.vsb.item;

import net.minecraft.item.ItemStack;

public interface IUpgradeWrapper
{
    IBackpackWrapper getContainer();

    IUpgrade getUpgrade();

    ItemStack getSelf();
}
