package com.gentcent.wechat.enhancement.util;

import android.app.Activity;

import com.gentcent.wechat.enhancement.AcceptReceiver;

import de.robv.android.xposed.callbacks.XC_LoadPackage.*;

/**
 * @author zuozhi
 * @since 2019-07-09
 */
public class StaticDepot {
	public static Activity activity;
	public static LoadPackageParam wxLpparam;
	public static AcceptReceiver acceptReceiver;
}
