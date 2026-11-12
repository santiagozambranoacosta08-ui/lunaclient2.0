package com.lunaclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

import java.util.ArrayDeque;

/** Contador de clics por segundo. Se consulta cada frame (poll) y cuenta cada vez que el boton pasa a "presionado". */
public final class Cps {
	private static final ArrayDeque<Long> LEFT = new ArrayDeque<>();
	private static final ArrayDeque<Long> RIGHT = new ArrayDeque<>();
	private static boolean lastLeft;
	private static boolean lastRight;

	private Cps() {
	}

	public static void poll() {
		Options o = Minecraft.getInstance().options;
		boolean l = o.keyAttack.isDown();
		boolean r = o.keyUse.isDown();
		long now = System.currentTimeMillis();
		if (l && !lastLeft) {
			LEFT.addLast(now);
		}
		if (r && !lastRight) {
			RIGHT.addLast(now);
		}
		lastLeft = l;
		lastRight = r;
	}

	public static int get(int button) {
		ArrayDeque<Long> q = button == 0 ? LEFT : RIGHT;
		long now = System.currentTimeMillis();
		while (!q.isEmpty() && now - q.peekFirst() > 1000) {
			q.removeFirst();
		}
		return q.size();
	}
}
