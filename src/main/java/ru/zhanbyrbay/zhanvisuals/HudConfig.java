
package ru.zhanbyrbay.zhanvisuals;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import net.fabricmc.loader.api.FabricLoader;

public final class HudConfig {

    private static final Properties properties = new Properties();

    private static final Path FILE = FabricLoader.getInstance()
        .getConfigDir()
        .resolve("zhanvisuals")
        .resolve("hud.properties");

    private HudConfig() {}

    public static void load() {
        try {
            Files.createDirectories(FILE.getParent());

            if (Files.exists(FILE)) {
                try (InputStream input = Files.newInputStream(FILE)) {
                    properties.load(input);
                }
            }

            ZhanHud.setPosition("watermark",
                get("watermark.x", 8), get("watermark.y", 8));

            ZhanHud.setPosition("target",
                get("target.x", 8), get("target.y", 45));

            ZhanHud.setPosition("armor",
                get("armor.x", 8), get("armor.y", 90));

            ZhanHud.setPosition("speed",
                get("speed.x", 8), get("speed.y", 130));

        } catch (IOException e) {
            System.err.println("[ZhanVisuals] HUD config load error: " + e.getMessage());
        }
    }

    public static void saveAll() {
        properties.setProperty("watermark.x",
            String.valueOf(ZhanHud.getX("watermark")));
        properties.setProperty("watermark.y",
            String.valueOf(ZhanHud.getY("watermark")));

        properties.setProperty("target.x",
            String.valueOf(ZhanHud.getX("target")));
        properties.setProperty("target.y",
            String.valueOf(ZhanHud.getY("target")));

        properties.setProperty("armor.x",
            String.valueOf(ZhanHud.getX("armor")));
        properties.setProperty("armor.y",
            String.valueOf(ZhanHud.getY("armor")));

        properties.setProperty("speed.x",
            String.valueOf(ZhanHud.getX("speed")));
        properties.setProperty("speed.y",
            String.valueOf(ZhanHud.getY("speed")));

        try {
            Files.createDirectories(FILE.getParent());

            try (OutputStream output = Files.newOutputStream(FILE)) {
                properties.store(output, "ZhanVisuals HUD positions");
            }

        } catch (IOException e) {
            System.err.println("[ZhanVisuals] HUD config save error: " + e.getMessage());
        }
    }

    private static int get(String key, int fallback) {
        try {
            return Integer.parseInt(
                properties.getProperty(key, String.valueOf(fallback))
            );
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
