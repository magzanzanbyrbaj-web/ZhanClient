
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

    public static boolean hitColor = true;
    public static boolean customCrosshair = true;
    public static boolean fullbright = false;

    public static boolean fpsCounter = true;
    public static boolean coordinates = true;
    public static boolean armorHUD = true;
    public static boolean totemCounter = true;
    public static boolean pingDisplay = true;
    public static boolean targetHUD = true;
    public static boolean keystrokes = true;

    public static boolean lowFire = false;
    public static boolean noParticles = false;
    public static boolean noWeather = false;
    public static boolean noPumpkin = false;
    public static boolean noBossBar = false;
    public static boolean noHurtCamera = false;

    public static boolean optimized = false;

    private static double previousGamma;
    private static int previousRenderDistance;
    private static int previousSimulationDistance;

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
        System.out.println("[ZhanVisuals] 3.1 loaded");
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
            case "Ping Display" -> pingDisplay;
            case "Target HUD" -> targetHUD;
            case "Keystrokes" -> keystrokes;

            case "Low Fire" -> lowFire;
            case "No Particles" -> noParticles;
            case "No Weather" -> noWeather;
            case "No Pumpkin" -> noPumpkin;
            case "No Boss Bar" -> noBossBar;
            case "No Hurt Camera" -> noHurtCamera;

            case "Performance Boost" -> optimized;
            default -> false;
        };
    }

    public static void toggleModule(String name) {
        switch (name) {
            case "HitColor" -> hitColor = !hitColor;
            case "CustomCrosshair" -> customCrosshair = !customCrosshair;
            case "Fullbright" -> toggleFullbright();

            case "FPS Counter" -> fpsCounter = !fpsCounter;
            case "Coordinates" -> coordinates = !coordinates;
            case "Armor HUD" -> armorHUD = !armorHUD;
            case "Totem Counter" -> totemCounter = !totemCounter;
            case "Ping Display" -> pingDisplay = !pingDisplay;
            case "Target HUD" -> targetHUD = !targetHUD;
            case "Keystrokes" -> keystrokes = !keystrokes;

            case "Low Fire" -> lowFire = !lowFire;
            case "No Particles" -> noParticles = !noParticles;
            case "No Weather" -> noWeather = !noWeather;
            case "No Pumpkin" -> noPumpkin = !noPumpkin;
            case "No Boss Bar" -> noBossBar = !noBossBar;
            case "No Hurt Camera" -> noHurtCamera = !noHurtCamera;

            case "Performance Boost" -> toggleOptimization();
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

        if (client.level == null) return;

        if (!optimized) {
            previousRenderDistance = client.options.renderDistance().get();
            previousSimulationDistance = client.options.simulationDistance().get();

            client.options.renderDistance().set(
                Math.min(previousRenderDistance, 6)
            );

            client.options.simulationDistance().set(
                Math.min(previousSimulationDistance, 4)
            );

            optimized = true;
        } else {
            client.options.renderDistance().set(previousRenderDistance);
            client.options.simulationDistance().set(previousSimulationDistance);
            optimized = false;
        }
    }
}
