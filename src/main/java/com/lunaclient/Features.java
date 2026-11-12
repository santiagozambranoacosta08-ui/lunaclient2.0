package com.lunaclient;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.LinkedHashMap;
import java.util.Map;

/** Funciones visuales y de rendimiento: zoom, fullbright, antilag y fondo negro. */
public final class Features {
	public static KeyMapping ZOOM_KEY;
	public static KeyMapping FULLBRIGHT_KEY;

	/** Opciones que baja el antilag: nombre de la opcion -> valor "ligero". */
	private static final Map<String, Object> LIGHT = new LinkedHashMap<>();

	static {
		LIGHT.put("renderDistance", 8);
		LIGHT.put("simulationDistance", 5);
		LIGHT.put("entityDistanceScaling", 0.5);
		LIGHT.put("biomeBlendRadius", 0);
		LIGHT.put("particles", "MINIMAL");
		LIGHT.put("cloudStatus", "OFF");
		LIGHT.put("entityShadows", false);
		LIGHT.put("ambientOcclusion", false);
		LIGHT.put("graphicsMode", "FAST");
	}

	private static Integer savedFov;
	private static boolean nightVisionApplied;
	private static Boolean lastAntilag;
	private static Boolean lastBlack;

	private Features() {
	}

	public static void tick(Minecraft mc) {
		zoom(mc);
		fullbright(mc);
		antilag();
		blackBackground();
	}

	// ---- Zoom (cambia el FOV mientras mantienes la tecla; no necesita mixins) ----
	private static void zoom(Minecraft mc) {
		var fov = mc.options.fov();
		boolean want = ZOOM_KEY != null && ZOOM_KEY.isDown() && noScreen(mc) && mc.player != null;
		int target = LunaConfig.get().zoomFov;
		if (want) {
			if (savedFov == null) {
				savedFov = fov.get();
			}
			int cur = fov.get();
			if (cur > target) {
				fov.set(Math.max(target, cur - 10));
			} else if (cur < target) {
				fov.set(target);
			}
		} else if (savedFov != null) {
			int cur = fov.get();
			if (cur < savedFov) {
				fov.set(Math.min(savedFov, cur + 10));
			} else {
				fov.set(savedFov);
				savedFov = null;
			}
		}
	}

	/** true si no hay ninguna pantalla abierta. Por reflexion, porque 26.2 movio `screen` de Minecraft a Gui. */
	private static boolean noScreen(Minecraft mc) {
		try {
			Object gui = mc.getClass().getField("gui").get(mc);
			for (String n : new String[] {"screen", "getScreen", "currentScreen"}) {
				try {
					return gui.getClass().getMethod(n).invoke(gui) == null;
				} catch (NoSuchMethodException ignored) {
				}
			}
			return gui.getClass().getField("screen").get(gui) == null;
		} catch (Throwable t) {
			return true;
		}
	}

	/** Deja el FOV como estaba (al cerrar el juego). */
	public static void restore(Minecraft mc) {
		if (savedFov != null) {
			mc.options.fov().set(savedFov);
			savedFov = null;
		}
	}

	// ---- Fullbright (efecto de vision nocturna solo en tu cliente, invisible) ----
	private static void fullbright(Minecraft mc) {
		if (mc.player == null) {
			return;
		}
		boolean on = LunaConfig.get().fullbright;
		if (on) {
			if (!mc.player.hasEffect(MobEffects.NIGHT_VISION)) {
				mc.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false, false));
				nightVisionApplied = true;
			}
		} else if (nightVisionApplied) {
			mc.player.removeEffect(MobEffects.NIGHT_VISION);
			nightVisionApplied = false;
		}
	}

	public static void toggleFullbright() {
		LunaConfig c = LunaConfig.get();
		c.fullbright = !c.fullbright;
		LunaConfig.save();
	}

	// ---- Antilag (baja graficos; al apagarlo restaura tus valores originales) ----
	private static void antilag() {
		LunaConfig c = LunaConfig.get();
		if (lastAntilag == null) {
			lastAntilag = c.antilag;
			return;
		}
		if (c.antilag == lastAntilag) {
			return;
		}
		lastAntilag = c.antilag;
		if (c.antilag) {
			c.antilagBackup.clear();
			for (Map.Entry<String, Object> e : LIGHT.entrySet()) {
				Object cur = Opt.get(e.getKey());
				if (cur != null && Opt.set(e.getKey(), e.getValue())) {
					c.antilagBackup.put(e.getKey(), Opt.str(cur));
				}
			}
		} else {
			for (Map.Entry<String, String> e : c.antilagBackup.entrySet()) {
				Opt.set(e.getKey(), e.getValue());
			}
			c.antilagBackup.clear();
		}
		LunaConfig.save();
	}

	// ---- Fondo negro de la pantalla de carga (opcion oficial de accesibilidad) ----
	private static void blackBackground() {
		boolean b = LunaConfig.get().blackMenu;
		if (lastBlack == null || lastBlack != b) {
			lastBlack = b;
			Opt.set("darkMojangStudiosBackground", b);
		}
	}
}
