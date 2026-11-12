package com.lunaclient;

import com.lunaclient.gui.HudEditScreen;
import com.lunaclient.gui.LunaMenuScreen;
import com.lunaclient.hud.HudModule;
import com.lunaclient.hud.Modules;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class LunaClient implements ClientModInitializer {
	public static KeyMapping MENU_KEY;

	@Override
	public void onInitializeClient() {
		LunaConfig.load();

		KeyMapping.Category category = KeyMapping.Category.register(
			Identifier.fromNamespaceAndPath("lunaclient", "main"));
		MENU_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.lunaclient.menu", InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT, category));

		Features.ZOOM_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.lunaclient.zoom", InputConstants.Type.KEYSYM, InputConstants.KEY_C, category));
		Features.FULLBRIGHT_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.lunaclient.fullbright", InputConstants.Type.KEYSYM, InputConstants.KEY_G, category));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (MENU_KEY.consumeClick()) {
				client.gui.setScreen(new LunaMenuScreen(null));
			}
			while (Features.FULLBRIGHT_KEY.consumeClick()) {
				Features.toggleFullbright();
			}
			Features.tick(client);
		});
		ClientLifecycleEvents.CLIENT_STOPPING.register(Features::restore);

		// Crosshair propio (si esta apagado, se dibuja el original de Minecraft)
		HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, original -> (graphics, tickCounter) -> {
			if (LunaConfig.get().crosshairEnabled) {
				CrosshairRenderer.render(graphics);
			} else {
				CrosshairRenderer.callOriginal(original, graphics, tickCounter);
			}
		});

		HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
			Identifier.fromNamespaceAndPath("lunaclient", "hud"), (graphics, tickCounter) -> {
				Cps.poll();
				Minecraft mc = Minecraft.getInstance();
				if (mc.player == null || !LunaConfig.get().hudVisible || HudEditScreen.OPEN) {
					return;
				}
				for (HudModule m : Modules.ALL) {
					m.render(graphics, false);
				}
			});
	}
}
