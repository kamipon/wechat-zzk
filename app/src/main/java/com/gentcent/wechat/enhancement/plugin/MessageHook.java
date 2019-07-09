package com.gentcent.wechat.enhancement.plugin;


import android.annotation.SuppressLint;
import android.content.ContentValues;

import com.gentcent.wechat.enhancement.util.XLog;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class MessageHook implements IPlugin {
    @Override
    public void hook(final XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", lpparam.classLoader, "insertWithOnConflict", String.class, String.class, ContentValues.class, int.class, new XC_MethodHook() {
            @SuppressLint("CommitPrefEdits")
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                try {
                    String str = (String) param.args[0];
                    XLog.e(str);
//				if (str.equals("fmessage_conversation")) {
//				}
                    if (param.args[2] != null) {
                        ContentValues contentValues = (ContentValues) param.args[2];
//					if (str.equals("AppMessage")) {
//						a.a(this.a, contentValues);
//						return;
//					}
//					if (str.equals("WxFileIndex2")) {
//						m.a().a(new Runnable(this, contentValues) {
//							public void run() {
//								g.a(this.b.a, this.a);
//							}
//						} 1L, TimeUnit.SECONDS);
//						return;
//					}
                        if ("message".equals(str)) {
                            log(contentValues);
    
                            
                            if(contentValues.getAsInteger("isSend")!=0) return;
                            final Class bClass = XposedHelpers.findClass("com.tencent.mm.ah.p", lpparam.classLoader);
                            XLog.d("start sendText bClass:  " + bClass);
                            final Object sendMessageObj = XposedHelpers.getStaticObjectField(bClass, "fej");
    
//                            List<MessageBean> sendMessageQueue = PreferencesUtils.getSendMessageQueue();
//                            MessageBean messageBean = sendMessageQueue.get(0);
//                            PreferencesUtils.clearSendMessageQueue();
                            
                            //发消息
//							String friendWxId = messageBean.getFriendWxId();
//							String content = messageBean.getContent();
//							int type = messageBean.getType();
//                            XLog.d("messageBean:"+ GsonUtils.GsonString(messageBean));
							String friendWxId = "wxid_pcxj1zyjpc5n21";
							String content = "测试发消息";
							int type = 1;

							XLog.d("wxClassLoader:"+lpparam.classLoader);
							Class h = XposedHelpers.findClass("com.tencent.mm.modelmulti.h", lpparam.classLoader);
							XLog.d("start sendText h:  " + h);
							Object obj = XposedHelpers.newInstance(h, friendWxId, content, type);
							XLog.d("start sendText obj:  " + obj);
							//发送消息
							Object callMethod = XposedHelpers.callMethod(sendMessageObj, "d", obj);
							XLog.e("callmethod:" + callMethod);
                            return;
                        }
//					if ("chatroom".equals(str)) {
//						b.a(this.a, contentValues);
//					}
                    }
                } catch (Error | Exception e) {
                    XLog.e("错误："+e.toString());
                }
            }
    
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                try {
                
                } catch (Error | Exception e) {
                    XLog.e("错误："+e.toString());
                }
            }
            
        });
    }
    
    private void log(ContentValues contentValues){
        //1：纯文本消息
        int type = contentValues.getAsInteger("type");
        //0：接受 , 1：发送
        int isSend = contentValues.getAsInteger("isSend");
        //信息ID ,递增
        long msgId = contentValues.getAsLong("msgId");
        //发送者ID
        String talker = contentValues.getAsString("talker");
        //消息内容
        String content = contentValues.getAsString("content");
        int createTime = (int) (contentValues.getAsLong("createTime") / 1000L);
//		MessageBean messageBean = a(content);
    
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("message || type=");
        stringBuilder.append(type);
        stringBuilder.append("; msgId=");
        stringBuilder.append(msgId);
        stringBuilder.append("; isSend=");
        stringBuilder.append(isSend);
        stringBuilder.append("; talker=");
        stringBuilder.append(talker);
//		stringBuilder.append("; friendId is ");
//		stringBuilder.append(messageBean);
        stringBuilder.append("; content=");
        stringBuilder.append(content);
        XLog.d(stringBuilder.toString());
    }
}
