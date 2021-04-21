package com.lipeng.common.catmonitor;

import com.dianping.cat.Cat;
import com.lipeng.common.catmonitor.registry.CatLogger;

public class DubboCat {

	private static boolean isEnable = true;

	/**
	 * 禁用dubbo cat
	 */
	public static void disable() {
		isEnable = false;
	}

	/**
	 * 启用dubbo cat
	 */
	public static void enable() {
		isEnable = true;
	}

	/**
	 * 是否有效
	 *
	 * @return
	 */
	public static boolean isEnable() {
		boolean isCatEnabled = false;
		try {
			isCatEnabled = Cat.getManager().isCatEnabled();
		} catch (Throwable e) {
			CatLogger.getInstance().error("[DUBBO] Cat init error.", e);
		}
		return isCatEnabled && isEnable;
	}

}