
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

    private static final int PURPLE = 0xFFB77CFF;
    private static final int LIGHT_PURPLE = 0xFFDCC4FF;
    private static final int PANEL = 0xD914101F;
    private static final int MUTED = 0xFFAAA0B8;

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

        int x = 8;
        int y = 8;

        int fps = mc.getFps();
        int ping = getPing(mc);

        String title = "ZHANVISUALS";
        String details = fps + " FPS  |  " + ping + " MS";

        int width = mc.font.width(title) +
                    mc.font.width(details) + 36;

        g.fill(
            x,
            y,
            x + width,
            y + 27,
            PANEL
        );

        g.fill(
            x,
            y,
            x + 3,
            y + 27,
            PURPLE
        );

        g.drawString(
            mc.font,
            title,
            x + 10,
            y + 6,
            LIGHT_PURPLE,
            false
        );

        int detailsX = x + width - mc.font.width(details) - 9;

        g.drawString(
            mc.font,
            details,
            detailsX,
            y + 6,
            MUTED,
            false
        );

        g.fill(
            x + 8,
            y + 22,
            x + width - 8,
            y + 23,
            0xFF554269
        );
    }

    // FPS / COORDINATES / PING / TOTEMS
    private static void renderStats(
        Minecraft mc,
        GuiGraphics g
    ) {
        int x = 8;
        int y = ZhanVisuals.watermark ? 41 : 8;
        int lineHeight = 13;

        int count = 0;

        if (ZhanVisuals.fpsCounter) count++;
        if (ZhanVisuals.coordinates) count++;
        if (ZhanVisuals.pingDisplay) count++;
        if (ZhanVisuals.totemCounter) count++;

        if (count == 0) return;

        int height = 8 + count * lineHeight;

        g.fill(
            x,
            y,
            x + 145,
            y + height,
            PANEL
        );

        g.fill(
            x,
            y,
            x + 2,
            y + height,
            0xFF8752C9
        );

        int textY = y + 5;

        if (ZhanVisuals.fpsCounter) {
            drawStat(
                g,
                mc,
                "FPS",
                String.valueOf(mc.getFps()),
                x + 9,
                textY
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
                g,
                mc,
                "XYZ",
                coords,
                x + 9,
                textY
            );
            textY += lineHeight;
        }

        if (ZhanVisuals.pingDisplay) {
            drawStat(
                g,
                mc,
                "PING",
                getPing(mc) + " ms",
                x + 9,
                textY
            );
            textY += lineHeight;
        }

        if (ZhanVisuals.totemCounter) {
            drawStat(
                g,
                mc,
                "TOTEMS",
                String.valueOf(getTotems(mc)),
                x + 9,
                textY
            );
        }
    }

    private static void drawStat(
        GuiGraphics g,
        Minecraft mc,
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
            x + 48,
            y,
            0xFFF4F0FA,
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

        g.fill(
            startX - 5,
            y - 4,
            startX + 91,
            y + 24,
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

    // CUSTOM CROSSHAIR + HIT COLOR
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
                ? 0xFFFF526D
                : LIGHT_PURPLE;

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

        int width = 150;
        int height = 48;

        int x = Math.min(
            screenWidth - width - 6,
            screenWidth / 2 + 18
        );

        int y = Math.min(
            screenHeight - height - 6,
            screenHeight / 2 + 18
        );

        float health = living.getHealth();
        float maxHealth = Math.max(1.0F, living.getMaxHealth());
        float ratio = Math.max(0.0F, Math.min(1.0F, health / maxHealth));

        g.fill(
            x,
            y,
            x + width,
            y + height,
            PANEL
        );

        g.fill(
            x,
            y,
            x + 3,
            y + height,
            PURPLE
        );

        g.drawString(
            mc.font,
            living.getName(),
            x + 10,
            y + 7,
            LIGHT_PURPLE,
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
            x + 10,
            y + 21,
            0xFFF0EAF8,
            false
        );

        int barX = x + 10;
        int barY = y + 36;
        int barWidth = width - 20;

        g.fill(
            barX,
            barY,
            barX + barWidth,
            barY + 5,
            0xFF42394D
        );

        g.fill(
            barX,
            barY,
            barX + (int)(barWidth * ratio),
            barY + 5,
            PURPLE
        );
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
        int background = pressed
            ? 0xFF7040A8
            : PANEL;

        g.fill(
            x,
            y,
            x + width,
            y + 20,
            background
        );

        g.fill(
            x,
            y + 18,
            x + width,
            y + 20,
            pressed ? PURPLE : 0xFF51445F
        );

        int textWidth = mc.font.width(text);

        g.drawString(
            mc.font,
            text,
            x + (width - textWidth) / 2,
            y + 6,
            0xFFFFFFFF,
            false
        );
    }
}
