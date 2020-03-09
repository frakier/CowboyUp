package frakier.cowboyup.worker;

import org.apache.logging.log4j.Logger;
import frakier.cowboyup.CowboyUp;
import frakier.cowboyup.config.CowboyUpConfig;
import frakier.cowboyup.init.KeyBindings;

import org.apache.logging.log4j.LogManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

public class HorseSwim {
	
	public KeyBinding myKey;

    private static Minecraft mc = Minecraft.getInstance();

    private static final Logger LOGGER = LogManager.getLogger();
    
    static BlockPos FeetWetPos = new BlockPos(0,0,0);
    static Boolean swimmingWithPlayer = false;
    
	private static double swimTimer=0;
	private static int swimTicks = 0;
	
	private static boolean swimUP = false;
    private static int swimUPTicks = 0;
    private static float fallDistance = 0;
	
	
    public HorseSwim() {}
     
    public static void init(PlayerEntity intplayer) {
    	CowboyUp.player = intplayer;
    	status();
    	
    }
    
    public static void TickEvent(PlayerTickEvent event) {
    	Boolean horseSwimsWithRider = CowboyUpConfig.COMMON.horseSwimsWithRider.get();
    	
    	//get the horse
		AbstractHorseEntity horse = (AbstractHorseEntity) event.player.getRidingEntity();
    	if (CowboyUp.player != null && horse!=null) {
    		//double speed = horse.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
    		//LOGGER.info("Speed "+speed);
	    	//if horse can swim with rider
	    	if(horseSwimsWithRider) {
	    		//get block under horse
	    		BlockPos underHorseBlock = horse.getPosition().add(0, -1, 0);
	    		//is the block solid
	    		boolean solidUnderHorse = mc.world.getBlockState(underHorseBlock).getMaterial().isSolid();
	    		//eyes in fluid
	    		boolean eyesAboveFluid = ((!horse.areEyesInFluid(FluidTags.WATER)) && (!horse.areEyesInFluid(FluidTags.LAVA)));
	    		//in fluid
	    		boolean inFluid = (horse.isInLava() || horse.isInWater());
	    		
	    		//build the conditions 
	    		boolean deepWater = (inFluid && !solidUnderHorse && swimTimer<=30);
	    		boolean shallowWater = (inFluid && solidUnderHorse && eyesAboveFluid);
	    		boolean surfaceing = (inFluid && !eyesAboveFluid);
	    		boolean dryLand = (!inFluid && solidUnderHorse);
	    		
	    		BlockPos nextStep = horse.getPosition().offset(horse.getHorizontalFacing(), 1);
	    		boolean watersEdge = mc.world.getBlockState(nextStep).getMaterial().isSolid();
	    		
	    		float moveSpeed = horse.getAIMoveSpeed();
	    		
	    		swimUpTimer(horse, eyesAboveFluid);
	    		
	    		//reporting dryland|deepwater|shallowwater speed:f velocity:f Fall:b Surfacing:b
	    		
	    		if (dryLand) {
	    			double velocity = 1.0d;
	    			if (CowboyUp.debug) {LOGGER.info("dryland speed:"+moveSpeed+" velocity:"+velocity+" swimUp"+swimUP);}
	    			swimTimer=0;
	    			swimTicks=0;
	    			horse.addVelocity(0.0f, 0.0f, 0.0f);
	    			horse.setMotion(horse.getMotion().mul(1.00D,1.0D,1.00D));
	    		}
	    		
	    		if(!swimUP) {
		    		if(deepWater||shallowWater) {
		    			horse.setSwimming(false);
			    		if (deepWater) {//horse in deep water
			    			double deepWaterVelocity = 1.01D;
			    			if (CowboyUp.debug) {LOGGER.info("deepwater speed:"+moveSpeed+" velocity:"+deepWaterVelocity+" swimUp"+swimUP);}
			    			horse.addVelocity(0.0f, 0f, 0.0f);
			        		horse.setMotion(horse.getMotion().mul(1.01D,0.0D,1.01D)); 
			        		advanceSwimTimer(horse);
			    		} else if (shallowWater) {//horse in shallow water
			    			double deepWaterVelocity = 1.05D;
			    			if (CowboyUp.debug) {LOGGER.info("shallowWater speed:"+moveSpeed+" velocity:"+deepWaterVelocity+" swimUp"+swimUP);}
			    			horse.addVelocity(0.0f, 0.0f, 0.0f);
			        		horse.setMotion(horse.getMotion().mul(1.05D,0.0D,1.05D));
			    		}
		    		}
		    		
		    		if (surfaceing) {
		    			if (CowboyUp.debug) {LOGGER.info("surfacing speed:"+moveSpeed+" velocity:0 swimUp"+swimUP);}
		    			horse.addVelocity(0.0f, 0.075f, 0.0f);
		    			
		    		}
		    		
		    		if (watersEdge && (deepWater||shallowWater)) {
		    			BlockPos nextStepUp = nextStep.add(0, 1, 0);
		    			
			    		//next step solid
			    		boolean nextStepSolid = mc.world.getBlockState(nextStep).getMaterial().isSolid();
			    		boolean nextStepUpSolid = mc.world.getBlockState(nextStepUp).getMaterial().isSolid();
			    		//
			    		boolean jumping = horse.isHorseJumping();
			    		
			        	if (nextStepSolid && !jumping) {
			    			horse.travel(new Vec3d(nextStep));
			    		}
			        	if (nextStepUpSolid && jumping) {
			    			horse.travel(new Vec3d(nextStepUp));
			        	}
		    		}
	    		}
	    	}
    	}
    }
    
