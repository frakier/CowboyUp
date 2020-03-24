package frakier.cowboyup.worker.helper;

import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class HorseWorkerHelpers {

	//loop over the registered fluids to see if the entity is in any fluid
	public static boolean areEyesInAnyFuid(Entity entity) {
		boolean wet = false;
		
		Iterator<Entry<ResourceLocation, Tag<Fluid>>> iterator = FluidTags.getCollection().getTagMap().entrySet().iterator();
		
		while (iterator.hasNext()) {
			if(entity.areEyesInFluid((Tag<Fluid>) iterator.next().getValue())) {
				wet=true;
			}
		}
		
		return wet;
	}
	
}
