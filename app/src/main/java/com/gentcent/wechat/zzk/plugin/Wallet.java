package com.gentcent.wechat.zzk.plugin;

import com.gentcent.wechat.zzk.model.wallet.LuckyMoney;
import com.gentcent.wechat.zzk.model.wallet.MyKeyboardWindow;
import com.gentcent.wechat.zzk.model.wallet.ReceivableManger;
import com.gentcent.wechat.zzk.model.wallet.Remittance;
import com.gentcent.wechat.zzk.model.wallet.WalletBaseUI;
import com.gentcent.wechat.zzk.model.wallet.WalletChangeBankcardUI;
import com.gentcent.wechat.zzk.model.wallet.WalletPayUI;
import com.gentcent.wechat.zzk.model.wallet.WcPayCashierDialog;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * @author zuozhi
 * @since 2019-07-25
 */
public class Wallet implements IPlugin {
	@Override
	public void hook(LoadPackageParam lpparam) {
		MyKeyboardWindow.hook(lpparam);
		WalletChangeBankcardUI.hook(lpparam);
		WalletPayUI.hook(lpparam);
		WalletBaseUI.hook(lpparam);
		LuckyMoney.hook(lpparam);
		Remittance.hook(lpparam);
		WcPayCashierDialog.hook(lpparam);
		
		//接收
		ReceivableManger.hook(lpparam);
		ReceivableManger.hook2(lpparam);
	}
}
