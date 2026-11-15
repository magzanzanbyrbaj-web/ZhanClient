
package ru.zhanbyrbay.zhanvisuals.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zhanbyrbay.zhanvisuals.ZhanVisuals;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Inject(
    method = "renderBossOverlay",
    at = @At("HEAD"),
    cancellable = true
)
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void zhanvisuals$noScoreboard(CallbackInfo ci) {
        if (ZhanVisuals.noScoreboard) ci.cancel();
    }

    @Inject(method = "renderTextureOverlay", at = @At("HEAD"), cancellable = true)
    private void zhanvisuals$overlays(
        GuiGraphics graphics,
        Identifier texture,
        float opacity,
        CallbackInfo ci
    ) {
        String path = texture.getPath();

        if (ZhanVisuals.noPumpkin && path.contains("pumpkinblur")) {
            ci.cancel();
        }

        if (ZhanVisuals.lowFire && path.contains("fire")) {
            ci.cancel();
        }
    }
}
