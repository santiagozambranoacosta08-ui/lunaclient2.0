package com.lunaclient.hud;

import com.lunaclient.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Muestra el icono de un item y cuantos tienes en el inventario (totems, cristales, obsidiana...). */
public class ItemModule extends HudModule {
	private final Item item;
	private final ItemStack icon;

	public ItemModule(String id, String name, int x, int y, Item item) {
		super(id, name, x, y);
		this.item = item;
		this.icon = new ItemStack(item);
	}

	private String count() {
		return "x" + Modules.count(item);
	}

	@Override
	public int width() {
		return 7 + 16 + 5 + Minecraft.getInstance().font.width(count()) + 8;
	}

	@Override
	public int height() {
		return 24;
	}

	@Override
	protected void content(GuiGraphicsExtractor g, Theme t) {
		g.item(icon, 7, 4);
		g.text(Minecraft.getInstance().font, count(), 28, 8, t.text, true);
	}
}
