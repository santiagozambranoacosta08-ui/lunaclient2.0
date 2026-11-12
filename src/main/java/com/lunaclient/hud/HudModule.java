package com.lunaclient.hud;

import com.lunaclient.Glass;
import com.lunaclient.LunaConfig;
import com.lunaclient.Theme;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Un elemento del HUD (FPS, CPS, contador de totems, etc.) con posicion, escala y on/off editables. */
public abstract class HudModule {
	public final String id;
	public final String name;
	/** true mientras se dibuja dentro del editor de HUD. */
	protected boolean editing;
	private final int defX;
	private final int defY;

	protected HudModule(String id, String name, int defX, int defY) {
		this.id = id;
		this.name = name;
		this.defX = defX;
		this.defY = defY;
	}

	public abstract int width();

	public abstract int height();

	/** Dibuja el contenido en coordenadas locales (0,0 = esquina del modulo). */
	protected abstract void content(GuiGraphicsExtractor g, Theme t);

	protected boolean background() {
		return true;
	}

	public LunaConfig.ModuleCfg cfg() {
		return LunaConfig.get().modules.computeIfAbsent(id, k -> {
			LunaConfig.ModuleCfg c = new LunaConfig.ModuleCfg();
			c.x = defX;
			c.y = defY;
			return c;
		});
	}

	public boolean hit(double mx, double my) {
		LunaConfig.ModuleCfg c = cfg();
		return mx >= c.x && mx <= c.x + width() * c.scale && my >= c.y && my <= c.y + height() * c.scale;
	}

	public void render(GuiGraphicsExtractor g, boolean editing) {
		this.editing = editing;
		LunaConfig.ModuleCfg c = cfg();
		if (!c.enabled && !editing) {
			return;
		}
		Theme t = LunaConfig.get().theme;
		int w = width();
		int h = height();
		g.pose().pushMatrix();
		g.pose().translate((float) c.x, (float) c.y);
		g.pose().scale(c.scale, c.scale);
		if (background()) {
			Glass.panel(g, 0, 0, w, h, Math.min(t.radius, 8), t.panel, t.border);
		}
		content(g, t);
		if (!c.enabled) {
			Glass.roundRect(g, 0, 0, w, h, Math.min(t.radius, 8), 0x99000000);
		}
		g.pose().popMatrix();
	}
}
