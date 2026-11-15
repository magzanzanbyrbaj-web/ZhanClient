
package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.List;

public class ConfigManagerScreen extends Screen {

    private static final int PURPLE = 0xFFAA78FF;
    private static final int LIGHT = 0xFFF4EEFF;
    private static final int MUTED = 0xFFAAA1B8;
    private static final int CARD = 0xFF191620;

    private int panelX;
    private int panelY;
    private int panelW;
    private int panelH;

    private String status = "Выбери действие для профиля.";

    private final String[] slots = {
        "config1",
        "config2",
        "config3"
    };

    public ConfigManagerScreen() {
        super(Component.literal("ZhanVisuals Config Manager"));
    }

    @Override
    protected void init() {
        panelW = Math.min(420, width - 24);
        panelH = Math.min(250, height - 24);

        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
    }

    private void rounded(
        GuiGraphics g,
        int x,
        int y,
        int w,
        int h,
        int color
    ) {
        g.fill(x + 3, y, x + w - 3, y + h, color);
        g.fill(x, y + 3, x + w, y + h - 3, color);
    }

    private void draw(
        GuiGraphics g,
        String text,
        int x,
        int y,
        int color
    ) {
        g.drawString(font, text, x, y, color, false);
    }

    @Override
    public void render(
        GuiGraphics g,
        int mouseX,
        int mouseY,
        float partialTick
    ) {
        g.fill(0, 0, width, height, 0xAA05040A);

        rounded(
            g,
            panelX - 1,
            panelY - 1,
            panelW + 2,
            panelH + 2,
            0xFF49375D
        );

        rounded(
            g,
            panelX,
            panelY,
            panelW,
            panelH,
            0xF00D0C12
        );

        draw(
            g,
            "ZHANVISUALS",
            panelX + 18,
            panelY + 15,
            LIGHT
        );

        draw(
            g,
            "CONFIG MANAGER",
            panelX + 18,
            panelY + 31,
            PURPLE
        );

        draw(
            g,
            "Профили сохраняют настройки и позиции HUD.",
            panelX + 18,
            panelY + 51,
            MUTED
        );

        List<String> configs = ConfigManager.getConfigs();

        for (int i = 0; i < slots.length; i++) {
            int y = panelY + 76 + i * 43;

            rounded(
                g,
                panelX + 14,
                y,
                panelW - 28,
                36,
                CARD
            );

            String name = "Config " + (i + 1);
            boolean exists = configs.contains(slots[i]);

            draw(
                g,
                name,
                panelX + 24,
                y + 13,
                LIGHT
            );

            draw(
                g,
                exists ? "СОХРАНЁН" : "ПУСТО",
                panelX + 100,
                y + 13,
                exists ? PURPLE : MUTED
            );

            draw(
                g,
                "SAVE",
                panelX + panelW - 155,
                y + 13,
                LIGHT
            );

            draw(
                g,
                "LOAD",
                panelX + panelW - 105,
                y + 13,
                exists ? PURPLE : MUTED
            );

            draw(
                g,
                "DEL",
                panelX + panelW - 55,
                y + 13,
                exists ? 0xFFFF7777 : MUTED
            );
        }

        g.fill(
            panelX + 14,
            panelY + panelH - 39,
            panelX + panelW - 14,
            panelY + panelH - 38,
            0xFF393044
        );

        draw(
            g,
            status,
            panelX + 18,
            panelY + panelH - 26,
            MUTED
        );

        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(
        MouseButtonEvent event,
        boolean doubleClick
    ) {
        double mx = event.x();
        double my = event.y();

        for (int i = 0; i < slots.length; i++) {
            int y = panelY + 76 + i * 43;

            if (
                mx < panelX + 14 ||
                mx > panelX + panelW - 14 ||
                my < y ||
                my > y + 36
            ) {
                continue;
            }

            String slot = slots[i];
            int saveX = panelX + panelW - 165;
            int loadX = panelX + panelW - 115;
            int deleteX = panelX + panelW - 65;

            if (mx >= saveX && mx < loadX) {
                boolean result = ConfigManager.save(slot);
                status = result
                    ? "Конфиг сохранён!"
                    : "Ошибка сохранения.";
                return true;
            }

            if (mx >= loadX && mx < deleteX) {
                boolean result = ConfigManager.load(slot);
                status = result
                    ? "Конфиг загружен!"
                    : "Конфиг не найден.";
                return true;
            }

            if (mx >= deleteX) {
                boolean result = ConfigManager.delete(slot);
                status = result
                    ? "Конфиг удалён."
                    : "Не удалось удалить.";
                return true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
