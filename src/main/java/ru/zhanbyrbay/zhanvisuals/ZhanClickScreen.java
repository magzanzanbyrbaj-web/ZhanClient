package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ZhanClickScreen extends Screen {

    public ZhanClickScreen() {
        super(Component.literal("ZhanVisuals 3.0"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(
            net.minecraft.client.gui.components.Button.builder(
                Component.literal("Close"),
                button -> this.onClose()
            ).bounds(centerX - 60, centerY + 65, 120, 20).build()
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        graphics.fill(
            centerX - 150,
            centerY - 100,
            centerX + 150,
            centerY + 100,
            0xEE151515
        );

        graphics.fill(
            centerX - 150,
            centerY - 100,
            centerX + 150,
            centerY - 97,
            0xFFFF3030
        );

        graphics.drawCenteredString(
            this.font,
            "ZHANVISUALS 3.0",
            centerX,
            centerY - 80,
            0xFFFF3030
        );

        graphics.drawCenteredString(
            this.font,
            "Visual Client",
            centerX,
            centerY - 55,
            0xFFFFFFFF
        );

        graphics.drawCenteredString(
            this.font,
            "Right Shift - Open Menu",
            centerX,
            centerY + 40,
            0xFFAAAAAA
        );

        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
