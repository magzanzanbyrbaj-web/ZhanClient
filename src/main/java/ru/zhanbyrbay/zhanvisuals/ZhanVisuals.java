package ru.zhanbyrbay.zhanvisuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class ZhanVisuals implements ClientModInitializer {

    public static final String MOD_ID = "zhanvisuals";

    private static KeyMapping openMenuKey;

    @Override
    public void onInitializeClient() {
        openMenuKey = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                "key.zhanvisuals.open_menu",
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyMapping.Category.MISC
            )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.consumeClick()) {
                if (client.player != null) {
                    client.setScreen(new ZhanClickScreen());
                }
            }
        });

        ZhanHud.register();

        System.out.println("[ZhanVisuals] Loaded successfully!");
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
