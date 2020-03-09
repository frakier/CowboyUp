package frakier.cowboyup.worker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import frakier.cowboyup.CowboyUp;
import frakier.cowboyup.config.CowboyUpConfig;
import frakier.cowboyup.init.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;

public class HorseStays {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public HorseStays() {}
	
	public static void init(PlayerEntity intplayer) {
		if (CowboyUp.debug) {LOGGER.info("HorseStay init");}
		CowboyUp.player = intplayer;
    	status();
    }
	
	public static void onMount(EntityMountEvent event) {
		if (CowboyUp.debug) {LOGGER.info("HorseStay onMount");}
		Entity target = event.getEntityBeingMounted();
		
		Boolean horseStaysCommand = CowboyUpConfig.COMMON.horseStaysCommand.get();
		if (target instanceof AbstractHorseEntity && horseStaysCommand) {
			if(event.isMounting()) {
				if (CowboyUp.debug) {LOGGER.info("mount horse");}
				stayCmd(false, (AbstractHorseEntity) target);
			}
			if(event.isDismounting()) {
				if (CowboyUp.debug) {LOGGER.info("dismount horse");}
				stayCmd(true, (AbstractHorseEntity) target);
			}
		}
	}
	
	public static void onInteract(PlayerInteractEvent.EntityInteract event) {
		//this method fires twice, once for each hand on any interaction
		//so we have to handle both hands
		 
		//player is interacting with a horse
		if (CowboyUp.player instanceof PlayerEntity  && event.getTarget() instanceof AbstractHorseEntity) {
			 //main hand used
			 if(event.getHand().equals(Hand.MAIN_HAND)) {
				 if (CowboyUp.debug) {LOGGER.info("HorseStay onInteract main hand");}
				 leash(Hand.MAIN_HAND, event);
			 }
			 if(event.getHand().equals(Hand.OFF_HAND)) {
				 if (CowboyUp.debug) {LOGGER.info("HorseStay onInteract off hand");}
				 leash(Hand.OFF_HAND, event);
			 }
		 }
	}
	
	private static void leash(Hand hand, PlayerInteractEvent.EntityInteract event) {
		Boolean dontDropLead = CowboyUpConfig.COMMON.dontDropLead.get();
		
		//get the held item in the hand
		ItemStack heldItem = event.getPlayer().getHeldItem(hand);
		
		//get the target
		Entity target = event.getTarget();
		
		//get the player
		PlayerEntity player = event.getPlayer();
		
		//determine the action to take
		//is the target leased
		boolean leashed = ((AbstractHorseEntity) target).getLeashed();
		Entity leashholder = ((AbstractHorseEntity) target).getLeashHolder();
		
		//leashed
		if(leashed && leashholder != null) {
			if (CowboyUp.debug) {LOGGER.info("remove leash "+ hand.name());}
			
			//remove the lead
			((AbstractHorseEntity) target).clearLeashed(true, false);
			
			//try to put the lead back into the players inventory else drop the lead
			if (player.inventory.hasItemStack(new ItemStack(Items.LEAD)) && dontDropLead) {
				int i = player.inventory.getSlotFor(new ItemStack(Items.LEAD));
				player.inventory.getStackInSlot(i).grow(1);
			} else {
				target.entityDropItem(Items.LEAD, 1);
			}
			
			player.inventory.hasItemStack(new ItemStack(Items.LEAD));
			
			 stayCmd(true, (AbstractHorseEntity) target);
			 
			 event.setCanceled(true);
		     event.setCancellationResult(ActionResultType.SUCCESS);
			
		} else if (heldItem.getItem().equals(Items.LEAD)) { //not leashed
			if (CowboyUp.debug) {LOGGER.info("add leash "+ hand.name());}
			
			//add a leash
			((AbstractHorseEntity) target).setLeashHolder(player, true);
			
			//adjust the inventory
			heldItem.shrink(1);
	        if (heldItem.isEmpty()) {
	        	player.inventory.deleteStack(heldItem);
             }
	        
	        stayCmd(false, (AbstractHorseEntity) target);
	        
	        event.setCanceled(true);
		    event.setCancellationResult(ActionResultType.SUCCESS);
		}
	}
	
