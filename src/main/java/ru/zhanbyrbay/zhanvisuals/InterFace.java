
package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.Minecraft;

public class InterFace {

    private static boolean enabled = true;

    private static float hudScale = 1.0f;

    public static boolean waterMark = true;
    public static boolean information = true;
    public static boolean keyBinds = true;
    public static boolean potions = true;
    public static boolean arrayList = true;
    public static boolean notifications = true;
    public static boolean targetHud = true;
    public static boolean itemHud = true;
    public static boolean useTracker = true;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
        ZhanVisuals.watermark = value && waterMark;
        ZhanVisuals.targetHUD = value && targetHud;
        ZhanVisuals.armorHUD = value && itemHud;
        ZhanVisuals.keystrokes = value && keyBinds;
    }

    public static void toggle() {
        setEnabled(!enabled);
    }

    public static float getHudScale() {
        return hudScale;
    }

    public static void setHudScale(float value) {
        hudScale = Math.max(0.5f, Math.min(1.5f, value));
    }

    public static void toggleElement(String name) {
        switch (name) {
            case "WaterMark" -> {
                waterMark = !waterMark;
                ZhanVisuals.watermark = enabled && waterMark;
            }
            case "Information" -> information = !information;
            case "Key Binds" -> {
                keyBinds = !keyBinds;
                ZhanVisuals.keystrokes = enabled && keyBinds;
            }
            case "Potions" -> potions = !potions;
            case "ArrayList" -> arrayList = !arrayList;
            case "Notifications" -> notifications = !notifications;
            case "Target Hud" -> {
                targetHud = !targetHud;
                ZhanVisuals.targetHUD = enabled && targetHud;
            }
            case "Item Hud" -> {
                itemHud = !itemHud;
                ZhanVisuals.armorHUD = enabled && itemHud;
            }
            case "Use Tracker" -> useTracker = !useTracker;
        }

        ZhanVisuals.saveSettings();
    }

    public static boolean isElementEnabled(String name) {
        return switch (name) {
            case "WaterMark" -> waterMark;
            case "Information" -> information;
            case "Key Binds" -> keyBinds;
            case "Potions" -> potions;
            case "ArrayList" -> arrayList;
            case "Notifications" -> notifications;
            case "Target Hud" -> targetHud;
            case "Item Hud" -> itemHud;
            case "Use Tracker" -> useTracker;
            default -> false;
        };
    }

    public static void reset() {
        waterMark = true;
        information = true;
        keyBinds = true;
        potions = true;
        arrayList = true;
        notifications = true;
        targetHud = true;
        itemHud = true;
        useTracker = true;
        hudScale = 1.0f;

        setEnabled(true);
        ZhanVisuals.saveSettings();
    }
}
