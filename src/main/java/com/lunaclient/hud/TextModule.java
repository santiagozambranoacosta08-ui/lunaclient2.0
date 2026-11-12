package com.lunaclient.hud;

import com.lunaclient.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.function.Supplier;

public class TextModule extends HudModule {
	private final Supplier<String> text;

	public TextModule(String id, String name, int x, int y, Supplier<String> text) {
		super(id, name, x, y);
		this.text = text;
	}

	@Override
	public int width() {
		return Minecraft.getInstance().font.width(text.get()) + 14;
	}

	@Override
	public int height() {
		return 20;
	}

	@Override
	protected void content(GuiGraphicsExtractor g, Theme t) {
		g.text(Minecraft.getInstance().font, text.get(), 7, 6, t.text, true);
	}
}
