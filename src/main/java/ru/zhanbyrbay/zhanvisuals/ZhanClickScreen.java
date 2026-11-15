
package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ZhanClickScreen extends Screen {

    private static final String[] CATEGORIES = {
        "Visuals",
        "HUD",
        "NoRender",
        "Performance"
    };

    private static final String[][] MODULES = {
        {
            "HitColor",
            "CustomCrosshair",
            "Fullbright"
        },
        {
            "FPS Counter",
            "Coordinates",
            "Armor HUD",
            "Totem Counter",
            "Ping Display",
            "Target HUD",
            "Keystrokes"
        },
        {
            "Low Fire",
            "No Particles",
            "No Weather",
            "No Pumpkin",
            "No Boss Bar",
            "No Hurt Camera"
        },
        {
            "Performance Boost"
        }
    };

    private int selectedCategory = 0;
    private int panelX;
    private int panelY;
    private int panelW;
    private int panelH;

    public ZhanClickScreen() {
        super(Component.literal("ZhanVisuals 3.1"));
    }

    private void drawText(
        GuiGraphics g,
        String text,
        int x,
        int y,
        int color
    ) {
        Component component = Component.literal(text)
            .withStyle(style -> style.withFont(ZhanVisuals.id("ui")));

        g.drawString(font, component, x, y, color, false);
    }

    private void calculatePanel() {
        panelW = Math.min(650, width - 20);
        panelH = Math.min(320, height - 20);

        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
    }

    @Override
    public void render(
        GuiGraphics g,
        int mouseX,
        int mouseY,
        float delta
    ) {
        calculatePanel();

        int x = panelX;
        int y = panelY;
        int w = panelW;
        int h = panelH;

        // Background
        g.fill(0, 0, width, height, 0xB8000000);

        // Main window
        g.fill(x, y, x + w, y + h, 0xFF0D1018);

        // Top red accent
        g.fill(x, y, x + w, y + 3, 0xFFFF3548);

        // Sidebar
        g.fill(x, y + 3, x + 125, y + h, 0xFF11151F);

        // Logo
        drawText(g, "ZHAN", x + 15, y + 15, 0xFFFF4558);
        drawText(g, "VISUALS", x + 48, y + 15, 0xFFFFFFFF);
        drawText(g, "3.1", x + 15, y + 31, 0xFF777F91);

        g.fill(x + 12, y + 48, x + 112, y + 49, 0xFF292E3B);

        // Categories
        for (int i = 0; i < CATEGORIES.length; i++) {
            int cy = y + 64 + i * 38;

            boolean selected = selectedCategory == i;
            boolean hovered =
                mouseX >= x + 8 &&
                mouseX <= x + 117 &&
                mouseY >= cy - 8 &&
                mouseY <= cy + 20;

            if (selected) {
                g.fill(
                    x + 8, cy - 8,
                    x + 117, cy + 20,
                    0xFF29202A
                );

                g.fill(
                    x + 8, cy - 8,
                    x + 11, cy + 20,
                    0xFFFF3548
                );
            } else if (hovered) {
                g.fill(
                    x + 8, cy - 8,
                    x + 117, cy + 20,
                    0xFF202530
                );
            }

            drawText(
                g,
                CATEGORIES[i],
                x + 20,
                cy,
                selected ? 0xFFFF6876 : 0xFFB7BDCA
            );
        }

        drawText(
            g,
            "Zhanbyrbay",
            x + 14,
            y + h - 21,
            0xFF747D8F
        );

        // Content
        int contentX = x + 143;

        drawText(
            g,
            CATEGORIES[selectedCategory].toUpperCase(),
            contentX,
            y + 17,
            0xFFFFFFFF
        );

        drawText(
            g,
            "Customize your experience",
            contentX,
            y + 33,
            0xFF858D9E
        );

        String[] modules = MODULES[selectedCategory];

        int cardW = Math.min(220, (w - 175) / 2);
        int cardH = 43;
        int gapX = 12;
        int gapY = 10;

        for (int i = 0; i < modules.length; i++) {
            int col = i % 2;
            int row = i / 2;

            int cx = contentX + col * (cardW + gapX);
            int cy = y + 53 + row * (cardH + gapY);

            boolean active = ZhanVisuals.isEnabled(modules[i]);

            boolean hovered =
                mouseX >= cx &&
                mouseX <= cx + cardW &&
                mouseY >= cy &&
                mouseY <= cy + cardH;

            // Card
            g.fill(
                cx, cy,
                cx + cardW, cy + cardH,
                hovered ? 0xFF252B38 : 0xFF191E29
            );

            // Accent
            g.fill(
                cx, cy,
                cx + 2, cy + cardH,
                active ? 0xFFFF3548 : 0xFF343B49
            );

            drawText(
                g,
                modules[i],
                cx + 10,
                cy + 8,
                0xFFE9EBF2
            );

            drawText(
                g,
                active ? "ENABLED" : "DISABLED",
                cx + 10,
                cy + 25,
                active ? 0xFFFF6574 : 0xFF777F90
            );

            // Toggle
            int tx = cx + cardW - 37;
            int ty = cy + 15;

            g.fill(
                tx, ty,
                tx + 25, ty + 13,
                active ? 0xFFB92D42 : 0xFF343B49
            );

            int knobX = active ? tx + 14 : tx + 2;

            g.fill(
                knobX, ty + 2,
                knobX + 9, ty + 11,
                0xFFFFFFFF
            );
        }

        drawText(
            g,
            "RIGHT SHIFT  -  CLOSE",
            contentX,
            y + h - 18,
            0xFF626C7E
        );
    }

    @Override
    public boolean mouseClicked(
        MouseButtonEvent event,
        boolean doubleClicked
    ) {
        if (event.button() != 0) {
            return super.mouseClicked(event, doubleClicked);
        }

        calculatePanel();

        double mx = event.x();
        double my = event.y();

        int x = panelX;
        int y = panelY;
        int w = panelW;

        // Category clicks
        for (int i = 0; i < CATEGORIES.length; i++) {
            int cy = y + 64 + i * 38;

            if (
                mx >= x + 8 &&
                mx <= x + 117 &&
                my >= cy - 8 &&
                my <= cy + 20
            ) {
                selectedCategory = i;
                return true;
            }
        }

        // Module clicks
        int contentX = x + 143;
        int cardW = Math.min(220, (w - 175) / 2);
        int cardH = 43;
        int gapX = 12;
        int gapY = 10;

        String[] modules = MODULES[selectedCategory];

        for (int i = 0; i < modules.length; i++) {
            int col = i % 2;
            int row = i / 2;

            int cx = contentX + col * (cardW + gapX);
            int cy = y + 53 + row * (cardH + gapY);

            if (
                mx >= cx &&
                mx <= cx + cardW &&
                my >= cy &&
                my <= cy + cardH
            ) {
                ZhanVisuals.toggleModule(modules[i]);
                return true;
            }
        }

        return super.mouseClicked(event, doubleClicked);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
