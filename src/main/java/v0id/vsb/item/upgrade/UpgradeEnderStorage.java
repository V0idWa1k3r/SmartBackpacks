package v0id.vsb.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import v0id.api.vsb.capability.IBackpack;
import v0id.api.vsb.capability.IFilter;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.api.vsb.item.IBackpackWrapper;
import v0id.api.vsb.item.IUpgradeWrapper;
import v0id.vsb.container.ContainerUpgradeEnderStorage;
import v0id.vsb.net.VSBNet;
import v0id.vsb.util.EnumGuiType;
import v0id.vsb.util.Lazy;
import v0id.vsb.util.VSBUtils;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UpgradeEnderStorage extends UpgradeFiltered
{
    private static final Lazy<Class<?>> classEnderStorageManager = new Lazy<>(() -> VSBUtils.getOptionalClass("codechicken.enderstorage.manager.EnderStorageManager", () -> Loader.isModLoaded("enderstorage")));
    private static final Lazy<Class<?>> classFrequency = new Lazy<>(() -> VSBUtils.getOptionalClass("codechicken.enderstorage.api.Frequency", () -> Loader.isModLoaded("enderstorage")));
    private static final Lazy<Class<?>> classChestHelper = new Lazy<>(() -> VSBUtils.getOptionalClass("shetiphian.multistorage.common.enderlink.ChestHelper", () -> Loader.isModLoaded("multistorage")));
    private static final Lazy<Method> frequency_fromString = new Lazy<>(() -> VSBUtils.getMethodSafe(classFrequency.get(), new Class[]{ String.class, String.class, String.class }, "fromString"));
    private static final Lazy<Method> enderStorageManager_instance = new Lazy<>(() -> VSBUtils.getMethodSafe(classEnderStorageManager.get(), new Class[]{ boolean.class }, "instance"));
    private static final Lazy<Method> enderStorageManager_getStorage = new Lazy<>(() -> VSBUtils.getMethodSafe(classEnderStorageManager.get(), new Class[]{ classFrequency.get(), String.class }, "getStorage"));
    private static final Lazy<Method> chestHelper_getChest = new Lazy<>(() -> VSBUtils.getMethodSafe(classChestHelper.get(), new Class[]{ World.class, String.class, String.class }, "getChest"));

    public UpgradeEnderStorage()
    {
        super(VSBRegistryNames.itemUpgradeEnderStorage);
        this.inventory_size = 4;
    }

    private int getSlot(ItemStack is)
    {
        return is.getTagCompound().getInteger("index");
    }

    private void setSlot(ItemStack is, int i)
    {
        is.getTagCompound().setInteger("index", i);
    }

    private String dyeToColour(ItemStack dye)
    {
        if (!dye.isEmpty())
        {
            for (String s : VSBUtils.getOreNames(dye))
            {
                String colour = this.oreDictToColour(s);
                if (colour != null)
                {
                    return colour;
                }
            }
        }

        return null;
    }

    private String oreDictToColour(String oreDictName)
    {
        if (!oreDictName.startsWith("dye"))
        {
            return null;
        }

        if (oreDictName.equalsIgnoreCase("dyeWhite"))
        {
            return "white";
        }

        if (oreDictName.equalsIgnoreCase("dyeOrange"))
        {
            return "orange";
        }

        if (oreDictName.equalsIgnoreCase("dyeMagenta"))
        {
            return "magenta";
        }

        if (oreDictName.equalsIgnoreCase("dyeLightBlue"))
        {
            return "light_blue";
        }

        if (oreDictName.equalsIgnoreCase("dyeYellow"))
        {
            return "yellow";
        }

        if (oreDictName.equalsIgnoreCase("dyeLime"))
        {
            return "lime";
        }

        if (oreDictName.equalsIgnoreCase("dyePink"))
        {
            return "pink";
        }

        if (oreDictName.equalsIgnoreCase("dyeGray"))
        {
            return "gray";
        }

        if (oreDictName.equalsIgnoreCase("dyeLightGray"))
        {
            return "light_gray";
        }

        if (oreDictName.equalsIgnoreCase("dyeCyan"))
        {
            return "cyan";
        }

        if (oreDictName.equalsIgnoreCase("dyePurple"))
        {
            return "purple";
        }

        if (oreDictName.equalsIgnoreCase("dyeBlue"))
        {
            return "blue";
        }

        if (oreDictName.equalsIgnoreCase("dyeBrown"))
        {
            return "brown";
        }

        if (oreDictName.equalsIgnoreCase("dyeGreen"))
        {
            return "green";
        }

        if (oreDictName.equalsIgnoreCase("dyeRed"))
        {
            return "red";
        }

        if (oreDictName.equalsIgnoreCase("dyeBlack"))
        {
            return "black";
        }

        return null;
    }

    private String dyeToCode(ItemStack dye)
    {
        if (!dye.isEmpty())
        {
            for (String s : VSBUtils.getOreNames(dye))
            {
                String code = this.oreDictToCode(s);
                if (code != null)
                {
                    return code;
                }
            }
        }

        return null;
    }

    private String oreDictToCode(String oreDictName)
    {
        if (!oreDictName.startsWith("dye"))
        {
            return null;
        }

        if (oreDictName.equalsIgnoreCase("dyeWhite"))
        {
            return "0";
        }

        if (oreDictName.equalsIgnoreCase("dyeOrange"))
        {
            return "1";
        }

        if (oreDictName.equalsIgnoreCase("dyeMagenta"))
        {
            return "2";
        }

        if (oreDictName.equalsIgnoreCase("dyeLightBlue"))
        {
            return "3";
        }

        if (oreDictName.equalsIgnoreCase("dyeYellow"))
        {
            return "4";
        }

        if (oreDictName.equalsIgnoreCase("dyeLime"))
        {
            return "5";
        }

        if (oreDictName.equalsIgnoreCase("dyePink"))
        {
            return "6";
        }

        if (oreDictName.equalsIgnoreCase("dyeGray"))
        {
            return "7";
        }

        if (oreDictName.equalsIgnoreCase("dyeLightGray"))
        {
            return "8";
        }

        if (oreDictName.equalsIgnoreCase("dyeCyan"))
        {
            return "9";
        }

        if (oreDictName.equalsIgnoreCase("dyePurple"))
        {
            return "A";
        }

        if (oreDictName.equalsIgnoreCase("dyeBlue"))
        {
            return "B";
        }

        if (oreDictName.equalsIgnoreCase("dyeBrown"))
        {
            return "C";
        }

        if (oreDictName.equalsIgnoreCase("dyeGreen"))
        {
            return "D";
        }

        if (oreDictName.equalsIgnoreCase("dyeRed"))
        {
            return "E";
        }

        if (oreDictName.equalsIgnoreCase("dyeBlack"))
        {
            return "F";
        }

        return null;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("vsb.txt.upgrade.ender_storage.desc").split("\\|")));
    }

    private Object tryInvokeMethod(Method m, Object instance, Object... params)
    {
        if (m == null)
        {
            throw new NullPointerException("Supplied method was null, should be impossible.");
        }

        try
        {
            return m.invoke(instance, params);
        }
        catch (IllegalAccessException ex)
        {
            m.setAccessible(true);
            try
            {
                return m.invoke(instance, params);
            }
            catch (IllegalAccessException e)
            {
                FMLCommonHandler.instance().raiseException(e, "Impossible reflection exception thrown", true);
            }
            catch (InvocationTargetException e)
            {
                FMLCommonHandler.instance().raiseException(e, "Unable to reflect frequency class!", true);
            }
        }
        catch (InvocationTargetException e)
        {
            FMLCommonHandler.instance().raiseException(e, "Unable to reflect frequency class!", true);
        }

        return null;
    }
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if (playerIn.isSneaking() && !worldIn.isRemote)
        {
            ItemStack is = playerIn.getHeldItem(handIn);
            if (!is.hasTagCompound())
            {
                is.setTagCompound(new NBTTagCompound());
            }

            is.getTagCompound().setBoolean("multistorage", !is.getTagCompound().getBoolean("multistorage"));
            worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1, 1);
            return new ActionResult<>(EnumActionResult.SUCCESS, is);
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker)
    {
        if (!Loader.isModLoaded("enderstorage") && !Loader.isModLoaded("multistorage"))
        {
            return;
        }

        boolean useMultiStorage = Loader.isModLoaded("enderstorage") && Loader.isModLoaded("multistorage") ? self.getSelf().hasTagCompound() && self.getSelf().getTagCompound().getBoolean("multistorage") : Loader.isModLoaded("multistorage");
        if (!self.getSelf().hasTagCompound())
        {
            self.getSelf().setTagCompound(new NBTTagCompound());
        }

        int index = this.getSlot(self.getSelf());
        if (index >= backpack.getInventory().getSlots())
        {
            index = 0;
        }

        ItemStack is = backpack.getInventory().getStackInSlot(index).copy();
        IFilter filter = IFilter.of(self.getSelf().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0));
        if (!is.isEmpty() && IBackpack.of(is) == null && (filter == null || filter.accepts(is)))
        {
            IItemHandler inv = self.getSelf().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            ItemStack dye0 = inv.getStackInSlot(1);
            ItemStack dye1 = inv.getStackInSlot(2);
            ItemStack dye2 = inv.getStackInSlot(3);
            if (!dye0.isEmpty() && !dye1.isEmpty() && !dye2.isEmpty())
            {
                if (!useMultiStorage)
                {
                    String dyeName0 = this.dyeToColour(dye0);
                    String dyeName1 = this.dyeToColour(dye1);
                    String dyeName2 = this.dyeToColour(dye2);
                    if (dyeName0 != null && dyeName1 != null && dyeName2 != null)
                    {
                        Object frequency = this.tryInvokeMethod(frequency_fromString.get(), null, dyeName0, dyeName1, dyeName2);
                        Object enderStorageManager = this.tryInvokeMethod(enderStorageManager_instance.get(), null, false);
                        Object storage = this.tryInvokeMethod(enderStorageManager_getStorage.get(), enderStorageManager, frequency, "item");
                        if (storage instanceof IInventory)
                        {
                            IItemHandler itemHandler = new InvWrapper((IInventory) storage);
                            ItemStack result = ItemHandlerHelper.insertItemStacked(itemHandler, is, true);
                            if (result != is)
                            {
                                ItemHandlerHelper.insertItemStacked(itemHandler, is, false);
                                int extracted = result.isEmpty() ? is.getCount() : is.getCount() - result.getCount();
                                backpack.getInventory().extractItem(index, extracted, false);
                            }
                        }
                    }
                }
                else
                {
                    String dyeCode0 = this.dyeToCode(dye0);
                    String dyeCode1 = this.dyeToCode(dye1);
                    String dyeCode2 = this.dyeToCode(dye2);
                    if (dyeCode0 != null && dyeCode1 != null && dyeCode2 != null)
                    {
                        Object chest = this.tryInvokeMethod(chestHelper_getChest.get(), null, ticker.world, "all", dyeCode0 + dyeCode1 + dyeCode2);
                        if (chest instanceof IInventory)
                        {
                            IItemHandler itemHandler = new InvWrapper((IInventory) chest);
                            ItemStack result = ItemHandlerHelper.insertItemStacked(itemHandler, is, true);
                            if (result != is)
                            {
                                ItemHandlerHelper.insertItemStacked(itemHandler, is, false);
                                int extracted = result.isEmpty() ? is.getCount() : is.getCount() - result.getCount();
                                backpack.getInventory().extractItem(index, extracted, false);
                            }
                        }
                    }
                }
            }
        }

        this.setSlot(self.getSelf(), ++index);
    }

    @Override
    public void onPulse(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity pulsar)
    {
    }

    @Override
    public boolean onItemPickup(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, EntityItem item, Entity picker)
    {
        return false;
    }

    @Override
    public void onInstalled(IBackpackWrapper backpack, IUpgradeWrapper self)
    {
    }

    @Override
    public void onUninstalled(IBackpackWrapper backpack, IUpgradeWrapper self)
    {
    }

    @Override
    public boolean canInstall(IBackpackWrapper backpack, IUpgradeWrapper self)
    {
        return Loader.isModLoaded("enderstorage") && !Arrays.stream(backpack.getReadonlyUpdatesArray()).filter(Objects::nonNull).map(IUpgradeWrapper::getSelf).anyMatch(i -> i.getItem() == self.getSelf().getItem());
    }

    @Override
    public boolean hasSyncTag()
    {
        return false;
    }

    @Override
    public void openContainer(EntityPlayerMP player, ItemStack stack, int slot, int slotID, boolean inventory)
    {
        VSBUtils.openContainer(player, new ContainerUpgradeEnderStorage(player.inventory, stack, slot));
        VSBNet.sendOpenGUI(player, slotID, inventory, slot, EnumGuiType.UPGRADE_ENDER_STORAGE);
    }
}
