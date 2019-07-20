package com.gentcent.wechat.zzk.job;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import com.gentcent.wechat.zzk.manager.MainManager;
import com.gentcent.wechat.zzk.util.XLog;

/**
 * @author zuozhi
 * @since 2019-07-19
 */
public class TaskManager {
	private JobManager jobManager;
	private static TaskManager instance;
	
	//私有构造器
	private TaskManager(){
		instance=this;
	}
	public JobManager getJobManager() {
		return jobManager;
	}
	
	public static TaskManager getInstance() {
		return instance;
	}
	
	private void configureJobManager() {
		//3. JobManager的配置器，利用Builder模式
		Configuration configuration = new Configuration.Builder(MainManager.activity)
				.customLogger(new CustomLogger() {
					private static final String TAG = "JOBS";
					@Override
					public boolean isDebugEnabled() {
						return true;
					}
					
					@Override
					public void d(String text, Object... args) {
						XLog.d(TAG + String.format(text, args));
					}
					
					@Override
					public void e(Throwable t, String text, Object... args) {
						XLog.e(TAG+ String.format(text, args) + t);
					}
					
					@Override
					public void e(String text, Object... args) {
						XLog.e(TAG+ String.format(text, args));
					}
					
					@Override
					public void v(String text, Object... args) {
						XLog.d(TAG + String.format(text, args));
					}
				})
				.minConsumerCount(1)//always keep at least one consumer alive
				.maxConsumerCount(3)//up to 3 consumers at a time
				.loadFactor(3)//3 jobs per consumer
				.consumerKeepAlive(120)//wait 2 minute
				.build();
		jobManager = new JobManager( configuration);
	}
}
