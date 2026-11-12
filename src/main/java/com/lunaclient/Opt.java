package com.lunaclient;

import net.minecraft.client.Minecraft;

import java.lang.reflect.Method;

/**
 * Lee/escribe opciones de Minecraft por nombre usando reflexion.
 * Asi, si Mojang cambia o quita una opcion, el mod sigue compilando y simplemente la ignora.
 */
public final class Opt {
	private Opt() {
	}

	private static Object instance(String name) throws Exception {
		Object options = Minecraft.getInstance().options;
		Method m = options.getClass().getMethod(name);
		return m.invoke(options);
	}

	/** Valor actual de la opcion, o null si no existe. */
	public static Object get(String name) {
		try {
			Object oi = instance(name);
			return oi.getClass().getMethod("get").invoke(oi);
		} catch (Throwable t) {
			return null;
		}
	}

	/** Texto con el que se guarda el valor (para poder restaurarlo luego). */
	public static String str(Object v) {
		if (v instanceof Enum<?> e) {
			return e.name();
		}
		return String.valueOf(v);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static Object convert(Object cur, Object v) {
		if (cur instanceof Integer) {
			return v instanceof Number n ? n.intValue() : Integer.parseInt(String.valueOf(v));
		}
		if (cur instanceof Double) {
			return v instanceof Number n ? n.doubleValue() : Double.parseDouble(String.valueOf(v));
		}
		if (cur instanceof Boolean) {
			return v instanceof Boolean ? v : Boolean.parseBoolean(String.valueOf(v));
		}
		if (cur instanceof Enum<?> e) {
			return Enum.valueOf((Class) e.getDeclaringClass(), String.valueOf(v));
		}
		return null;
	}

	/** Cambia la opcion. Devuelve false si no existe o el valor no sirve. */
	public static boolean set(String name, Object v) {
		try {
			Object oi = instance(name);
			Object cur = oi.getClass().getMethod("get").invoke(oi);
			Object val = convert(cur, v);
			if (val == null) {
				return false;
			}
			oi.getClass().getMethod("set", Object.class).invoke(oi, val);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
}
