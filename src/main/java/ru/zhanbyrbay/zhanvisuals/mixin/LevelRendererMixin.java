
package ru.zhanbyrbay.zhanvisuals.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zhanbyrbay.zhanvisuals.ZhanVisuals;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Inject(method = "addWeatherPass", at = @At("HEAD"), cancellable = true)
    private void zhanvisuals$noWeather(CallbackInfo ci) {
        if (ZhanVisuals.noWeather) ci.cancel();
    }

    @Inject(method = "addParticlesPass", at = @At("HEAD"), cancellable = true)
    private void zhanvisuals$noParticles(CallbackInfo ci) {
        if (ZhanVisuals.noParticles) ci.cancel();
    }
}
