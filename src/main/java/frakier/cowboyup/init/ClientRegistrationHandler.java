package frakier.cowboyup.init;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import frakier.cowboyup.worker.HorseStays;
import frakier.cowboyup.worker.HorseSwim;
import frakier.cowboyup.worker.LeashReturn;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

public class ClientRegistrationHandler {
	
	public static boolean inti = true;
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static Minecraft mc = Minecraft.getInstance();
	
	public static void setupClient() {
		
	}
	
	@EventBusSubscriber(value = Dist.CLIENT)
    public static class ClientEventHandler {

        @SubscribeEvent(priority= EventPriority.NORMAL, receiveCanceled=true)
        public static void clientTickEvent(final PlayerTickEvent event) {
        	//make sure there is a player, a horse 
        	if (event.player != null && event.player.getRidingEntity() != null) {
        		
        		Entity target = event.player.getRidingEntity();
        		
        		//and the player is riding a horse, donkey or mule
        		if (target.getEntity().getType() == EntityType.HORSE || 
    					target.getEntity().getType() == EntityType.DONKEY || 
    					target.getEntity().getType() == EntityType.MULE) {
        			
            		Entity horse = (AbstractHorseEntity) event.player.getRidingEntity();
            			if (((AbstractHorseEntity) horse).isHorseSaddled() && horse.isRidingOrBeingRiddenBy(mc.player)) {
    	        			HorseSwim.TickEvent(event);
    	        		}
    			}
        	}
        	
        	if (Minecraft.getInstance().isGameFocused() && (!Minecraft.getInstance().isGamePaused())) {
        		if (inti == true) {
        			inti = false;
        			HorseStays.init();
            		HorseSwim.init();
            		LeashReturn.init();
        		}
        	}
        }
        
        @SubscribeEvent
        public static void onKeyInput(KeyInputEvent event) {
        	if (Minecraft.getInstance().isGameFocused() && (!Minecraft.getInstance().isGamePaused())) {
	        	HorseSwim.onKeyInput(event);
	        	HorseStays.onKeyInput(event);
        	}
        }
        
        @SubscribeEvent
        public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        	if (Minecraft.getInstance().isGameFocused() && (!Minecraft.getInstance().isGamePaused())) {
        		LeashReturn.onInteract(event);
        	}
        }
        
        @SubscribeEvent
        public static void onMount(EntityMountEvent event) {
        	
        	if (event.getEntityMounting().equals(mc.player) && Minecraft.getInstance().isGameFocused() && (!Minecraft.getInstance().isGamePaused())) {
        		
        		Entity target = event.getEntityBeingMounted().getEntity();
	        		
        		//and the player is riding a horse, donkey or mule
        		if (target.getEntity().getType() == EntityType.HORSE || 
    					target.getEntity().getType() == EntityType.DONKEY || 
    					target.getEntity().getType() == EntityType.MULE) {
        			
            		Entity horse = (AbstractHorseEntity) event.getEntityBeingMounted().getEntity();
            			if (((AbstractHorseEntity) horse).isHorseSaddled()) {
            				HorseStays.onMount(event);
    	        		}
    			}
	        }
        }
        
        /*
        @SubscribeEvent
        public static void joinWorld(final EntityJoinWorldEvent event) {
        	//StepChanger.init();
        }*/
        
        /*@SubscribeEvent
        public static void loadComplete(FMLLoadCompleteEvent event) {
        	if (CowboyUp.debug) {LOGGER.debug("loadComplete");}
        }
        */
        
        @SubscribeEvent
        public static void unload (WorldEvent.Unload event) {
        	LOGGER.info("WorldEvent.Unload");
        	inti=true;
        }
        /*
        @SubscribeEvent
        public static void load (WorldEvent.Load event) {
        	if (CowboyUp.debug) {LOGGER.info("WorldEvent.Load");}
        	//StepChanger.init();
        }*/
    }

}
