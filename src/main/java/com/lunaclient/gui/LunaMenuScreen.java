package com.lunaclient.gui;

import com.lunaclient.CrosshairRenderer;
import com.lunaclient.Features;
import com.lunaclient.Glass;
import com.lunaclient.LunaConfig;
import com.lunaclient.Theme;
import com.lunaclient.hud.HudModule;
import com.lunaclient.hud.Modules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Menu principal del client (Right Shift): modulos, temas, HUD, visual y crosshair. */
public class LunaMenuScreen extends Screen {
	private enum Tab {
		MODULOS("Módulos"), TEMAS("Temas"), HUD("HUD"), VISUAL("Visual"), CROSSHAIR("Crosshair");

		final String label;

		Tab(String label) {
			this.label = label;
		}
	}

	private static final int PW = 400;
	private static final int PH = 270;

	private final Screen parent;
	private Tab tab = Tab.MODULOS;
	private int px;
	private int py;

	public LunaMenuScreen(Screen parent) {
		super(Component.literal("Luna Client"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		px = (width - PW) / 2;
		py = (height - PH) / 2;
		int inner = PW - 28;

		Tab[] tabs = Tab.values();
		int tw = (inner - 4 * (tabs.length - 1)) / tabs.length;
		for (int i = 0; i < tabs.length; i++) {
			final Tab t = tabs[i];
			addRenderableWidget(new GlassButton(px + 14 + i * (tw + 4), py + 34, tw, 20,
				() -> t.label, () -> {
					tab = t;
					rebuildWidgets();
				}).selected(() -> tab == t));
		}

		int cy = py + 64;
		switch (tab) {
			case MODULOS -> initModules(cy, inner);
			case TEMAS -> initThemes(cy, inner);
			case HUD -> initHud(cy, inner);
			case VISUAL -> initVisual(cy, inner);
			case CROSSHAIR -> initCrosshair(cy, inner);
		}
	}

	private GlassButton toggle(int x, int y, int w, String name, java.util.function.BooleanSupplier get,
							   Runnable flip) {
		return new GlassButton(x, y, w, 20, () -> name + ": " + (get.getAsBoolean() ? "ON" : "OFF"), () -> {
			flip.run();
			LunaConfig.save();
		}).selected(get);
	}

	private void initModules(int cy, int inner) {
		int cols = 3;
		int bw = (inner - 4 * (cols - 1)) / cols;
		int i = 0;
		for (HudModule m : Modules.ALL) {
			int x = px + 14 + (i % cols) * (bw + 4);
			int y = cy + (i / cols) * 24;
			addRenderableWidget(new GlassButton(x, y, bw, 20,
				() -> m.name + ": " + (m.cfg().enabled ? "ON" : "OFF"),
				() -> {
					m.cfg().enabled = !m.cfg().enabled;
					LunaConfig.save();
				}).selected(() -> m.cfg().enabled));
			i++;
		}
		int rows = (Modules.ALL.size() + cols - 1) / cols;
		addRenderableWidget(toggle(px + 14, cy + rows * 24 + 8, inner, "Mostrar HUD",
			() -> LunaConfig.get().hudVisible,
			() -> LunaConfig.get().hudVisible = !LunaConfig.get().hudVisible));
	}

	private void initThemes(int cy, int inner) {
		Theme t = LunaConfig.get().theme;
		Theme[] presets = Theme.PRESETS;
		int perRow = 5;
		int bw = (inner - 4 * (perRow - 1)) / perRow;
		for (int i = 0; i < presets.length; i++) {
			final Theme p = presets[i];
			addRenderableWidget(new GlassButton(px + 14 + (i % perRow) * (bw + 4), cy + (i / perRow) * 24, bw, 20,
				() -> p.name, () -> {
					LunaConfig.get().theme.set(p);
					LunaConfig.save();
					rebuildWidgets();
				}).selected(() -> p.name.equals(LunaConfig.get().theme.name)));
		}

		int sw = (inner - 8) / 2;
		int lx = px + 14;
		int rx = px + 14 + sw + 8;
		int y1 = cy + ((presets.length + perRow - 1) / perRow) * 24 + 8;
		int y2 = y1 + 24;
		int y3 = y2 + 24;

		addRenderableWidget(new GlassSlider(lx, y1, sw, 20, 0, 255, (t.accent >> 16) & 255,
			v -> "Acento R: " + v.intValue(),
			v -> t.accent = (t.accent & 0xFF00FFFF) | (v.intValue() << 16)));
		addRenderableWidget(new GlassSlider(lx, y2, sw, 20, 0, 255, (t.accent >> 8) & 255,
			v -> "Acento G: " + v.intValue(),
			v -> t.accent = (t.accent & 0xFFFF00FF) | (v.intValue() << 8)));
		addRenderableWidget(new GlassSlider(lx, y3, sw, 20, 0, 255, t.accent & 255,
			v -> "Acento B: " + v.intValue(),
			v -> t.accent = (t.accent & 0xFFFFFF00) | v.intValue()));

		addRenderableWidget(new GlassSlider(rx, y1, sw, 20, 0, 255, Glass.alpha(t.panel),
			v -> "Opacidad vidrio: " + v.intValue(),
			v -> t.panel = Glass.withAlpha(t.panel, v.intValue())));
		addRenderableWidget(new GlassSlider(rx, y2, sw, 20, 0, 255, Glass.alpha(t.border),
			v -> "Opacidad borde: " + v.intValue(),
			v -> t.border = Glass.withAlpha(t.border, v.intValue())));
		addRenderableWidget(new GlassSlider(rx, y3, sw, 20, 0, 16, t.radius,
			v -> "Redondeo: " + v.intValue(),
			v -> t.radius = v.intValue()));

		addRenderableWidget(toggle(px + 14, y3 + 28, inner, "Botones de Minecraft en cristal",
			() -> LunaConfig.get().glassVanillaButtons,
			() -> LunaConfig.get().glassVanillaButtons = !LunaConfig.get().glassVanillaButtons));
	}

	private void initHud(int cy, int inner) {
		addRenderableWidget(new GlassButton(px + 14, cy, inner, 24,
			() -> "Abrir editor de HUD (arrastrar y cambiar tamaño)",
			() -> Minecraft.getInstance().gui.setScreen(new HudEditScreen(this))));
		addRenderableWidget(new GlassButton(px + 14, cy + 32, inner, 24,
			() -> "Restablecer posiciones y tamaños del HUD",
			() -> {
				LunaConfig.get().modules.clear();
				LunaConfig.save();
			}));
	}

	private void initVisual(int cy, int inner) {
		int sw = (inner - 8) / 2;
		int lx = px + 14;
		int rx = px + 14 + sw + 8;
		LunaConfig c = LunaConfig.get();

		addRenderableWidget(toggle(lx, cy, sw, "Fullbright (G)", () -> LunaConfig.get().fullbright,
			() -> LunaConfig.get().fullbright = !LunaConfig.get().fullbright));
		addRenderableWidget(toggle(rx, cy, sw, "Antilag", () -> LunaConfig.get().antilag,
			() -> LunaConfig.get().antilag = !LunaConfig.get().antilag));
		addRenderableWidget(toggle(lx, cy + 24, sw, "Fondo negro de menús", () -> LunaConfig.get().blackMenu,
			() -> LunaConfig.get().blackMenu = !LunaConfig.get().blackMenu));
		addRenderableWidget(new GlassSlider(rx, cy + 24, sw, 20, 30, 90, c.zoomFov,
			v -> "Zoom FOV (tecla C): " + v.intValue(),
			v -> LunaConfig.get().zoomFov = v.intValue()));
	}

	private void initCrosshair(int cy, int inner) {
		int sw = (inner - 8) / 2;
		int lx = px + 14;
		int rx = px + 14 + sw + 8;
		LunaConfig c = LunaConfig.get();
		int y1 = cy + 28;
		int y2 = y1 + 24;
		int y3 = y2 + 24;

		addRenderableWidget(toggle(lx, cy, sw, "Crosshair propio", () -> LunaConfig.get().crosshairEnabled,
			() -> LunaConfig.get().crosshairEnabled = !LunaConfig.get().crosshairEnabled));
		addRenderableWidget(new GlassButton(rx, cy, sw, 20,
			() -> "Estilo: " + CrosshairRenderer.STYLES[LunaConfig.get().crosshairStyle % CrosshairRenderer.STYLES.length],
			() -> {
				LunaConfig.get().crosshairStyle = (LunaConfig.get().crosshairStyle + 1) % CrosshairRenderer.STYLES.length;
				LunaConfig.save();
			}));

		addRenderableWidget(new GlassSlider(lx, y1, sw, 20, 1, 12, c.crosshairSize,
			v -> "Tamaño: " + v.intValue(), v -> LunaConfig.get().crosshairSize = v.intValue()));
		addRenderableWidget(new GlassSlider(lx, y2, sw, 20, 0, 10, c.crosshairGap,
			v -> "Separación: " + v.intValue(), v -> LunaConfig.get().crosshairGap = v.intValue()));
		addRenderableWidget(new GlassSlider(lx, y3, sw, 20, 1, 4, c.crosshairThickness,
			v -> "Grosor: " + v.intValue(), v -> LunaConfig.get().crosshairThickness = v.intValue()));

		addRenderableWidget(new GlassSlider(rx, y1, sw, 20, 0, 255, (c.crosshairColor >> 16) & 255,
			v -> "Color R: " + v.intValue(),
			v -> LunaConfig.get().crosshairColor = (LunaConfig.get().crosshairColor & 0xFF00FFFF) | (v.intValue() << 16)));
		addRenderableWidget(new GlassSlider(rx, y2, sw, 20, 0, 255, (c.crosshairColor >> 8) & 255,
			v -> "Color G: " + v.intValue(),
			v -> LunaConfig.get().crosshairColor = (LunaConfig.get().crosshairColor & 0xFFFF00FF) | (v.intValue() << 8)));
		addRenderableWidget(new GlassSlider(rx, y3, sw, 20, 0, 255, c.crosshairColor & 255,
			v -> "Color B: " + v.intValue(),
			v -> LunaConfig.get().crosshairColor = (LunaConfig.get().crosshairColor & 0xFFFFFF00) | v.intValue()));

		addRenderableWidget(toggle(lx, y3 + 28, sw, "Contorno negro", () -> LunaConfig.get().crosshairOutline,
			() -> LunaConfig.get().crosshairOutline = !LunaConfig.get().crosshairOutline));
		addRenderableWidget(toggle(rx, y3 + 28, sw, "Indicador de elytra", () -> LunaConfig.get().elytraIndicator,
			() -> LunaConfig.get().elytraIndicator = !LunaConfig.get().elytraIndicator));
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(g, mouseX, mouseY, partialTick);
		Theme t = LunaConfig.get().theme;
		Glass.panel(g, px, py, PW, PH, t.radius + 4, t.panel, t.border);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(g, mouseX, mouseY, partialTick);
		Theme t = LunaConfig.get().theme;
		g.text(font, "LUNA CLIENT", px + 14, py + 14, t.text, true);
		String hint = "Right Shift: abrir menú";
		g.text(font, hint, px + PW - 14 - font.width(hint), py + 14, Glass.withAlpha(t.text, 0xB0), true);
		int ty = py + 64 + 68;
		if (tab == Tab.HUD) {
			g.text(font, "En el editor: clic izq. arrastra, rueda = tamaño, clic der. = ON/OFF",
				px + 14, ty, Glass.withAlpha(t.text, 0xC0), false);
		} else if (tab == Tab.VISUAL) {
			int y = py + 64 + 60;
			int col = Glass.withAlpha(t.text, 0xC0);
			g.text(font, "Fullbright: visión nocturna solo en tu cliente.", px + 14, y, col, false);
			g.text(font, "Algunos servidores lo prohíben, úsalo bajo tu riesgo.", px + 14, y + 12, col, false);
			g.text(font, "Antilag: baja render, partículas, nubes y sombras.", px + 14, y + 24, col, false);
			g.text(font, "Al apagarlo se restauran tus valores originales.", px + 14, y + 36, col, false);
			g.text(font, "Zoom: mantén C. Fullbright: tecla G (cambiables en Controles).", px + 14, y + 48, col, false);
			g.text(font, "Fondo negro: pantalla de carga y menús fuera del mundo.", px + 14, y + 60, col, false);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void removed() {
		LunaConfig.save();
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().gui.setScreen(parent);
	}
}