	private static void stayCmd(boolean staycmd, AbstractHorseEntity horse) {
		if (CowboyUp.debug) {LOGGER.info("HorseStay stayCmd");}
		 Boolean horseStaysCommand = CowboyUpConfig.COMMON.horseStaysCommand.get();
		 if (horseStaysCommand) {
			 if (!horse.isInLava() && !horse.isInWater()) {
				 if(staycmd==true) {
					 if (CowboyUp.debug) {LOGGER.info("stay enabled");}
					 BlockPos setHomePos = new BlockPos(horse.posX, horse.posY, horse.posZ);
					 //set the hose home position
					 horse.setHomePosAndDistance(setHomePos,1);
					 horse.setHomePosAndDistance(setHomePos,1);
					 //disable the move flag
					 horse.goalSelector.disableFlag(Goal.Flag.MOVE);
				 } else {
					 if (CowboyUp.debug) {LOGGER.info("stay disabled");}
					//Erase the hoses home position
					 horse.detachHome();
					 horse.detachHome();
					 //enable the move flag
					 horse.goalSelector.enableFlag(Goal.Flag.MOVE);
				}
			 }
		 }
	 }
	 
	//@SubscribeEvent
	public static void onKeyInput(KeyInputEvent event) {
		Boolean horseStaysCommand = CowboyUpConfig.COMMON.horseStaysCommand.get();

        if (KeyBindings.KEYBINDINGS[1].isPressed()) {//(event.getKey() == 36) {
            if (horseStaysCommand == HorseStaysCommand.ENABLED.getBooleanCode()) {
            	CowboyUpConfig.COMMON.horseStaysCommand.set(HorseStaysCommand.DISABLED.getBooleanCode());; //0 CowboyUp HorseStaysCommand Disabled
            } else if (horseStaysCommand == HorseStaysCommand.DISABLED.getBooleanCode()) {
            	CowboyUpConfig.COMMON.horseStaysCommand.set(HorseStaysCommand.ENABLED.getBooleanCode());; //1 CowboyUp HorseStaysCommand Enabled
            }
            status();
        }
	}
	
	private static void status() {
		if (Minecraft.getInstance().isGameFocused()) {
	    	Boolean horseStaysCommand = CowboyUpConfig.COMMON.horseStaysCommand.get();
	    	
	    	
	        String m = (Object) TextFormatting.DARK_AQUA + "[" + (Object) TextFormatting.YELLOW + CowboyUp.MOD_NAME + (Object) TextFormatting.DARK_AQUA + "]" + " ";
	        if(horseStaysCommand == HorseStaysCommand.DISABLED.getBooleanCode()) {
	            m = m + (Object) TextFormatting.RED + I18n.format(HorseStaysCommand.DISABLED.getDesc());
	        } else {
	            m = m + (Object) TextFormatting.GREEN + I18n.format(HorseStaysCommand.ENABLED.getDesc());
	        }
	
	        CowboyUp.player.sendMessage((ITextComponent) new StringTextComponent(m));
		}
    }
	
	private static void message(String msg) {
		if (Minecraft.getInstance().isGameFocused()) {
	        String m = (Object) TextFormatting.BLUE + msg;
	        
	        CowboyUp.player.sendMessage((ITextComponent) new StringTextComponent(m));
		}
    }

    public enum HorseStaysCommand
    {
    	DISABLED (0,"msg.CowboyUp.HorseStaysCommand.disbaled"), //"Horse stays when dismounted or unleashed" 
        ENABLED (1,"msg.CowboyUp.HorseStaysCommand.enabled");//"Horse can move around"
        
        private final int levelCode;
        private final String desc;

        HorseStaysCommand(int levelCode, String desc) {
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
