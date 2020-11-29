package frakier.cowboyup.config;

import frakier.cowboyup.CowboyUp;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;

import org.apache.commons.lang3.tuple.Pair;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

@EventBusSubscriber(modid = CowboyUp.MODID, bus = Bus.MOD)
public class CowboyUpConfig {

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMONSPEC;
    
    //private static final Logger LOGGER = LogManager.getLogger();

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        COMMONSPEC = specPair.getRight();
    }

    public static class Common {

    	public final BooleanValue reportStatus;
    	public final BooleanValue horseCanSwimWithRider;
		public final BooleanValue horseStaysCommand;
		public final BooleanValue leashReturns;
		public final ConfigValue<Integer> swimTime;
		public final ConfigValue<Double> swimSpeed;
		public final ConfigValue<Double> wadeSpeed;

		Common(final Builder builder) {
            builder.push("general");
            	this.reportStatus = builder.comment("Report status of CowboyUp options.")
                    .define("reportStatus", true);
            builder.pop();
            
            builder.push("swim options");
            	this.horseCanSwimWithRider = builder.comment("Horse can swim with a rider?")
            		.define("horseSwimWithRider", true);
            	this.swimTime = builder.comment("Time before horse tires out when swimming in deep water.")
                    .define("swimTime", 30);
            	this.swimSpeed = builder.comment("Swim speed in deep water [2 deep or more].")
                    .define("swimSpeed", 1.01D);
            	this.wadeSpeed = builder.comment("Wading speed in shallow water [1 deep].")
                    .define("wadeSpeed", 1.05D);
            builder.pop();
            
            builder.push("stay options");
            	this.horseStaysCommand = builder.comment("Horse stays when dismounted or unleashed?")
                    .define("horseStaysCommand", true);
            builder.pop();
            
            builder.push("leash options");
            	this.leashReturns = builder.comment("Catch the lead when unleashing / removing leads?")
                        .define("leashReturn", true);
            builder.pop();
        }
    }
    
    @SubscribeEvent
	public static void onLoad(final ModConfig.Loading configevent) {
		
	}

}
