package frakier.cowboyup.worker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import frakier.cowboyup.CowboyUp;
import frakier.cowboyup.config.CowboyUpConfig;
import frakier.cowboyup.init.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.DistExecutor;

public class LeashReturn {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public LeashReturn() {}
	
	public static void init() {
    	if (CowboyUp.debug) {LOGGER.info("LeashReturn init");}
    	status();
    }
	
	public static void onInteract(PlayerInteractEvent.EntityInteract event) {
		//this method fires twice, once for each hand on any interaction
		//so we have to handle both hands
		 
		//player is interacting with a horse
		
		if (CowboyUp.mc.player instanceof PlayerEntity  && event.getTarget() instanceof AnimalEntity) {
			 //main hand used
			 if(event.getHand().equals(Hand.MAIN_HAND)) {
				 if (CowboyUp.debug) {LOGGER.info("LeashReturn onInteract main hand");}
				 leash(Hand.MAIN_HAND, event);
			 }
			 if(event.getHand().equals(Hand.OFF_HAND)) {
				 if (CowboyUp.debug) {LOGGER.info("LeashReturn onInteract off hand");}
				 leash(Hand.OFF_HAND, event);
			 }
		 }
	}
	
	private static void leash(Hand hand, PlayerInteractEvent.EntityInteract event) {
		Boolean leashReturn = CowboyUpConfig.COMMON.leashReturns.get();
		
		//get the held item in the hand
		ItemStack heldItem = event.getPlayer().getHeldItem(hand);
		
		//get the target
		Entity target = event.getTarget();
		
		//get the player
		PlayerEntity player = event.getPlayer();
		
		//determine the action to take
		//is the target leased
		boolean leashed = ((AnimalEntity) target).getLeashed();
		Entity leashholder = ((AnimalEntity) target).getLeashHolder();
		
		//leashed
		if(leashed && leashholder != null) {
			if (CowboyUp.debug) {LOGGER.info("remove leash "+ hand.name());}
			
			//remove the lead
			((AnimalEntity) target).clearLeashed(true, false);
			
			//try to put the lead back into the players inventory else drop the lead
			if (player.inventory.hasItemStack(new ItemStack(Items.LEAD)) && leashReturn) {
				int i = player.inventory.getSlotFor(new ItemStack(Items.LEAD));
				player.inventory.getStackInSlot(i).grow(1);
			} else {
				player.inventory.addItemStackToInventory(new ItemStack(Items.LEAD));
				//did the lead go into inventory
				if (!player.inventory.hasItemStack(new ItemStack(Items.LEAD))) {
					//if not drop the lead
					target.entityDropItem(Items.LEAD, 1);
				}
			}
			
			//TODO: implement on lama or other enitites?
			//stay only works on horses ? maybe other like lama
			if (target.getEntity().getType() == EntityType.HORSE || 
					target.getEntity().getType() == EntityType.DONKEY || 
					target.getEntity().getType() == EntityType.MULE) {
				HorseStays.stayCmd(true, (AbstractHorseEntity) target);
			}
			 
			 event.setCanceled(true);
		     event.setCancellationResult(ActionResultType.SUCCESS);
			
		} else if (heldItem.getItem().equals(Items.LEAD)) { //not leashed
			if (CowboyUp.debug) {LOGGER.info("add leash "+ hand.name());}
			
			//add a leash
			((AnimalEntity) target).setLeashHolder(player, true);
			
			//adjust the inventory
			heldItem.shrink(1);
	        if (heldItem.isEmpty()) {
	        	player.inventory.deleteStack(heldItem);
             }
	        
	        //TODO: implement on lama or other enitites?
	        //stay only works on horses ? maybe other like lama
			if (target.getEntity().getType() == EntityType.HORSE || 
					target.getEntity().getType() == EntityType.DONKEY || 
					target.getEntity().getType() == EntityType.MULE) {
	        	HorseStays.stayCmd(false, (AbstractHorseEntity) target);
	        }
	        
	        event.setCanceled(true);
		    event.setCancellationResult(ActionResultType.SUCCESS);
		}
	}
	
	//@SubscribeEvent
	public static void onKeyInput(KeyInputEvent event) {
		Boolean leashReturns = CowboyUpConfig.COMMON.leashReturns.get();

        if (KeyBindings.KEYBINDINGS[3].isPressed()) {//(event.getKey() == 36) {
            if (leashReturns == LeashReturns.ENABLED.getBooleanCode()) {
            	CowboyUpConfig.COMMON.leashReturns.set(LeashReturns.DISABLED.getBooleanCode());; //0 CowboyUp LeashReturns Disabled
            } else if (leashReturns == LeashReturns.DISABLED.getBooleanCode()) {
            	CowboyUpConfig.COMMON.leashReturns.set(LeashReturns.ENABLED.getBooleanCode());; //1 CowboyUp LeashReturns Enabled
            }
            status();
        }
	}
	
	private static void status() {
		if (CowboyUpConfig.COMMON.reportStatus.get() && Minecraft.getInstance().isGameFocused() && (!Minecraft.getInstance().isGamePaused())) {
	    	Boolean leashReturns = CowboyUpConfig.COMMON.leashReturns.get();
	    	
	    	
	        String m = (Object) TextFormatting.DARK_AQUA + "[" + (Object) TextFormatting.YELLOW + CowboyUp.MOD_NAME + (Object) TextFormatting.DARK_AQUA + "]" + " ";
	        if(leashReturns == LeashReturns.DISABLED.getBooleanCode()) {
	            m = m + (Object) TextFormatting.RED + I18n.format(LeashReturns.DISABLED.getDesc());
	        } else {
	            m = m + (Object) TextFormatting.GREEN + I18n.format(LeashReturns.ENABLED.getDesc());
	        }
	
	        CowboyUp.mc.player.sendMessage((ITextComponent) new StringTextComponent(m));
		}
    }
	
	/*
	 * private static void message(String msg) {
		if (Minecraft.getInstance().isGameFocused() && (!Minecraft.getInstance().isGamePaused())) {
	        String m = (Object) TextFormatting.BLUE + msg;
	        
	        CowboyUp.player.sendMessage((ITextComponent) new StringTextComponent(m));
		}
    }
	 */
	
	public enum LeashReturns
    {
    	DISABLED (0,"msg.CowboyUp.LeashReturns.disbaled"), //"Leash drops as usual." 
        ENABLED (1,"msg.CowboyUp.LeashReturns.enabled");//"Leash returns to inventory if it can."
        
        private final int levelCode;
        private final String desc;

        LeashReturns(int levelCode, String desc) {
            this.levelCode = levelCode;
            this.desc = desc;
        }
        
        public boolean getBooleanCode() {
        	if(this.levelCode==0) {
        		return false;
        	}else {
        		return true;
        	}
        }

		public String getDesc() {
			return this.desc;
		}
    }

}
