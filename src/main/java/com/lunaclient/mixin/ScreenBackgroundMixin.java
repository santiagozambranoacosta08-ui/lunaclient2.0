package com.lunaclient.mixin;

import com.lunaclient.LunaConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fondo negro en los menus fuera del mundo (menu principal, carga, opciones sin partida). */
@Mixin(Screen.class)
public class ScreenBackgroundMixin {
	// require = 0: si Mojang renombra el metodo, el mod sigue cargando y solo no pinta el fondo negro.
	@Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true, require = 0)
	private void luna$black(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		if (!LunaConfig.get().blackMenu) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null) {
			return;
		}
		graphics.fill(0, 0, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight(), 0xFF000000);
		ci.cancel();
	}
}
