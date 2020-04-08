package frakier.cowboyup.worker;

import org.apache.logging.log4j.Logger;
import frakier.cowboyup.CowboyUp;
import frakier.cowboyup.config.CowboyUpConfig;
import frakier.cowboyup.init.KeyBindings;
import frakier.cowboyup.worker.helper.HorseWorkerHelpers;

import org.apache.logging.log4j.LogManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

public class HorseSwim {

    private static Minecraft mc = Minecraft.getInstance();

    private static final Logger LOGGER = LogManager.getLogger();
    
    static BlockPos FeetWetPos = new BlockPos(0,0,0);
    static Boolean swimmingWithPlayer = false;
    
    //falling
    	//currently falling
    	private static boolean falling = false;
    
    //swimming
	    //elapsed swim time
		private static double swimTimer=0;
		//elapsed ticks between movement
		private static int swimTicks = 0;
		
		private static Integer swimTime=0;
	
    public HorseSwim() {}
     
    public static void init() {
    	if (CowboyUp.debug) {LOGGER.info("HorseSwim init");}
    	status();
    }
    
    public static void TickEvent(PlayerTickEvent event) {
    	if (CowboyUp.debug) {LOGGER.info("HorseSwim tickevent");}
    	Boolean horseSwimsWithRider = CowboyUpConfig.COMMON.horseCanSwimWithRider.get();
    	swimTime = CowboyUpConfig.COMMON.swimTime.get();
    	
    	if(horseSwimsWithRider) {
	    	//get some basic information
    			//get the horse
				AbstractHorseEntity horse = (AbstractHorseEntity) event.player.getRidingEntity();
				//get block under horse
	    		BlockPos underHorseBlock = horse.getPosition().add(0, -1, 0);
	    		//is the block under the horse solid
	    		boolean solidUnderHorse = mc.world.getBlockState(underHorseBlock).getMaterial().isSolid();
	    		
	    		//in fluid
	    			//horse is standing in a liquid
	    			boolean inFluid = mc.world.getBlockState(horse.getPosition()).getMaterial().isLiquid();
	    			//eyes are below water level of any fluid
	    			boolean eyesInFluid = HorseWorkerHelpers.areEyesInAnyFuid(horse);
	    			
	    		//next step used with waters edge
	    			BlockPos nextStepBlock = horse.getPosition().offset(horse.getHorizontalFacing(), 1);
		    		boolean nextStep = mc.world.getBlockState(nextStepBlock).getMaterial().isSolid();
		    		
		    	//get base move speed
		    	float moveSpeed = horse.getAIMoveSpeed();
		    	
		    	if (CowboyUp.debug) {LOGGER.info("solidUnderHorse "+solidUnderHorse+" inFluid "+inFluid+" eyesInFluid "+eyesInFluid);}
    		
	    	//build the conditions 
	    		//not in fluid
	    		boolean dryLand = (!inFluid && solidUnderHorse);
	    		//basically 1 block deep water, horse will not drown
	    		boolean wading = (inFluid && solidUnderHorse && !eyesInFluid);
	    		//basically 1 block deep water, horse will not drown
	    		boolean swimming = (inFluid && !solidUnderHorse && !eyesInFluid);
	    		//water two blocks or deeper, the horse can drown
	    		boolean underWater = (inFluid && eyesInFluid);
	    		//at waters edge assist horse out
	    		boolean watersEdge = (inFluid && nextStep);
	    		
	    		if (CowboyUp.debug) {LOGGER.info("dryLand "+dryLand+" wading "+wading+" swimming "+swimming+" underWater "+underWater+" watersEdge "+watersEdge);}
			
			//is the horse falling
    		if (horse.fallDistance > 4) {
    			falling = true;
    		}
			
    		//start the logic for further actions
    		if (underWater) {
    			falling = false;
    			if (horse.isBeingRidden()) {
    				//dismountEntity
    				horse.stopRiding();
    			}
    		}
    		if (dryLand) {
    			falling = false;
    			swimmingWithPlayer=false;
    			swimTimer=0;
    		}
    		
    		if (CowboyUp.debug) {LOGGER.info("dryLand "+dryLand+" wading "+wading+" swimming "+swimming+" underWater "+underWater+" watersEdge "+watersEdge+" falling "+falling);}
			
    		if (CowboyUp.debug) {LOGGER.info("moveSpeed "+moveSpeed+" horse.getMotion(): "+horse.getMotion().getX()+":"+horse.getMotion().getZ()+":"+horse.getMotion().getY());}
    		
    		if (!falling && !eyesInFluid)
    		{
    			if (CowboyUp.debug) {LOGGER.info("moveSpeed "+ moveSpeed+ " swimming "+swimming+" wading"+wading);}
	    		if (swimming && swimTimer <= swimTime) {//horse in deep water
	    			double deepWaterVelocity = 0.9D;
	    			if (CowboyUp.debug) {LOGGER.info("deepwater speed: "+deepWaterVelocity);}
	    			horse.setMotion(horse.getMotion().mul(deepWaterVelocity,0.0D,deepWaterVelocity)); 
	        		advanceSwimTimer(horse);
	    		} else if (wading) {//horse in shallow water
	    			//double shallowWaterVelocity = moveSpeed+.05D;
	    			//if (CowboyUp.debug) {LOGGER.info("shallowWater speed:"+moveSpeed+" velocity:"+shallowWaterVelocity);}
	    			//horse.addVelocity(0.0f, 0.0f, 0.0f);
	        		//horse.setMotion(horse.getMotion().mul(shallowWaterVelocity,0.0D,shallowWaterVelocity));
	    		}
    		}
    		
    		if (watersEdge) {
    			boolean jumping = horse.isHorseJumping();
	    		
	        	if (watersEdge && !jumping) {
	    			horse.travel(new Vec3d(nextStepBlock));
	    		}
    		}
	    	
    	}
    }
    
