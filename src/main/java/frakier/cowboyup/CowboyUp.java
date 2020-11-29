package frakier.cowboyup;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Files;
import java.nio.file.Paths;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import frakier.cowboyup.config.CowboyUpConfig;
import frakier.cowboyup.init.ClientRegistrationHandler;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(CowboyUp.MODID)
public class CowboyUp {
	
//	TODO: see if you can make the horse water breathing temporarily or move the eye level up half a block.
    public static final String MODID = "cowboyup";
	public static final String MOD_NAME = "CowboyUp";
	public static final String CONFIG_FILE = MODID+"-common.toml";
	public static boolean firstRun = false;
	public static boolean debug = false;
	
	//private static final Logger LOGGER = LogManager.getLogger();
	

    public CowboyUp() {
    	if (Files.notExists(Paths.get("config", CONFIG_FILE))){
        	firstRun = true;
        }
    	if (CowboyUp.firstRun) {
            //if (!CowboyUp.versionChecker.isLatestVersion()) {
            //    updateMessage();
            //}
            //message();
            CowboyUp.firstRun = false;
        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CowboyUpConfig.COMMONSPEC,CONFIG_FILE);
    }
    
    public void onClientInit(FMLClientSetupEvent event){
		ClientRegistrationHandler.setupClient();
	}
    
    
    
   /*
    private void updateMessage() {
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
    }
    */
}
