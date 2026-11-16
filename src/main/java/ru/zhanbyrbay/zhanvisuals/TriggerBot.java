package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class TriggerBot {
    
    // Поля настроек для модуля
    public static float range = 3.2f;
    public static float extraRange = 0.4f;
    public static boolean throughWalls = false;
    public static boolean raycastCheck = true;
    public static boolean coolDownCheck = true;
    public static float coolDownValue = 80.0f;
    public static boolean tpsSync = true;
    public static boolean targetPlayers = true;
    public static boolean targetMobs = false;

    private static LivingEntity targetEntity;

    public static void tick(Minecraft mc) {
        if (!ZhanVisuals.attackAura && !ZhanVisuals.aimAssist) return; // Проверка активности
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null || mc.gameMode == null || mc.screen != null) return;

        LivingEntity foundTarget = findBestTarget(player, mc);
        if (foundTarget != null) {
            targetEntity = foundTarget;
            if (checkConditions(player)) {
                attack(player, foundTarget, mc);
            }
        } else {
            targetEntity = null;
        }
    }

    private static LivingEntity findBestTarget(LocalPlayer player, Minecraft mc) {
        double totalRange = range + extraRange;
        Vec3 eye = player.getEyePosition(1.0f);
        AABB searchBox = player.getBoundingBox().inflate(totalRange);

        LivingEntity best = null;
        double minDistanceSq = totalRange * totalRange;

        for (Entity entity : mc.level.getEntitiesOfClass(LivingEntity.class, searchBox, e -> e != player && e.isAlive())) {
            if (!isValidTarget((LivingEntity) entity)) continue;
            
            double distSq = player.distanceToSqr(entity);
            if (distSq <= minDistanceSq) {
                best = (LivingEntity) entity;
                minDistanceSq = distSq;
            }
        }
        return best;
    }

    private static boolean isValidTarget(LivingEntity entity) {
        if (entity instanceof Player && !targetPlayers) return false;
        if (entity instanceof Mob && !targetMobs) return false;
        return entity.isAlive() && !entity.isSpectator();
    }

    private static boolean checkConditions(LocalPlayer player) {
        if (coolDownCheck) {
            float requiredCD = coolDownValue / 100.0f;
            if (player.getAttackStrengthScale(0.5f) < requiredCD) {
                return false;
            }
        }
        return true;
    }

    private static void attack(LocalPlayer player, LivingEntity target, Minecraft mc) {
        mc.gameMode.attack((Player) player, (Entity) target);
        player.swing(InteractionHand.MAIN_HAND);
        player.resetAttackStrengthTicker();
    }
}
