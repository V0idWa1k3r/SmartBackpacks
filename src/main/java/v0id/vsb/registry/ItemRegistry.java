package v0id.vsb.registry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import v0id.api.vsb.data.VSBRegistryNames;
import v0id.api.vsb.item.EnumBackpackType;
import v0id.vsb.config.VSBCfg;
import v0id.vsb.item.ItemBackpack;
import v0id.vsb.item.ItemSimple;
import v0id.vsb.item.upgrade.*;
import v0id.vsb.util.VSBUtils;

@Mod.EventBusSubscriber(modid = VSBRegistryNames.MODID)
public class ItemRegistry
{
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemBackpack(EnumBackpackType.BASIC, VSBRegistryNames.itemBackpack));
        event.getRegistry().register(new ItemBackpack(EnumBackpackType.REINFORCED, VSBRegistryNames.itemReinforcedBackpack));
        event.getRegistry().register(new ItemBackpack(EnumBackpackType.ADVANCED, VSBRegistryNames.itemAdvancedBackpack));
        event.getRegistry().register(new ItemBackpack(EnumBackpackType.ULTIMATE, VSBRegistryNames.itemUltimateBackpack));
        event.getRegistry().register(new ItemSimple(VSBRegistryNames.itemReinforcedLeather));
        event.getRegistry().register(new ItemSimple(VSBRegistryNames.itemDragonScales));
        event.getRegistry().register(new ItemSimple(VSBRegistryNames.itemUpgradeBase));
        event.getRegistry().register(new UpgradeDamageBar());
        event.getRegistry().register(new UpgradeNesting());
        event.getRegistry().register(new UpgradeFilter());
        event.getRegistry().register(new UpgradePulling());
        event.getRegistry().register(new UpgradeCompressor());
        event.getRegistry().register(new UpgradeSorter());
        event.getRegistry().register(new UpgradeVoid());
        event.getRegistry().register(new UpgradePushing());
        event.getRegistry().register(new UpgradeSmelting());
        event.getRegistry().register(new UpgradeGrinding());
        event.getRegistry().register(new UpgradeEnergy(VSBRegistryNames.itemUpgradeEnergyBasic, VSBCfg.energyUpgradeBasic));
        event.getRegistry().register(new UpgradeEnergy(VSBRegistryNames.itemUpgradeEnergyAdvanced, VSBCfg.energyUpgradeAdvanced));
        event.getRegistry().register(new UpgradeEnergy(VSBRegistryNames.itemUpgradeEnergyUltimate, VSBCfg.energyUpgradeUltimate));
        event.getRegistry().register(new UpgradeEnergy(VSBRegistryNames.itemUpgradeEnergyCreatve, VSBCfg.energyUpgradeCreative));
        event.getRegistry().register(new UpgradeFurnaceGenerator());
        event.getRegistry().register(new UpgradeSolarGenerator());
        event.getRegistry().register(new UpgradeWindGenerator());
        event.getRegistry().register(new UpgradeKineticGenerator());
        event.getRegistry().register(new UpgradeNuclearGenerator());
        event.getRegistry().register(new UpgradeEMGenerator());
        event.getRegistry().register(new UpgradeInductionCoil());
        event.getRegistry().register(new UpgradeCharging());
        event.getRegistry().register(new UpgradeFeeding());
        event.getRegistry().register(new UpgradeWaterSpring());
        event.getRegistry().register(new UpgradeEnderStorage());
        event.getRegistry().register(new UpgradeSoulbound());
        event.getRegistry().register(new UpgradeAirBags());
        event.getRegistry().register(new UpgradeMending());
        event.getRegistry().register(new UpgradeLimiting());
        event.getRegistry().register(new UpgradeDepositing());
        event.getRegistry().register(new UpgradeHotbarSwapper());
        event.getRegistry().register(new UpgradeMagnet());
        event.getRegistry().register(new UpgradeQuiver());
        event.getRegistry().register(new UpgradeExperience());
        event.getRegistry().register(new UpgradeEnderChest());
        event.getRegistry().register(new UpgradeSharing());
        event.getRegistry().register(new UpgradeLighting());
        event.getRegistry().register(new UpgradeCrafting());

        if (Loader.isModLoaded("harvestcraft"))
        {
            OreDictionary.registerOre("leatherHardened", event.getRegistry().getValue(new ResourceLocation("harvestcraft:hardenedleatheritem")));
        }

        VSBUtils.registerOreSafe("flint", new ItemStack(Items.FLINT));
        VSBUtils.registerOreSafe("chestWood", new ItemStack(Blocks.CHEST));
        VSBUtils.registerOreSafe("workbenchWood", new ItemStack(Blocks.CRAFTING_TABLE));
        VSBUtils.registerOreSafe("piston", new ItemStack(Blocks.PISTON));
        VSBUtils.registerOreSafe("piston", new ItemStack(Blocks.STICKY_PISTON));
        VSBUtils.registerOreSafe("glowstone", new ItemStack(Blocks.GLOWSTONE));
        VSBUtils.registerOreSafe("obsidian", new ItemStack(Blocks.OBSIDIAN));
        VSBUtils.registerOreSafe("furnaceStone", new ItemStack(Blocks.FURNACE));
        VSBUtils.registerOreSafe("rodBlaze", new ItemStack(Items.BLAZE_ROD));
        VSBUtils.registerOreSafe("book", new ItemStack(Items.BOOK));
        VSBUtils.registerOreSafe("coal", new ItemStack(Items.COAL));
        VSBUtils.registerOreSafe("charcoal", new ItemStack(Items.COAL, 1, 1));
        VSBUtils.registerOreSafe("dustGlowstone", new ItemStack(Items.GLOWSTONE_DUST));
        VSBUtils.registerOreSafe("dustBlaze", new ItemStack(Items.BLAZE_POWDER));
        VSBUtils.registerOreSafe("gemPrismarine", new ItemStack(Items.PRISMARINE_CRYSTALS));
        VSBUtils.registerOreSafe("arrow", new ItemStack(Items.ARROW));
        VSBUtils.registerOreSafe("arrow", new ItemStack(Items.SPECTRAL_ARROW));
        VSBUtils.registerOreSafe("arrow", new ItemStack(Items.TIPPED_ARROW));
        VSBUtils.registerOreSafe("torch", new ItemStack(Blocks.TORCH));
        for (int i = 0; i < 16; ++i)
        {
            VSBUtils.registerOreSafe("wool", new ItemStack(Blocks.WOOL, 1, i));
        }

        OreDictionary.registerOre("leatherHardened", event.getRegistry().getValue(VSBRegistryNames.asLocation(VSBRegistryNames.itemReinforcedLeather)));
        OreDictionary.registerOre("leatherReinforced", event.getRegistry().getValue(VSBRegistryNames.asLocation(VSBRegistryNames.itemReinforcedLeather)));
        OreDictionary.registerOre("dragonScales", event.getRegistry().getValue(VSBRegistryNames.asLocation(VSBRegistryNames.itemDragonScales)));
        OreDictionary.registerOre("dragonScale", event.getRegistry().getValue(VSBRegistryNames.asLocation(VSBRegistryNames.itemDragonScales)));
        OreDictionary.registerOre("scalesDragon", event.getRegistry().getValue(VSBRegistryNames.asLocation(VSBRegistryNames.itemDragonScales)));
        OreDictionary.registerOre("scaleDragon", event.getRegistry().getValue(VSBRegistryNames.asLocation(VSBRegistryNames.itemDragonScales)));
    }
}
