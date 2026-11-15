
package ru.zhanbyrbay.zhanvisuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class ZhanVisuals implements ClientModInitializer {

    public static final String MOD_ID = "zhanvisuals";

    private static KeyMapping openMenuKey;

    // Visual modules
    public static boolean hitColor = true;
    public static boolean customCrosshair = true;
    public static boolean fullbright = false;

    // HUD modules
    public static boolean fpsCounter = true;
    public static boolean coordinates = true;
    public static boolean armorHUD = true;
    public static boolean totemCounter = true;

    // Optimization
    public static boolean optimized = false;

    private static double previousGamma;

    private static int previousRenderDistance;
    private static int previousSimulationDistance;
    private static double previousEntityDistance;

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

    public static boolean isEnabled(String name) {
        return switch (name) {
            case "HitColor" -> hitColor;
            case "CustomCrosshair" -> customCrosshair;
            case "Fullbright" -> fullbright;
            case "FPS Counter" -> fpsCounter;
            case "Coordinates" -> coordinates;
            case "Armor HUD" -> armorHUD;
            case "Totem Counter" -> totemCounter;
            case "Performance Boost" -> optimized;
            default -> false;
        };
    }

    public static void toggleModule(String name) {

        switch (name) {

            case "HitColor" ->
                hitColor = !hitColor;

            case "CustomCrosshair" ->
                customCrosshair = !customCrosshair;

            case "Fullbright" ->
                toggleFullbright();

            case "FPS Counter" ->
                fpsCounter = !fpsCounter;

            case "Coordinates" ->
                coordinates = !coordinates;

            case "Armor HUD" ->
                armorHUD = !armorHUD;

            case "Totem Counter" ->
                totemCounter = !totemCounter;

            case "Performance Boost" ->
                toggleOptimization();
        }
    }

    private static void toggleFullbright() {

        Minecraft client = Minecraft.getInstance();

        if (!fullbright) {
            previousGamma = client.options.gamma().get();
            client.options.gamma().set(1.0);
            fullbright = true;
        } else {
            client.options.gamma().set(previousGamma);
            fullbright = false;
        }
    }

    public static void toggleOptimization() {

        Minecraft client = Minecraft.getInstance();

        if (!optimized) {

            previousRenderDistance =
                client.options.renderDistance().get();

            previousSimulationDistance =
                client.options.simulationDistance().get();

            previousEntityDistance =
                client.options.entityDistanceScaling().get();

            client.options.renderDistance().set(
                Math.min(previousRenderDistance, 8)
            );

            client.options.simulationDistance().set(
                Math.min(previousSimulationDistance, 5)
            );

            client.options.entityDistanceScaling().set(
                Math.min(previousEntityDistance, 0.5)
            );

            optimized = true;

        } else {

            client.options.renderDistance().set(
                previousRenderDistance
            );

            client.options.simulationDistance().set(
                previousSimulationDistance
            );

            client.options.entityDistanceScaling().set(
                previousEntityDistance
            );

            optimized = false;
        }
    }
}
