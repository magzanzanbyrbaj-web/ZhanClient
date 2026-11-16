package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.ClickType;

public class AutoMace {

    public static void tick(Minecraft mc) {
        if (mc.player == null || mc.gameMode == null) return;

        // Если игрок падает с высоты больше 2 блоков — ищем булаву в инвентаре
        if (mc.player.fallDistance > 2.0F && !mc.player.isFallFlying()) {
            if (mc.player.getMainHandItem().is(Items.MACE)) {
                return; // Булава уже в руке
            }

            for (int slot = 0; slot < 36; slot++) {
                ItemStack stack = mc.player.getInventory().getItem(slot);
                if (stack.is(Items.MACE)) {
                    int menuSlot = slot < 9 ? slot + 36 : slot;

                    mc.gameMode.handleInventoryMouseClick(
                        mc.player.inventoryMenu.containerId,
                        menuSlot,
                        mc.player.getInventory().selected,
                        ClickType.SWAP,
                        mc.player
                    );
                    break;
                }
            }
        }
    }
}
