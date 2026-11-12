package com.lunaclient.gui;

import com.lunaclient.Glass;
import com.lunaclient.LunaConfig;
import com.lunaclient.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.Function;

/** Slider de cristal. El valor se aplica en vivo y se guarda al soltar el mouse. */
public class GlassSlider extends AbstractWidget {
	private final float min;
	private final float max;
	private float value;
	private final Function<Float, String> format;
	private final Consumer<Float> onChange;

	public GlassSlider(int x, int y, int w, int h, float min, float max, float initial,
					   Function<Float, String> format, Consumer<Float> onChange) {
		super(x, y, w, h, Component.empty());
		this.min = min;
		this.max = max;
		this.value = Mth.clamp(initial, min, max);
		this.format = format;
		this.onChange = onChange;
	}

	private void setFromMouse(double mouseX) {
		float f = (float) ((mouseX - (getX() + 3)) / (getWidth() - 6));
		f = Mth.clamp(f, 0f, 1f);
		value = Math.round(min + f * (max - min));
		onChange.accept(value);
	}

	@Override
	public void extractWidgetRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		Theme t = LunaConfig.get().theme;
		int r = Math.min(t.radius, getHeight() / 2);
		int border = isHovered() ? t.accent : t.border;
		Glass.panel(g, getX(), getY(), getWidth(), getHeight(), r, t.panel, border);
		float frac = (value - min) / (max - min);
		int fillW = (int) ((getWidth() - 4) * frac);
		if (fillW > 0) {
			Glass.roundRect(g, getX() + 2, getY() + 2, fillW, getHeight() - 4, Math.max(0, r - 2),
				Glass.withAlpha(t.accent, 0x80));
		}
		Font font = Minecraft.getInstance().font;
		String s = format.apply(value);
		g.text(font, s, getX() + (getWidth() - font.width(s)) / 2, getY() + (getHeight() - 8) / 2, t.text, true);
	}

	@Override
	public void onClick(MouseButtonEvent event, boolean doubleClick) {
		setFromMouse(event.x());
	}

	@Override
	public void onDrag(MouseButtonEvent event, double dragX, double dragY) {
		setFromMouse(event.x());
	}

	@Override
	public void onRelease(MouseButtonEvent event) {
		LunaConfig.save();
	}

	@Override
	public void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}
}
