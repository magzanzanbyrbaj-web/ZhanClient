
package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HudEditorScreen extends Screen {

    private String dragging = null;

    private int offsetX;
    private int offsetY;

    private int watermarkX;
    private int watermarkY;

    private int targetX;
    private int targetY;

    private int armorX;
    private int armorY;

    private int speedX;
    private int speedY;

    public HudEditorScreen() {
        super(Component.literal("ZhanVisuals HUD Editor"));
    }

    @Override
    protected void init() {
        super.init();

        ZhanHud.setEditing(true);

        watermarkX = ZhanHud.getX("watermark");
        watermarkY = ZhanHud.getY("watermark");

        targetX = ZhanHud.getX("target");
        targetY = ZhanHud.getY("target");

        armorX = ZhanHud.getX("armor");
        armorY = ZhanHud.getY("armor");

        speedX = ZhanHud.getX("speed");
        speedY = ZhanHud.getY("speed");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX,
                       int mouseY, float delta) {

        graphics.fill(0, 0, width, height, 0xCC08060D);

        graphics.drawCenteredString(font,
            "ZHANVISUALS | HUD EDITOR",
            width / 2, 12, 0xFFE4B5FF);

        graphics.drawCenteredString(font,
            "Зажми ЛКМ на элементе и перетащи его",
            width / 2, 27, 0xFFBBBBBB);

        drawWatermark(graphics);
        drawTarget(graphics);
        drawArmor(graphics);
        drawSpeed(graphics);

        graphics.drawCenteredString(font,
            "ESC — выйти и сохранить",
            width / 2, height - 18, 0xFFAAAAAA);

        super.render(graphics, mouseX, mouseY, delta);
    }

    private void drawWatermark(GuiGraphics g) {
        g.fill(watermarkX, watermarkY,
            watermarkX + 125, watermarkY + 22, 0xDD17131F);
        g.fill(watermarkX, watermarkY,
            watermarkX + 125, watermarkY + 2, 0xFFB66BFF);
        g.drawString(font, "ZhanVisuals | FPS",
            watermarkX + 7, watermarkY + 7, 0xFFFFFFFF);
    }

    private void drawTarget(GuiGraphics g) {
        g.fill(targetX, targetY,
            targetX + 150, targetY + 48, 0xDD17131F);
        g.fill(targetX, targetY,
            targetX + 150, targetY + 2, 0xFFB66BFF);

        g.drawString(font, "TargetHUD",
            targetX + 8, targetY + 8, 0xFFFFFFFF);
        g.drawString(font, "HP: 20 / 20",
            targetX + 8, targetY + 22, 0xFFFF7777);

        g.fill(targetX + 8, targetY + 36,
            targetX + 138, targetY + 40, 0xFF39313F);
        g.fill(targetX + 8, targetY + 36,
            targetX + 138, targetY + 40, 0xFFBD72FF);
    }

    private void drawArmor(GuiGraphics g) {
        g.fill(armorX, armorY,
            armorX + 100, armorY + 20, 0xDD17131F);
        g.fill(armorX, armorY,
            armorX + 100, armorY + 2, 0xFFB66BFF);
        g.drawString(font, "Armor HUD",
            armorX + 8, armorY + 7, 0xFFFFFFFF);
    }

    private void drawSpeed(GuiGraphics g) {
        g.fill(speedX, speedY,
            speedX + 100, speedY + 22, 0xDD17131F);
        g.fill(speedX, speedY,
            speedX + 100, speedY + 2, 0xFFB66BFF);
        g.drawString(font, "Speedometer",
            speedX + 7, speedY + 7, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (inside(mouseX, mouseY, watermarkX, watermarkY, 125, 22)) {
            startDragging("watermark", mouseX, mouseY, watermarkX, watermarkY);
            return true;
        }

        if (inside(mouseX, mouseY, targetX, targetY, 150, 48)) {
            startDragging("target", mouseX, mouseY, targetX, targetY);
            return true;
        }

        if (inside(mouseX, mouseY, armorX, armorY, 100, 20)) {
            startDragging("armor", mouseX, mouseY, armorX, armorY);
            return true;
        }

        if (inside(mouseX, mouseY, speedX, speedY, 100, 22)) {
            startDragging("speed", mouseX, mouseY, speedX, speedY);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void startDragging(String element, double mouseX,
                               double mouseY, int x, int y) {
        dragging = element;
        offsetX = (int) mouseX - x;
        offsetY = (int) mouseY - y;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY,
                                int button, double dragX, double dragY) {
        if (dragging == null || button != 0) {
            return super.mouseDragged(mouseX, mouseY,
                button, dragX, dragY);
        }

        int x = Math.max(0, Math.min(width - 150,
            (int) mouseX - offsetX));
        int y = Math.max(0, Math.min(height - 48,
            (int) mouseY - offsetY));

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
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = null;
            HudConfig.saveAll();
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean inside(double mouseX, double mouseY,
                           int x, int y, int w, int h) {
        return mouseX >= x && mouseX <= x + w
            && mouseY >= y && mouseY <= y + h;
    }

    @Override
    public void onClose() {
        HudConfig.saveAll();
        ZhanHud.setEditing(false);
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
