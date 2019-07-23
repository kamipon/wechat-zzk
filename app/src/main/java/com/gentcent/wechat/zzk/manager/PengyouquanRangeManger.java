package com.gentcent.wechat.zzk.manager;

import android.app.Activity;
import android.content.Intent;

import com.gentcent.zzk.xped.XposedHelpers;


public class PengyouquanRangeManger {
    private static final int requestCode = 5;
    private static final int resultCode = -1;
    String Klabel_name_list;
    String Kother_user_name_list;
    int Ktag_range_index;

    public PengyouquanRangeManger(int i, String str, String str2) {
        this.Ktag_range_index = i;
        this.Klabel_name_list = str;
        this.Kother_user_name_list = str2;
    }

    public void addRange(Activity activity) {
        if (this.Klabel_name_list == null) {
            this.Klabel_name_list = "";
        }
        if (this.Kother_user_name_list == null) {
            this.Kother_user_name_list = "";
        }
        if (this.Ktag_range_index < 2) {
            return;
        }
        if (!this.Kother_user_name_list.equals("") || !this.Klabel_name_list.equals("")) {
            Intent intent = new Intent();
            intent.putExtra("Ktag_range_index", this.Ktag_range_index);
            intent.putExtra("Klabel_name_list", this.Klabel_name_list);
            intent.putExtra("Kother_user_name_list", this.Kother_user_name_list);
            XposedHelpers.callMethod(activity, "onActivityResult", new Object[]{Integer.valueOf(5), Integer.valueOf(-1), intent});
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PengyouquanRangeManger{Ktag_range_index='");
        sb.append(this.Ktag_range_index);
        sb.append('\'');
        sb.append(", Klabel_name_list=");
        sb.append(this.Klabel_name_list);
        sb.append(", Kother_user_name_list='");
        sb.append(this.Kother_user_name_list);
        sb.append('\'');
        sb.append('}');
        return sb.toString();
    }
}
