
package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.MouseButtonEvent;

public class ZhanClickScreen extends Screen {

    private String category = "Visuals";

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

    private int panelX;
    private int panelY;
    private int panelW = 650;
    private int panelH = 320;

    public ZhanClickScreen() {
        super(Component.literal("ZhanVisuals"));
    }

    @Override
    protected void init() {
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
        g.fill(0, 0, width, height, 0x99000000);

        // Main panel
        g.fill(
            panelX,
            panelY,
            panelX + panelW,
            panelY + panelH,
            0xFF111116
        );

        // Header
        g.fill(
            panelX,
            panelY,
            panelX + panelW,
            panelY + 42,
            0xFF191920
        );

        g.fill(
            panelX,
            panelY + 41,
            panelX + panelW,
            panelY + 43,
            0xFFE53945
        );

        g.drawString(
            this.font,
            "ZHANVISUALS",
            panelX + 18,
            panelY + 15,
            0xFFFF4654
        );

        g.drawString(
            this.font,
            "3.1",
            panelX + panelW - 35,
            panelY + 15,
            0xFF888888
        );

        // Sidebar
        g.fill(
            panelX,
            panelY + 43,
            panelX + 145,
            panelY + panelH,
            0xFF15151B
        );

        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = panelY + 58 + i * 42;

            boolean selected = category.equals(CATEGORIES[i]);

            if (selected) {
                g.fill(
                    panelX + 10,
                    y - 7,
                    panelX + 135,
                    y + 25,
                    0xFF292027
                );

                g.fill(
                    panelX + 10,
                    y - 7,
                    panelX + 13,
                    y + 25,
                    0xFFFF4654
                );
            }

            g.drawString(
                this.font,
                CATEGORIES[i],
                panelX + 23,
                y + 3,
                selected ? 0xFFFF5965 : 0xFFAAAAAA
            );
        }

        // Content
        g.drawString(
            this.font,
            category.toUpperCase(),
            panelX + 165,
            panelY + 60,
            0xFFFFFFFF
        );

        g.drawString(
            this.font,
            "Click module to enable or disable",
            panelX + 165,
            panelY + 77,
            0xFF777777
        );

        int categoryIndex = getCategoryIndex();
        String[] modules = MODULES[categoryIndex];

        int startX = panelX + 165;
        int startY = panelY + 105;

        for (int i = 0; i < modules.length; i++) {
            int column = i % 2;
            int row = i / 2;

            int x = startX + column * 225;
            int y = startY + row * 48;

            boolean enabled = ZhanVisuals.isEnabled(modules[i]);

            g.fill(
                x,
                y,
                x + 210,
                y + 38,
                0xFF202027
            );

            g.drawString(
                this.font,
                modules[i],
                x + 10,
                y + 14,
                0xFFE5E5E5
            );

            int switchColor = enabled
                ? 0xFFFF4654
                : 0xFF55555D;

            g.fill(
                x + 174,
                y + 10,
                x + 200,
                y + 28,
                switchColor
            );

            int circleX = enabled
                ? x + 185
                : x + 176;

            g.fill(
                circleX,
                y + 12,
                circleX + 13,
                y + 26,
                0xFFFFFFFF
            );
        }

        // Footer
        g.drawString(
            this.font,
            "Zhanbyrbay",
            panelX + 18,
            panelY + panelH - 22,
            0xFF777777
        );

        g.drawString(
            this.font,
            "RIGHT SHIFT - CLOSE",
            panelX + panelW - 160,
            panelY + panelH - 22,
            0xFF777777
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
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();

        // Category buttons
        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = panelY + 58 + i * 42;

            if (
                mouseX >= panelX + 10 &&
                mouseX <= panelX + 135 &&
                mouseY >= y - 7 &&
                mouseY <= y + 25
            ) {
                category = CATEGORIES[i];
                return true;
            }
        }

        // Module buttons
        int categoryIndex = getCategoryIndex();
        String[] modules = MODULES[categoryIndex];

        int startX = panelX + 165;
        int startY = panelY + 105;

        for (int i = 0; i < modules.length; i++) {
            int column = i % 2;
            int row = i / 2;

            int x = startX + column * 225;
            int y = startY + row * 48;

            if (
                mouseX >= x &&
                mouseX <= x + 210 &&
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
