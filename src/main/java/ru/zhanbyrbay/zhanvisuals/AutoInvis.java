package ru.zhanbyrbay.zhanvisuals;

import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffects;

public class AutoInvis {

    public static void tick(Minecraft mc) {
        if (mc.player == null) return;

        boolean hasInvis = mc.player.hasEffect(MobEffects.INVISIBILITY);
        
        if (hasInvis) {
            // Дополнительная логика при активной невидимости
        }
    }
}
