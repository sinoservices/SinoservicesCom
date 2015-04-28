package com.sinoservices.common.push;
import java.util.List;
import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.sinoservices.common.Global;
import com.sinoservices.common.R;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;

/**
 * 百度云推送管理类
 * 
 * @author Jerry
 *
 */
public class BaiDuPushManager {
	private static Context mContext;
	private static BaiDuPushManager baiDuPushManager;

	private BaiDuPushManager() {

	}

	private BaiDuPushManager(Context context) {
		super();
		this.mContext = context;
		initBaiDuPush();
	}

	public static BaiDuPushManager getInstance(Context context) {
		if (baiDuPushManager == null) {
			baiDuPushManager = new BaiDuPushManager(context);
		}
		return baiDuPushManager;
	}

	/**
	 * 初始化百度云推送配置
	 */
	private void initBaiDuPush() {
		CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
				mContext, R.layout.baidupush_notification_custom_builder,
				R.id.notification_icon, R.id.notification_title,
				R.id.notification_text) {
		};
		cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
		cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND
				| Notification.DEFAULT_VIBRATE);
		cBuilder.setStatusbarIcon(mContext.getApplicationInfo().icon);
		cBuilder.setLayoutDrawable(R.drawable.ic_launcher);
		// Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
		// PushManager.enableLbs(mContext);
		PushManager.setNotificationBuilder(mContext, 1, cBuilder);
		
		initWithApiKey();
	}

	/**
	 * Push: 无账号初始化，用api key绑定
	 * 无账号登陆
	 */
	private static void initWithApiKey() {
		PushManager.startWork(mContext, PushConstants.LOGIN_TYPE_API_KEY,
				Global.BAIDU_PUSH_API);
	}

	/**
	 * 打开富媒体列表界面
	 */
	public static void openRichMediaList() {
		// Push: 打开富媒体消息列表
		Intent sendIntent = new Intent();
		sendIntent.setClassName(mContext,
				"com.baidu.android.pushservice.richmedia.MediaListActivity");
		sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(sendIntent);
		
	}

	/**
	 * 设置标签,以英文逗号隔开
	 */
	private static void setTags(List<String> tags) {
		// Push: 设置tag调用方式
		PushManager.setTags(mContext, tags);
	}

	/**
	 * 删除tag操作
	 */
	private static void deleteTags(List<String> tags) {
		PushManager.delTags(mContext, tags);
	}
    /**
     * 以百度账号登陆
     * @param accessToken
     */
	private static void LoginByBaiDu(String accessToken) {
		PushManager.startWork(mContext, PushConstants.LOGIN_TYPE_ACCESS_TOKEN,
				accessToken);
	}
}
