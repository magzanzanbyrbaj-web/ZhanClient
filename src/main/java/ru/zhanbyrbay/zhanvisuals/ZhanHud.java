
package ru.zhanbyrbay.zhanvisuals;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

public class ZhanHud {

    private static LivingEntity lastTarget;
    private static long targetExpireTime;

    private static int watermarkX = 8;
    private static int watermarkY = 8;
    private static int targetX = 8;
    private static int targetY = 45;
    private static int armorX = 8;
    private static int armorY = 90;
    private static int speedX = 8;
    private static int speedY = 130;

    private static boolean editing = false;

    private static final Map<String, Boolean> MODULE_STATES =
        new HashMap<>();

    private static String notificationText = "";
    private static long notificationTime = 0;

    private static final String[] TRACKED_MODULES = {
        "HitColor",
        "CustomCrosshair",
        "Fullbright",
        "Low Fire",
        "Watermark",
        "FPS Counter",
        "Coordinates",
        "Armor HUD",
        "Totem Counter",
        "Ping Display",
        "Target HUD",
        "Keystrokes",
        "Speedometer",
        "Zoom",
        "AutoSprint",
        "No Particles",
        "No Weather",
        "No Pumpkin",
        "NoBossBar",
        "No Hurt Camera",
        "No Fluid",
        "Performance Boost"
    };

