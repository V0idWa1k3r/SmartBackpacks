package v0id.vsb.util;

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
}
