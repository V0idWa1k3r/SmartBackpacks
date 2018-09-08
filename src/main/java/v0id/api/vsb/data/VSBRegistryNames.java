package v0id.api.vsb.data;

import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class VSBRegistryNames
{
    public static final String MODID = "v0idssmartbackpacks";

    public static final String
        itemBackpack                                                                        = "backpack",
        itemReinforcedBackpack                                                              = "backpack_reinforced",
        itemAdvancedBackpack                                                                = "backpack_advanced",
        itemUltimateBackpack                                                                = "backpack_ultimate",
        itemUpgradeBase                                                                     = "upgrade_base",
        itemUpgradeDamageBar                                                                = "upgrade_damage_bar",
        itemUpgradeNesting                                                                  = "upgrade_nesting",
        itemUpgradeFilter                                                                   = "upgrade_filter",
        itemUpgradePulling                                                                  = "upgrade_pulling",
        itemUpgradeCompressor                                                               = "upgrade_compressor",
        itemUpgradeSorting                                                                  = "upgrade_sorting",
        itemUpgradeVoid                                                                     = "upgrade_void",
        itemUpgradePushing                                                                  = "upgrade_pushing",
        itemUpgradeSmelting                                                                 = "upgrade_smelting",
        itemUpgradeGrinding                                                                 = "upgrade_grinding",
        itemUpgradeEnergyBasic                                                              = "upgrade_energy_basic",
        itemUpgradeEnergyAdvanced                                                           = "upgrade_energy_advanced",
        itemUpgradeEnergyUltimate                                                           = "upgrade_energy_ultimate",
        itemUpgradeEnergyCreatve                                                            = "upgrade_energy_creative",
        itemUpgradeFurnaceGenerator                                                         = "upgrade_generator_furnace",
        itemUpgradeSolarGenerator                                                           = "upgrade_generator_solar",
        itemUpgradeWindGenerator                                                            = "upgrade_generator_wind",
        itemUpgradeKineticGenerator                                                         = "upgrade_generator_kinetic",
        itemUpgradeNuclearGenerator                                                         = "upgrade_generator_nuclear",
        itemUpgradeEMGenerator                                                              = "upgrade_generator_em",
        itemUpgradeInductionCoil                                                            = "upgrade_induction_coil",
        itemUpgradeCharging                                                                 = "upgrade_charging",
        itemUpgradeFeeding                                                                  = "upgrade_feeding",
        itemUpgradeWaterSpring                                                              = "upgrade_water_spring",
        itemUpgradeEnderStorage                                                             = "upgrade_ender_storage",
        itemUpgradeSoulbound                                                                = "upgrade_soulbound",
        itemUpgradeAirBags                                                                  = "upgrade_air_bags",
        itemUpgradeMending                                                                  = "upgrade_mending",
        itemUpgradeLimiting                                                                 = "upgrade_limiting",
        itemUpgradeDepositing                                                               = "upgrade_depositing",
        itemUpgradeHotbarSwapping                                                           = "upgrade_hotbar",
        itemReinforcedLeather                                                               = "reinforced_leather",
        itemDragonScales                                                                    = "dragon_scales";

    private static final Map<String, ResourceLocation> cache = Maps.newHashMap();

    public static ResourceLocation asLocation(String name)
    {
        return asLocation(name, true);
    }

    public static ResourceLocation asLocation(String name, boolean doCache)
    {
        if (doCache)
        {
            if (!cache.containsKey(name))
            {
                cache.put(name, new ResourceLocation(MODID, name));
            }

            return cache.get(name);
        }
        else
        {
            return new ResourceLocation(MODID, name);
        }
    }
}
