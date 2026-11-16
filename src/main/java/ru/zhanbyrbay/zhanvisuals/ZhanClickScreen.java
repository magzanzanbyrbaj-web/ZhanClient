package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ZhanClickScreen extends Screen {

    private String category = "Визуалы";

    private static final int PURPLE = 0xFFAA78FF;
    private static final int LIGHT = 0xFFF4EEFF;
    private static final int MUTED = 0xFFAAA1B8;
    private static final int PANEL = 0xF00D0C12;
    private static final int CARD = 0xFF191620;
    private static final int HOVER = 0xFF292235;

    private static final String[] CATEGORIES = {
        "Визуалы", "Интерфейс", "Комбат", "NoRender", "Движение", "Оптимизация", "Утилиты"
    };

    private static final String[][] MODULES = {
        {
            "HitColor",
            "CustomCrosshair",
            "Fullbright",
            "Low Fire"
        },
        {
            "Watermark",
            "FPS Counter",
            "Coordinates",
            "Armor HUD",
            "Totem Counter",
            "Ping Display",
            "Target HUD",
            "Keystrokes",
            "Speedometer"
        },
        {
            "AimAssist",
            "AttackAura",
            "AutoMace",
            "AutoSwap"
        },
        {
            "No Particles",
            "No Weather",
            "No Pumpkin",
            "NoBossBar",
            "No Hurt Camera",
            "No Fluid"
        },
        {
            "Zoom",
            "AutoSprint"
        },
        {
            "Performance Boost"
        },
        {
            "AntiSpam",
            "AutoInvis"
        }
    };

    private static final String[][] DESCRIPTIONS = {
        {
            "Изменяет цвет при наведении на цель.",
            "Заменяет стандартный прицел на компактный.",
            "Увеличивает яркость мира.",
            "Уменьшает видимую часть огня."
        },
        {
            "Показывает название клиента, FPS и ping.",
            "Отображает текущий FPS.",
            "Показывает координаты игрока.",
            "Показывает надетую броню.",
            "Считает тотемы в инвентаре.",
            "Показывает задержку соединения.",
            "Показывает здоровье существа под прицелом.",
            "Отображает нажатия WASD и кнопок мыши.",
            "Показывает скорость передвижения."
        },
        {
            "Плавная доводка до ближайшей цели.",
            "Автоматическая атака врагов вокруг.",
            "Автоматический выбор булавы при падении.",
            "Быстрый обмен предметов с офхендом."
        },
        {
            "Отключает отображение частиц.",
            "Отключает отображение погоды.",
            "Скрывает текстуру тыквы.",
            "Скрывает боссбар.",
            "Отключает эффект тряски камеры.",
            "Отключает визуальные эффекты жидкостей."
        },
        {
            "При удержании C приближает изображение.",
            "Автоматически включает бег при движении вперёд."
        },
        {
            "Уменьшает дальность прорисовки и симуляции."
        },
        {
            "Предотвращает отправку одинаковых сообщений в чат.",
            "Отслеживает эффект невидимости."
        }
    };

    private int panelX;
    private int panelY;
    private int panelW;
    private int panelH;

    private int contentX;
    private int contentY;
    private int sidebarW;
    private int cardWidth;
    private int startY;

    private String hoveredDescription =
        "Наведите на модуль, чтобы увидеть описание.";

    public ZhanClickScreen() {
        super(Component.literal("ZhanVisuals"));
    }

    @Override
    protected void init() {
        panelW = Math.min(560, width - 24);
        panelH = Math.min(320, height - 24);

        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;

        sidebarW = Math.max(88, Math.min(104, panelW / 5));

        contentX = panelX + sidebarW + 15;
        contentY = panelY + 59;

        cardWidth = Math.max(100, (panelW - sidebarW - 42) / 2);
        startY = contentY + 42;
    }

    private void rounded(
        GuiGraphics g,
        int x,
        int y,
        int w,
        int h,
        int radius,
        int color
    ) {
        radius = Math.min(radius, Math.min(w / 2, h / 2));

        for (int row = 0; row < h; row++) {
            int inset = 0;

            if (row < radius) {
                double dy = radius - row - 0.5;
                inset = radius - (int)Math.sqrt(
                    Math.max(0, radius * radius - dy * dy)
                );
            } else if (row >= h - radius) {
                double dy = row - (h - radius) + 0.5;
                inset = radius - (int)Math.sqrt(
                    Math.max(0, radius * radius - dy * dy)
                );
            }

            g.fill(
                x + inset,
                y + row,
                x + w - inset,
                y + row + 1,
                color
            );
        }
    }

    private void draw(
        String text,
        GuiGraphics g,
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
        g.fill(0, 0, width, height, 0x9906050C);

        rounded(
            g, panelX - 1, panelY - 1,
            panelW + 2, panelH + 2, 10, 0xFF49375D
        );

        rounded(
            g, panelX, panelY,
            panelW, panelH, 10, PANEL
        );

        // HEADER
        rounded(
            g, panelX + 1, panelY + 1,
            panelW - 2, 43, 9, 0xFF14111C
        );

        rounded(
            g, panelX + 13, panelY + 10,
            23, 23, 7, 0xFF39234F
        );

        draw("Z", g, panelX + 21, panelY + 18, PURPLE);
        draw("ZHANVISUALS", g, panelX + 44, panelY + 10, LIGHT);
        draw("КЛИЕНТ  /  1.21.11", g, panelX + 44, panelY + 25, MUTED);

        // CONFIG BUTTON
        rounded(
            g,
            panelX + panelW - 112,
            panelY + 8,
            98,
            27,
            6,
            0xFF352440
        );

        draw(
            "CONFIGS",
            g,
            panelX + panelW - 101,
            panelY + 18,
            PURPLE
        );

        // SIDEBAR
        rounded(
            g,
            panelX + 8,
            panelY + 51,
            sidebarW - 13,
            panelH - 60,
            8,
            0xFF121019
        );

        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = panelY + 55 + i * 28;
            boolean selected = category.equals(CATEGORIES[i]);

            if (selected) {
                rounded(
                    g,
                    panelX + 12,
                    y,
                    sidebarW - 21,
                    25,
                    6,
                    0xFF352440
                );

                g.fill(
                    panelX + 12,
                    y + 4,
                    panelX + 14,
                    y + 21,
                    PURPLE
                );
            }

            draw(
                CATEGORIES[i],
                g,
                panelX + 20,
                y + 8,
                selected ? LIGHT : MUTED
            );
        }

        // CONTENT
        draw(category, g, contentX, contentY, LIGHT);

        draw(
            "Настройка модулей клиента",
            g,
            contentX,
            contentY + 15,
            MUTED
        );

        String[] modules = MODULES[getCategoryIndex()];
        String[] descriptions = DESCRIPTIONS[getCategoryIndex()];

        hoveredDescription =
            "Наведите на модуль, чтобы увидеть описание.";

        for (int i = 0; i < modules.length; i++) {
            int column = i % 2;
            int row = i / 2;

            int x = contentX + column * (cardWidth + 8);
            int y = startY + row * 33;

            if (y + 27 > panelY + panelH - 39) {
                continue;
            }

            boolean enabled = ZhanVisuals.isEnabled(modules[i]);

            boolean hovered =
                mouseX >= x && mouseX <= x + cardWidth &&
                mouseY >= y && mouseY <= y + 27;

            if (hovered) {
                hoveredDescription = descriptions[i];
            }

            rounded(
                g,
                x,
                y,
                cardWidth,
                27,
                6,
                hovered ? HOVER : CARD
            );

            draw(
                modules[i],
                g,
                x + 8,
                y + 10,
                enabled ? LIGHT : MUTED
            );

            int sx = x + cardWidth - 29;

            rounded(
                g,
                sx,
                y + 7,
                21,
                13,
                7,
                enabled ? 0xFF7545B5 : 0xFF403B49
            );

            rounded(
                g,
                enabled ? sx + 11 : sx + 2,
                y + 9,
                9,
                9,
                5,
                0xFFFFFFFF
            );
        }

        // DESCRIPTION
        g.fill(
            contentX,
            panelY + panelH - 39,
            panelX + panelW - 12,
            panelY + panelH - 38,
            0xFF393044
        );

        draw(
            hoveredDescription,
            g,
            contentX,
            panelY + panelH - 28,
            MUTED
        );

        String close = "RIGHT SHIFT — ЗАКРЫТЬ";

        draw(
            close,
            g,
            panelX + panelW - font.width(close) - 13,
            panelY + panelH - 13,
            PURPLE
        );
    }

    private int getCategoryIndex() {
        for (int i = 0; i < CATEGORIES.length; i++) {
            if (CATEGORIES[i].equals(category)) {
                return i;
            }
        }

        return 0;
    }

    @Override
    public boolean mouseClicked(
        MouseButtonEvent event,
        boolean doubleClick
    ) {
        double mx = event.x();
        double my = event.y();

        if (
            mx >= panelX + panelW - 112 &&
            mx <= panelX + panelW - 14 &&
            my >= panelY + 8 &&
            my <= panelY + 35
        ) {
            Minecraft.getInstance().setScreen(
                new ConfigManagerScreen()
            );

            return true;
        }

        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = panelY + 55 + i * 28;

            if (
                mx >= panelX + 8 &&
                mx <= panelX + sidebarW &&
                my >= y &&
                my <= y + 25
            ) {
                category = CATEGORIES[i];
                return true;
            }
        }

        String[] modules = MODULES[getCategoryIndex()];

        for (int i = 0; i < modules.length; i++) {
            int column = i % 2;
            int row = i / 2;

            int x = contentX + column * (cardWidth + 8);
            int y = startY + row * 33;

            if (
                mx >= x &&
                mx <= x + cardWidth &&
                my >= y &&
                my <= y + 27 &&
                y + 27 <= panelY + panelH - 39
            ) {
                ZhanVisuals.toggleModule(modules[i]);
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
