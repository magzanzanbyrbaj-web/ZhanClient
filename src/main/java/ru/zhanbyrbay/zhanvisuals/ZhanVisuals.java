
package ru.zhanbyrbay.zhanvisuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ZhanVisuals implements ClientModInitializer {

    public static final String MOD_ID = "zhanvisuals";

    private static KeyMapping openMenuKey;
    private static KeyMapping zoomKey;

    // VISUALS
    public static boolean hitColor = true;
    public static boolean customCrosshair = true;
    public static boolean fullbright = false;
    public static boolean lowFire = false;

    // HUD
    public static boolean watermark = true;
    public static boolean fpsCounter = true;
    public static boolean coordinates = true;
    public static boolean armorHUD = true;
    public static boolean totemCounter = true;
    public static boolean pingDisplay = true;
    public static boolean targetHUD = true;
    public static boolean keystrokes = true;
    public static boolean speedometer = true;

    // MOVEMENT
    public static boolean zoom = false;
    public static boolean autoSprint = false;

    // NO RENDER
    public static boolean noParticles = false;
    public static boolean noWeather = false;
    public static boolean noPumpkin = false;
    public static boolean noBossBar = false;
    public static boolean noHurtCamera = false;
    public static boolean noFluid = false;

    // OTHER
    public static boolean optimized = false;

    public static double currentSpeed = 0.0;

    private static double previousGamma = 1.0;
    private static int previousFov = 70;
    private static int previousRenderDistance = 12;
    private static int previousSimulationDistance = 8;

    private static boolean zooming = false;
    private static double lastX;
    private static double lastZ;
    private static boolean hasLastPosition = false;

    @Override
    public void onInitializeClient() {

        openMenuKey = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                "key.zhanvisuals.open_menu",
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyMapping.Category.MISC
            )
        );

        zoomKey = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                "key.zhanvisuals.zoom",
                GLFW.GLFW_KEY_C,
                KeyMapping.Category.MISC
            )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while (openMenuKey.consumeClick()) {
                if (client.player != null) {
                    client.setScreen(new ZhanClickScreen());
                }
            }

            updateZoom(client);
            updateMovement(client);
            updateSpeed(client);
        });

        ZhanHud.register();

        System.out.println("[ZhanVisuals] Initialized");
    }

    private static void updateZoom(Minecraft mc) {
        if (mc.player == null) {
            restoreZoom(mc);
            return;
        }

        boolean shouldZoom = zoom && zoomKey.isDown();

        if (shouldZoom && !zooming) {
            previousFov = mc.options.fov().get();
            zooming = true;
        }

        if (shouldZoom) {
            mc.options.fov().set(30);
        } else if (zooming) {
            restoreZoom(mc);
        }
    }

    private static void restoreZoom(Minecraft mc) {
        if (zooming) {
            mc.options.fov().set(previousFov);
            zooming = false;
        }
    }

    private static void updateMovement(Minecraft mc) {
        if (mc.player == null) return;

        if (autoSprint) {
            boolean movingForward = mc.options.keyUp.isDown();
            boolean sneaking = mc.options.keyShift.isDown();

            if (movingForward && !sneaking && !mc.player.isPassenger()) {
                mc.player.setSprinting(true);
            }
        }
    }

    private static void updateSpeed(Minecraft mc) {
        if (mc.player == null) {
            hasLastPosition = false;
            currentSpeed = 0;
            return;
        }

        double x = mc.player.getX();
        double z = mc.player.getZ();

        if (hasLastPosition) {
            double dx = x - lastX;
            double dz = z - lastZ;

            currentSpeed = Math.sqrt(dx * dx + dz * dz) * 20.0;
        }

        lastX = x;
        lastZ = z;
        hasLastPosition = true;
    }

    public static boolean isEnabled(String name) {
        return switch (name) {
            case "HitColor" -> hitColor;
            case "CustomCrosshair" -> customCrosshair;
            case "Fullbright" -> fullbright;
            case "Low Fire" -> lowFire;

            case "Watermark" -> watermark;
            case "FPS Counter" -> fpsCounter;
            case "Coordinates" -> coordinates;
            case "Armor HUD" -> armorHUD;
            case "Totem Counter" -> totemCounter;
            case "Ping Display" -> pingDisplay;
            case "Target HUD" -> targetHUD;
            case "Keystrokes" -> keystrokes;
            case "Speedometer" -> speedometer;

            case "Zoom" -> zoom;
            case "AutoSprint" -> autoSprint;

            case "No Particles" -> noParticles;
            case "No Weather" -> noWeather;
            case "No Pumpkin" -> noPumpkin;
            case "NoBossBar" -> noBossBar;
            case "No Hurt Camera" -> noHurtCamera;
            case "No Fluid" -> noFluid;

            case "Performance Boost" -> optimized;
            default -> false;
        };
    }

    public static void toggleModule(String name) {
        switch (name) {
            case "HitColor" -> hitColor = !hitColor;
            case "CustomCrosshair" -> customCrosshair = !customCrosshair;
            case "Fullbright" -> toggleFullbright();
            case "Low Fire" -> lowFire = !lowFire;

            case "Watermark" -> watermark = !watermark;
            case "FPS Counter" -> fpsCounter = !fpsCounter;
            case "Coordinates" -> coordinates = !coordinates;
            case "Armor HUD" -> armorHUD = !armorHUD;
            case "Totem Counter" -> totemCounter = !totemCounter;
            case "Ping Display" -> pingDisplay = !pingDisplay;
            case "Target HUD" -> targetHUD = !targetHUD;
            case "Keystrokes" -> keystrokes = !keystrokes;
            case "Speedometer" -> speedometer = !speedometer;

            case "Zoom" -> {
                zoom = !zoom;
                if (!zoom) restoreZoom(Minecraft.getInstance());
            }

            case "AutoSprint" -> autoSprint = !autoSprint;

            case "No Particles" -> noParticles = !noParticles;
            case "No Weather" -> noWeather = !noWeather;
            case "No Pumpkin" -> noPumpkin = !noPumpkin;
            case "NoBossBar" -> noBossBar = !noBossBar;
            case "No Hurt Camera" -> noHurtCamera = !noHurtCamera;
            case "No Fluid" -> noFluid = !noFluid;

            case "Performance Boost" -> toggleOptimization();
        }
    }

    private static void toggleFullbright() {
        Minecraft mc = Minecraft.getInstance();

        if (!fullbright) {
            previousGamma = mc.options.gamma().get();
            mc.options.gamma().set(16.0);
            fullbright = true;
        } else {
            mc.options.gamma().set(previousGamma);
            fullbright = false;
        }
    }

    private static void toggleOptimization() {
        Minecraft mc = Minecraft.getInstance();

        if (!optimized) {
            previousRenderDistance = mc.options.renderDistance().get();
            previousSimulationDistance = mc.options.simulationDistance().get();

            mc.options.renderDistance().set(
                Math.min(previousRenderDistance, 6)
            );

            mc.options.simulationDistance().set(
                Math.min(previousSimulationDistance, 4)
            );

            optimized = true;
        } else {
            mc.options.renderDistance().set(previousRenderDistance);
            mc.options.simulationDistance().set(previousSimulationDistance);

            optimized = false;
        }
    }
}
