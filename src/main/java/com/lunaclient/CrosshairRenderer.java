package com.lunaclient;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Locale;

/** Crosshair personalizable con indicador de elytra. */
public final class CrosshairRenderer {
	public static final String[] STYLES = {"Cruz", "Punto", "Círculo", "Cruz + punto"};
	private static final ItemStack ELYTRA = new ItemStack(Items.ELYTRA);
	private static Method originalMethod;

	private CrosshairRenderer() {
	}

	/** Llama al crosshair original de Minecraft (por reflexion, para no depender del nombre del metodo). */
	public static void callOriginal(HudElement original, Object graphics, Object tick) {
		try {
			if (originalMethod == null) {
				for (Method m : HudElement.class.getMethods()) {
					if (Modifier.isAbstract(m.getModifiers())) {
						originalMethod = m;
						break;
					}
				}
			}
			if (originalMethod != null) {
				originalMethod.invoke(original, graphics, tick);
			}
		} catch (Throwable ignored) {
		}
	}

	public static void render(GuiGraphicsExtractor g) {
		Minecraft mc = Minecraft.getInstance();
		Player p = mc.player;
		if (p == null || !mc.options.getCameraType().isFirstPerson()) {
			return;
		}
		LunaConfig c = LunaConfig.get();
		int cx = mc.getWindow().getGuiScaledWidth() / 2;
		int cy = mc.getWindow().getGuiScaledHeight() / 2;

		ItemStack chest = p.getItemBySlot(EquipmentSlot.CHEST);
		boolean hasElytra = c.elytraIndicator && chest.is(Items.ELYTRA);
		boolean flying = hasElytra && p.isFallFlying();

		int color = flying ? LunaConfig.get().theme.accent : c.crosshairColor;
		draw(g, cx, cy, c, color);

		if (hasElytra) {
			int reach = c.crosshairGap + c.crosshairSize + 6;
			// icono de elytra pequeño a la derecha del crosshair
			g.pose().pushMatrix();
			g.pose().translate((float) (cx + reach), (float) (cy - 4));
			g.pose().scale(0.5f, 0.5f);
			g.item(ELYTRA, 0, 0);
			g.pose().popMatrix();

			// barra de durabilidad debajo
			int max = chest.getMaxDamage();
			float frac = max <= 0 ? 1f : 1f - (float) chest.getDamageValue() / max;
			frac = Math.max(0f, Math.min(1f, frac));
			int bw = 22;
			int bx = cx - bw / 2;
			int by = cy + reach + 2;
			g.fill(bx - 1, by - 1, bx + bw + 1, by + 3, 0xAA000000);
			int r = (int) (255 * (1f - frac));
			int gr = (int) (255 * frac);
			g.fill(bx, by, bx + Math.max(1, (int) (bw * frac)), by + 2, 0xFF000000 | (r << 16) | (gr << 8) | 0x40);

			if (flying) {
				double speed = p.getDeltaMovement().length() * 20.0;
				String s = String.format(Locale.ROOT, "%.0f b/s", speed);
				g.centeredText(mc.font, Component.literal(s), cx, by + 6, c.crosshairColor);
			}
		}
	}

	private static void rect(GuiGraphicsExtractor g, int x1, int y1, int x2, int y2, int color, int grow) {
		g.fill(x1 - grow, y1 - grow, x2 + grow, y2 + grow, color);
	}

	private static void shape(GuiGraphicsExtractor g, int cx, int cy, LunaConfig c, int color, int grow) {
		int size = c.crosshairSize;
		int gap = c.crosshairGap;
		int t = c.crosshairThickness;
		int lo = -(t / 2);
		int hi = lo + t;
		switch (c.crosshairStyle) {
			case 1 -> rect(g, cx + lo - 1, cy + lo - 1, cx + hi + 1, cy + hi + 1, color, grow);
			case 2 -> {
				int rad = Math.max(2, size + gap);
				Glass.roundBorder(g, cx - rad - grow, cy - rad - grow, (rad + grow) * 2, (rad + grow) * 2, rad + grow, color);
			}
			default -> {
				rect(g, cx - gap - size, cy + lo, cx - gap, cy + hi, color, grow);
				rect(g, cx + gap, cy + lo, cx + gap + size, cy + hi, color, grow);
				rect(g, cx + lo, cy - gap - size, cx + hi, cy - gap, color, grow);
				rect(g, cx + lo, cy + gap, cx + hi, cy + gap + size, color, grow);
				if (c.crosshairStyle == 3) {
					rect(g, cx + lo, cy + lo, cx + hi, cy + hi, color, grow);
				}
			}
		}
	}

	public static void draw(GuiGraphicsExtractor g, int cx, int cy, LunaConfig c, int color) {
		if (c.crosshairOutline) {
			shape(g, cx, cy, c, 0xFF000000, 1);
		}
		shape(g, cx, cy, c, color | 0xFF000000, 0);
	}
}
