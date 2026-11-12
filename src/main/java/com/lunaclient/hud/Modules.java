package com.lunaclient.hud;

import com.lunaclient.Cps;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Lista de todos los modulos del HUD pensados para crystal PvP. */
public final class Modules {
	public static final List<HudModule> ALL = new ArrayList<>();

	static {
		ALL.add(new TextModule("fps", "FPS", 6, 6, () -> Minecraft.getInstance().getFps() + " FPS"));
		ALL.add(new TextModule("cps", "CPS", 6, 30, () -> Cps.get(0) + " | " + Cps.get(1) + " CPS"));
		ALL.add(new TextModule("ping", "Ping", 6, 54, Modules::ping));
		ALL.add(new TextModule("coords", "Coordenadas", 6, 78, Modules::coords));
		ALL.add(new ItemModule("totems", "Tótems", 6, 102, Items.TOTEM_OF_UNDYING));
		ALL.add(new ItemModule("crystals", "Cristales", 6, 130, Items.END_CRYSTAL));
		ALL.add(new ItemModule("obsidian", "Obsidiana", 6, 158, Items.OBSIDIAN));
		ALL.add(new KeystrokesModule(6, 188));
		ALL.add(new TextModule("hunger", "Hambre (AppleSkin)", 130, 6, Modules::hunger));
		ALL.add(new TextModule("direction", "Dirección", 130, 30, Modules::direction));
		ALL.add(new TextModule("speed", "Velocidad", 130, 54, Modules::speed));
		ALL.add(new TextModule("memory", "Memoria RAM", 130, 78, Modules::memory));
		ALL.add(new TextModule("clock", "Reloj", 130, 102, Modules::clock));
		ALL.add(new ArmorModule(130, 126));
		ALL.add(new PotionModule(240, 6));
	}

	private Modules() {
	}

	public static int count(Item item) {
		Player p = Minecraft.getInstance().player;
		if (p == null) {
			return 0;
		}
		int n = 0;
		Inventory inv = p.getInventory();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack s = inv.getItem(i);
			if (s.is(item)) {
				n += s.getCount();
			}
		}
		return n;
	}

	private static String ping() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.getConnection() == null) {
			return "-- ms";
		}
		PlayerInfo e = mc.getConnection().getPlayerInfo(mc.player.getUUID());
		return (e == null ? 0 : e.getLatency()) + " ms";
	}

	private static String coords() {
		Player p = Minecraft.getInstance().player;
		if (p == null) {
			return "X 0  Y 0  Z 0";
		}
		return "X " + p.getBlockX() + "  Y " + p.getBlockY() + "  Z " + p.getBlockZ();
	}

	private static String hunger() {
		Player p = Minecraft.getInstance().player;
		if (p == null) {
			return "Hambre 20 | Sat 5.0";
		}
		var fd = p.getFoodData();
		String s = "Hambre " + fd.getFoodLevel() + " | Sat " + String.format(Locale.ROOT, "%.1f", fd.getSaturationLevel());
		var food = p.getMainHandItem().get(DataComponents.FOOD);
		if (food != null) {
			s += " | +" + food.nutrition();
		}
		return s;
	}

	private static String direction() {
		Player p = Minecraft.getInstance().player;
		if (p == null) {
			return "S (0°)";
		}
		float yaw = ((p.getYRot() % 360f) + 360f) % 360f;
		String[] names = {"S", "SO", "O", "NO", "N", "NE", "E", "SE"};
		int i = Math.round(yaw / 45f) % 8;
		return names[i] + " (" + Math.round(yaw) + "°)";
	}

	private static String speed() {
		Player p = Minecraft.getInstance().player;
		if (p == null) {
			return "0.0 b/s";
		}
		return String.format(Locale.ROOT, "%.1f b/s", p.getDeltaMovement().horizontalDistance() * 20.0);
	}

	private static String memory() {
		Runtime r = Runtime.getRuntime();
		long used = (r.totalMemory() - r.freeMemory()) / 1048576L;
		long max = r.maxMemory() / 1048576L;
		return "RAM " + used + "/" + max + " MB";
	}

	private static String clock() {
		return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
	}
}