    private static boolean swimTimerChanged;
    public static void advanceSwimTimer(AbstractHorseEntity horse) {
    	if (CowboyUp.debug) {LOGGER.info("swimming "+swimmingWithPlayer+" time left:"+ swimTimer);}
    	if (swimmingWithPlayer==false) {
			swimmingWithPlayer=true; FeetWetPos=horse.getPosition(); 
			
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
    
    private static boolean swimUPTimerChanged;
    public static void swimUpTimer (AbstractHorseEntity horse, boolean eyesAboveFluid) {
    	if (CowboyUp.debug) {LOGGER.info("swim up "+swimmingWithPlayer+" eyesAboveFluid "+eyesAboveFluid+" fall distance:"+ fallDistance+":"+swimUPTicks);}
    	if (horse.fallDistance > 2) {
    		fallDistance = horse.fallDistance;
			swimUP = true;
		}
		
		if(swimUP && swimUPTicks>10) {
			if (fallDistance <=2 && eyesAboveFluid) {
				if (CowboyUp.debug) {LOGGER.info("*******************************************************************************");}
				fallDistance=0;
				swimUP = false;
				horse.setMoveVertical(1);
				horse.setMoveVertical(0);
			}
			fallDistance--;
			swimUPTimerChanged=true;
			swimUPTicks=0;
		}
		else
		{
			swimUPTicks++;
		}
		if (swimUPTimerChanged) {
    		swimUPTimerChanged=false;
    	}
		
    }
    
    //@SubscribeEvent
    public static void onKeyInput(KeyInputEvent event) {
    	Boolean horseSwimWithRider = CowboyUpConfig.COMMON.horseSwimsWithRider.get();

        if (KeyBindings.KEYBINDINGS[0].isPressed()) {//(event.getKey() == 36) {
            if (horseSwimWithRider == HorseSwimsWithRider.ENABLED.getBooleanCode()) {
            	CowboyUpConfig.COMMON.horseSwimsWithRider.set(HorseSwimsWithRider.DISABLED.getBooleanCode());; //0 CowboyUp HorseSwimWithRider Disabled
            } else if (horseSwimWithRider == HorseSwimsWithRider.DISABLED.getBooleanCode()) {
            	CowboyUpConfig.COMMON.horseSwimsWithRider.set(HorseSwimsWithRider.ENABLED.getBooleanCode());; //1 CowboyUp HorseSwimWithRider Enabled
            }
            status();
        }
        
        /*
        //set the horses home position when the player dismounts
        if (player != null && player.getRidingEntity() instanceof AbstractHorseEntity && event.getKey()==GLFW.GLFW_KEY_LEFT_SHIFT) {
        	AbstractHorseEntity horse = (AbstractHorseEntity) player.getRidingEntity();
        	
        	BlockPos setHomePos = new BlockPos(horse.posX, horse.posY, horse.posZ);
        	//lets see if multiple calls reinforces the command.
        	horse.goalSelector.enableFlag(Goal.Flag.MOVE);
        	
        	horse.setHomePosAndDistance(setHomePos,1);
        	swimmingWithPlayer = false;
        	//horse.getAttributes().registerAttribute((IAttribute) new AttributeModifier("LastHome", setHomePos, null));
        	//boolean t = horse.addTag("LastHome");
        	
        	
        	//horse.setHomePosAndDistance(setHomePos,1);
        	//horse.setHomePosAndDistance(setHomePos,1);
        }
        */
    }

    private static void status() {
    	if (Minecraft.getInstance().isGameFocused()) {
	    	Boolean horseSwimWithRider = CowboyUpConfig.COMMON.horseSwimsWithRider.get();
	    	
	    	
	        String m = (Object) TextFormatting.DARK_AQUA + "[" + (Object) TextFormatting.YELLOW + CowboyUp.MOD_NAME + (Object) TextFormatting.DARK_AQUA + "]" + " ";
	        if(horseSwimWithRider == HorseSwimsWithRider.DISABLED.getBooleanCode()) {
	            m = m + (Object) TextFormatting.RED + I18n.format(HorseSwimsWithRider.DISABLED.getDesc());
	        } else {
	            m = m + (Object) TextFormatting.GREEN + I18n.format(HorseSwimsWithRider.ENABLED.getDesc());
	        }
	
	        CowboyUp.player.sendMessage((ITextComponent) new StringTextComponent(m));
    	}
    }
    
    private static void message(String msg) {
    	if (Minecraft.getInstance().isGameFocused()) {
	        String m = (Object) TextFormatting.YELLOW + msg;
	        
	        CowboyUp.player.sendMessage((ITextComponent) new StringTextComponent(m));
    	}
    }

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
