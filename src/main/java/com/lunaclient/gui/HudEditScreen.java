package com.lunaclient.gui;

import com.lunaclient.Glass;
import com.lunaclient.LunaConfig;
import com.lunaclient.Theme;
import com.lunaclient.hud.HudModule;
import com.lunaclient.hud.Modules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.List;

/** Editor de HUD: arrastra los modulos, rueda del mouse = tamaño, clic derecho = activar/desactivar. */
public class HudEditScreen extends Screen {
	/** Mientras el editor esta abierto, el HUD normal no se dibuja (lo dibuja el editor). */
	public static boolean OPEN = false;

	private final Screen parent;
	private HudModule drag;
	private int offX;
	private int offY;

	public HudEditScreen(Screen parent) {
		super(Component.literal("Editor de HUD"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		OPEN = true;
	}

	private HudModule moduleAt(double mx, double my) {
		List<HudModule> all = Modules.ALL;
		for (int i = all.size() - 1; i >= 0; i--) {
			if (all.get(i).hit(mx, my)) {
				return all.get(i);
			}
		}
		return null;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(g, mouseX, mouseY, partialTick);
		Theme t = LunaConfig.get().theme;
		HudModule hover = drag != null ? drag : moduleAt(mouseX, mouseY);
		for (HudModule m : Modules.ALL) {
			m.render(g, true);
			LunaConfig.ModuleCfg c = m.cfg();
			int w = (int) (m.width() * c.scale);
			int h = (int) (m.height() * c.scale);
			Glass.roundBorder(g, c.x - 2, c.y - 2, w + 4, h + 4, 4,
				m == hover ? t.accent : Glass.withAlpha(t.border, 0x70));
			if (m == hover) {
				String info = m.name + (c.enabled ? "" : " (OFF)") + "  x" + String.format("%.1f", c.scale);
				g.text(font, info, c.x, c.y - 12, t.text, true);
			}
		}
		String hint = "Arrastra: mover  |  Rueda: tamaño  |  Clic derecho: ON/OFF  |  Esc: guardar y volver";
		g.centeredText(font, Component.literal(hint), width / 2, height - 16, t.text);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		HudModule m = moduleAt(event.x(), event.y());
		if (m == null) {
			return super.mouseClicked(event, doubleClick);
		}
		if (event.button() == 0) {
			drag = m;
			offX = (int) event.x() - m.cfg().x;
			offY = (int) event.y() - m.cfg().y;
		} else if (event.button() == 1) {
			m.cfg().enabled = !m.cfg().enabled;
		}
		return true;
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
		if (drag != null && event.button() == 0) {
			LunaConfig.ModuleCfg c = drag.cfg();
			int w = (int) (drag.width() * c.scale);
			int h = (int) (drag.height() * c.scale);
			c.x = Mth.clamp((int) event.x() - offX, 0, Math.max(0, width - w));
			c.y = Mth.clamp((int) event.y() - offY, 0, Math.max(0, height - h));
			return true;
		}
		return super.mouseDragged(event, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (drag != null) {
			drag = null;
			LunaConfig.save();
			return true;
		}
		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		HudModule m = drag != null ? drag : moduleAt(mouseX, mouseY);
		if (m != null) {
			LunaConfig.ModuleCfg c = m.cfg();
			c.scale = Mth.clamp(Math.round((c.scale + (float) scrollY * 0.1f) * 10f) / 10f, 0.5f, 2.5f);
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void removed() {
		OPEN = false;
		LunaConfig.save();
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().gui.setScreen(parent);
	}
}
