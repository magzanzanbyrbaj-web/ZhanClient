
package ru.zhanbyrbay.zhanvisuals;

import net.fabricmc.fabric.api.client.rendering.v1.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ZhanHud {

    private static LivingEntity lastTarget;
    private static long targetExpireTime;

    private static int watermarkX = 8;
    private static int watermarkY = 8;

    private static int targetX = 8;
    private static int targetY = 45;

    private static int armorX = 8;
    private static int armorY = 90;

    private static int speedX = 8;
    private static int speedY = 130;

    private static boolean editing = false;

    public static void register() {
        HudElementRegistry.addLast(
            ResourceLocation.fromNamespaceAndPath("zhanvisuals", "main_hud"),
            (graphics, tickCounter) -> render(graphics)
        );
    }

    private static void render(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.options.hideGui) return;

        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        if (ZhanVisuals.watermark) {
            drawWatermark(graphics, mc);
        }

        updateTarget(mc);

        if (ZhanVisuals.targetHUD && lastTarget != null
            && System.currentTimeMillis() < targetExpireTime) {
            drawTarget(graphics, lastTarget);
        }

        if (ZhanVisuals.armorHUD) {
            drawArmor(graphics, mc);
        }

        if (ZhanVisuals.speedometer) {
            drawSpeed(graphics);
        }

        if (ZhanVisuals.coordinates) {
            drawCoordinates(graphics, mc);
        }

        if (ZhanVisuals.fpsCounter) {
            graphics.drawString(
                mc.font,
                "FPS: " + mc.getFps(),
                screenWidth - 65,
                8,
                0xFFFFFFFF,
                true
            );
        }

        if (ZhanVisuals.pingDisplay && mc.player.connection != null) {
            var info = mc.player.connection.getPlayerInfo(mc.player.getUUID());

            if (info != null) {
                graphics.drawString(
                    mc.font,
                    "PING: " + info.getLatency(),
                    screenWidth - 80,
                    20,
                    0xFFFFFFFF,
                    true
                );
            }
        }

        if (ZhanVisuals.totemCounter) {
            drawTotems(graphics, mc);
        }

        if (ZhanVisuals.keystrokes) {
            drawKeystrokes(graphics, mc);
        }
    }

    private static void updateTarget(Minecraft mc) {
        if (mc.crosshairPickEntity instanceof LivingEntity target
            && target.isAlive()
            && target != mc.player) {

            lastTarget = target;
            targetExpireTime = System.currentTimeMillis() + 3000;
        }

        if (lastTarget != null && System.currentTimeMillis() > targetExpireTime) {
            lastTarget = null;
        }
    }

    private static void drawWatermark(GuiGraphics g, Minecraft mc) {
        String text = "ZhanVisuals | " + mc.getFps() + " FPS";

        drawPanel(g, watermarkX, watermarkY, 125, 22);
        g.drawString(mc.font, text, watermarkX + 7, watermarkY + 7, 0xFFE4B5FF, true);
    }

    private static void drawTarget(GuiGraphics g, LivingEntity target) {
        int x = targetX;
        int y = targetY;

        drawPanel(g, x, y, 150, 48);

        String name = target.getName().getString();

        g.drawString(
            Minecraft.getInstance().font,
            name,
            x + 8,
            y + 8,
            0xFFFFFFFF,
            true
        );

        int health = (int) target.getHealth();
        int maxHealth = (int) target.getMaxHealth();

        g.drawString(
            Minecraft.getInstance().font,
            "HP: " + health + " / " + maxHealth,
            x + 8,
            y + 22,
            0xFFFF7777,
            true
        );

        int barWidth = 130;
        int currentWidth = Math.max(
            0,
            Math.min(barWidth, (int) (barWidth * target.getHealth() / target.getMaxHealth()))
        );

        g.fill(x + 8, y + 36, x + 8 + barWidth, y + 40, 0xFF39313F);
        g.fill(x + 8, y + 36, x + 8 + currentWidth, y + 40, 0xFFBD72FF);
    }

    private static void drawArmor(GuiGraphics g, Minecraft mc) {
        int x = armorX;
        int y = armorY;

        drawPanel(g, x, y, 100, 20);

        int offset = 4;

        for (ItemStack stack : mc.player.getArmorSlots()) {
            if (!stack.isEmpty()) {
                g.renderItem(stack, x + offset, y + 2);
            }

            offset += 22;
        }
    }

    private static void drawSpeed(GuiGraphics g) {
        String text = String.format("Speed: %.2f", ZhanVisuals.currentSpeed);

        drawPanel(g, speedX, speedY, 100, 22);

        g.drawString(
            Minecraft.getInstance().font,
            text,
            speedX + 7,
            speedY + 7,
            0xFFE4B5FF,
            true
        );
    }

    private static void drawCoordinates(GuiGraphics g, Minecraft mc) {
        var pos = mc.player.blockPosition();

        String text = "XYZ: " + pos.getX() + " "
            + pos.getY() + " " + pos.getZ();

        g.drawString(
            mc.font,
            text,
            8,
            g.guiHeight() - 15,
            0xFFFFFFFF,
            true
        );
    }

    private static void drawTotems(GuiGraphics g, Minecraft mc) {
        int count = 0;

        for (ItemStack stack : mc.player.getInventory().items) {
            if (stack.is(net.minecraft.world.item.Items.TOTEM_OF_UNDYING)) {
                count += stack.getCount();
            }
        }

        ItemStack offhand = mc.player.getOffhandItem();

        if (offhand.is(net.minecraft.world.item.Items.TOTEM_OF_UNDYING)) {
            count += offhand.getCount();
        }

        g.drawString(
            mc.font,
            "Totems: " + count,
            8,
            g.guiHeight() - 28,
            0xFFFFD77A,
            true
        );
    }

    private static void drawKeystrokes(GuiGraphics g, Minecraft mc) {
        int x = g.guiWidth() - 75;
        int y = g.guiHeight() - 65;

        drawKey(g, x + 24, y, "W", mc.options.keyUp.isDown());
        drawKey(g, x, y + 22, "A", mc.options.keyLeft.isDown());
        drawKey(g, x + 24, y + 22, "S", mc.options.keyDown.isDown());
        drawKey(g, x + 48, y + 22, "D", mc.options.keyRight.isDown());
        drawKey(g, x + 24, y + 44, "SP", mc.options.keyJump.isDown());
    }

    private static void drawKey(
        GuiGraphics g,
        int x,
        int y,
        String text,
        boolean pressed
    ) {
        int color = pressed ? 0xFF9D50D8 : 0xFF29232F;

        g.fill(x, y, x + 22, y + 20, color);

        g.drawString(
            Minecraft.getInstance().font,
            text,
            x + 7,
            y + 6,
            0xFFFFFFFF,
            true
        );
    }

    private static void drawPanel(
        GuiGraphics g,
        int x,
        int y,
        int width,
        int height
    ) {
        g.fill(x, y, x + width, y + height, 0xCC17131F);
        g.fill(x, y, x + width, y + 1, 0xFFB66BFF);
        g.fill(x, y + height - 1, x + width, y + height, 0xFF49305C);
    }

    public static void setEditing(boolean value) {
        editing = value;
    }

    public static boolean isEditing() {
        return editing;
    }

    public static void setPosition(String element, int x, int y) {
        switch (element) {
            case "watermark" -> {
                watermarkX = x;
                watermarkY = y;
            }
            case "target" -> {
                targetX = x;
                targetY = y;
            }
            case "armor" -> {
                armorX = x;
                armorY = y;
            }
            case "speed" -> {
                speedX = x;
                speedY = y;
            }
        }
    }
}
