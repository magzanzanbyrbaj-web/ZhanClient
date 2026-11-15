
package ru.zhanbyrbay.zhanvisuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ZhanVisuals implements ClientModInitializer {

    public static final String MOD_ID = "zhanvisuals";

    private static KeyMapping openMenuKey;
    private static KeyMapping zoomKey;
    private static KeyMapping autoSwapKey;
    private static KeyMapping hudEditorKey;

    public static boolean hitColor = true;
    public static boolean customCrosshair = true;
    public static boolean fullbright = false;
    public static boolean lowFire = false;

    public static boolean watermark = true;
    public static boolean fpsCounter = true;
    public static boolean coordinates = true;
    public static boolean armorHUD = true;
    public static boolean totemCounter = true;
    public static boolean pingDisplay = true;
    public static boolean targetHUD = true;
    public static boolean keystrokes = true;
    public static boolean speedometer = true;

    public static boolean zoom = false;
    public static boolean autoSprint = false;

    public static boolean noParticles = false;
    public static boolean noWeather = false;
    public static boolean noPumpkin = false;
    public static boolean noBossBar = false;
    public static boolean noHurtCamera = false;
    public static boolean noFluid = false;

    public static boolean optimized = false;
    public static boolean autoSwap = false;

    public static String autoSwapItemA = "minecraft:totem_of_undying";
    public static String autoSwapItemB = "minecraft:ender_pearl";

    public static double currentSpeed = 0.0;

    private static double previousGamma = 1.0;
    private static int previousFov = 70;
    private static int previousRenderDistance = 12;
    private static int previousSimulationDistance = 8;

    private static boolean zooming = false;
    private static double lastX;
    private static double lastZ;
    private static boolean hasLastPosition = false;

    private static Path configFile;

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

        autoSwapKey = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                "key.zhanvisuals.auto_swap",
                GLFW.GLFW_KEY_V,
                KeyMapping.Category.MISC
            )
        );

        hudEditorKey = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                "key.zhanvisuals.hud_editor",
                GLFW.GLFW_KEY_H,
                KeyMapping.Category.MISC
            )
        );

        loadSettings();
        HudConfig.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while (openMenuKey.consumeClick()) {
                if (client.player != null) {
                    client.setScreen(new ZhanClickScreen());
                }
            }

            while (hudEditorKey.consumeClick()) {
                if (client.player != null) {
                    client.setScreen(new HudEditorScreen());
                }
            }

            while (autoSwapKey.consumeClick()) {
                if (autoSwap && client.player != null) {
                    swapOffhand();
                }
            }

            updateZoom(client);
            updateMovement(client);
            updateSpeed(client);
        });

        ZhanHud.register();

        System.out.println("[ZhanVisuals] Initialized");
    }

    private static void swapOffhand() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.gameMode == null) return;

        ItemStack offhand = mc.player.getOffhandItem();
        String offhandId = getItemId(offhand);

        String wantedId;

        if (offhandId.equals(autoSwapItemA)) {
            wantedId = autoSwapItemB;
        } else {
            wantedId = autoSwapItemA;
        }

        ResourceLocation id = ResourceLocation.tryParse(wantedId);
        if (id == null) return;

        Item wantedItem = BuiltInRegistries.ITEM.get(id);

        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = mc.player.getInventory().getItem(slot);

            if (stack.isEmpty() || stack.getItem() != wantedItem) {
                continue;
            }

            int menuSlot = slot < 9 ? slot + 36 : slot;

            mc.gameMode.handleInventoryMouseClick(
                mc.player.inventoryMenu.containerId,
                menuSlot,
                40,
                ClickType.SWAP,
                mc.player
            );

            return;
        }
    }

    private static String getItemId(ItemStack stack) {
        if (stack.isEmpty()) return "";

        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    public static void saveSettings() {
        try {
            if (configFile == null) {
                configFile = Minecraft.getInstance().gameDirectory.toPath()
                    .resolve("config/zhanvisuals/settings.properties");
            }

            Files.createDirectories(configFile.getParent());

            Properties p = new Properties();

            p.setProperty("autoSwap", String.valueOf(autoSwap));
            p.setProperty("autoSwapItemA", autoSwapItemA);
            p.setProperty("autoSwapItemB", autoSwapItemB);

            p.setProperty("hitColor", String.valueOf(hitColor));
            p.setProperty("customCrosshair", String.valueOf(customCrosshair));
            p.setProperty("fullbright", String.valueOf(fullbright));
            p.setProperty("lowFire", String.valueOf(lowFire));

            p.setProperty("watermark", String.valueOf(watermark));
            p.setProperty("fpsCounter", String.valueOf(fpsCounter));
            p.setProperty("coordinates", String.valueOf(coordinates));
            p.setProperty("armorHUD", String.valueOf(armorHUD));
            p.setProperty("totemCounter", String.valueOf(totemCounter));
            p.setProperty("pingDisplay", String.valueOf(pingDisplay));
            p.setProperty("targetHUD", String.valueOf(targetHUD));
            p.setProperty("keystrokes", String.valueOf(keystrokes));
            p.setProperty("speedometer", String.valueOf(speedometer));

            try (var out = Files.newOutputStream(configFile)) {
                p.store(out, "ZhanVisuals settings");
            }

        } catch (IOException e) {
            System.err.println("[ZhanVisuals] Save error: " + e.getMessage());
        }
    }

    public static void loadSettings() {
        try {
            if (configFile == null) {
                configFile = Minecraft.getInstance().gameDirectory.toPath()
                    .resolve("config/zhanvisuals/settings.properties");
            }

            if (!Files.exists(configFile)) return;

            Properties p = new Properties();

            try (var in = Files.newInputStream(configFile)) {
                p.load(in);
            }

            autoSwap = Boolean.parseBoolean(p.getProperty("autoSwap", "false"));
            autoSwapItemA = p.getProperty("autoSwapItemA", autoSwapItemA);
            autoSwapItemB = p.getProperty("autoSwapItemB", autoSwapItemB);

            hitColor = Boolean.parseBoolean(p.getProperty("hitColor", "true"));
            customCrosshair = Boolean.parseBoolean(p.getProperty("customCrosshair", "true"));
            fullbright = Boolean.parseBoolean(p.getProperty("fullbright", "false"));
            lowFire = Boolean.parseBoolean(p.getProperty("lowFire", "false"));

            watermark = Boolean.parseBoolean(p.getProperty("watermark", "true"));
            fpsCounter = Boolean.parseBoolean(p.getProperty("fpsCounter", "true"));
            coordinates = Boolean.parseBoolean(p.getProperty("coordinates", "true"));
            armorHUD = Boolean.parseBoolean(p.getProperty("armorHUD", "true"));
            totemCounter = Boolean.parseBoolean(p.getProperty("totemCounter", "true"));
            pingDisplay = Boolean.parseBoolean(p.getProperty("pingDisplay", "true"));
            targetHUD = Boolean.parseBoolean(p.getProperty("targetHUD", "true"));
            keystrokes = Boolean.parseBoolean(p.getProperty("keystrokes", "true"));
            speedometer = Boolean.parseBoolean(p.getProperty("speedometer", "true"));

        } catch (IOException e) {
            System.err.println("[ZhanVisuals] Load error: " + e.getMessage());
        }
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
            case "AutoSwap" -> autoSwap;
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
            case "AutoSwap" -> autoSwap = !autoSwap;
        }

        saveSettings();
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

            mc.options.renderDistance().set(Math.min(previousRenderDistance, 6));
            mc.options.simulationDistance().set(Math.min(previousSimulationDistance, 4));

            optimized = true;
        } else {
            mc.options.renderDistance().set(previousRenderDistance);
            mc.options.simulationDistance().set(previousSimulationDistance);

            optimized = false;
        }
    }
}
