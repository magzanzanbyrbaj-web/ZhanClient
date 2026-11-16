package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;

public class AttackAura {

    private static long lastAttackTime = 0;
    private static final long ATTACK_DELAY = 300L; // Задержка между ударами (мс)

    public static void tick(Minecraft mc) {
        if (mc.player == null || mc.level == null || mc.gameMode == null) return;

        long now = System.currentTimeMillis();
        if (now - lastAttackTime < ATTACK_DELAY) return;

        LivingEntity target = null;
        double minDistance = 4.2; // Дистанция удара

        for (var entity : mc.level.entitiesForRendering()) {
            if (entity instanceof LivingEntity living && living != mc.player && living.isAlive()) {
                double dist = mc.player.distanceTo(living);
                if (dist <= minDistance) {
                    target = living;
                    break;
                }
            }
        }

        if (target != null) {
            mc.gameMode.attack(mc.player, target);
            mc.player.swing(InteractionHand.MAIN_HAND);
            lastAttackTime = now;
        }
    }
}
