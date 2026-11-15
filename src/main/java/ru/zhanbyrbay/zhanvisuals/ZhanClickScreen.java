
package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.MouseButtonEvent;

public class ZhanClickScreen extends Screen {

    private String category = "Visuals";

    private static final int PURPLE = 0xFFAA78FF;
    private static final int LIGHT = 0xFFF5F0FF;
    private static final int MUTED = 0xFF92899F;
    private static final int PANEL = 0xF00D0C12;
    private static final int CARD = 0xFF17151F;
    private static final int CARD_HOVER = 0xFF24202F;
    private static final int BORDER = 0xFF302A3B;

    private static final String[] CATEGORIES = {
        "Visuals", "HUD", "NoRender", "Performance"
    };

    private static final String[] ICONS = {
        "V", "H", "N", "P"
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
            "Keystrokes"
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
            "Performance Boost"
        }
    };

    private int panelX;
    private int panelY;
    private int panelW;
    private int panelH;

    private int sidebarW = 58;
    private int contentX;
    private int contentY;
    private int cardWidth;
    private int startY;

    public ZhanClickScreen() {
        super(Component.literal("ZhanVisuals"));
    }

    @Override
    protected void init() {
        panelW = Math.min(650, width - 24);
        panelH = Math.min(370, height - 24);

        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;

        contentX = panelX + sidebarW + 17;
        contentY = panelY + 65;

        cardWidth = Math.max(110, (panelW - sidebarW - 48) / 2);
        startY = contentY + 43;
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

    private void drawText(
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
        g.fill(0, 0, width, height, 0x9906050C);

        // Main window
        rounded(
            g,
            panelX - 1,
            panelY - 1,
            panelW + 2,
            panelH + 2,
            10,
            0xFF40334F
        );

        rounded(
            g,
            panelX,
            panelY,
            panelW,
            panelH,
            10,
            PANEL
        );

        // Header
        rounded(
            g,
            panelX + 1,
            panelY + 1,
            panelW - 2,
            48,
            9,
            0xFF111019
        );

        g.fill(
            panelX + 12,
            panelY + 48,
            panelX + panelW - 12,
            panelY + 49,
            0xFF292333
        );

        // Logo
        rounded(
            g,
            panelX + 15,
            panelY + 13,
            24,
            24,
            7,
            0xFF38234F
        );

        drawText(
            g,
            "Z",
            panelX + 23,
            panelY + 21,
            PURPLE
        );

        drawText(
            g,
            "ZHANVISUALS",
            panelX + 47,
            panelY + 13,
            LIGHT
        );

        drawText(
            g,
            "CLIENT  /  1.21.11",
            panelX + 47,
            panelY + 28,
            MUTED
        );

        // Search-like header decoration
        int searchW = Math.min(135, panelW / 3);
        int searchX = panelX + panelW - searchW - 15;

        rounded(
            g,
            searchX,
            panelY + 13,
            searchW,
            25,
            8,
            0xFF1B1824
        );

        drawText(
            g,
            "⌕",
            searchX + 9,
            panelY + 20,
            PURPLE
        );

        drawText(
            g,
            "Modules",
            searchX + 25,
            panelY + 21,
            MUTED
        );

        // Sidebar
        rounded(
            g,
            panelX + 9,
            panelY + 59,
            40,
            panelH - 70,
            8,
            0xFF121019
        );

        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = panelY + 72 + i * 45;
            boolean selected = category.equals(CATEGORIES[i]);

            if (selected) {
                rounded(
                    g,
                    panelX + 13,
                    y,
                    32,
                    32,
                    8,
                    0xFF39264F
                );

                rounded(
                    g,
                    panelX + 13,
                    y + 5,
                    3,
                    22,
                    2,
                    PURPLE
                );
            }

            String icon = ICONS[i];
            int color = selected ? LIGHT : MUTED;

            drawText(
                g,
                icon,
                panelX + 25,
                y + 12,
                color
            );
        }

        // Sidebar bottom label
        drawText(
            g,
            "Z",
            panelX + 27,
            panelY + panelH - 25,
            PURPLE
        );

        // Content title
        drawText(
            g,
            category,
            contentX,
            contentY,
            LIGHT
        );

        drawText(
            g,
            "Configure your modules",
            contentX,
            contentY + 16,
            MUTED
        );

        String[] modules = MODULES[getCategoryIndex()];

        for (int i = 0; i < modules.length; i++) {
            int column = i % 2;
            int row = i / 2;

            int x = contentX + column * (cardWidth + 9);
            int y = startY + row * 45;

            if (y + 35 > panelY + panelH - 30) {
                continue;
            }

            boolean enabled = ZhanVisuals.isEnabled(modules[i]);

            boolean hovered =
                mouseX >= x && mouseX <= x + cardWidth &&
                mouseY >= y && mouseY <= y + 35;

            rounded(
                g,
                x,
                y,
                cardWidth,
                35,
                8,
                hovered ? CARD_HOVER : CARD
            );

            // Subtle card border
            g.fill(
                x + 8,
                y + 6,
                x + cardWidth - 8,
                y + 7,
                BORDER
            );

            drawText(
                g,
                modules[i],
                x + 10,
                y + 14,
                enabled ? LIGHT : MUTED
            );

            // Toggle
            int sx = x + cardWidth - 34;
            int sy = y + 10;

            rounded(
                g,
                sx,
                sy,
                25,
                15,
                8,
                enabled ? 0xFF7545B5 : 0xFF403B49
            );

            rounded(
                g,
                enabled ? sx + 13 : sx + 2,
                sy + 2,
                11,
                11,
                6,
                0xFFFFFFFF
            );
        }

        // Footer
        int active = 0;

        for (String[] group : MODULES) {
            for (String module : group) {
                if (ZhanVisuals.isEnabled(module)) {
                    active++;
                }
            }
        }

        g.fill(
            contentX,
            panelY + panelH - 29,
            panelX + panelW - 14,
            panelY + panelH - 28,
            BORDER
        );

        drawText(
            g,
            "● " + active + " modules enabled",
            contentX,
            panelY + panelH - 19,
            PURPLE
        );

        String close = "RIGHT SHIFT  •  CLOSE";

        drawText(
            g,
            close,
            panelX + panelW - font.width(close) - 15,
            panelY + panelH - 19,
            MUTED
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
        double mouseX = event.x();
        double mouseY = event.y();

        // Category buttons
        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = panelY + 72 + i * 45;

            if (
                mouseX >= panelX + 10 &&
                mouseX <= panelX + 51 &&
                mouseY >= y - 2 &&
                mouseY <= y + 34
            ) {
                category = CATEGORIES[i];
                return true;
            }
        }

        // Module buttons
        String[] modules = MODULES[getCategoryIndex()];

        for (int i = 0; i < modules.length; i++) {
            int column = i % 2;
            int row = i / 2;

            int x = contentX + column * (cardWidth + 9);
            int y = startY + row * 45;

            if (
                mouseX >= x &&
                mouseX <= x + cardWidth &&
                mouseY >= y &&
                mouseY <= y + 35
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
