
package ru.zhanbyrbay.zhanvisuals;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;

import java.util.Locale;

public class ZhanHud {

    private static boolean registered = false;

    private static final int PURPLE = 0xFFAA78FF;
    private static final int LIGHT = 0xFFF4EEFF;
    private static final int MUTED = 0xFFAAA1B8;
    private static final int PANEL = 0xE6100E17;
    private static final int PANEL_LIGHT = 0xFF1B1724;
    private static final int BORDER = 0xFF393044;

    public static void register() {
        if (registered) return;
        registered = true;

        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath(
                "zhanvisuals",
                "main_hud"
            ),
            (graphics, tickCounter) -> render(graphics)
        );
    }

    private static void rounded(
        GuiGraphics g,
        int x,
        int y,
        int w,
        int h,
        int radius,
        int color
    ) {
        radius = Math.min(radius, Math.min(w / 2, h / 2));

        for (int row = 0; row < h; row++) {
            int inset = 0;

            if (row < radius) {
                double dy = radius - row - 0.5;
                inset = radius - (int)Math.sqrt(
                    Math.max(0, radius * radius - dy * dy)
                );
            } else if (row >= h - radius) {
                double dy = row - (h - radius) + 0.5;
                inset = radius - (int)Math.sqrt(
                    Math.max(0, radius * radius - dy * dy)
                );
            }

            g.fill(
                x + inset,
                y + row,
                x + w - inset,
                y + row + 1,
                color
            );
        }
    }

    private static void render(GuiGraphics g) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.options.hideGui) {
            return;
        }

        renderWatermark(mc, g);
        renderStats(mc, g);
        renderArmor(mc, g);
        renderCrosshair(mc, g);
        renderTargetHUD(mc, g);
        renderKeys(mc, g);
    }

    // WATERMARK
    private static void renderWatermark(
        Minecraft mc,
        GuiGraphics g
    ) {
        if (!ZhanVisuals.watermark) return;

        int screenWidth = mc.getWindow().getGuiScaledWidth();

        String title = "ZHANVISUALS";
        String fps = mc.getFps() + " FPS";
        String ping = getPing(mc) + " MS";

        int titleWidth = mc.font.width(title);
        int fpsWidth = mc.font.width(fps);
        int pingWidth = mc.font.width(ping);

        int width = titleWidth + fpsWidth + pingWidth + 67;
        int height = 25;

        int x = (screenWidth - width) / 2;
        int y = 7;

        rounded(
            g,
            x,
            y,
            width,
            height,
            9,
            PANEL
        );

        // Border and purple accent
        g.fill(
            x + 9,
            y + 5,
            x + 11,
            y + 20,
            PURPLE
        );

        // Small logo pulse
        g.fill(
            x + 16,
            y + 11,
            x + 19,
            y + 14,
            PURPLE
        );

        g.fill(
            x + 20,
            y + 8,
            x + 22,
            y + 17,
            PURPLE
        );

        g.fill(
            x + 23,
            y + 11,
            x + 26,
            y + 14,
            PURPLE
        );

        g.drawString(
            mc.font,
            title,
            x + 32,
            y + 9,
            LIGHT,
            false
        );

        int separatorX = x + 32 + titleWidth + 9;

        g.fill(
            separatorX,
            y + 6,
            separatorX + 1,
            y + 19,
            BORDER
        );

        g.drawString(
            mc.font,
            fps,
            separatorX + 9,
            y + 9,
            PURPLE,
            false
        );

        int pingX = separatorX + 9 + fpsWidth + 9;

        g.fill(
            pingX - 5,
            y + 6,
            pingX - 4,
            y + 19,
            BORDER
        );

        g.drawString(
            mc.font,
            ping,
            pingX + 4,
            y + 9,
            MUTED,
            false
        );
    }

    // STATS
    private static void renderStats(
        Minecraft mc,
        GuiGraphics g
    ) {
        int count = 0;

        if (ZhanVisuals.fpsCounter) count++;
        if (ZhanVisuals.coordinates) count++;
        if (ZhanVisuals.pingDisplay) count++;
        if (ZhanVisuals.totemCounter) count++;

        if (count == 0) return;

        int x = 8;
        int y = ZhanVisuals.watermark ? 39 : 8;
        int lineHeight = 13;
        int height = 10 + count * lineHeight;

        rounded(
            g,
            x,
            y,
            151,
            height,
            7,
            PANEL
        );

        g.fill(
            x + 7,
            y + 7,
            x + 9,
            y + height - 7,
            PURPLE
        );

        int textY = y + 7;

        if (ZhanVisuals.fpsCounter) {
            drawStat(
                mc, g, "FPS",
                String.valueOf(mc.getFps()),
                x + 15, textY
            );
            textY += lineHeight;
        }

        if (ZhanVisuals.coordinates) {
            String coords = String.format(
                Locale.ROOT,
                "%.1f %.1f %.1f",
                mc.player.getX(),
                mc.player.getY(),
                mc.player.getZ()
            );

            drawStat(
                mc, g, "XYZ",
                coords,
                x + 15, textY
            );
            textY += lineHeight;
        }

        if (ZhanVisuals.pingDisplay) {
            drawStat(
                mc, g, "PING",
                getPing(mc) + " ms",
                x + 15, textY
            );
            textY += lineHeight;
        }

        if (ZhanVisuals.totemCounter) {
            drawStat(
                mc, g, "TOTEMS",
                String.valueOf(getTotems(mc)),
                x + 15, textY
            );
        }
    }

    private static void drawStat(
        Minecraft mc,
        GuiGraphics g,
        String label,
        String value,
        int x,
        int y
    ) {
        g.drawString(
            mc.font,
            label,
            x,
            y,
            PURPLE,
            false
        );

        g.drawString(
            mc.font,
            value,
            x + 49,
            y,
            LIGHT,
            false
        );
    }

    private static int getPing(Minecraft mc) {
        if (mc.getConnection() == null || mc.player == null) {
            return 0;
        }

        var info = mc.getConnection().getPlayerInfo(
            mc.player.getUUID()
        );

        return info == null ? 0 : info.getLatency();
    }

    private static int getTotems(Minecraft mc) {
        int total = 0;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);

            if (stack.is(Items.TOTEM_OF_UNDYING)) {
                total += stack.getCount();
            }
        }

        ItemStack offhand = mc.player.getOffhandItem();

        if (offhand.is(Items.TOTEM_OF_UNDYING)) {
            total += offhand.getCount();
        }

        return total;
    }

    // ARMOR HUD
    private static void renderArmor(
        Minecraft mc,
        GuiGraphics g
    ) {
        if (!ZhanVisuals.armorHUD) return;

        EquipmentSlot[] slots = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
        };

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int startX = screenWidth / 2 - 42;
        int y = mc.getWindow().getGuiScaledHeight() - 48;

        rounded(
            g,
            startX - 7,
            y - 5,
            100,
            29,
            7,
            PANEL
        );

        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = mc.player.getItemBySlot(slots[i]);

            if (!stack.isEmpty()) {
                g.renderItem(
                    stack,
                    startX + i * 22,
                    y
                );
            }
        }
    }

    // CROSSHAIR
    private static void renderCrosshair(
        Minecraft mc,
        GuiGraphics g
    ) {
        if (!ZhanVisuals.customCrosshair) return;

        boolean target = false;

        if (mc.hitResult instanceof EntityHitResult hit) {
            Entity entity = hit.getEntity();

            target =
                entity instanceof LivingEntity &&
                entity.isAlive();
        }

        int color =
            ZhanVisuals.hitColor && target
                ? 0xFFFF587C
                : PURPLE;

        int cx = mc.getWindow().getGuiScaledWidth() / 2;
        int cy = mc.getWindow().getGuiScaledHeight() / 2;

        g.fill(cx - 6, cy - 1, cx - 2, cy + 1, color);
        g.fill(cx + 2, cy - 1, cx + 6, cy + 1, color);
        g.fill(cx - 1, cy - 6, cx + 1, cy - 2, color);
        g.fill(cx - 1, cy + 2, cx + 1, cy + 6, color);
    }

    // TARGET HUD
    private static void renderTargetHUD(
        Minecraft mc,
        GuiGraphics g
    ) {
        if (!ZhanVisuals.targetHUD) return;

        if (!(mc.hitResult instanceof EntityHitResult hit)) {
            return;
        }

        Entity entity = hit.getEntity();

        if (!(entity instanceof LivingEntity living) ||
            !living.isAlive()) {
            return;
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int width = 164;
        int height = 57;

        int x = Math.min(
            screenWidth - width - 8,
            screenWidth / 2 + 23
        );

        int y = Math.min(
            screenHeight - height - 8,
            screenHeight / 2 + 20
        );

        float health = living.getHealth();
        float maxHealth = Math.max(1.0F, living.getMaxHealth());
        float ratio = Math.max(
            0.0F,
            Math.min(1.0F, health / maxHealth)
        );

        // Main panel
        rounded(
            g,
            x,
            y,
            width,
            height,
            8,
            PANEL
        );

        // Border accent
        g.fill(
            x + 8,
            y + 7,
            x + width - 8,
            y + 8,
            BORDER
        );

        g.fill(
            x + 9,
            y + 9,
            x + 11,
            y + 43,
            PURPLE
        );

        // Entity marker
        rounded(
            g,
            x + 17,
            y + 14,
            25,
            25,
            6,
            0xFF30243F
        );

        g.drawString(
            mc.font,
            living.getName(),
            x + 49,
            y + 14,
            LIGHT,
            false
        );

        String hp = String.format(
            Locale.ROOT,
            "%.1f / %.1f HP",
            health,
            maxHealth
        );

        g.drawString(
            mc.font,
            hp,
            x + 49,
            y + 28,
            MUTED,
            false
        );

        // Health bar
        int barX = x + 17;
        int barY = y + 45;
        int barWidth = width - 34;

        rounded(
            g,
            barX,
            barY,
            barWidth,
            5,
            3,
            0xFF393342
        );

        if (ratio > 0) {
            rounded(
                g,
                barX,
                barY,
                Math.max(3, (int)(barWidth * ratio)),
                5,
                3,
                PURPLE
            );
        }
    }

    // KEYSTROKES
    private static void renderKeys(
        Minecraft mc,
        GuiGraphics g
    ) {
        if (!ZhanVisuals.keystrokes) return;

        int x = 10;
        int y = mc.getWindow().getGuiScaledHeight() - 72;

        drawKey(mc, g, x + 26, y, 26, "W",
            mc.options.keyUp.isDown());

        drawKey(mc, g, x, y + 23, 26, "A",
            mc.options.keyLeft.isDown());

        drawKey(mc, g, x + 26, y + 23, 26, "S",
            mc.options.keyDown.isDown());

        drawKey(mc, g, x + 52, y + 23, 26, "D",
            mc.options.keyRight.isDown());

        drawKey(mc, g, x + 84, y + 23, 38, "LMB",
            mc.options.keyAttack.isDown());

        drawKey(mc, g, x + 126, y + 23, 38, "RMB",
            mc.options.keyUse.isDown());
    }

    private static void drawKey(
        Minecraft mc,
        GuiGraphics g,
        int x,
        int y,
        int width,
        String text,
        boolean pressed
    ) {
        rounded(
            g,
            x,
            y,
            width,
            20,
            5,
            pressed ? 0xFF7045A8 : PANEL
        );

        g.fill(
            x + 5,
            y + 17,
            x + width - 5,
            y + 18,
            pressed ? PURPLE : BORDER
        );

        int textWidth = mc.font.width(text);

        g.drawString(
            mc.font,
            text,
            x + (width - textWidth) / 2,
            y + 6,
            LIGHT,
            false
        );
    }
}
