package ru.zhanbyrbay.zhanvisuals;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BindManager {
    // Название модуля -> Код клавиши GLFW
    public static final Map<String, Integer> binds = new HashMap<>();
    private static final Map<Integer, Boolean> keyStates = new HashMap<>();

    public static void init() {
        // Перехват отправки сообщений в чат (команды с точки)
        ClientSendMessageEvents.ALLOW.register(message -> {
            if (message.startsWith(".")) {
                handleCommand(message);
                return false; // Отменяем отправку сообщения на сервер
            }
            return true;
        });

        // Проверка нажатия клавиш каждый тик клиента
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.getWindow() == null || client.player == null) return;
            long window = client.getWindow().window();

            for (Map.Entry<String, Integer> entry : binds.entrySet()) {
                String module = entry.getKey();
                int keyCode = entry.getValue();

                boolean isDown = GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
                boolean wasDown = keyStates.getOrDefault(keyCode, false);

                // Срабатывает ровно один раз при нажатии (чтобы модуль не переключался бесконечно)
                if (isDown && !wasDown) {
                    ZhanVisuals.toggleModule(module);
                    boolean state = ZhanVisuals.isEnabled(module);
                    client.player.displayClientMessage(
                        Component.literal("§5[ZhanVisuals] §fМодуль §d" + module + " §f" + (state ? "§aвключен" : "§cвыключен")),
                        false
                    );
                }
                keyStates.put(keyCode, isDown);
            }
        });
    }

    private static void handleCommand(String command) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        String[] args = command.substring(1).split(" ");
        if (args.length >= 3 && args[0].equalsIgnoreCase("bind")) {
            String subAction = args[1]; // add / remove
            if (subAction.equalsIgnoreCase("add")) {
                String module = args[2];
                String keyName = args[3].toUpperCase();

                int keyCode = parseKey(keyName);
                if (keyCode != -1) {
                    binds.put(module, keyCode);
                    mc.player.displayClientMessage(Component.literal("§5[ZhanVisuals] §aМодуль §d" + module + " §aпривязан к клавише §e" + keyName), false);
                    ZhanVisuals.saveSettings();
                } else {
                    mc.player.displayClientMessage(Component.literal("§c[ZhanVisuals] Неизвестная клавиша: " + keyName), false);
                }
            } else if (subAction.equalsIgnoreCase("remove")) {
                String module = args[2];
                if (binds.remove(module) != null) {
                    mc.player.displayClientMessage(Component.literal("§5[ZhanVisuals] §cБинд для модуля §d" + module + " §cудален"), false);
                    ZhanVisuals.saveSettings();
                } else {
                    mc.player.displayClientMessage(Component.literal("§c[ZhanVisuals] Бинд для модуля " + module + " не найден"), false);
                }
            }
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("bind") && args[1].equalsIgnoreCase("list")) {
            mc.player.displayClientMessage(Component.literal("§5[ZhanVisuals] §fАктивные бинды:"), false);
            if (binds.isEmpty()) {
                mc.player.displayClientMessage(Component.literal(" §7Нет активных биндов."), false);
            } else {
                for (Map.Entry<String, Integer> entry : binds.entrySet()) {
                    mc.player.displayClientMessage(Component.literal(" §7- §d" + entry.getKey() + " §7: §e" + getKeyName(entry.getValue())), false);
                }
            }
        } else {
            mc.player.displayClientMessage(Component.literal("§cИспользование: .bind add <Модуль> <Клавиша> | .bind remove <Модуль> | .bind list"), false);
        }
    }

    private static int parseKey(String name) {
        if (name.length() == 1) {
            char c = name.charAt(0);
            if (c >= 'A' && c <= 'Z') return GLFW.GLFW_KEY_A + (c - 'A');
            if (c >= '0' && c <= '9') return GLFW.GLFW_KEY_0 + (c - '0');
        }
        return switch (name) {
            case "RSHIFT", "RIGHT_SHIFT" -> GLFW.GLFW_KEY_RIGHT_SHIFT;
            case "LSHIFT", "LEFT_SHIFT" -> GLFW.GLFW_KEY_LEFT_SHIFT;
            case "SPACE" -> GLFW.GLFW_KEY_SPACE;
            case "LCTRL", "CTRL" -> GLFW.GLFW_KEY_LEFT_CONTROL;
            case "ALT" -> GLFW.GLFW_KEY_LEFT_ALT;
            case "TAB" -> GLFW.GLFW_KEY_TAB;
            case "UP" -> GLFW.GLFW_KEY_UP;
            case "DOWN" -> GLFW.GLFW_KEY_DOWN;
            case "LEFT" -> GLFW.GLFW_KEY_LEFT;
            case "RIGHT" -> GLFW.GLFW_KEY_RIGHT;
            default -> -1;
        };
    }

    private static String getKeyName(int keyCode) {
        for (int i = GLFW.GLFW_KEY_A; i <= GLFW.GLFW_KEY_Z; i++) {
            if (keyCode == i) return String.valueOf((char)('A' + (i - GLFW.GLFW_KEY_A)));
        }
        for (int i = GLFW.GLFW_KEY_0; i <= GLFW.GLFW_KEY_9; i++) {
            if (keyCode == i) return String.valueOf((char)('0' + (i - GLFW.GLFW_KEY_0)));
        }
        return switch (keyCode) {
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT";
            case GLFW.GLFW_KEY_SPACE -> "SPACE";
            case GLFW.GLFW_KEY_TAB -> "TAB";
            default -> "KEY_" + keyCode;
        };
    }

    public static void save(Properties p) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : binds.entrySet()) {
            sb.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
        }
        p.setProperty("custom_binds", sb.toString());
    }

    public static void load(Properties p) {
        binds.clear();
        String raw = p.getProperty("custom_binds", "");
        if (!raw.isEmpty()) {
            String[] pairs = raw.split(",");
            for (String pair : pairs) {
                String[] parts = pair.split(":");
                if (parts.length == 2) {
                    try {
                        binds.put(parts[0], Integer.parseInt(parts[1]));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
    }
}
