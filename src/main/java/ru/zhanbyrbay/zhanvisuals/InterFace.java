
package ru.zhanbyrbay.zhanvisuals;

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
        apply();
        ZhanVisuals.saveSettings();
    }

    public static void toggle() {
        setEnabled(!enabled);
    }

    public static float getHudScale() {
        return hudScale;
    }

    public static void setHudScale(float value) {
        hudScale = Math.max(0.5f, Math.min(1.5f, value));
        ZhanVisuals.saveSettings();
    }

    public static void toggleElement(String name) {
        switch (name) {
            case "Watermark", "WaterMark" -> waterMark = !waterMark;
            case "Information" -> information = !information;
            case "Key Binds", "Keystrokes" -> keyBinds = !keyBinds;
            case "Potions" -> potions = !potions;
            case "ArrayList" -> arrayList = !arrayList;
            case "Notifications" -> notifications = !notifications;
            case "Target Hud", "Target HUD" -> targetHud = !targetHud;
            case "Item Hud", "Armor HUD" -> itemHud = !itemHud;
            case "Use Tracker" -> useTracker = !useTracker;
        }

        apply();
        ZhanVisuals.saveSettings();
    }

    public static boolean isElementEnabled(String name) {
        return switch (name) {
            case "Watermark", "WaterMark" -> waterMark;
            case "Information" -> information;
            case "Key Binds", "Keystrokes" -> keyBinds;
            case "Potions" -> potions;
            case "ArrayList" -> arrayList;
            case "Notifications" -> notifications;
            case "Target Hud", "Target HUD" -> targetHud;
            case "Item Hud", "Armor HUD" -> itemHud;
            case "Use Tracker" -> useTracker;
            default -> false;
        };
    }

    public static void apply() {
        ZhanVisuals.watermark = enabled && waterMark;
        ZhanVisuals.targetHUD = enabled && targetHud;
        ZhanVisuals.armorHUD = enabled && itemHud;
        ZhanVisuals.keystrokes = enabled && keyBinds;
    }

    public static void reset() {
        enabled = true;
        hudScale = 1.0f;

        waterMark = true;
        information = true;
        keyBinds = true;
        potions = true;
        arrayList = true;
        notifications = true;
        targetHud = true;
        itemHud = true;
        useTracker = true;

        apply();
        ZhanVisuals.saveSettings();
    }
}
