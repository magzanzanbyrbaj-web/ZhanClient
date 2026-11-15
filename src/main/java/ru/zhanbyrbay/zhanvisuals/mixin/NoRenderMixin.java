
package ru.zhanbyrbay.zhanvisuals.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ru.zhanbyrbay.zhanvisuals.ZhanVisuals;

@Mixin(LevelRenderer.class)
abstract class LevelRendererMixin {

    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    private void zhanvisuals$noWeather(CallbackInfo ci) {
        if (ZhanVisuals.noWeather) {
            ci.cancel();
        }
    }

    @Inject(method = "addParticlesPass", at = @At("HEAD"), cancellable = true)
    private void zhanvisuals$noParticles(CallbackInfo ci) {
        if (ZhanVisuals.noParticles) {
            ci.cancel();
        }
    }
}

@Mixin(Gui.class)
abstract class GuiMixin {

    @Inject(method = "renderBossBarHud", at = @At("HEAD"), cancellable = true)
    private void zhanvisuals$noBossBar(CallbackInfo ci) {
        if (ZhanVisuals.noBossBar) {
            ci.cancel();
        }
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void zhanvisuals$noScoreboard(CallbackInfo ci) {
        if (ZhanVisuals.noScoreboard) {
            ci.cancel();
        }
    }

    @Inject(method = "renderTextureOverlay", at = @At("HEAD"), cancellable = true)
    private void zhanvisuals$noScreenOverlays(
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

@Mixin(GameRenderer.class)
abstract class GameRendererMixin {

    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void zhanvisuals$noHurtCamera(CallbackInfo ci) {
        if (ZhanVisuals.noHurtCamera) {
            ci.cancel();
        }
    }
}
