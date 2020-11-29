package frakier.cowboyup.worker.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import frakier.cowboyup.CowboyUp;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag.INamedTag;

public class HorseWorkerHelpers {

	private static final Logger LOGGER = LogManager.getLogger();
	
	//loop over the registered fluids to see if the entity is in any fluid
	public static boolean areEyesInAnyFuid(Entity entity) {
		boolean wet = false;
		
		for(INamedTag<Fluid> entry: FluidTags.getAllTags())
        {
			if(entity.areEyesInFluid(entry)) {
				wet=true;
			}
		}

		if (CowboyUp.debug) {
			if (wet==true) {
				LOGGER.info("WET:true enugh");
			}else{
				LOGGER.info("WET:false enugh");
			}
		}
		
		return wet;
	}
	
}
