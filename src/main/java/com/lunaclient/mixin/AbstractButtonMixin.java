package com.lunaclient.mixin;

import com.lunaclient.Glass;
import com.lunaclient.LunaConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Convierte los botones normales de Minecraft (menu principal, opciones, pausa...) en botones de cristal. */
@Mixin(AbstractButton.class)
public class AbstractButtonMixin {
	// require = 0: si Mojang renombra este metodo, el mod sigue cargando (solo no cambia los botones de vanilla).
	@Inject(method = "extractWidgetRenderState", at = @At("HEAD"), cancellable = true, require = 0)
	private void luna$glass(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		LunaConfig cfg = LunaConfig.get();
		if (!cfg.glassVanillaButtons) {
			return;
		}
		AbstractWidget w = (AbstractWidget) (Object) this;
		Glass.button(graphics, w.getX(), w.getY(), w.getWidth(), w.getHeight(),
			w.isHovered(), w.active, false, w.getMessage(), cfg.theme);
		ci.cancel();
	}
}
