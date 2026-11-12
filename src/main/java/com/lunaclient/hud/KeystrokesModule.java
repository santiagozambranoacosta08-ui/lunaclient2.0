package com.lunaclient.hud;

import com.lunaclient.Cps;
import com.lunaclient.Glass;
import com.lunaclient.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Teclas WASD + clics del mouse con CPS. Cada tecla se ilumina con el color de acento al presionarla. */
public class KeystrokesModule extends HudModule {
	public KeystrokesModule(int x, int y) {
		super("keystrokes", "Keystrokes", x, y);
	}

	@Override
	public int width() {
		return 64;
	}

	@Override
	public int height() {
		return 64;
	}

	@Override
	protected boolean background() {
		return false;
	}

	@Override
	protected void content(GuiGraphicsExtractor g, Theme t) {
		Options o = Minecraft.getInstance().options;
		key(g, t, 22, 0, 20, 20, "W", o.keyUp.isDown());
		key(g, t, 0, 22, 20, 20, "A", o.keyLeft.isDown());
		key(g, t, 22, 22, 20, 20, "S", o.keyDown.isDown());
		key(g, t, 44, 22, 20, 20, "D", o.keyRight.isDown());
		key(g, t, 0, 44, 31, 20, "L " + Cps.get(0), o.keyAttack.isDown());
		key(g, t, 33, 44, 31, 20, "R " + Cps.get(1), o.keyUse.isDown());
	}

	private void key(GuiGraphicsExtractor g, Theme t, int x, int y, int w, int h, String label, boolean down) {
		int fill = down ? Glass.withAlpha(t.accent, 0xA0) : t.panel;
		Glass.panel(g, x, y, w, h, Math.min(t.radius, 5), fill, down ? t.accent : t.border);
		Font font = Minecraft.getInstance().font;
		g.text(font, label, x + (w - font.width(label)) / 2, y + (h - 8) / 2, t.text, true);
	}
}
