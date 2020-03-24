package frakier.cowboyup;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import frakier.cowboyup.config.CowboyUpConfig;
import frakier.cowboyup.worker.HorseSwim;
import frakier.cowboyup.worker.LeashReturn;
import frakier.cowboyup.worker.HorseStays;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;

@Mod(CowboyUp.MODID)
public class CowboyUp {
	
//	TODO: see if you can make the horse water breathing temporarily or move the eye level up half a block.
    public static final String MODID = "cowboyup";
	public static final String MOD_VERSION = "2.0.1";
	public static final String MOD_NAME = "CowboyUp";
	public static final String CONFIG_FILE = MODID+"-common.toml";
	public static boolean firstRun = false;
	public static boolean inti = true;
	public static String MC_VERSION;
	public static boolean debug = false;
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static Minecraft mc = Minecraft.getInstance();
	public static PlayerEntity player = mc.player;

    public CowboyUp() {
    	if (Files.notExists(Paths.get("config", CONFIG_FILE))){
        	firstRun = true;
        }
    	if (CowboyUp.firstRun) {
        	CowboyUp.MC_VERSION = mc.getVersion();
            //if (!CowboyUp.versionChecker.isLatestVersion()) {
            //    updateMessage();
            //}
            //message();
            CowboyUp.firstRun = false;
        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CowboyUpConfig.COMMONSPEC,CONFIG_FILE);
    }
    
    @EventBusSubscriber(value = Dist.CLIENT)
    public static class ClientEventHandler {

        @SubscribeEvent(priority= EventPriority.NORMAL, receiveCanceled=true)
        public static void clientTickEvent(final PlayerTickEvent event) {
        	//make sure there is a player, a horse 
        	if (event.player != null && (AbstractHorseEntity) event.player.getRidingEntity() != null) {
        		//and the player is riding the horse
        		Entity horse = (AbstractHorseEntity) event.player.getRidingEntity();
        			if (((AbstractHorseEntity) horse).isHorseSaddled() && horse.isRidingOrBeingRiddenBy(CowboyUp.player)) {
	        			HorseSwim.TickEvent(event);
	        		}
        	}
        	
        	if (Minecraft.getInstance().isGameFocused() && (!Minecraft.getInstance().isGamePaused())) {
        		if (inti == true) {
        			inti = false;
        			player = event.player;
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
        	if (event.getEntityMounting().equals(player) && Minecraft.getInstance().isGameFocused() && (!Minecraft.getInstance().isGamePaused())) {
        		AbstractHorseEntity t = (AbstractHorseEntity) event.getEntityBeingMounted().getEntity();
        		if (t.isHorseSaddled()) {
        			HorseStays.onMount(event);
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
    
    private void updateMessage() {
    	/* disabled for now
        String m2 = (Object) TextFormatting.GOLD + I18n.format("msg.CowboyUp.updateAvailable") + ": " + (Object) TextFormatting.DARK_AQUA + "[" + (Object) TextFormatting.YELLOW + "CowboyUp" + (Object) TextFormatting.WHITE + " v" + "" + (Object) TextFormatting.DARK_AQUA + "]";
        String url = "";
        ClickEvent versionCheckChatClickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
        HoverEvent versionCheckChatHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(I18n.format("msg.CowboyUp.updateAvailable") + "!"));
        TextComponent component = new StringTextComponent(m2);
        Style s = component.getStyle();
        s.setClickEvent(versionCheckChatClickEvent);
        s.setHoverEvent(versionCheckChatHoverEvent);
        component.setStyle(s);
        player.sendMessage((ITextComponent) component);
        */
    }
}
