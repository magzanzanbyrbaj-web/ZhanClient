
package ru.zhanbyrbay.zhanvisuals;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ZhanHud {

    private static boolean registered = false;

    public static void register() {
        if (registered) return;
        registered = true;

        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath("zhanvisuals", "main_hud"),
            (graphics, tickCounter) -> render(graphics)
        );
    }

    private static void render(GuiGraphics g) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.options.hideGui) return;

        int x = 8;
        int y = 8;
        int lineHeight = 14;

        List<String> lines = new ArrayList<>();

        if (ZhanVisuals.fpsCounter) {
            lines.add("FPS  " + mc.getFps());
        }

        if (ZhanVisuals.coordinates) {
            lines.add(String.format(
                Locale.ROOT,
                "XYZ  %.1f / %.1f / %.1f",
                mc.player.getX(),
                mc.player.getY(),
                mc.player.getZ()
            ));
        }

        if (ZhanVisuals.armorHUD) {
            int count = 0;
            for (ItemStack stack : mc.player.getArmorSlots()) {
                if (!stack.isEmpty()) count++;
            }
            lines.add("ARMOR  " + count + "/4");
        }

        if (ZhanVisuals.totemCounter) {
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

            lines.add("TOTEMS  " + total);
        }

        if (ZhanVisuals.pingDisplay && mc.getConnection() != null) {
            var info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
            if (info != null) {
                lines.add("PING  " + info.getLatency() + " ms");
            }
        }

        if (!lines.isEmpty()) {
            int panelWidth = 154;
            int panelHeight = 12 + lines.size() * lineHeight;

            g.fill(
                x, y,
                x + panelWidth, y + panelHeight,
                0xC910121A
            );

            g.fill(
                x, y,
                x + 2, y + panelHeight,
                0xFFFF3548
            );

            g.fill(
                x + 9, y + 7,
                x + 35, y + 9,
                0xFFFF3548
            );

            int textY = y + 15;

            for (String line : lines) {
                g.drawString(
                    mc.font,
                    line,
                    x + 10,
                    textY,
                    0xFFE8EAF0,
                    false
                );
                textY += lineHeight;
            }
        }

        renderCrosshair(mc, g);

        if (ZhanVisuals.targetHUD && mc.hitResult instanceof EntityHitResult hit) {
            Entity entity = hit.getEntity();

            if (entity instanceof LivingEntity living && living.isAlive()) {
                int tx = mc.getWindow().getGuiScaledWidth() / 2 + 14;
                int ty = mc.getWindow().getGuiScaledHeight() / 2 + 14;

                g.fill(tx, ty, tx + 130, ty + 38, 0xD910121A);
                g.fill(tx, ty, tx + 2, ty + 38, 0xFFFF3548);

                g.drawString(
                    mc.font,
                    living.getName(),
                    tx + 9,
                    ty + 7,
                    0xFFFFFFFF,
                    false
                );

                g.drawString(
                    mc.font,
                    String.format(
                        Locale.ROOT,
                        "HP: %.1f",
                        living.getHealth()
                    ),
                    tx + 9,
                    ty + 21,
                    0xFFFF6672,
                    false
                );
            }
        }

        if (ZhanVisuals.keystrokes) {
            renderKeys(mc, g);
        }
    }

    private static void renderCrosshair(Minecraft mc, GuiGraphics g) {
        if (!ZhanVisuals.customCrosshair && !ZhanVisuals.hitColor) return;

        boolean target = false;

        if (mc.hitResult instanceof EntityHitResult hit) {
            Entity entity = hit.getEntity();
            target = entity instanceof LivingEntity && entity.isAlive();
        }

        int color = ZhanVisuals.hitColor && target
            ? 0xFFFF3030
            : 0xFFFFFFFF;

        int cx = mc.getWindow().getGuiScaledWidth() / 2;
        int cy = mc.getWindow().getGuiScaledHeight() / 2;

        g.fill(cx - 6, cy - 1, cx - 2, cy + 1, color);
        g.fill(cx + 2, cy - 1, cx + 6, cy + 1, color);
        g.fill(cx - 1, cy - 6, cx + 1, cy - 2, color);
        g.fill(cx - 1, cy + 2, cx + 1, cy + 6, color);
    }

    private static void renderKeys(Minecraft mc, GuiGraphics g) {
        int x = 10;
        int y = mc.getWindow().getGuiScaledHeight() - 62;

        drawKey(mc, g, x + 24, y, "W", mc.options.keyUp.isDown());
        drawKey(mc, g, x, y + 22, "A", mc.options.keyLeft.isDown());
        drawKey(mc, g, x + 24, y + 22, "S", mc.options.keyDown.isDown());
        drawKey(mc, g, x + 48, y + 22, "D", mc.options.keyRight.isDown());
        drawKey(mc, g, x + 78, y + 22, "J", mc.options.keyAttack.isDown());
        drawKey(mc, g, x + 102, y + 22, "K", mc.options.keyUse.isDown());
    }

    private static void drawKey(
        Minecraft mc,
        GuiGraphics g,
        int x,
        int y,
        String text,
        boolean pressed
    ) {
        int color = pressed ? 0xFFFF3548 : 0xCC171B25;

        g.fill(x, y, x + 21, y + 19, color);
        g.drawString(mc.font, text, x + 7, y + 6, 0xFFFFFFFF, false);
    }
}
