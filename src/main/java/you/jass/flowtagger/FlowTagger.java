package you.jass.flowtagger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import you.jass.flowtagger.settings.Settings;
import you.jass.flowtagger.ui.UIScreen;
import you.jass.flowtagger.utility.Format;

import static you.jass.flowtagger.utility.MultiVersion.message;

public class FlowTagger implements ModInitializer {
    public static MinecraftClient client;
    public static KeyBinding key;
    public static boolean tutorialAlreadySeen;
    public static long lastKitUpdate;

    @Override
    public void onInitialize() {
        client = MinecraftClient.getInstance();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registry) -> {
            dispatcher.register(ClientCommandManager.literal("flowtagger").executes(context -> open()));
        });

        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("flowtagger", "flowtagger"));
        key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "FlowTagger Menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN, category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (key.wasPressed() && client.currentScreen == null) client.setScreen(new UIScreen());

            if (System.currentTimeMillis() - lastKitUpdate >= 1000) {
                Format.updatePlayersKit();
                lastKitUpdate = System.currentTimeMillis();
            }

            if (!tutorialAlreadySeen && Settings.getBoolean("tutorial")) {
                message("Thanks for using FlowTagger!", "flowtagger");
                message("use /flowtagger or set the menu keybind to configure", "flowtagger");
                tutorialAlreadySeen = true;
            }
        });
    }

    public int open() {
        client.execute(() -> client.setScreen(new UIScreen()));
        return 1;
    }
}