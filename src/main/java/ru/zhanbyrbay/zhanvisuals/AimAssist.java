package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

public class AimAssist {

    public static void tick(Minecraft mc) {
        if (mc.player == null || mc.level == null) return;
        
        LivingEntity target = null;
        double closestDistance = 16.0; // Радиус поиска в блоках

        for (var entity : mc.level.entitiesForRendering()) {
            if (entity instanceof LivingEntity living && living != mc.player && living.isAlive()) {
                double dist = mc.player.distanceTo(living);
                if (dist < closestDistance) {
                    closestDistance = dist;
                    target = living;
                }
            }
        }

        if (target != null) {
            double dx = target.getX() - mc.player.getX();
            double dy = target.getEyeY() - mc.player.getEyeY();
            double dz = target.getZ() - mc.player.getZ();
            double distanceXZ = Math.sqrt(dx * dx + dz * dz);

            float targetYaw = (float) (Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
            float targetPitch = (float) (-(Math.atan2(dy, distanceXZ) * (180.0 / Math.PI)));

            // Плавное доведение (доворот)
            mc.player.setYRot(mc.player.getYRot() + wrapAngle(targetYaw - mc.player.getYRot()) * 0.3f);
            mc.player.setXRot(mc.player.getXRot() + (targetPitch - mc.player.getXRot()) * 0.3f);
        }
    }

    private static float wrapAngle(float angle) {
        angle = angle % 360.0F;
        if (angle >= 180.0F) angle -= 360.0F;
        if (angle < -180.0F) angle += 360.0F;
        return angle;
    }
}
