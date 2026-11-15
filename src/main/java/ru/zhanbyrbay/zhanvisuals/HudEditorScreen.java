
package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class HudEditorScreen extends Screen {

    private String dragging;

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

    private static final int PURPLE = 0xFFB66BFF;
    private static final int PANEL = 0xDD17131F;
    private static final int TEXT = 0xFFFFFFFF;

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
    public void render(
        GuiGraphics g,
        int mouseX,
        int mouseY,
        float delta
    ) {
        g.fill(0, 0, width, height, 0xCC08060D);

        g.drawCenteredString(
            font,
            "ZHANVISUALS HUD EDITOR",
            width / 2,
            12,
            PURPLE
        );

        g.drawCenteredString(
            font,
            "Зажми ЛКМ на элементе и перетащи",
            width / 2,
            27,
            0xFFBBBBBB
        );

        drawElement(g, watermarkX, watermarkY, 125, 22,
            "ZhanVisuals | 120 FPS");

        drawElement(g, targetX, targetY, 150, 48,
            "TargetHUD");

        g.drawString(font, "HP: 20 / 20",
            targetX + 8, targetY + 22, 0xFFFF7777);

        g.fill(targetX + 8, targetY + 36,
            targetX + 138, targetY + 40, 0xFF39313F);

        g.fill(targetX + 8, targetY + 36,
            targetX + 138, targetY + 40, PURPLE);

        drawElement(g, armorX, armorY, 100, 22,
            "Armor HUD");

        drawElement(g, speedX, speedY, 110, 22,
            "Speedometer");

        g.drawCenteredString(
            font,
            "H — открыть редактор | ESC — выйти и сохранить",
            width / 2,
            height - 18,
            0xFFAAAAAA
        );

        super.render(g, mouseX, mouseY, delta);
    }

    private void drawElement(
        GuiGraphics g,
        int x,
        int y,
        int w,
        int h,
        String text
    ) {
        g.fill(x, y, x + w, y + h, PANEL);
        g.fill(x, y, x + w, y + 2, PURPLE);

        g.drawString(
            font,
            text,
            x + 7,
            y + 7,
            TEXT
        );
    }

    @Override
    public boolean mouseClicked(
        MouseButtonEvent event,
        boolean doubleClick
    ) {
        if (event.button() != 0) {
            return super.mouseClicked(event, doubleClick);
        }

        double mx = event.x();
        double my = event.y();

        if (inside(mx, my, watermarkX, watermarkY, 125, 22)) {
            startDragging("watermark", mx, my, watermarkX, watermarkY);
            return true;
        }

        if (inside(mx, my, targetX, targetY, 150, 48)) {
            startDragging("target", mx, my, targetX, targetY);
            return true;
        }

        if (inside(mx, my, armorX, armorY, 100, 22)) {
            startDragging("armor", mx, my, armorX, armorY);
            return true;
        }

        if (inside(mx, my, speedX, speedY, 110, 22)) {
            startDragging("speed", mx, my, speedX, speedY);
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    private void startDragging(
        String element,
        double mouseX,
        double mouseY,
        int x,
        int y
    ) {
        dragging = element;
        offsetX = (int) mouseX - x;
        offsetY = (int) mouseY - y;
    }

    @Override
    public boolean mouseDragged(
        MouseButtonEvent event,
        double deltaX,
        double deltaY
    ) {
        if (dragging == null || event.button() != 0) {
            return super.mouseDragged(event, deltaX, deltaY);
        }

        int x = Math.max(
            0,
            Math.min(width - 150, (int) event.x() - offsetX)
        );

        int y = Math.max(
            0,
            Math.min(height - 48, (int) event.y() - offsetY)
        );

        switch (dragging) {
            case "watermark" -> {
                watermarkX = Math.min(x, width - 125);
                watermarkY = Math.min(y, height - 22);
                ZhanHud.setPosition("watermark", watermarkX, watermarkY);
            }

            case "target" -> {
                targetX = x;
                targetY = y;
                ZhanHud.setPosition("target", x, y);
            }

            case "armor" -> {
                armorX = Math.min(x, width - 100);
                armorY = Math.min(y, height - 22);
                ZhanHud.setPosition("armor", armorX, armorY);
            }

            case "speed" -> {
                speedX = Math.min(x, width - 110);
                speedY = Math.min(y, height - 22);
                ZhanHud.setPosition("speed", speedX, speedY);
            }
        }

        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0 && dragging != null) {
            dragging = null;
            HudConfig.saveAll();
            return true;
        }

        return super.mouseReleased(event);
    }

    private boolean inside(
        double mouseX,
        double mouseY,
        int x,
        int y,
        int w,
        int h
    ) {
        return mouseX >= x
            && mouseX <= x + w
            && mouseY >= y
            && mouseY <= y + h;
    }

    @Override
    public void onClose() {
        dragging = null;
        HudConfig.saveAll();
        ZhanHud.setEditing(false);
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
