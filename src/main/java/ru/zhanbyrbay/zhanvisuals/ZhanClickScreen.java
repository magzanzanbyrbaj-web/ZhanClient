
package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;

import java.util.HashSet;
import java.util.Set;

public class ZhanClickScreen extends Screen {

    private static final Identifier FONT =
        Identifier.fromNamespaceAndPath("zhanvisuals", "ui");

    private static final String[] CATEGORIES = {
        "Combat",
        "Visuals",
        "HUD",
        "Player",
        "Settings"
    };

    private static final String[][] MODULES = {
        {"Hit Indicator", "Combo Counter", "CPS Counter"},
        {"Low Fire", "Fullbright", "Custom Crosshair", "Hit Particles"},
        {"FPS Counter", "Coordinates", "Armor HUD", "Totem Counter"},
        {"Item Physics", "Animations", "Motion Blur"},
        {"Interface", "Color Theme", "Configuration"}
    };

    private final Set<String> enabled = new HashSet<>();

    private int selectedCategory = 1;
    private int panelX;
    private int panelY;

    public ZhanClickScreen() {
        super(Component.literal("ZhanVisuals 3.0"));
    }

    private Component custom(String text) {
        return Component.literal(text)
            .withStyle(style ->
                style.withFont(new FontDescription.Resource(FONT))
            );
    }

    @Override
    public void render(
        GuiGraphics graphics,
        int mouseX,
        int mouseY,
        float delta
    ) {
        // Background without blur
        graphics.fill(
            0, 0, width, height,
            0x9905080D
        );

        int w = Math.min(600, width - 24);
        int h = Math.min(310, height - 24);

        panelX = (width - w) / 2;
        panelY = (height - h) / 2;

        int x = panelX;
        int y = panelY;

        // Main window
        graphics.fill(
            x, y, x + w, y + h,
            0xF20C0E14
        );

        // Red top accent
        graphics.fill(
            x, y, x + w, y + 3,
            0xFFFF3030
        );

        // Sidebar
        graphics.fill(
            x, y + 3, x + 135, y + h,
            0xFF11141C
        );

        // Logo
        graphics.drawString(
            font,
            custom("ZHANVISUALS"),
            x + 16, y + 17,
            0xFFFF414D,
            false
        );

        graphics.drawString(
            font,
            custom("3.0"),
            x + 103, y + 17,
            0xFF777D8B,
            false
        );

        graphics.fill(
            x + 14, y + 37,
            x + 120, y + 38,
            0xFF292D38
        );

        // Categories
        for (int i = 0; i < CATEGORIES.length; i++) {

            int cy = y + 51 + i * 35;

            boolean selected = selectedCategory == i;

            boolean hovered =
                mouseX >= x + 9 &&
                mouseX <= x + 126 &&
                mouseY >= cy - 9 &&
                mouseY <= cy + 20;

            if (selected) {
                graphics.fill(
                    x + 9, cy - 9,
                    x + 126, cy + 20,
                    0xFF29202A
                );

                graphics.fill(
                    x + 9, cy - 9,
                    x + 12, cy + 20,
                    0xFFFF3548
                );

            } else if (hovered) {
                graphics.fill(
                    x + 9, cy - 9,
                    x + 126, cy + 20,
                    0xFF1B1E28
                );
            }

            graphics.drawString(
                font,
                custom(CATEGORIES[i]),
                x + 21,
                cy,
                selected ? 0xFFFF5967 : 0xFFB5B8C3,
                false
            );
        }

        // Sidebar footer
        graphics.drawString(
            font,
            custom("Zhanbyrbay"),
            x + 15,
            y + h - 23,
            0xFF777D8B,
            false
        );

        // Main content
        int contentX = x + 155;

        graphics.drawString(
            font,
            custom(CATEGORIES[selectedCategory].toUpperCase()),
            contentX,
            y + 20,
            0xFFFFFFFF,
            false
        );

        graphics.drawString(
            font,
            custom("Customize your experience"),
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

            boolean active = enabled.contains(modules[i]);

            boolean hovered =
                mouseX >= cx &&
                mouseX <= cx + cardW &&
                mouseY >= cy &&
                mouseY <= cy + cardH;

            // Card
            graphics.fill(
                cx, cy,
                cx + cardW, cy + cardH,
                hovered ? 0xFF242936 : 0xFF191D27
            );

            // Accent
            graphics.fill(
                cx, cy,
                cx + 2, cy + cardH,
                active ? 0xFFFF3548 : 0xFF343946
            );

            // Module name
            graphics.drawString(
                font,
                custom(modules[i]),
                cx + 12,
                cy + 12,
                0xFFE8EAF0,
                false
            );

            // Toggle
            int tx = cx + cardW - 37;
            int ty = cy + 31;

            graphics.fill(
                tx, ty,
                tx + 25, ty + 12,
                active ? 0xFFB82D42 : 0xFF343946
            );

            int knobX = active ? tx + 15 : tx + 2;

            graphics.fill(
                knobX, ty + 2,
                knobX + 8, ty + 10,
                0xFFFFFFFF
            );

            graphics.drawString(
                font,
                custom(active ? "ON" : "OFF"),
                cx + 12,
                cy + 32,
                active ? 0xFFFF6672 : 0xFF777D8B,
                false
            );
        }

        // Bottom hint
        graphics.drawString(
            font,
            custom("RIGHT SHIFT / CLOSE"),
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

            int cy = y + 51 + i * 35;

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
                String name = modules[i];

                if (enabled.contains(name)) {
                    enabled.remove(name);
                } else {
                    enabled.add(name);
                }

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
