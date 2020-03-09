package frakier.cowboyup.config;

import frakier.cowboyup.CowboyUp;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber(modid = CowboyUp.MODID, bus = Bus.MOD)
public class CowboyUpConfig {

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMONSPEC;
    
    private static final Logger LOGGER = LogManager.getLogger();

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        COMMONSPEC = specPair.getRight();
    }

    public static class Common {

    	public final BooleanValue horseSwimsWithRider;
		public final BooleanValue horseStaysCommand;
		public final BooleanValue dontDropLead;

        Common(final Builder builder) {
            builder.push("client");

            this.horseSwimsWithRider = builder.comment("Horse can swim with a rider?")
                .define("horseSwimWithRider", true);
            this.horseStaysCommand = builder.comment("Horse stays when dismounted or unleashed?")
                    .define("horseStaysCommand", true);
            this.dontDropLead = builder.comment("Catch the lead when unleashing?")
                    .define("dontDropLead", true);

            builder.pop();
        }
    }
    
    @SubscribeEvent
	public static void onLoad(final ModConfig.Loading configevent) {
		
	}

}