    public static void register() {
        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath("zhanvisuals", "main_hud"),
            (graphics, tickCounter) -> render(graphics)
        );
    }

    private static void render(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.options.hideGui) {
            return;
        }

        float scale = InterFace.isEnabled()
            ? InterFace.getHudScale()
            : 1.0f;

        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 1.0f);

        int screenWidth = graphics.guiWidth();

        boolean interfaceEnabled = InterFace.isEnabled();

        if (ZhanVisuals.watermark) {
            drawWatermark(graphics, mc);
        }

        updateTarget(mc);

        if (ZhanVisuals.targetHUD
            && lastTarget != null
            && System.currentTimeMillis() < targetExpireTime) {
            drawTarget(graphics, lastTarget);
        }

        if (ZhanVisuals.armorHUD) {
            drawArmor(graphics, mc);
        }

        if (ZhanVisuals.speedometer) {
            drawSpeed(graphics);
        }

        if (ZhanVisuals.coordinates) {
            drawCoordinates(graphics, mc);
        }

        if (ZhanVisuals.fpsCounter) {
            graphics.drawString(
                mc.font,
                "FPS: " + mc.getFps(),
                screenWidth - 65,
                8,
                0xFFFFFFFF,
                true
            );
        }

        if (ZhanVisuals.pingDisplay && mc.player.connection != null) {
            var info = mc.player.connection.getPlayerInfo(
                mc.player.getUUID()
            );

            if (info != null) {
                graphics.drawString(
                    mc.font,
                    "PING: " + info.getLatency(),
                    screenWidth - 80,
                    20,
                    0xFFFFFFFF,
                    true
                );
            }
        }

        if (ZhanVisuals.totemCounter) {
            drawTotems(graphics, mc);
        }

        if (ZhanVisuals.keystrokes) {
            drawKeystrokes(graphics, mc);
        }

        if (interfaceEnabled) {
            if (InterFace.isElementEnabled("Information")) {
                drawInformation(graphics, mc);
            }

            if (InterFace.isElementEnabled("Potions")) {
                drawPotions(graphics, mc);
            }

            if (InterFace.isElementEnabled("ArrayList")) {
                drawArrayList(graphics, mc);
            }

            if (InterFace.isElementEnabled("Notifications")) {
                updateNotifications();
                drawNotifications(graphics, mc);
            }

            if (InterFace.isElementEnabled("Use Tracker")) {
                drawUseTracker(graphics, mc);
            }
        }

        graphics.pose().popPose();
    }

    // ==============================
    // TARGET
    // ==============================

    private static void updateTarget(Minecraft mc) {
        if (mc.crosshairPickEntity instanceof LivingEntity target
            && target.isAlive()
            && target != mc.player) {

            lastTarget = target;
            targetExpireTime = System.currentTimeMillis() + 3000;
        }

        if (lastTarget != null
            && System.currentTimeMillis() > targetExpireTime) {
            lastTarget = null;
        }
    }

    // ==============================
    // WATERMARK
    // ==============================

    private static void drawWatermark(
        GuiGraphics g,
        Minecraft mc
    ) {
        int x = watermarkX;
        int y = watermarkY;

        drawPanel(g, x, y, 174, 23);

        g.fill(
            x,
            y,
            x + 3,
            y + 23,
            0xFFB66BFF
        );

        drawText(g, "Z", x + 9, y + 7, 0xFFB66BFF);
        drawText(g, "ZhanVisuals", x + 23, y + 7, 0xFFFFFFFF);

        g.fill(
            x + 107,
            y + 5,
            x + 108,
            y + 18,
            0xFF49305C
        );

        drawText(
            g,
            mc.getFps() + " FPS",
            x + 115,
            y + 7,
            0xFFE4B5FF
        );

        if (mc.player.connection != null) {
            var info = mc.player.connection.getPlayerInfo(
                mc.player.getUUID()
            );

            if (info != null) {
                String ping = info.getLatency() + " MS";

                drawText(
                    g,
                    ping,
                    x + 115,
                    y + 18,
                    0xFFAAA1B8
                );
            }
        }
    }

    // ==============================
    // TARGET HUD
    // ==============================

    private static void drawTarget(
        GuiGraphics g,
        LivingEntity target
    ) {
        int x = targetX;
        int y = targetY;

        drawPanel(g, x, y, 150, 48);

        drawText(
            g,
            target.getName().getString(),
            x + 8,
            y + 8,
            0xFFFFFFFF
        );

        int health = (int) target.getHealth();
        int maxHealth = Math.max(
            1,
            (int) target.getMaxHealth()
        );

        drawText(
            g,
            "HP: " + health + " / " + maxHealth,
            x + 8,
            y + 22,
            0xFFFF7777
        );

        int barWidth = 130;

        int currentWidth = Math.max(
            0,
            Math.min(
                barWidth,
                (int) (
                    barWidth
                    * target.getHealth()
                    / target.getMaxHealth()
                )
            )
        );

        g.fill(
            x + 8,
            y + 36,
            x + 8 + barWidth,
            y + 40,
            0xFF39313F
        );

        g.fill(
            x + 8,
            y + 36,
            x + 8 + currentWidth,
            y + 40,
            0xFFBD72FF
        );
    }

    // ==============================
    // ARMOR HUD
    // ==============================

    private static void drawArmor(
        GuiGraphics g,
        Minecraft mc
    ) {
        int x = armorX;
        int y = armorY;

        drawPanel(g, x, y, 100, 20);

        ItemStack[] armor = {
            mc.player.getItemBySlot(EquipmentSlot.HEAD),
            mc.player.getItemBySlot(EquipmentSlot.CHEST),
            mc.player.getItemBySlot(EquipmentSlot.LEGS),
            mc.player.getItemBySlot(EquipmentSlot.FEET)
        };

        int offset = 4;

        for (ItemStack stack : armor) {
            if (!stack.isEmpty()) {
                g.renderItem(stack, x + offset, y + 2);
            }

            offset += 22;
        }
    }

    // ==============================
    // SPEEDOMETER
    // ==============================

    private static void drawSpeed(GuiGraphics g) {
        String text = String.format(
            "Speed: %.2f",
            ZhanVisuals.currentSpeed
        );

        drawPanel(g, speedX, speedY, 100, 22);

        drawText(
            g,
            text,
            speedX + 7,
            speedY + 7,
            0xFFE4B5FF
        );
    }

    // ==============================
    // COORDINATES
    // ==============================

    private static void drawCoordinates(
        GuiGraphics g,
        Minecraft mc
    ) {
        var pos = mc.player.blockPosition();

        String text = "XYZ: "
            + pos.getX() + " "
            + pos.getY() + " "
            + pos.getZ();

        drawText(
            g,
            text,
            8,
            g.guiHeight() - 15,
            0xFFFFFFFF
        );
    }

    // ==============================
    // TOTEM COUNTER
    // ==============================

    private static void drawTotems(
        GuiGraphics g,
        Minecraft mc
    ) {
        int count = 0;

        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack =
                mc.player.getInventory().getItem(slot);

            if (stack.is(Items.TOTEM_OF_UNDYING)) {
                count += stack.getCount();
            }
        }

        ItemStack offhand = mc.player.getOffhandItem();

        if (offhand.is(Items.TOTEM_OF_UNDYING)) {
            count += offhand.getCount();
        }

        drawText(
            g,
            "Totems: " + count,
            8,
            g.guiHeight() - 28,
            0xFFFFD77A
        );
    }

    // ==============================
    // KEYSTROKES
    // ==============================

    private static void drawKeystrokes(
        GuiGraphics g,
        Minecraft mc
    ) {
        int x = g.guiWidth() - 75;
        int y = g.guiHeight() - 65;

        drawKey(
            g, x + 24, y,
            "W", mc.options.keyUp.isDown()
        );

        drawKey(
            g, x, y + 22,
            "A", mc.options.keyLeft.isDown()
        );

        drawKey(
            g, x + 24, y + 22,
            "S", mc.options.keyDown.isDown()
        );

        drawKey(
            g, x + 48, y + 22,
            "D", mc.options.keyRight.isDown()
        );

        drawKey(
            g, x + 24, y + 44,
            "SP", mc.options.keyJump.isDown()
        );
    }

    private static void drawKey(
        GuiGraphics g,
        int x,
        int y,
        String text,
        boolean pressed
    ) {
        int color = pressed
            ? 0xFF9D50D8
            : 0xFF29232F;

        drawPanel(g, x, y, 22, 20);

        g.fill(
            x + 1,
            y + 1,
            x + 21,
            y + 19,
            color
        );

        drawText(
            g,
            text,
            x + 7,
            y + 6,
            0xFFFFFFFF
        );
    }

    // ==============================
    // INFORMATION
    // ==============================

    private static void drawInformation(
        GuiGraphics g,
        Minecraft mc
    ) {
        int x = 8;
        int y = 38;

        drawPanel(g, x, y, 145, 62);

        drawText(
            g,
            "INFORMATION",
            x + 7,
            y + 6,
            0xFFB66BFF
        );

        var pos = mc.player.blockPosition();

        drawText(
            g,
            "XYZ: " + pos.getX() + " "
                + pos.getY() + " " + pos.getZ(),
            x + 7,
            y + 20,
            0xFFFFFFFF
        );

        drawText(
            g,
            "FPS: " + mc.getFps(),
            x + 7,
            y + 33,
            0xFFE4B5FF
        );

        int ping = -1;

        if (mc.player.connection != null) {
            var info = mc.player.connection.getPlayerInfo(
                mc.player.getUUID()
            );

            if (info != null) {
                ping = info.getLatency();
            }
        }

        drawText(
            g,
            "PING: " + (ping < 0 ? "N/A" : ping + " MS"),
            x + 7,
            y + 46,
            0xFFE4B5FF
        );
    }

    // ==============================
    // POTIONS
    // ==============================

    private static void drawPotions(
        GuiGraphics g,
        Minecraft mc
    ) {
        int x = g.guiWidth() - 150;
        int y = 42;

        int count = mc.player.getActiveEffects().size();

        if (count == 0) {
            return;
        }

        int height = 20 + count * 14;

        drawPanel(g, x, y, 142, height);

        drawText(
            g,
            "POTIONS",
            x + 7,
            y + 6,
            0xFFB66BFF
        );

        int offset = 20;

        for (var effect : mc.player.getActiveEffects()) {
            String name = effect.getEffect()
                .value()
                .getDisplayName()
                .getString();

            int seconds = Math.max(
                0,
                effect.getDuration() / 20
            );

            String duration = String.format(
                "%d:%02d",
                seconds / 60,
                seconds % 60
            );

            String amplifier = effect.getAmplifier() > 0
                ? " " + (effect.getAmplifier() + 1)
                : "";

            drawText(
                g,
                name + amplifier + "  " + duration,
                x + 7,
                y + offset,
                0xFFFFFFFF
            );

            offset += 14;
        }
    }

    // ==============================
    // ARRAYLIST
    // ==============================

    private static void drawArrayList(
        GuiGraphics g,
        Minecraft mc
    ) {
        String[] modules = {
            "HitColor",
            "CustomCrosshair",
            "Fullbright",
            "Low Fire",
            "Watermark",
            "FPS Counter",
            "Coordinates",
            "Armor HUD",
            "Totem Counter",
            "Ping Display",
            "Target HUD",
            "Keystrokes",
            "Speedometer",
            "Zoom",
            "AutoSprint",
            "No Particles",
            "No Weather",
            "No Pumpkin",
            "NoBossBar",
            "No Hurt Camera",
            "No Fluid",
            "Performance Boost"
        };

        int x = mc.getWindow().getGuiScaledWidth() - 8;
        int y = 8;

        for (String module : modules) {
            if (!ZhanVisuals.isEnabled(module)) {
                continue;
            }

            int textWidth = mc.font.width(module);
            int rowWidth = textWidth + 12;

            drawPanel(
                g,
                x - rowWidth,
                y,
                rowWidth,
                17
            );

            g.fill(
                x - 2,
                y,
                x,
                y + 17,
                0xFFB66BFF
            );

            drawText(
                g,
                module,
                x - textWidth - 6,
                y + 5,
                0xFFFFFFFF
            );

            y += 19;
        }
    }

    // ==============================
    // NOTIFICATIONS
    // ==============================

    private static void updateNotifications() {
        for (String module : TRACKED_MODULES) {
            boolean current = ZhanVisuals.isEnabled(module);

            Boolean previous = MODULE_STATES.get(module);

            if (previous == null) {
                MODULE_STATES.put(module, current);
                continue;
            }

            if (previous != current) {
                notificationText = module
                    + (current ? " включён" : " выключен");

                notificationTime =
                    System.currentTimeMillis();

                MODULE_STATES.put(module, current);
            }
        }
    }

    private static void drawNotifications(
        GuiGraphics g,
        Minecraft mc
    ) {
        if (notificationText.isEmpty()) {
            return;
        }

        long elapsed =
            System.currentTimeMillis() - notificationTime;

        if (elapsed > 2500) {
            return;
        }

        int width = mc.font.width(notificationText) + 24;

        int x = (g.guiWidth() - width) / 2;
        int y = g.guiHeight() - 55;

        drawPanel(g, x, y, width, 24);

        g.fill(
            x,
            y,
            x + 3,
            y + 24,
            0xFFB66BFF
        );

        drawText(
            g,
            notificationText,
            x + 10,
            y + 8,
            0xFFFFFFFF
        );
    }

    // ==============================
    // USE TRACKER
    // ==============================

    private static void drawUseTracker(
        GuiGraphics g,
        Minecraft mc
    ) {
        if (!mc.options.keyUse.isDown()) {
            return;
        }

        ItemStack stack = mc.player.getMainHandItem();

        if (stack.isEmpty()) {
            return;
        }

        String name = stack.getHoverName().getString();

        int width = mc.font.width(name) + 35;

        int x = (g.guiWidth() - width) / 2;
        int y = g.guiHeight() - 88;

        drawPanel(g, x, y, width, 24);

        drawText(
            g,
            "USE: " + name,
            x + 9,
            y + 8,
            0xFFE4B5FF
        );
    }

    // ==============================
    // DRAW HELPERS
    // ==============================

    private static void drawText(
        GuiGraphics g,
        String text,
        int x,
        int y,
        int color
    ) {
        g.drawString(
            Minecraft.getInstance().font,
            text,
            x,
            y,
            color,
            true
        );
    }

    private static void drawPanel(
        GuiGraphics g,
        int x,
        int y,
        int width,
        int height
    ) {
        g.fill(
            x,
            y,
            x + width,
            y + height,
            0xCC17131F
        );

        g.fill(
            x,
            y,
            x + width,
            y + 1,
            0xFFB66BFF
        );

        g.fill(
            x,
            y + height - 1,
            x + width,
            y + height,
            0xFF49305C
        );
    }

    // ==============================
    // EDITOR
    // ==============================

    public static void setEditing(boolean value) {
        editing = value;
    }

    public static boolean isEditing() {
        return editing;
    }

    public static void setPosition(
        String element,
        int x,
        int y
    ) {
        switch (element) {
            case "watermark" -> {
                watermarkX = x;
                watermarkY = y;
            }
            case "target" -> {
                targetX = x;
                targetY = y;
            }
            case "armor" -> {
                armorX = x;
                armorY = y;
            }
            case "speed" -> {
                speedX = x;
                speedY = y;
            }
        }
    }

    public static int getX(String element) {
        return switch (element) {
            case "watermark" -> watermarkX;
            case "target" -> targetX;
            case "armor" -> armorX;
            case "speed" -> speedX;
            default -> 0;
        };
    }

    public static int getY(String element) {
        return switch (element) {
            case "watermark" -> watermarkY;
            case "target" -> targetY;
            case "armor" -> armorY;
            case "speed" -> speedY;
            default -> 0;
        };
    }
}
