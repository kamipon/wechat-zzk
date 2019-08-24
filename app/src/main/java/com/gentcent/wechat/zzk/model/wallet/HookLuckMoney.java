package com.gentcent.wechat.zzk.model.wallet;

public class HookLuckMoney {
    public static String Carg;
    public static String DetaiDes;
    public static String FJ;
    public static String Listnpr;
    public static String LuckyMoneyDetailUI;
    public static String LuckyMoneyReceiveUI;
    public static String MoneyNubmber;
    public static String Runmethod;
    public static String RunmethodNeedClass;
    public static String VerifyModeUI;
    public static String data;
    public static String key_native_url;
    public static String key_username;
    public static String lucky_money_detail_amount;
    public static String lucky_money_detail_desc;
    public static String lucky_money_detail_group_icon;
    public static String lucky_money_detail_wishing;
    public static String lucky_money_receive_wishing;
    public static String lucky_money_recieve_check_detail_ll;
    public static String lucky_money_recieve_open;

    public static void init_7_0_3() {
        LuckyMoneyReceiveUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI";
        lucky_money_recieve_open = "oNE";
        lucky_money_recieve_check_detail_ll = "oNF";
        lucky_money_receive_wishing = "omt";
        Runmethod = "onSceneEnd";
        RunmethodNeedClass = "com.tencent.mm.ak.m";
        FJ = "com.tencent.mm.plugin.luckymoney.model.k";
        LuckyMoneyDetailUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";
        Listnpr = "oMy";
        lucky_money_detail_group_icon = "oMP";
        lucky_money_detail_amount = "oME";
        lucky_money_detail_desc = "lsg";
        lucky_money_detail_wishing = "omt";
        key_native_url = "key_native_url";
        key_username = "key_username";
        data = "loJ";
        DetaiDes = "kFG";
        MoneyNubmber = "lfW";
        VerifyModeUI = "com.tencent.mm.plugin.wallet_core.id_verify.SwitchRealnameVerifyModeUI";
    }
}
