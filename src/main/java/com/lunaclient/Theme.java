package com.lunaclient;

/** Tema visual. Todos los colores son ARGB. Es mutable: los sliders lo editan en vivo. */
public class Theme {
	public String name = "Cristal";
	public int panel = 0x55FFFFFF;
	public int border = 0xAAFFFFFF;
	public int text = 0xFFFFFFFF;
	public int accent = 0xFF7FD6FF;
	public int radius = 8;

	public Theme() {
	}

	public Theme(String name, int panel, int border, int text, int accent, int radius) {
		this.name = name;
		this.panel = panel;
		this.border = border;
		this.text = text;
		this.accent = accent;
		this.radius = radius;
	}

	public Theme copy() {
		return new Theme(name, panel, border, text, accent, radius);
	}

	public void set(Theme o) {
		name = o.name;
		panel = o.panel;
		border = o.border;
		text = o.text;
		accent = o.accent;
		radius = o.radius;
	}

	public static final Theme[] PRESETS = {
		new Theme("Cristal", 0x55FFFFFF, 0xAAFFFFFF, 0xFFFFFFFF, 0xFF7FD6FF, 8),
		new Theme("Luna", 0xB0080808, 0x99FFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 10),
		new Theme("Hielo", 0x6690C8FF, 0xBBCFEFFF, 0xFFFFFFFF, 0xFF4FC3FF, 6),
		new Theme("Sakura", 0x66FFA0CC, 0xBBFFD6E8, 0xFFFFFFFF, 0xFFFF6FB0, 10),
		new Theme("Neón", 0xAA14002A, 0xCCB04DFF, 0xFFFFFFFF, 0xFFB04DFF, 4),
		new Theme("Medianoche", 0xC00A0F1E, 0xAA3A5BFF, 0xFFFFFFFF, 0xFF5B7CFF, 8),
		new Theme("Océano", 0x6630B8D8, 0xBB9BE8F5, 0xFFFFFFFF, 0xFF00E5FF, 8),
		new Theme("Fuego", 0xAA2A0A00, 0xCCFF7A1A, 0xFFFFFFFF, 0xFFFF5A00, 4),
		new Theme("Bosque", 0x9910301A, 0xBB5EDC7A, 0xFFFFFFFF, 0xFF3DDC6B, 6),
		new Theme("Oro", 0xAA1A1405, 0xCCFFD24D, 0xFFFFFFFF, 0xFFFFC21A, 6),
	};
}
