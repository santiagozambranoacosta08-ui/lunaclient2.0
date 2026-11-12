package com.lunaclient.hud;

import com.lunaclient.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Durabilidad exacta de la armadura y del item en la mano. */
public class ArmorModule extends HudModule {
	private static final EquipmentSlot[] SLOTS = {
		EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND
	};

	public ArmorModule(int x, int y) {
		super("armor", "Armadura", x, y);
	}

	@Override
	public int width() {
		return 70;
	}

	@Override
	public int height() {
		return SLOTS.length * 18 + 6;
	}

	@Override
	protected void content(GuiGraphicsExtractor g, Theme t) {
		Player p = Minecraft.getInstance().player;
		if (p == null) {
			return;
		}
		for (int i = 0; i < SLOTS.length; i++) {
			ItemStack s = p.getItemBySlot(SLOTS[i]);
			if (s.isEmpty()) {
				continue;
			}
			int y = 4 + i * 18;
			g.item(s, 6, y);
			String text;
			int color = t.text;
			if (s.isDamageableItem()) {
				int max = s.getMaxDamage();
				int left = max - s.getDamageValue();
				float frac = max <= 0 ? 1f : (float) left / max;
				text = String.valueOf(left);
				color = frac > 0.5f ? 0xFF55FF55 : frac > 0.2f ? 0xFFFFFF55 : 0xFFFF5555;
			} else {
				text = s.getCount() > 1 ? "x" + s.getCount() : "";
			}
			g.text(Minecraft.getInstance().font, text, 28, y + 4, color, true);
		}
	}
}
