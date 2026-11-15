
package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HudEditorScreen extends Screen {

    private String dragging = null;

    private int offsetX;
    private int offsetY;

    private int watermarkX = 8;
    private int watermarkY = 8;

    private int targetX = 8;
    private int targetY = 45;

    private int armorX = 8;
    private int armorY = 90;

    private int speedX = 8;
    private int speedY = 130;

    public HudEditorScreen() {
        super(Component.literal("ZhanVisuals HUD Editor"));
    }

    @Override
    protected void init() {
        super.init();
        ZhanHud.setEditing(true);
    }

    @Override
    public void render(
        GuiGraphics graphics,
        int mouseX,
        int mouseY,
        float delta
    ) {
        graphics.fill(
            0,
            0,
            this.width,
            this.height,
            0xCC08060D
        );

        graphics.drawCenteredString(
            this.font,
            "ZhanVisuals | HUD Editor",
            this.width / 2,
            12,
            0xFFE4B5FF
        );

        graphics.drawCenteredString(
            this.font,
            "Зажми ЛКМ на элементе и перетащи его",
            this.width / 2,
            27,
            0xFFBBBBBB
        );

        drawWatermark(graphics);
        drawTarget(graphics);
        drawArmor(graphics);
        drawSpeed(graphics);

        graphics.drawCenteredString(
            this.font,
            "ESC — выйти",
            this.width / 2,
            this.height - 18,
            0xFFAAAAAA
        );

        super.render(graphics, mouseX, mouseY, delta);
    }

    private void drawWatermark(GuiGraphics g) {
        g.fill(
            watermarkX,
            watermarkY,
            watermarkX + 125,
            watermarkY + 22,
            0xDD17131F
        );

        g.fill(
            watermarkX,
            watermarkY,
            watermarkX + 125,
            watermarkY + 2,
            0xFFB66BFF
        );

        g.drawString(
            font,
            "ZhanVisuals | 120 FPS",
            watermarkX + 7,
            watermarkY + 7,
            0xFFFFFFFF
        );
    }

    private void drawTarget(GuiGraphics g) {
        g.fill(
            targetX,
            targetY,
            targetX + 150,
            targetY + 48,
            0xDD17131F
        );

        g.fill(
            targetX,
            targetY,
            targetX + 150,
            targetY + 2,
            0xFFB66BFF
        );

        g.drawString(
            font,
            "TargetHUD",
            targetX + 8,
            targetY + 8,
            0xFFFFFFFF
        );

        g.drawString(
            font,
            "HP: 20 / 20",
            targetX + 8,
            targetY + 22,
            0xFFFF7777
        );

        g.fill(
            targetX + 8,
            targetY + 36,
            targetX + 138,
            targetY + 40,
            0xFF39313F
        );

        g.fill(
            targetX + 8,
            targetY + 36,
            targetX + 138,
            targetY + 40,
            0xFFBD72FF
        );
    }

    private void drawArmor(GuiGraphics g) {
        g.fill(
            armorX,
            armorY,
            armorX + 100,
            armorY + 20,
            0xDD17131F
        );

        g.fill(
            armorX,
            armorY,
            armorX + 100,
            armorY + 2,
            0xFFB66BFF
        );

        g.drawString(
            font,
            "Armor HUD",
            armorX + 8,
            armorY + 7,
            0xFFFFFFFF
        );
    }

    private void drawSpeed(GuiGraphics g) {
        g.fill(
            speedX,
            speedY,
            speedX + 100,
            speedY + 22,
            0xDD17131F
        );

        g.fill(
            speedX,
            speedY,
            speedX + 100,
            speedY + 2,
            0xFFB66BFF
        );

        g.drawString(
            font,
            "Speed: 0.28",
            speedX + 7,
            speedY + 7,
            0xFFFFFFFF
        );
    }

    @Override
    public boolean mouseClicked(
        double mouseX,
        double mouseY,
        int button
    ) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (inside(mouseX, mouseY, watermarkX, watermarkY, 125, 22)) {
            dragging = "watermark";
            offsetX = (int) mouseX - watermarkX;
            offsetY = (int) mouseY - watermarkY;
            return true;
        }

        if (inside(mouseX, mouseY, targetX, targetY, 150, 48)) {
            dragging = "target";
            offsetX = (int) mouseX - targetX;
            offsetY = (int) mouseY - targetY;
            return true;
        }

        if (inside(mouseX, mouseY, armorX, armorY, 100, 20)) {
            dragging = "armor";
            offsetX = (int) mouseX - armorX;
            offsetY = (int) mouseY - armorY;
            return true;
        }

        if (inside(mouseX, mouseY, speedX, speedY, 100, 22)) {
            dragging = "speed";
            offsetX = (int) mouseX - speedX;
            offsetY = (int) mouseY - speedY;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(
        double mouseX,
        double mouseY,
        int button,
        double dragX,
        double dragY
    ) {
        if (dragging == null || button != 0) {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        int x = Math.max(0, Math.min(width - 100, (int) mouseX - offsetX));
        int y = Math.max(0, Math.min(height - 22, (int) mouseY - offsetY));

        switch (dragging) {
            case "watermark" -> {
                watermarkX = x;
                watermarkY = y;
                ZhanHud.setPosition("watermark", x, y);
            }

            case "target" -> {
                targetX = x;
                targetY = y;
                ZhanHud.setPosition("target", x, y);
            }

            case "armor" -> {
                armorX = x;
                armorY = y;
                ZhanHud.setPosition("armor", x, y);
            }

            case "speed" -> {
                speedX = x;
                speedY = y;
                ZhanHud.setPosition("speed", x, y);
            }
        }

        return true;
    }

    @Override
    public boolean mouseReleased(
        double mouseX,
        double mouseY,
        int button
    ) {
        if (button == 0) {
            dragging = null;
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean inside(
        double mouseX,
        double mouseY,
        int x,
        int y,
        int width,
        int height
    ) {
        return mouseX >= x
            && mouseX <= x + width
            && mouseY >= y
            && mouseY <= y + height;
    }

    @Override
    public void onClose() {
        ZhanHud.setEditing(false);
        super.onClose();
    }
}
