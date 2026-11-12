package com.lunaclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/** Dibujo de paneles y botones "cristal": relleno translucido, brillo superior y borde redondeado. */
public final class Glass {
	private Glass() {
	}

	public static int alpha(int c) {
		return c >>> 24;
	}

	public static int withAlpha(int c, int a) {
		return (Math.max(0, Math.min(255, a)) << 24) | (c & 0xFFFFFF);
	}

	public static int mixWhite(int c, float f) {
		int r = (c >> 16) & 255, g = (c >> 8) & 255, b = c & 255;
		r += (int) ((255 - r) * f);
		g += (int) ((255 - g) * f);
		b += (int) ((255 - b) * f);
		return (c & 0xFF000000) | (r << 16) | (g << 8) | b;
	}

	private static int inset(int r, int i) {
		double dy = r - i - 0.5;
		double dx = Math.sqrt(Math.max(0, r * r - dy * dy));
		return Math.max(0, r - (int) Math.round(dx));
	}

	/** Rectangulo redondeado sin solapar pixeles (para que la transparencia no se acumule). */
	public static void roundRect(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int color) {
		r = Math.max(0, Math.min(r, Math.min(w, h) / 2));
		if (r == 0) {
			g.fill(x, y, x + w, y + h, color);
			return;
		}
		g.fill(x + r, y, x + w - r, y + h, color);
		g.fill(x, y + r, x + r, y + h - r, color);
		g.fill(x + w - r, y + r, x + w, y + h - r, color);
		for (int i = 0; i < r; i++) {
			int in = inset(r, i);
			g.fill(x + in, y + i, x + r, y + i + 1, color);
			g.fill(x + w - r, y + i, x + w - in, y + i + 1, color);
			g.fill(x + in, y + h - 1 - i, x + r, y + h - i, color);
			g.fill(x + w - r, y + h - 1 - i, x + w - in, y + h - i, color);
		}
	}

	public static void roundBorder(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int color) {
		r = Math.max(0, Math.min(r, Math.min(w, h) / 2));
		if (r == 0) {
			g.fill(x, y, x + w, y + 1, color);
			g.fill(x, y + h - 1, x + w, y + h, color);
			g.fill(x, y + 1, x + 1, y + h - 1, color);
			g.fill(x + w - 1, y + 1, x + w, y + h - 1, color);
			return;
		}
		for (int i = 0; i < r; i++) {
			int in = inset(r, i);
			if (i == 0) {
				g.fill(x + in, y, x + w - in, y + 1, color);
				g.fill(x + in, y + h - 1, x + w - in, y + h, color);
			} else {
				int end = Math.max(in + 1, inset(r, i - 1));
				g.fill(x + in, y + i, x + end, y + i + 1, color);
				g.fill(x + w - end, y + i, x + w - in, y + i + 1, color);
				g.fill(x + in, y + h - 1 - i, x + end, y + h - i, color);
				g.fill(x + w - end, y + h - 1 - i, x + w - in, y + h - i, color);
			}
		}
		g.fill(x, y + r, x + 1, y + h - r, color);
		g.fill(x + w - 1, y + r, x + w, y + h - r, color);
	}

	public static void panel(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int fill, int border) {
		roundRect(g, x, y, w, h, r, fill);
		int rr = Math.max(0, Math.min(r, Math.min(w, h) / 2));
		if (w - 2 * rr > 0) {
			g.fillGradient(x + rr, y + 1, x + w - rr, y + Math.max(2, h / 2), 0x38FFFFFF, 0x00FFFFFF);
		}
		roundBorder(g, x, y, w, h, r, border);
	}

	public static void button(GuiGraphicsExtractor g, int x, int y, int w, int h, boolean hover, boolean active,
							  boolean selected, Component label, Theme t) {
		int r = Math.min(t.radius, h / 2);
		int fill = t.panel;
		if (selected) {
			fill = withAlpha(t.accent, Math.max(alpha(t.panel), 0x70));
		}
		if (hover && active) {
			fill = withAlpha(mixWhite(fill, 0.25f), Math.min(255, alpha(fill) + 50));
		}
		if (!active) {
			fill = withAlpha(fill, alpha(fill) / 2);
		}
		int border = (hover || selected) && active ? t.accent : t.border;
		panel(g, x, y, w, h, r, fill, border);
		Minecraft mc = Minecraft.getInstance();
		int color = active ? t.text : 0xFF8A8A8A;
		g.centeredText(mc.font, label, x + w / 2, y + (h - 8) / 2, color);
	}
}
