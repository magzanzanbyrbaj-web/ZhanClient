
package ru.zhanbyrbay.zhanvisuals;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public final class ConfigManager {

    private static final Path ROOT = FabricLoader.getInstance()
        .getConfigDir()
        .resolve("zhanvisuals");

    private static final Path PROFILES = ROOT.resolve("profiles");

    private ConfigManager() {}

    private static Path getProfile(String name) {
        if (name == null || !name.matches("[a-zA-Z0-9_-]{1,24}")) {
            throw new IllegalArgumentException("Invalid config name");
        }

        return PROFILES.resolve(name);
    }

    public static boolean save(String name) {
        try {
            Path folder = getProfile(name);
            Files.createDirectories(folder);

            ZhanVisuals.saveSettings();
            HudConfig.saveAll();

            Path settings = ROOT.resolve("settings.properties");
            Path hud = ROOT.resolve("hud.properties");

            if (Files.exists(settings)) {
                Files.copy(settings, folder.resolve("settings.properties"),
                    StandardCopyOption.REPLACE_EXISTING);
            }

            if (Files.exists(hud)) {
                Files.copy(hud, folder.resolve("hud.properties"),
                    StandardCopyOption.REPLACE_EXISTING);
            }

            return true;
        } catch (Exception e) {
            System.err.println("[ZhanVisuals] Config save error: " + e.getMessage());
            return false;
        }
    }

    public static boolean load(String name) {
        try {
            Path folder = getProfile(name);

            Path settings = folder.resolve("settings.properties");
            Path hud = folder.resolve("hud.properties");

            if (!Files.exists(settings) && !Files.exists(hud)) {
                return false;
            }

            Files.createDirectories(ROOT);

            if (Files.exists(settings)) {
                Files.copy(settings, ROOT.resolve("settings.properties"),
                    StandardCopyOption.REPLACE_EXISTING);
                ZhanVisuals.loadSettings();
            }

            if (Files.exists(hud)) {
                Files.copy(hud, ROOT.resolve("hud.properties"),
                    StandardCopyOption.REPLACE_EXISTING);
                HudConfig.load();
            }

            return true;
        } catch (Exception e) {
            System.err.println("[ZhanVisuals] Config load error: " + e.getMessage());
            return false;
        }
    }

    public static boolean delete(String name) {
        try {
            Path folder = getProfile(name);

            if (!Files.exists(folder)) {
                return false;
            }

            try (var paths = Files.walk(folder)) {
                paths.sorted((a, b) -> b.compareTo(a))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            }

            return true;
        } catch (Exception e) {
            System.err.println("[ZhanVisuals] Config delete error: " + e.getMessage());
            return false;
        }
    }

    public static List<String> getConfigs() {
        List<String> result = new ArrayList<>();

        try {
            Files.createDirectories(PROFILES);

            try (var files = Files.list(PROFILES)) {
                files.filter(Files::isDirectory)
                    .forEach(path ->
                        result.add(path.getFileName().toString())
                    );
            }
        } catch (IOException e) {
            System.err.println("[ZhanVisuals] Config list error: " + e.getMessage());
        }

        return result;
    }

    public static boolean exists(String name) {
        try {
            return Files.isDirectory(getProfile(name));
        } catch (Exception e) {
            return false;
        }
    }
}
