
package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.MouseButtonEvent;

public class ZhanClickScreen extends Screen {

    private String category = "Visuals";

    private static final int PURPLE = 0xFFAA70FF;
    private static final int DARK_PURPLE = 0xFF7040B8;
    private static final int PANEL = 0xF011101A;
    private static final int CARD = 0xFF211D2D;
    private static final int TEXT = 0xFFF3EDFF;
    private static final int MUTED = 0xFF958AA8;

    private static final String[] CATEGORIES = {
        "Visuals", "HUD", "NoRender", "Performance"
    };

    private static final String[][] MODULES = {
        {
            "HitColor",
            "CustomCrosshair",
            "Fullbright"
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
            "Low Fire",
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

    public ZhanClickScreen() {
        super(Component.literal("ZhanVisuals Premium"));
    }

    @Override
    protected void init() {
        panelW = Math.min(650, this.width - 20);
        panelH = Math.min(370, this.height - 20);

        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;
    }

    @Override
    public void render(
        GuiGraphics g,
        int mouseX,
        int mouseY,
        float partialTick
    ) {
        g.fill(0, 0, width, height, 0xB9000000);

        // Main window
        g.fill(panelX, panelY, panelX + panelW, panelY + panelH, PANEL);

        // Header
        g.fill(panelX, panelY, panelX + panelW, panelY + 49, 0xFF191522);
        g.fill(panelX, panelY + 48, panelX + panelW, panelY + 50, PURPLE);

        // Logo
        g.fill(panelX + 17, panelY + 13, panelX + 21, panelY + 36, PURPLE);

        g.drawString(
            font,
            "ZHAN",
            panelX + 29,
            panelY + 13,
            TEXT
        );

        g.drawString(
            font,
            "VISUALS",
            panelX + 66,
            panelY + 13,
            PURPLE
        );

        g.drawString(
            font,
            "PREMIUM 4.0",
            panelX + 29,
            panelY + 29,
            MUTED
        );

        String version = "1.21.11";
        g.drawString(
            font,
            version,
            panelX + panelW - font.width(version) - 17,
            panelY + 20,
            MUTED
        );

        // Sidebar
        g.fill(
            panelX,
            panelY + 50,
            panelX + 145,
            panelY + panelH,
            0xFF17141F
        );

        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = panelY + 70 + i * 43;
            boolean selected = category.equals(CATEGORIES[i]);

            if (selected) {
                g.fill(
                    panelX + 10,
                    y - 8,
                    panelX + 135,
                    y + 25,
                    0xFF302440
                );

                g.fill(
                    panelX + 10,
                    y - 8,
                    panelX + 13,
                    y + 25,
                    PURPLE
                );
            }

            g.drawString(
                font,
                CATEGORIES[i],
                panelX + 23,
                y + 3,
                selected ? PURPLE : MUTED
            );
        }

        // Sidebar footer
        g.drawString(
            font,
            "Zhanbyrbayy",
            panelX + 16,
            panelY + panelH - 20,
            MUTED
        );

        // Content area
        int contentX = panelX + 165;
        int contentY = panelY + 70;

        g.drawString(
            font,
            category.toUpperCase(),
            contentX,
            contentY,
            TEXT
        );

        g.drawString(
            font,
            "Manage your client modules",
            contentX,
            contentY + 17,
            MUTED
        );

        String[] modules = MODULES[getCategoryIndex()];

        int cardWidth = Math.max(150, (panelW - 190) / 2);
        int startY = contentY + 43;

        for (int i = 0; i < modules.length; i++) {
            int column = i % 2;
            int row = i / 2;

            int x = contentX + column * (cardWidth + 10);
            int y = startY + row * 48;

            boolean enabled = ZhanVisuals.isEnabled(modules[i]);
            boolean hovered =
                mouseX >= x && mouseX <= x + cardWidth &&
                mouseY >= y && mouseY <= y + 38;

            int background = hovered ? 0xFF30283D : CARD;

            g.fill(x, y, x + cardWidth, y + 38, background);

            // Card accent
            g.fill(
                x,
                y + 5,
                x + 2,
                y + 33,
                enabled ? PURPLE : 0xFF51485E
            );

            g.drawString(
                font,
                modules[i],
                x + 10,
                y + 14,
                enabled ? TEXT : MUTED
            );

            // Switch
            int switchX = x + cardWidth - 39;
            int switchColor = enabled ? DARK_PURPLE : 0xFF494451;

            g.fill(
                switchX,
                y + 10,
                switchX + 29,
                y + 28,
                switchColor
            );

            int knobX = enabled ? switchX + 15 : switchX + 2;

            g.fill(
                knobX,
                y + 12,
                knobX + 12,
                y + 26,
                enabled ? 0xFFFFFFFF : 0xFFB8B1C3
            );
        }

        // Bottom status
        int enabledCount = 0;
        for (String[] group : MODULES) {
            for (String module : group) {
                if (ZhanVisuals.isEnabled(module)) {
                    enabledCount++;
                }
            }
        }

        String status = "ACTIVE MODULES: " + enabledCount;

        g.drawString(
            font,
            status,
            contentX,
            panelY + panelH - 20,
            PURPLE
        );

        String close = "RIGHT SHIFT  |  CLOSE";

        g.drawString(
            font,
            close,
            panelX + panelW - font.width(close) - 17,
            panelY + panelH - 20,
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

        // Category selection
        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = panelY + 70 + i * 43;

            if (
                mouseX >= panelX + 10 &&
                mouseX <= panelX + 135 &&
                mouseY >= y - 8 &&
                mouseY <= y + 25
            ) {
                category = CATEGORIES[i];
                return true;
            }
        }

        // Module toggles
        String[] modules = MODULES[getCategoryIndex()];

        int contentX = panelX + 165;
        int contentY = panelY + 70;
        int cardWidth = Math.max(150, (panelW - 190) / 2);
        int startY = contentY + 43;

        for (int i = 0; i < modules.length; i++) {
            int column = i % 2;
            int row = i / 2;

            int x = contentX + column * (cardWidth + 10);
            int y = startY + row * 48;

            if (
                mouseX >= x &&
                mouseX <= x + cardWidth &&
                mouseY >= y &&
                mouseY <= y + 38
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
