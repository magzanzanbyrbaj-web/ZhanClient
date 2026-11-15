
package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ZhanClickScreen extends Screen {

    private static final String[] CATEGORIES = {
        "Visuals",
        "HUD",
        "Settings"
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
            "Totem Counter"
        },
        {
            "Performance Boost"
        }
    };

    private int selectedCategory = 0;

    private int panelX;
    private int panelY;

    public ZhanClickScreen() {
        super(Component.literal("ZhanVisuals 3.0"));
    }

    @Override
    public void render(
        GuiGraphics g,
        int mouseX,
        int mouseY,
        float delta
    ) {

        // Background without blur
        g.fill(
            0, 0, width, height,
            0x9905080D
        );

        int w = Math.min(600, width - 24);
        int h = Math.min(310, height - 24);

        panelX = (width - w) / 2;
        panelY = (height - h) / 2;

        int x = panelX;
        int y = panelY;

        // Main panel
        g.fill(
            x, y,
            x + w, y + h,
            0xF20C0E14
        );

        // Red line
        g.fill(
            x, y,
            x + w, y + 3,
            0xFFFF3030
        );

        // Sidebar
        g.fill(
            x, y + 3,
            x + 135, y + h,
            0xFF11141C
        );

        // Logo
        g.drawString(
            font,
            "ZHANVISUALS",
            x + 15, y + 17,
            0xFFFF414D,
            false
        );

        g.drawString(
            font,
            "3.0",
            x + 105, y + 17,
            0xFF777D8B,
            false
        );

        g.fill(
            x + 14, y + 37,
            x + 120, y + 38,
            0xFF292D38
        );

        // Categories
        for (int i = 0; i < CATEGORIES.length; i++) {

            int cy = y + 55 + i * 38;

            boolean selected = selectedCategory == i;

            boolean hovered =
                mouseX >= x + 9 &&
                mouseX <= x + 126 &&
                mouseY >= cy - 9 &&
                mouseY <= cy + 20;

            if (selected) {

                g.fill(
                    x + 9, cy - 9,
                    x + 126, cy + 20,
                    0xFF29202A
                );

                g.fill(
                    x + 9, cy - 9,
                    x + 12, cy + 20,
                    0xFFFF3548
                );

            } else if (hovered) {

                g.fill(
                    x + 9, cy - 9,
                    x + 126, cy + 20,
                    0xFF1B1E28
                );
            }

            g.drawString(
                font,
                CATEGORIES[i],
                x + 21,
                cy,
                selected ? 0xFFFF5967 : 0xFFB5B8C3,
                false
            );
        }

        // Footer
        g.drawString(
            font,
            "Zhanbyrbay",
            x + 15,
            y + h - 23,
            0xFF777D8B,
            false
        );

        // Content title
        int contentX = x + 155;

        g.drawString(
            font,
            CATEGORIES[selectedCategory].toUpperCase(),
            contentX,
            y + 20,
            0xFFFFFFFF,
            false
        );

        g.drawString(
            font,
            "Customize your experience",
            contentX,
            y + 37,
            0xFF858B99,
            false
        );

        // Module cards
        String[] modules = MODULES[selectedCategory];

        int cardW = Math.min(190, (w - 190) / 2);
        int cardH = 53;

        for (int i = 0; i < modules.length; i++) {

            int col = i % 2;
            int row = i / 2;

            int cx = contentX + col * (cardW + 12);
            int cy = y + 65 + row * 65;

            boolean active =
                ZhanVisuals.isEnabled(modules[i]);

            boolean hovered =
                mouseX >= cx &&
                mouseX <= cx + cardW &&
                mouseY >= cy &&
                mouseY <= cy + cardH;

            // Card
            g.fill(
                cx, cy,
                cx + cardW, cy + cardH,
                hovered ? 0xFF242936 : 0xFF191D27
            );

            // Accent
            g.fill(
                cx, cy,
                cx + 2, cy + cardH,
                active ? 0xFFFF3548 : 0xFF343946
            );

            // Module name
            g.drawString(
                font,
                modules[i],
                cx + 12,
                cy + 12,
                0xFFE8EAF0,
                false
            );

            // Toggle
            int tx = cx + cardW - 37;
            int ty = cy + 31;

            g.fill(
                tx, ty,
                tx + 25, ty + 12,
                active ? 0xFFB82D42 : 0xFF343946
            );

            int knobX = active ? tx + 15 : tx + 2;

            g.fill(
                knobX, ty + 2,
                knobX + 8, ty + 10,
                0xFFFFFFFF
            );

            g.drawString(
                font,
                active ? "ON" : "OFF",
                cx + 12,
                cy + 32,
                active ? 0xFFFF6672 : 0xFF777D8B,
                false
            );
        }

        g.drawString(
            font,
            "RIGHT SHIFT / CLOSE",
            contentX,
            y + h - 19,
            0xFF626977,
            false
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

        double mouseX = event.x();
        double mouseY = event.y();

        int x = panelX;
        int y = panelY;
        int w = Math.min(600, width - 24);

        // Category clicks
        for (int i = 0; i < CATEGORIES.length; i++) {

            int cy = y + 55 + i * 38;

            if (
                mouseX >= x + 9 &&
                mouseX <= x + 126 &&
                mouseY >= cy - 9 &&
                mouseY <= cy + 20
            ) {
                selectedCategory = i;
                return true;
            }
        }

        // Module clicks
        int contentX = x + 155;
        int cardW = Math.min(190, (w - 190) / 2);
        int cardH = 53;

        String[] modules = MODULES[selectedCategory];

        for (int i = 0; i < modules.length; i++) {

            int col = i % 2;
            int row = i / 2;

            int cx = contentX + col * (cardW + 12);
            int cy = y + 65 + row * 65;

            if (
                mouseX >= cx &&
                mouseX <= cx + cardW &&
                mouseY >= cy &&
                mouseY <= cy + cardH
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
