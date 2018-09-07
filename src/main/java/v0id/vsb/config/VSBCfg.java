package v0id.vsb.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import v0id.api.vsb.data.VSBRegistryNames;

@Config(modid = VSBRegistryNames.MODID)
public class VSBCfg
{
    @Config.RangeInt(min = 0)
    @Config.RequiresMcRestart
    public static int energyUpgradeBasic = 10000;

    @Config.RangeInt(min = 0)
    @Config.RequiresMcRestart
    public static int energyUpgradeAdvanced = 100000;

    @Config.RangeInt(min = 0)
    @Config.RequiresMcRestart
    public static int energyUpgradeUltimate = 1000000;

    @Config.RangeInt(min = 0)
    @Config.RequiresMcRestart
    public static int energyUpgradeCreative = Integer.MAX_VALUE;

    @Config.RangeInt(min = 0)
    public static int furnaceUpgradeFEPerTick = 40;

    @Config.RangeInt(min = 0)
    public static int solarUpgradeFEPerTick = 16;

    @Config.RangeInt(min = 0)
    public static int windUpgradeFEPerTick = 32;

    @Config.RangeInt(min = 0)
    public static int kineticUpgradeFEPerMeter = 20;

    @Config.RangeInt(min = 0)
    public static int nuclearUpgradeFEPerTick = 10;

    @Config.RangeInt(min = 0)
    public static int emUpgradeFEPerPulse = 1;

    @Config.RangeInt(min = 0)
    public static int inductionCoilUpgradeEnergyPerFuel = 40;

    @Config.RequiresMcRestart
    public static boolean dragonDropsScales = true;

    @Mod.EventBusSubscriber(modid = VSBRegistryNames.MODID)
    public static class ConfigHandler
    {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equalsIgnoreCase(VSBRegistryNames.MODID))
            {
                ConfigManager.sync(VSBRegistryNames.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
