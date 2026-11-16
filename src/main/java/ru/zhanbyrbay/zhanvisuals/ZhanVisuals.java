
package ru.zhanbyrbay.zhanvisuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.ClickType;
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

    // Visuals
    public static boolean hitColor = true;
    public static boolean hitSound = false;
    public static boolean damageIndicator = true;
    public static boolean hitParticles = true;
    public static boolean swingAnimation = true;

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

    // Movement
    public static boolean zoom = false;
    public static boolean autoSprint = false;

    // NoRender
    public static boolean noParticles = false;
    public static boolean noWeather = false;
    public static boolean noPumpkin = false;
    public static boolean noBossBar = false;
    public static boolean noHurtCamera = false;
    public static boolean noFluid = false;

    // Optimization
    public static boolean optimized = false;

    // AutoSwap
    public static boolean autoSwap = false;

    // Legacy states
    public static boolean aimAssist = false;
    public static boolean attackAura = false;
    public static boolean autoInvis = false;
    public static boolean autoMace = false;
    public static boolean antiSpam = true;

    public static String autoSwapItemA =
        "minecraft:totem_of_undying";

    public static String autoSwapItemB =
        "minecraft:ender_pearl";

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
        BindManager.init();

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

        if (mc.player == null || mc.gameMode == null) {
            return;
        }

        ItemStack offhand = mc.player.getOffhandItem();
        String offhandId = getItemId(offhand);

        String wantedId = offhandId.equals(autoSwapItemA)
            ? autoSwapItemB
            : autoSwapItemA;

        Identifier id = Identifier.tryParse(wantedId);

        if (id == null) {
            return;
        }

        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack =
                mc.player.getInventory().getItem(slot);

            if (stack.isEmpty()) {
                continue;
            }

            Identifier stackId =
                BuiltInRegistries.ITEM.getKey(stack.getItem());

            if (!stackId.equals(id)) {
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
        if (stack.isEmpty()) {
            return "";
        }

        return BuiltInRegistries.ITEM
            .getKey(stack.getItem())
            .toString();
    }

    // =================================
    // SAVE SETTINGS
    // =================================

    public static void saveSettings() {
        try {
            if (configFile == null) {
                configFile =
                    Minecraft.getInstance().gameDirectory.toPath()
                        .resolve("config/zhanvisuals/settings.properties");
            }

            Files.createDirectories(configFile.getParent());

            Properties p = new Properties();

            p.setProperty("autoSwap", String.valueOf(autoSwap));
            p.setProperty("autoSwapItemA", autoSwapItemA);
            p.setProperty("autoSwapItemB", autoSwapItemB);

            p.setProperty("hitColor", String.valueOf(hitColor));
            p.setProperty("hitSound", String.valueOf(hitSound));
            p.setProperty("damageIndicator", String.valueOf(damageIndicator));
            p.setProperty("hitParticles", String.valueOf(hitParticles));
            p.setProperty("swingAnimation", String.valueOf(swingAnimation));

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

            p.setProperty("zoom", String.valueOf(zoom));
            p.setProperty("autoSprint", String.valueOf(autoSprint));

            p.setProperty("noParticles", String.valueOf(noParticles));
            p.setProperty("noWeather", String.valueOf(noWeather));
            p.setProperty("noPumpkin", String.valueOf(noPumpkin));
            p.setProperty("noBossBar", String.valueOf(noBossBar));
            p.setProperty("noHurtCamera", String.valueOf(noHurtCamera));
            p.setProperty("noFluid", String.valueOf(noFluid));

            p.setProperty("optimized", String.valueOf(optimized));

            p.setProperty("aimAssist", String.valueOf(aimAssist));
            p.setProperty("attackAura", String.valueOf(attackAura));
            p.setProperty("autoInvis", String.valueOf(autoInvis));
            p.setProperty("autoMace", String.valueOf(autoMace));
            p.setProperty("antiSpam", String.valueOf(antiSpam));

            BindManager.save(p);

            try (var out = Files.newOutputStream(configFile)) {
                p.store(out, "ZhanVisuals settings");
            }

        } catch (IOException e) {
            System.err.println(
                "[ZhanVisuals] Save error: " + e.getMessage()
            );
        }
    }

    // =================================
    // LOAD SETTINGS
    // =================================

    public static void loadSettings() {
        try {
            if (configFile == null) {
                configFile =
                    Minecraft.getInstance().gameDirectory.toPath()
                        .resolve("config/zhanvisuals/settings.properties");
            }

            if (!Files.exists(configFile)) {
                return;
            }

            Properties p = new Properties();

            try (var in = Files.newInputStream(configFile)) {
                p.load(in);
            }

            autoSwap = Boolean.parseBoolean(
                p.getProperty("autoSwap", "false")
            );

            autoSwapItemA = p.getProperty(
                "autoSwapItemA",
                autoSwapItemA
            );

            autoSwapItemB = p.getProperty(
                "autoSwapItemB",
                autoSwapItemB
            );

            hitColor = Boolean.parseBoolean(
                p.getProperty("hitColor", "true")
            );

            hitSound = Boolean.parseBoolean(
                p.getProperty("hitSound", "false")
            );

            damageIndicator = Boolean.parseBoolean(
                p.getProperty("damageIndicator", "true")
            );

            hitParticles = Boolean.parseBoolean(
                p.getProperty("hitParticles", "true")
            );

            swingAnimation = Boolean.parseBoolean(
                p.getProperty("swingAnimation", "true")
            );

            customCrosshair = Boolean.parseBoolean(
                p.getProperty("customCrosshair", "true")
            );

            fullbright = Boolean.parseBoolean(
                p.getProperty("fullbright", "false")
            );

            lowFire = Boolean.parseBoolean(
                p.getProperty("lowFire", "false")
            );

            watermark = Boolean.parseBoolean(
                p.getProperty("watermark", "true")
            );

            fpsCounter = Boolean.parseBoolean(
                p.getProperty("fpsCounter", "true")
            );

            coordinates = Boolean.parseBoolean(
                p.getProperty("coordinates", "true")
            );

            armorHUD = Boolean.parseBoolean(
                p.getProperty("armorHUD", "true")
            );

            totemCounter = Boolean.parseBoolean(
                p.getProperty("totemCounter", "true")
            );

            pingDisplay = Boolean.parseBoolean(
                p.getProperty("pingDisplay", "true")
            );

            targetHUD = Boolean.parseBoolean(
                p.getProperty("targetHUD", "true")
            );

            keystrokes = Boolean.parseBoolean(
                p.getProperty("keystrokes", "true")
            );

            speedometer = Boolean.parseBoolean(
                p.getProperty("speedometer", "true")
            );

            zoom = Boolean.parseBoolean(
                p.getProperty("zoom", "false")
            );

            autoSprint = Boolean.parseBoolean(
                p.getProperty("autoSprint", "false")
            );

            noParticles = Boolean.parseBoolean(
                p.getProperty("noParticles", "false")
            );

            noWeather = Boolean.parseBoolean(
                p.getProperty("noWeather", "false")
            );

            noPumpkin = Boolean.parseBoolean(
                p.getProperty("noPumpkin", "false")
            );

            noBossBar = Boolean.parseBoolean(
                p.getProperty("noBossBar", "false")
            );

            noHurtCamera = Boolean.parseBoolean(
                p.getProperty("noHurtCamera", "false")
            );

            noFluid = Boolean.parseBoolean(
                p.getProperty("noFluid", "false")
            );

            optimized = Boolean.parseBoolean(
                p.getProperty("optimized", "false")
            );

            aimAssist = Boolean.parseBoolean(
                p.getProperty("aimAssist", "false")
            );

            attackAura = Boolean.parseBoolean(
                p.getProperty("attackAura", "false")
            );

            autoInvis = Boolean.parseBoolean(
                p.getProperty("autoInvis", "false")
            );

            autoMace = Boolean.parseBoolean(
                p.getProperty("autoMace", "false")
            );

            antiSpam = Boolean.parseBoolean(
                p.getProperty("antiSpam", "true")
            );

            BindManager.load(p);

        } catch (IOException e) {
            System.err.println(
                "[ZhanVisuals] Load error: " + e.getMessage()
            );
        }
    }

    // =================================
    // ZOOM
    // =================================

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

    // =================================
    // MOVEMENT
    // =================================

    private static void updateMovement(Minecraft mc) {
        if (mc.player == null) {
            return;
        }

        if (autoSprint) {
            boolean movingForward =
                mc.options.keyUp.isDown();

            boolean sneaking =
                mc.options.keyShift.isDown();

            if (
                movingForward &&
                !sneaking &&
                !mc.player.isPassenger()
            ) {
                mc.player.setSprinting(true);
            }
        }
    }

    // =================================
    // SPEED
    // =================================

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

            currentSpeed =
                Math.sqrt(dx * dx + dz * dz) * 20.0;
        }

        lastX = x;
        lastZ = z;
        hasLastPosition = true;
    }

    // =================================
    // MODULE STATES
    // =================================

    public static boolean isEnabled(String name) {
        return switch (name) {
            case "HitColor" -> hitColor;
            case "HitSound" -> hitSound;
            case "DamageIndicator" -> damageIndicator;
            case "HitParticles" -> hitParticles;
            case "SwingAnimation" -> swingAnimation;

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

            case "AimAssist" -> aimAssist;
            case "AttackAura" -> attackAura;
            case "AutoInvis" -> autoInvis;
            case "AutoMace" -> autoMace;
            case "AntiSpam" -> antiSpam;

            default -> false;
        };
    }

    public static void toggleModule(String name) {
        switch (name) {
            case "HitColor" -> hitColor = !hitColor;
            case "HitSound" -> hitSound = !hitSound;
            case "DamageIndicator" ->
                damageIndicator = !damageIndicator;
            case "HitParticles" ->
                hitParticles = !hitParticles;
            case "SwingAnimation" ->
                swingAnimation = !swingAnimation;

            case "CustomCrosshair" ->
                customCrosshair = !customCrosshair;

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

                if (!zoom) {
                    restoreZoom(Minecraft.getInstance());
                }
            }

            case "AutoSprint" -> autoSprint = !autoSprint;

            case "No Particles" -> noParticles = !noParticles;
            case "No Weather" -> noWeather = !noWeather;
            case "No Pumpkin" -> noPumpkin = !noPumpkin;
            case "NoBossBar" -> noBossBar = !noBossBar;
            case "No Hurt Camera" ->
                noHurtCamera = !noHurtCamera;
            case "No Fluid" -> noFluid = !noFluid;

            case "Performance Boost" -> toggleOptimization();
            case "AutoSwap" -> autoSwap = !autoSwap;

            case "AimAssist" -> aimAssist = !aimAssist;
            case "AttackAura" -> attackAura = !attackAura;
            case "AutoInvis" -> autoInvis = !autoInvis;
            case "AutoMace" -> autoMace = !autoMace;
            case "AntiSpam" -> antiSpam = !antiSpam;
        }

        saveSettings();
    }

    // =================================
    // FULLBRIGHT
    // =================================

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

    // =================================
    // OPTIMIZATION
    // =================================

    private static void toggleOptimization() {
        Minecraft mc = Minecraft.getInstance();

        if (!optimized) {
            previousRenderDistance =
                mc.options.renderDistance().get();

            previousSimulationDistance =
                mc.options.simulationDistance().get();

            mc.options.renderDistance().set(
                Math.min(previousRenderDistance, 6)
            );

            mc.options.simulationDistance().set(
                Math.min(previousSimulationDistance, 4)
            );

            optimized = true;

        } else {
            mc.options.renderDistance().set(
                previousRenderDistance
            );

            mc.options.simulationDistance().set(
                previousSimulationDistance
            );

            optimized = false;
        }
    }
}
