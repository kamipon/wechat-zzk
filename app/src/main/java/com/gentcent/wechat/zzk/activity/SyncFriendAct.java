package com.gentcent.wechat.zzk.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSONObject;
import com.android.tu.loadingdialog.LoadingDailog;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.gentcent.wechat.zzk.R;
import com.gentcent.wechat.zzk.WxBroadcast;
import com.gentcent.wechat.zzk.background.Api;
import com.gentcent.wechat.zzk.bean.UserBean;
import com.gentcent.wechat.zzk.smscall.SmsManager;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.XLog;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class SyncFriendAct extends BaseActivity {
	CircleDialog.Builder mDialog;
	
	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		public void handleMessage(Message message) {
			if (message.what == 1) {
				String sync_over = MyHelper.readLine("sync_over");
				String sync_sns_over = MyHelper.readLine("sync_sns_over");
				XLog.e("sync_over:" + sync_over);
				XLog.e("sync_sns_over:" + sync_sns_over);
				
				SyncFriendAct.this.mTimes = SyncFriendAct.this.mTimes + 1;
				if (ObjectUtils.equals(sync_over, "true") && ObjectUtils.equals(sync_sns_over, "true")) {
					SyncFriendAct.this.completeTask();
				} else if (SyncFriendAct.this.mTimes >= 50) {
					SyncFriendAct.this.completeTask();
				}
			}
			super.handleMessage(message);
		}
	};
	public int mTimes;
	private final Timer timer = new Timer();
	
	@BindView(R.id.btn_start)
	Button btn_start;
	@BindView(R.id.progressBar)
	ProgressBar progressBar;
	
	LoadingDailog dialog = null;
	private TimerTask timerTask = new TimerTask() {
		public void run() {
			SyncFriendAct.this.mHandler.sendEmptyMessage(1);
		}
	};
	
	public int bindLayout() {
		return R.layout.activity_sync_friend;
	}
	
	public View bindView() {
		return null;
	}
	
	@OnClick({R.id.btn_start})
	public void next() {
		showDialog("是否确定要将该设备的所有好友信息、\n个人朋友圈全部同步");
	}
	
	/**
	 * 同步前检测
	 */
	public void completeTask() {
		try {
			this.timer.cancel();
			String userInfo = MyHelper.readLine("userInfo");
			String userFriendsInfo = MyHelper.readLine("userFriendsInfo");
			String self_sns_info = MyHelper.readLine("self_sns_info");
			
			Thread.sleep(1000);
			if (!TextUtils.isEmpty(userInfo)) {
				if (!TextUtils.isEmpty(userFriendsInfo)) {
					if (TextUtils.isEmpty(self_sns_info)) {
						syncError("同步朋友圈数据没有获取到，请打开微信相册，然后重试！");
						return;
					} else {
						XLog.e("-----------------postSelf----------------");
						syncInfo(userInfo, userFriendsInfo, self_sns_info);
						return;
					}
				}
			}
			syncError("同步好友数据没有获取到，请打开好友列表，然后重试！");
		} catch (Exception e) {
			XLog.d("completeTask error is " + Log.getStackTraceString(e));
		}
	}
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		String phoneId = MyHelper.readLine("phone-id");
		boolean isWechatOpen = TextUtils.equals(MyHelper.readLine("isWechatOpen"), "true");
		if (StringUtils.equals(phoneId, "")) {
			new CircleDialog.Builder().setTitle("提示").setText("请先绑定设备，才可同步好友").setCancelable(false).setCanceledOnTouchOutside(false).setNeutral("确定", new OnClickListener() {
				public void onClick(View view) {
					SyncFriendAct.this.finish();
				}
			}).show(getSupportFragmentManager());
		} else if (!isWechatOpen) {
			new CircleDialog.Builder().setTitle("提示").setText("微信环境异常，请打开微信后重试").setCancelable(false).setCanceledOnTouchOutside(false).setNeutral("确定", new OnClickListener() {
				public void onClick(View view) {
					SyncFriendAct.this.finish();
				}
			}).show(getSupportFragmentManager());
		}
		//创建等待弹窗
		LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
				.setMessage("同步中...");
		dialog = loadBuilder.create();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.timer.cancel();
	}
	
	/**
	 * 显示弹窗
	 *
	 * @param text 弹窗文本
	 */
	private void showDialog(String text) {
		if (this.mDialog == null) {
			this.mDialog = new CircleDialog.Builder().setCancelable(false).setCanceledOnTouchOutside(false).setTitle("提示").setText(text).configNeutral(new ConfigButton() {
				public void onConfig(ButtonParams buttonParams) {
					buttonParams.textColor = CircleColor.FOOTER_BUTTON_TEXT_POSITIVE;
				}
			}).setNegative("取消", new OnClickListener() {
				public void onClick(View view) {
					SyncFriendAct.this.btn_start.setText("确认下一步");
				}
			}).setNeutral("确认", new OnClickListener() {
				public void onClick(View view) {
					SyncFriendAct.this.syncData();
				}
			});
		}
		this.mDialog.show(getSupportFragmentManager());
		this.btn_start.setText("确认同步");
	}
	
	/**
	 * 开始轮询
	 */
	public void syncData() {
		MyHelper.writeLine("sync_over", "false");
		MyHelper.writeLine("sync_sns_over", "false");
		this.btn_start.setClickable(false);
		dialog.show();
		WxBroadcast.sendAct("sync_info");
		this.timer.schedule(this.timerTask, 1000, 1000);
		this.mTimes = 0;
	}
	
	/**
	 * 绑定微信
	 */
	private void syncInfo(String userInfo, final String userFriendsInfo, final String self_sns_info) {
		XLog.d("selfInfo is " + userInfo);
		
		final UserBean u = GsonUtils.GsonToBean(userInfo, UserBean.class);
		XLog.e("phone-id: " + MyHelper.readLine("phone-id"));
		OkHttpUtils.post().url(Api.addweixin + MyHelper.readLine("phone-id"))
				.addParams("weixinID", u.username)
				.addParams("weixin", u.alias)
				.addParams("pic", u.reserved2 == null ? "" : u.reserved2)
				.addParams("province", u.province == null ? "" : u.province)
				.addParams("city", u.region == null ? "" : u.region)
				.addParams("sex", String.valueOf(u.sex))
				.addParams("nick", String.valueOf(u.nickname))
				.addParams("signature", String.valueOf(u.signature))
				.build().execute(
				new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						XLog.e("error: " + Log.getStackTraceString(e));
						syncServiceError();
					}
					
					@Override
					public void onResponse(String response, int id) {
						XLog.d("response: " + response);
						JSONObject jsonObject = JSONObject.parseObject(response);
						if (jsonObject.getBoolean("flag")) {
							MyHelper.writeLine("mywxid", u.username);
							postFriends(userFriendsInfo, u.username, self_sns_info);
						} else {
							syncError(jsonObject.getString("msg"));
						}
					}
				});
	}
	
	/**
	 * 同步好友
	 */
	public void postFriends(String userFriendsInfo, final String mywxid, final String self_sns_info) {
		OkHttpUtils.post().url(Api.appfriendList)
				.addParams("weixinID", mywxid)
				.addParams("userBeanStr", userFriendsInfo)
				.build().execute(
				new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						XLog.e("error: " + Log.getStackTraceString(e));
						syncServiceError();
					}
					
					@Override
					public void onResponse(String response, int id) {
						XLog.d("response: " + response);
						JSONObject jsonObject = JSONObject.parseObject(response);
						if (jsonObject.getBoolean("flag")) {
							postSelfSns(self_sns_info, mywxid, jsonObject.getIntValue("snum"));
						} else {
							syncError(jsonObject.getString("msg"));
						}
					}
				});
	}
	
	/**
	 * 同步朋友圈
	 */
	public void postSelfSns(String self_sns_info, String mywxid, final int fnum) {
		OkHttpUtils.post().url(Api.syncSns)
				.addParams("snsInfoList", self_sns_info)
				.addParams("myWxId", mywxid)
				.build().execute(
				new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						XLog.e("error: " + Log.getStackTraceString(e));
						syncServiceError();
					}
					
					@Override
					public void onResponse(String response, int id) {
						XLog.d("response: " + response);
						JSONObject jsonObject = JSONObject.parseObject(response);
						if (jsonObject.getBoolean("flag")) {
							postSms(fnum, jsonObject.getIntValue("num"));
						} else {
							syncError(jsonObject.getString("msg"));
						}
					}
				});
	}
	
	/**
	 * 同步短信
	 */
	public void postSms(final int fnum, final int snum) {
		SmsManager.syncSms(getContentResolver(), new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				XLog.e("error: " + Log.getStackTraceString(e));
				syncServiceError();
			}
			
			@Override
			public void onResponse(String response, int id) {
				XLog.d("response: " + response);
				JSONObject jsonObject = JSONObject.parseObject(response);
				if (jsonObject.getBoolean("flag")) {
					String text = "已成功同步好友" + fnum + "个\n个人朋友圈" + snum + "条\n短信记录" + jsonObject.getIntValue("num") + "条";
					new CircleDialog.Builder().setCancelable(false).setCanceledOnTouchOutside(false)
							.setTitle("标题").setText(text)
							.setNeutral("确定", new OnClickListener() {
								public void onClick(View view) {
									SyncFriendAct.this.finish();
								}
							}).show(SyncFriendAct.this.getSupportFragmentManager());
				} else {
					syncError(jsonObject.getString("msg"));
				}
			}
		});
		
	}
	
	
	public void syncError(String str) {
		new CircleDialog.Builder().setTitle("提示").setCancelable(false).setCanceledOnTouchOutside(false).setText(str).setNeutral("确定", new OnClickListener() {
			public void onClick(View view) {
				SyncFriendAct.this.finish();
			}
		}).show(getSupportFragmentManager());
	}
	
	public void syncServiceError() {
		syncError("服务器错误：好友同步失败");
	}
}
