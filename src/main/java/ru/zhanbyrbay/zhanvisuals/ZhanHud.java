package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

public class ZhanHud {

    public static void register() {
        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath("zhanvisuals", "main_hud"),
            (graphics, tickCounter) -> {
                Minecraft client = Minecraft.getInstance();

                if (client.player == null || client.options.hideGui) {
                    return;
                }

                int x = 8;
                int y = 8;

                graphics.fill(x - 4, y - 4, x + 145, y + 43, 0x99000000);

                graphics.fill(x - 4, y - 4, x + 145, y - 2, 0xFFFF3030);

                graphics.drawString(
                    client.font,
                    "ZHANVISUALS",
                    x,
                    y,
                    0xFFFF3030,
                    false
                );

                graphics.drawString(
                    client.font,
                    "FPS: " + client.getFps(),
                    x,
                    y + 13,
                    0xFFFFFFFF,
                    false
                );

                graphics.drawString(
                    client.font,
                    String.format(
                        "XYZ: %.1f / %.1f / %.1f",
                        client.player.getX(),
                        client.player.getY(),
                        client.player.getZ()
                    ),
                    x,
                    y + 26,
                    0xFFFFFFFF,
                    false
                );
            }
        );
    }
}
