
package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ZhanHud {

    private static boolean registered = false;

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

    private static void render(GuiGraphics graphics) {

        Minecraft client = Minecraft.getInstance();

        if (client.player == null || client.options.hideGui) {
            return;
        }

        List<String> lines = new ArrayList<>();

        // FPS Counter
        if (ZhanVisuals.fpsCounter) {
            lines.add("FPS: " + client.getFps());
        }

        // Coordinates
        if (ZhanVisuals.coordinates) {

            double x = client.player.getX();
            double y = client.player.getY();
            double z = client.player.getZ();

            lines.add(String.format(
                Locale.ROOT,
                "XYZ: %.1f / %.1f / %.1f",
                x, y, z
            ));
        }

        // Armor HUD
        if (ZhanVisuals.armorHUD) {

            int armor = 0;

            EquipmentSlot[] slots = {
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
            };

            for (EquipmentSlot slot : slots) {

                ItemStack stack =
                    client.player.getItemBySlot(slot);

                if (!stack.isEmpty()) {
                    armor++;
                }
            }

            lines.add("Armor: " + armor + "/4");
        }

        // Totem Counter
        if (ZhanVisuals.totemCounter) {

            int totems = 0;

            // Main inventory
            for (int i = 0; i < 36; i++) {

                ItemStack stack =
                    client.player.getInventory().getItem(i);

                if (stack.is(Items.TOTEM_OF_UNDYING)) {
                    totems += stack.getCount();
                }
            }

            // Offhand
            ItemStack offhand =
                client.player.getOffhandItem();

            if (offhand.is(Items.TOTEM_OF_UNDYING)) {
                totems += offhand.getCount();
            }

            lines.add("Totems: " + totems);
        }

        // Draw HUD
        if (!lines.isEmpty()) {

            int panelHeight = 8 + lines.size() * 13;

            graphics.fill(
                4, 4,
                190, panelHeight,
                0x990B0D13
            );

            graphics.fill(
                4, 4,
                6, panelHeight,
                0xFFFF3548
            );

            int y = 9;

            for (String line : lines) {

                graphics.drawString(
                    client.font,
                    line,
                    12,
                    y,
                    0xFFFFFFFF,
                    false
                );

                y += 13;
            }
        }

        // HitColor + CustomCrosshair
        if (ZhanVisuals.hitColor || ZhanVisuals.customCrosshair) {

            boolean target = false;

            if (client.hitResult instanceof EntityHitResult result) {

                Entity entity = result.getEntity();

                target = entity instanceof LivingEntity
                    && entity.isAlive();
            }

            int color = (
                ZhanVisuals.hitColor && target
            ) ? 0xFFFF3030 : 0xFFFFFFFF;

            int centerX =
                client.getWindow().getGuiScaledWidth() / 2;

            int centerY =
                client.getWindow().getGuiScaledHeight() / 2;

            // Custom crosshair
            graphics.fill(
                centerX - 5,
                centerY - 1,
                centerX - 2,
                centerY + 1,
                color
            );

            graphics.fill(
                centerX + 2,
                centerY - 1,
                centerX + 5,
                centerY + 1,
                color
            );

            graphics.fill(
                centerX - 1,
                centerY - 5,
                centerX + 1,
                centerY - 2,
                color
            );

            graphics.fill(
                centerX - 1,
                centerY + 2,
                centerX + 1,
                centerY + 5,
                color
            );
        }
    }
}
