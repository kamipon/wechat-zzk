package com.gentcent.wechat.enhancement.util;

import com.gentcent.wechat.enhancement.bean.MessageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zuozhi
 * @since 2019-07-08
 */
public class SendManager {
	
	//发送消息的队列
	private static List<MessageBean> sendMessageQueue = new ArrayList<>();
	
	//是否上锁
	private static boolean isLock = false;
	
	/**
	 * 是否上锁
	 */
	public static boolean isLock(){
		return isLock;
	}
	
	/**
	 * 上锁
	 */
	public static void unLock(){
		isLock = false;
	}
	
	/**
	 * 解锁
	 */
	public static void lock(){
		isLock = true;
	}
	
	/**
	 * 添加消息到队列
	 */
	public static boolean addToQueque(MessageBean mb){
		return sendMessageQueue.add(mb);
	}
	
	/**
	 * 获取消息队列
	 */
	public static List<MessageBean> getQueque(){
		return sendMessageQueue;
	}
	
	/**
	 * 获取消息队列长度
	 */
	public static int getQuequeSize(){
		return sendMessageQueue.size();
	}
	
	/**
	 * 清空队列
	 */
	public static void clearQueque(){
		sendMessageQueue.clear();
	}
}
