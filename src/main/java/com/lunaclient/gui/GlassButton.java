package com.lunaclient.gui;

import com.lunaclient.Glass;
import com.lunaclient.LunaConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/** Boton de cristal con texto dinamico y estado "seleccionado" opcional. */
public class GlassButton extends AbstractWidget {
	private final Supplier<String> label;
	private final Runnable action;
	private BooleanSupplier selected = () -> false;

	public GlassButton(int x, int y, int w, int h, Supplier<String> label, Runnable action) {
		super(x, y, w, h, Component.literal(label.get()));
		this.label = label;
		this.action = action;
	}

	public GlassButton selected(BooleanSupplier s) {
		this.selected = s;
		return this;
	}

	@Override
	public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		Glass.button(graphics, getX(), getY(), getWidth(), getHeight(), isHovered(), active,
			selected.getAsBoolean(), Component.literal(label.get()), LunaConfig.get().theme);
	}

	@Override
	public void onClick(MouseButtonEvent event, boolean doubleClick) {
		action.run();
	}

	@Override
	public void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}
}
