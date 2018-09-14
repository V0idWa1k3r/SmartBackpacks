package v0id.vsb.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.lwjgl.input.Keyboard;
import v0id.api.vsb.capability.IBackpack;
import v0id.api.vsb.data.VSBItems;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.vsb.client.render.RenderLayerBackpack;
import v0id.vsb.item.ItemBackpack;

import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = VSBRegistryNames.MODID, value = { Side.CLIENT })
public class ClientRegistry
{
    public static KeyBinding key_openBackpack;
    public static KeyBinding key_removeBackpack;
    public static KeyBinding key_changeHotbar;

    static void onPreInit()
    {
        key_openBackpack = new KeyBinding("vsb.kb.openBackpack", KeyConflictContext.UNIVERSAL, KeyModifier.NONE, Keyboard.KEY_B, "key.categories.inventory");
        key_removeBackpack = new KeyBinding("vsb.kb.removeBackpack", KeyConflictContext.UNIVERSAL, KeyModifier.SHIFT, Keyboard.KEY_B, "key.categories.inventory");
        key_changeHotbar = new KeyBinding("vsb.kb.changeHotbar", KeyConflictContext.UNIVERSAL, KeyModifier.NONE, Keyboard.KEY_K, "key.categories.inventory");
        net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding(key_openBackpack);
        net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding(key_removeBackpack);
        net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding(key_changeHotbar);
    }

    static void onInit()
    {
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((is, layer) ->
        {
            if (is.getItem() instanceof ItemBackpack && layer == 0)
            {
                IBackpack backpack = IBackpack.of(is);
                return backpack != null ? backpack.createWrapper().getColor() : -1;
            }

            return -1;
        }, VSBItems.BASIC_BACKPACK, VSBItems.REINFORCED_BACKPACK, VSBItems.ADVANCED_BACKPACK, VSBItems.ULTIMATE_BACKPACK);
    }

    static void onPostInit()
    {
        Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
        for (RenderPlayer renderer : skinMap.values())
        {
            renderer.addLayer(new RenderLayerBackpack());
        }
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event)
    {
        registerSimpleModel(VSBItems.BASIC_BACKPACK);
        registerSimpleModel(VSBItems.REINFORCED_BACKPACK);
        registerSimpleModel(VSBItems.ADVANCED_BACKPACK);
        registerSimpleModel(VSBItems.ULTIMATE_BACKPACK);
        registerSimpleModel(VSBItems.UPGRADE_BASE);
        registerSimpleModel(VSBItems.UPGRADE_DAMAGE_BAR);
        registerSimpleModel(VSBItems.UPGRADE_NESTING);
        registerSimpleModel(VSBItems.UPGRADE_FILTER);
        registerSimpleModel(VSBItems.UPGRADE_PULLING);
        registerSimpleModel(VSBItems.UPGRADE_COMPRESSOR);
        registerSimpleModel(VSBItems.UPGRADE_SORTING);
        registerSimpleModel(VSBItems.UPGRADE_VOID);
        registerSimpleModel(VSBItems.UPGRADE_PUSHING);
        registerSimpleModel(VSBItems.UPGRADE_SMELTING);
        registerSimpleModel(VSBItems.UPGRADE_GRINDING);
        registerSimpleModel(VSBItems.UPGRADE_ENERGY_BASIC);
        registerSimpleModel(VSBItems.UPGRADE_ENERGY_ADVANCED);
        registerSimpleModel(VSBItems.UPGRADE_ENERGY_ULTIMATE);
        registerSimpleModel(VSBItems.UPGRADE_ENERGY_CREATIVE);
        registerSimpleModel(VSBItems.UPGRADE_FURNACE_GENERATOR);
        registerSimpleModel(VSBItems.UPGRADE_SOLAR_GENERATOR);
        registerSimpleModel(VSBItems.UPGRADE_WIND_GENERATOR);
        registerSimpleModel(VSBItems.UPGRADE_KINETIC_GENERATOR);
        registerSimpleModel(VSBItems.UPGRADE_NUCLEAR_GENERATOR);
        registerSimpleModel(VSBItems.UPGRADE_EM_GENERATOR);
        registerSimpleModel(VSBItems.UPGRADE_INDUCTION_COIL);
        registerSimpleModel(VSBItems.UPGRADE_CHARGING);
        registerSimpleModel(VSBItems.UPGRADE_FEEDING);
        registerSimpleModel(VSBItems.UPGRADE_WATER_SPRING);
        registerSimpleModel(VSBItems.UPGRADE_ENDER_STORAGE);
        registerSimpleModel(VSBItems.UPGRADE_SOULBOUND);
        registerSimpleModel(VSBItems.UPGRADE_AIR_BAG);
        registerSimpleModel(VSBItems.UPGRADE_MENDING);
        registerSimpleModel(VSBItems.UPGRADE_LIMITING);
        registerSimpleModel(VSBItems.UPGRADE_DEPOSITING);
        registerSimpleModel(VSBItems.UPGRADE_HOTBAR);
        registerSimpleModel(VSBItems.UPGRADE_MAGNET);
        registerSimpleModel(VSBItems.UPGRADE_QUIVER);
        registerSimpleModel(VSBItems.UPGRADE_EXPERIENCE);
        registerSimpleModel(VSBItems.UPGRADE_ENDER_CHEST);
        registerSimpleModel(VSBItems.UPGRADE_SHARING);
        registerSimpleModel(VSBItems.UPGRADE_LIGHTING);
        registerSimpleModel(VSBItems.UPGRADE_CRAFTING);
        registerSimpleModel(VSBItems.REINFORCED_LEATHER);
        registerSimpleModel(VSBItems.DRAGON_SCALES);
    }

    private static void registerStaticModel(IForgeRegistryEntry<?> entry)
    {
        registerStaticModel(entry, "inventory");
    }

    private static void registerStaticModel(IForgeRegistryEntry<?> entry, String variant)
    {
        ModelResourceLocation staticLocation = new ModelResourceLocation(entry.getRegistryName(), variant);
        Item item = entry instanceof Block ? Item.getItemFromBlock((Block) entry) : (Item) entry;
        ModelLoader.setCustomMeshDefinition(item, i -> staticLocation);
        ModelBakery.registerItemVariants(item, staticLocation);
    }

    private static void registerModelWithVariants(IForgeRegistryEntry<?> entry, int maxVariants, Function<Integer, String> variantProvider)
    {
        Item item = entry instanceof Block ? Item.getItemFromBlock((Block) entry) : (Item) entry;
        for (int i = 0; i < maxVariants; ++i)
        {
            ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(entry.getRegistryName(), variantProvider.apply(i)));
        }
    }

    private static void registerSimpleModel(IForgeRegistryEntry<?> entry)
    {
        registerSimpleModel(entry, "inventory");
    }

    private static void registerSimpleModel(IForgeRegistryEntry<?> entry, String variant)
    {
        ModelLoader.setCustomModelResourceLocation(entry instanceof Block ? Item.getItemFromBlock((Block) entry) : (Item) entry, 0, new ModelResourceLocation(entry.getRegistryName(), variant));
    }
}
