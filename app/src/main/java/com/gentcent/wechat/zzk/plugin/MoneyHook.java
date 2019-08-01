package com.gentcent.wechat.zzk.plugin;

import com.gentcent.wechat.zzk.wallet.LuckyMoney;
import com.gentcent.wechat.zzk.wallet.MyKeyboardWindow;
import com.gentcent.wechat.zzk.wallet.Remittance;
import com.gentcent.wechat.zzk.wallet.WalletBaseUI;
import com.gentcent.wechat.zzk.wallet.WalletChangeBankcardUI;
import com.gentcent.wechat.zzk.wallet.WalletPayUI;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * @author zuozhi
 * @since 2019-07-25
 */
public class MoneyHook implements IPlugin {
	@Override
	public void hook(LoadPackageParam lpparam) {
		MyKeyboardWindow.a(lpparam);
		WalletChangeBankcardUI.a(lpparam);
		WalletPayUI.a(lpparam);
		WalletBaseUI.a(lpparam);
		LuckyMoney.a(lpparam);
		Remittance.a(lpparam);
	}
}
