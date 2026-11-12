package com.lunaclient.hud;

import com.lunaclient.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/** Efectos de pocion activos con nivel y tiempo restante. */
public class PotionModule extends HudModule {
	public PotionModule(int x, int y) {
		super("potions", "Efectos de poción", x, y);
	}

	private List<String> lines() {
		List<String> r = new ArrayList<>();
		Player p = Minecraft.getInstance().player;
		if (p == null) {
			return r;
		}
		for (MobEffectInstance e : p.getActiveEffects()) {
			if (!e.isVisible()) {
				continue;
			}
			String name = e.getEffect().value().getDisplayName().getString();
			int amp = e.getAmplifier() + 1;
			int d = e.getDuration();
			String time = d < 0 ? "--" : String.format("%d:%02d", d / 1200, (d / 20) % 60);
			r.add(name + (amp > 1 ? " " + amp : "") + "  " + time);
		}
		return r;
	}

	@Override
	protected boolean background() {
		return editing || !lines().isEmpty();
	}

	@Override
	public int width() {
		int w = 90;
		for (String s : lines()) {
			w = Math.max(w, Minecraft.getInstance().font.width(s) + 14);
		}
		return w;
	}

	@Override
	public int height() {
		return Math.max(20, lines().size() * 12 + 8);
	}

	@Override
	protected void content(GuiGraphicsExtractor g, Theme t) {
		List<String> l = lines();
		if (l.isEmpty()) {
			if (editing) {
				g.text(Minecraft.getInstance().font, "Pociones", 7, 6, t.text, true);
			}
			return;
		}
		for (int i = 0; i < l.size(); i++) {
			g.text(Minecraft.getInstance().font, l.get(i), 7, 5 + i * 12, t.text, true);
		}
	}
}
