
package ru.zhanbyrbay.zhanvisuals.mixin;

import ru.zhanbyrbay.zhanvisuals.ZhanVisuals;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.Gui;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Inject(
        method = "renderScoreboard",
        at = @At("HEAD"),
        cancellable = true
    )
    private void zhanvisuals$noScoreboard(CallbackInfo ci) {
        if (ZhanVisuals.noBossBar) {
            ci.cancel();
        }
    }
}
