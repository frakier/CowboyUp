package frakier.cowboyup.init;

import frakier.cowboyup.CowboyUp;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = CowboyUp.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {

    public static final KeyBinding[] KEYBINDINGS = {
        new KeyBinding("key.CowboyUp.HorseSwimsWithRider.desc", GLFW.GLFW_KEY_KP_MULTIPLY, "key.categories.CowboyUp"),
        new KeyBinding("key.CowboyUp.HorseStaysCommand.desc", GLFW.GLFW_KEY_KP_DIVIDE, "key.categories.CowboyUp")
    };

    @SubscribeEvent
    public static void registerKeyBindings(final FMLClientSetupEvent event) {
        for (final KeyBinding bind : KEYBINDINGS) {
            ClientRegistry.registerKeyBinding(bind);
        }
    }

}