    //used to limit the amount of time the horse can swim carrying the player before tiring 
    private static boolean swimTimerChanged;
    public static void advanceSwimTimer(AbstractHorseEntity horse) {
    	if (CowboyUp.debug) {LOGGER.info("swimming "+swimmingWithPlayer+" time left:"+ swimTimer);}
    	if (CowboyUp.debug) {LOGGER.info("time left:"+ (swimTime-swimTimer));}
    	
    	//this method is being called from swimming so set swimmingWithPlayer to true
    	//and start keeping a record of how far the horse has swam carrying the player
    	if (swimmingWithPlayer==false) {
			swimmingWithPlayer=true; FeetWetPos=horse.getPosition(); 
			swimTimer = 0;
    	} else {
	    	if(FeetWetPos.manhattanDistance(horse.getPosition())>1 || swimTicks>30) {
				FeetWetPos = horse.getPosition();
				swimTimer++;
				swimTimerChanged=true;
				swimTicks=0;
			}
			else
			{
				swimTicks++;
			}
		}
    	if (swimTimerChanged) {
    		swimTimerChanged=false;
    	}
    }
    
    //@SubscribeEvent
    public static void onKeyInput(KeyInputEvent event) {
    	Boolean horseSwimWithRider = CowboyUpConfig.COMMON.horseCanSwimWithRider.get();

        if (KeyBindings.KEYBINDINGS[0].isPressed()) {//(event.getKey() == 36) {
            if (horseSwimWithRider == HorseSwimsWithRider.ENABLED.getBooleanCode()) {
            	CowboyUpConfig.COMMON.horseCanSwimWithRider.set(HorseSwimsWithRider.DISABLED.getBooleanCode());; //0 CowboyUp HorseSwimWithRider Disabled
            } else if (horseSwimWithRider == HorseSwimsWithRider.DISABLED.getBooleanCode()) {
            	CowboyUpConfig.COMMON.horseCanSwimWithRider.set(HorseSwimsWithRider.ENABLED.getBooleanCode());; //1 CowboyUp HorseSwimWithRider Enabled
            }
            status();
        }
    }

    private static void status() {
    	if (CowboyUpConfig.COMMON.reportStatus.get() && Minecraft.getInstance().isGameFocused() && (!Minecraft.getInstance().isGamePaused())) {
	    	Boolean horseSwimWithRider = CowboyUpConfig.COMMON.horseCanSwimWithRider.get();
	    	
	    	
	        String m = (Object) TextFormatting.DARK_AQUA + "[" + (Object) TextFormatting.YELLOW + CowboyUp.MOD_NAME + (Object) TextFormatting.DARK_AQUA + "]" + " ";
	        if(horseSwimWithRider == HorseSwimsWithRider.DISABLED.getBooleanCode()) {
	            m = m + (Object) TextFormatting.RED + I18n.format(HorseSwimsWithRider.DISABLED.getDesc());
	        } else {
	            m = m + (Object) TextFormatting.GREEN + I18n.format(HorseSwimsWithRider.ENABLED.getDesc());
	        }
	
	        mc.player.sendMessage((ITextComponent) new StringTextComponent(m));
    	}
    }
    
    /*
    private static void message(String msg) {
    	if (Minecraft.getInstance().isGameFocused() && (!Minecraft.getInstance().isGamePaused())) {
	        String m = (Object) TextFormatting.YELLOW + msg;
	        
	        CowboyUp.player.sendMessage((ITextComponent) new StringTextComponent(m));
    	}
    }
    */

    public enum HorseSwimsWithRider
    {
    	DISABLED (0,"msg.CowboyUp.HorseSwimsWithRider.disbaled"), //"Horse cannot Swim with Rider" 
        ENABLED (1,"msg.CowboyUp.HorseSwimsWithRider.enabled");//"Horse can Swim with Rider"
        
        private final int levelCode;
        private final String desc;

        HorseSwimsWithRider(int levelCode, String desc) {
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
