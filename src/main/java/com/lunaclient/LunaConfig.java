package com.lunaclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** Configuracion guardada en .minecraft/config/lunaclient.json */
public class LunaConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static LunaConfig inst;

	public Theme theme = Theme.PRESETS[0].copy();
	public boolean glassVanillaButtons = true;
	public boolean hudVisible = true;
	public Map<String, ModuleCfg> modules = new LinkedHashMap<>();

	// Crosshair
	public boolean crosshairEnabled = false;
	public int crosshairStyle = 0;
	public int crosshairSize = 5;
	public int crosshairGap = 3;
	public int crosshairThickness = 1;
	public int crosshairColor = 0xFFFFFFFF;
	public boolean crosshairOutline = true;
	public boolean elytraIndicator = true;

	// Visual / rendimiento
	public boolean fullbright = false;
	public boolean antilag = false;
	public boolean blackMenu = false;
	public int zoomFov = 30;
	public Map<String, String> antilagBackup = new LinkedHashMap<>();

	public static class ModuleCfg {
		public boolean enabled = true;
		public int x;
		public int y;
		public float scale = 1f;
	}

	public static LunaConfig get() {
		if (inst == null) {
			load();
		}
		return inst;
	}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("lunaclient.json");
	}

	public static void load() {
		try {
			Path p = path();
			if (Files.exists(p)) {
				inst = GSON.fromJson(Files.readString(p), LunaConfig.class);
			}
		} catch (Exception ignored) {
			inst = null;
		}
		if (inst == null) {
			inst = new LunaConfig();
		}
		if (inst.theme == null) {
			inst.theme = Theme.PRESETS[0].copy();
		}
		if (inst.modules == null) {
			inst.modules = new LinkedHashMap<>();
		}
		if (inst.antilagBackup == null) {
			inst.antilagBackup = new LinkedHashMap<>();
		}
	}

	public static void save() {
		try {
			Files.writeString(path(), GSON.toJson(get()));
		} catch (Exception ignored) {
		}
	}
}
