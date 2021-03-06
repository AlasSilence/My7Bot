package me.michaeljiang.basic7bot.ui;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

import me.michaeljiang.basic7bot.R;


@SuppressWarnings("deprecation")
public class Bluetooth extends TabActivity {
	/** Called when the activity is first created. */
	private Context mContext;
	static AnimationTabHost mTabHost;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 隐藏状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mContext = this;
		setContentView(R.layout.main);
		//实例化
		mTabHost = (AnimationTabHost) getTabHost();
		mTabHost.addTab(mTabHost.newTabSpec("Tab1")
				.setIndicator("设备列表",getResources().getDrawable(android.R.drawable.ic_menu_add))
				.setContent(new Intent(mContext, DeviceActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec("Tab2").
				setIndicator("对话列表",getResources().getDrawable(android.R.drawable.ic_menu_add))
				.setContent(new Intent(mContext, ChatActivity.class)));
		mTabHost.setOnTabChangedListener(new OnTabChangeListener(){
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if(tabId.equals("Tab1"))
				{
				}
			}
		});
		mTabHost.setCurrentTab(0);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Toast.makeText(mContext, "address:", Toast.LENGTH_SHORT).show();
	}

	@Override
	public synchronized void onResume() {
		/**
		 * 设置为横屏
		 */
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		super.onResume();
	}
	@Override
	protected void onDestroy() {
        /* unbind from the service */
		super.onDestroy();
	}

}