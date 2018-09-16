package v0id.vsb.util;

import com.google.common.base.Strings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;
import v0id.api.vsb.capability.IBackpack;
import v0id.api.vsb.capability.IVSBPlayer;
import v0id.api.vsb.item.EnumBackpackType;
import v0id.api.vsb.item.IUpgradeWrapper;
import v0id.vsb.item.upgrade.UpgradeHotbarSwapper;
import v0id.vsb.item.upgrade.UpgradeNesting;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class VSBUtils
{
    public static boolean areStringsEqual(String s1, String s2)
    {
        if (Strings.isNullOrEmpty(s1))
        {
            return Strings.isNullOrEmpty(s2);
        }

        return s1.equals(s2);
    }

    public static <T>boolean anyMatch(T[] array, Predicate<T> matcher)
    {
        Objects.requireNonNull(array);
        Objects.requireNonNull(matcher);
        for (T t : array)
        {
            if (matcher.test(t))
            {
                return true;
            }
        }

        return false;
    }

    public static int[] createDefaultArray(int size, int defaultElement)
    {
        int[] ret = new int[size];
        for (int i = 0; i < size; ++i)
        {
            ret[i] = defaultElement;
        }

        return ret;
    }

    public static int getPlayerXP(EntityPlayer player)
    {
        return (int) (getExperienceForLevel(player.experienceLevel) + (player.experience * player.xpBarCap()));
    }

    public static void addXP(EntityPlayer player, int amt)
    {
        int experience = Math.max(0, getPlayerXP(player) + amt);
        player.experienceTotal = experience;
        player.experienceLevel = getLevelForExperience(experience);
        int expForLevel = getExperienceForLevel(player.experienceLevel);
        player.experience = (experience - expForLevel) / (float) player.xpBarCap();
    }

    public static int getLevelForExperience(int experience)
    {
        int i = 0;
        while (getExperienceForLevel(i) <= experience)
        {
            i++;
        }

        return i - 1;
    }

    public static int getExperienceForLevel(int level)
    {
        if (level == 0)
        {
            return 0;
        }

        if (level > 0 && level < 16)
        {
            return (int) (Math.pow(level, 2)+ 6 * level);
        }
        else
        {
            if (level > 15 && level < 32)
            {
                return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360);
            }
            else
            {
                return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220);
            }
        }
    }

    public static void registerOreSafe(String ore, ItemStack item)
    {
        NonNullList<ItemStack> itemsList = OreDictionary.getOres(ore, false);
        if (itemsList.isEmpty() || itemsList.stream().noneMatch(s -> ItemHandlerHelper.canItemStacksStack(s, item)))
        {
            OreDictionary.registerOre(ore, item);
        }
    }

    public static Method getMethodSafe(Class<?> clazz, Class[] params, String... names)
    {
        try
        {
            return ReflectionHelper.findMethod(clazz, names[0], names.length == 1 ? names[0] : names[1], params);
        }
        catch (ReflectionHelper.UnableToFindMethodException e)
        {
            FMLCommonHandler.instance().raiseException(e, "VSB was unable to reflect a constructor!", true);
        }

        return null;
    }

    public static Constructor<?> getConstructorSafe(Class<?> clazz, Class... params)
    {
        try
        {
            Constructor<?> c = clazz.getConstructor(params);
            c.setAccessible(true);
            return c;
        }
        catch (NoSuchMethodException e)
        {
            FMLCommonHandler.instance().raiseException(e, "VSB was unable to reflect a constructor!", true);
        }

        return null;
    }

    public static Class<?> getOptionalClass(String clazzName, BooleanSupplier optionalTester)
    {
        if (optionalTester == null || optionalTester.getAsBoolean())
        {
            try
            {
                return Class.forName(clazzName);
            }
            catch (ClassNotFoundException ex)
            {
                FMLCommonHandler.instance().raiseException(ex, "VSB couldn't reflect a class. This is probably due to compatibility patches not finding the mod required", false);
                return null;
            }
        }

        return null;
    }

    public static Field getFieldSafe(Class<?> clazz, String... fieldNames)
    {
        try
        {
            return ReflectionHelper.findField(clazz, fieldNames);
        }
        catch (Exception ex)
        {
            FMLCommonHandler.instance().raiseException(ex, "VSB was unable to reflect field!", true);
        }

        return null;
    }

    public static Iterable<ItemStack> getPlayerInventory(EntityPlayer player)
    {
        return () -> Stream.concat(player.inventory.mainInventory.stream(), player.inventory.offHandInventory.stream()).iterator();
    }

    public static boolean isOreDictionaryMatch(ItemStack test, ItemStack matchTo)
    {
        if (!matchTo.isEmpty() && !test.isEmpty())
        {
            int[] ids_to = OreDictionary.getOreIDs(matchTo);
            int[] ids_test = OreDictionary.getOreIDs(test);
            for (int i : ids_to)
            {
                for (int j : ids_test)
                {
                    if (i == j)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static String[] getOreNames(ItemStack is)
    {
        return Arrays.stream(OreDictionary.getOreIDs(is)).mapToObj(OreDictionary::getOreName).toArray(String[]::new);
    }

    public static <T>T firstMatch(Predicate<T> matcher, T... things)
    {
        for (T t : things)
        {
            if (matcher.test(t))
            {
                return t;
            }
        }

        return null;
    }

    public static <T>T firstMatch(Predicate<T> matcher, Iterable<T> things)
    {
        for (T t : things)
        {
            if (matcher.test(t))
            {
                return t;
            }
        }

        return null;
    }

    public static void openContainer(EntityPlayerMP playerMP, Container container)
    {
        playerMP.getNextWindowId();
        playerMP.closeContainer();
        playerMP.openContainer = container;
        container.windowId = playerMP.currentWindowId;
        playerMP.openContainer.addListener(playerMP);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(playerMP, container));
    }

    public static <T>T[] capabilityToArray(IItemHandler cap, Class<T> clazz, Function<ItemStack, T> mapper)
    {
        Objects.requireNonNull(mapper);
        T[] array = (T[]) Array.newInstance(clazz, cap.getSlots());
        for (int i = 0; i < array.length; ++i)
        {
            array[i] = mapper.apply(cap.getStackInSlot(i));
        }

        return array;
    }

    public static ItemStack checkBackpackForHotbarUpgrade(ItemStack backpack)
    {
        if (!backpack.isEmpty())
        {
            IBackpack iBackpack = IBackpack.of(backpack);
            if (iBackpack != null)
            {
                boolean hasNesting = false;
                for (IUpgradeWrapper wrapper : iBackpack.createWrapper().getReadonlyUpdatesArray())
                {
                    if (wrapper != null)
                    {
                        if (wrapper.getUpgrade() instanceof UpgradeNesting)
                        {
                            hasNesting = true;
                        }
                        else
                        {
                            if (wrapper.getUpgrade() instanceof UpgradeHotbarSwapper)
                            {
                                return backpack;
                            }
                        }
                    }
                }

                if (hasNesting)
                {
                    for (ItemStack is : iBackpack.createWrapper().getReadonlyInventory())
                    {
                        ItemStack test = checkBackpackForHotbarUpgrade(is);
                        if (!test.isEmpty())
                        {
                            return test;
                        }
                    }
                }
            }
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack getBackpack(EntityPlayer player, int slot)
    {
        if (slot == -1)
        {
            return IVSBPlayer.of(player).getCurrentBackpack();
        }
        else
        {
            return player.inventory.getStackInSlot(slot);
        }
    }

    public static int getBackpackRows(EnumBackpackType backpackType)
    {
        switch (backpackType)
        {
            case BASIC:
            {
                return 2;
            }

            case REINFORCED:
            {
                return 4;
            }

            case ADVANCED:
            {
                return 6;
            }

            case ULTIMATE:
            {
                return 9;
            }

            default:
            {
                return backpackType.getInventorySize() / 9;
            }
        }
    }
}
