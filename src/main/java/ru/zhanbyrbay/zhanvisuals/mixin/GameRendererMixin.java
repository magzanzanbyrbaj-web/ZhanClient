
package ru.zhanbyrbay.zhanvisuals.mixin;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zhanbyrbay.zhanvisuals.ZhanVisuals;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void zhanvisuals$noHurtCamera(CallbackInfo ci) {
        if (ZhanVisuals.noHurtCamera) ci.cancel();
    }
}
