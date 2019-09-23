package com.vane.game.pintu;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.Toast;

import com.vane.game.MyApplication;
import com.vane.game.R;

public class PuzzleActivity extends Activity implements OnClickListener{
	private ViewGroup mMenuLayout;
	private Button mHelpBtn;
	private View mHelpLayout;
	private Button mHelpCloseBtn;
	private MyApplication mApp;
	private Button mChooseGameBtn;
	private Button menu_exitBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_puzzle);
		initViews();
		mCurView = mMenuLayout;
		//初始化两个动画
		mViewInAnim = AnimationUtils.loadAnimation(this, R.anim.menu_view_in);
		mViewOutAnim = AnimationUtils.loadAnimation(this, R.anim.menu_view_out);
		mApp = (MyApplication) getApplication();
		//播放背景音乐
		mApp.playMenuBgSound();
	}
	private void initViews() {
		mMenuLayout = (ViewGroup) findViewById(R.id.menuLayout);
		mMenuLayout.setLayoutAnimation(getMenuLayoutAnimController());
		mHelpLayout = findViewById(R.id.helpLayout);
		mHelpBtn = (Button) findViewById(R.id.menu_helperBtn);
		mHelpCloseBtn = (Button) findViewById(R.id.helpClose);
		mHelpBtn.setOnClickListener(this);
		mHelpCloseBtn.setOnClickListener(this);
		mChooseGameBtn = (Button) findViewById(R.id.menu_chooseGameBtn);
		mChooseGameBtn.setOnClickListener(this);
		menu_exitBtn = (Button) findViewById(R.id.menu_exitBtn);
		menu_exitBtn.setOnClickListener(this);
	}
	/**
	 * 获取一个LayoutAnimationController对象
	 * 控制菜单的出场动画
	 * @return
	 */
	private LayoutAnimationController getMenuLayoutAnimController() {
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.menu_item_anim);
		anim.setStartOffset(600);
		LayoutAnimationController controller =
				new LayoutAnimationController(anim, 0.5f);
		//倒序执行动画
		controller.setOrder(LayoutAnimationController.ORDER_REVERSE);
		return controller;
	}
	private boolean exit = false;
	@Override
	public void onBackPressed() {
		if(mCurView == mMenuLayout){
			if(exit){
				super.onBackPressed();
			}else{
				Toast.makeText(this, "再点击一次退出", Toast.LENGTH_SHORT).show();
				exit = true;
				//发延迟消息的作用：让exit在1秒后变为false
				handler.sendEmptyMessageDelayed(0, 1000);
			}
		}else{
			switchView(mMenuLayout, mCurView);
		}
	}
	//	@Override
	//	public boolean onKeyDown(int keyCode, KeyEvent event) {
	//		if(keyCode == KeyEvent.KEYCODE_BACK){
	//			if(exit){
	//				finish();
	//			}else{
	//				Toast.makeText(this, "再点击一次退出", Toast.LENGTH_SHORT).show();
	//				exit = true;
	//				//发延迟消息的作用：让exit在1秒后变为false
	//				handler.sendEmptyMessageDelayed(0, 1000);
	//			}
	//		}
	//		return true;
	//	}
	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			exit = false;
			return false;
		}
	});
	@Override
	public void onClick(View v) {
		mApp.playClickSound();
		if(v == mHelpBtn){
			switchView(mHelpLayout, mMenuLayout);
		}else if( v == mHelpCloseBtn){
			switchView(mMenuLayout, mHelpLayout);
		}else if(v == mChooseGameBtn){
			startActivity(new Intent(this, ChooseGameActivity.class));
		}else if(v == menu_exitBtn){
			finish();
		}
	}
	/**表示在前面显示的视图**/
	private View mCurView;
	private Animation mViewInAnim,mViewOutAnim;
	/**
	 * 切换视图。
	 * 控制要进来的和要出去的视图的显示和隐藏
	 * 并执行动画
	 * @param viewIn
	 * @param viewOut
	 */
	private void switchView(View viewIn,View viewOut){
		viewIn.setVisibility(View.VISIBLE);
		viewOut.setVisibility(View.GONE);
		viewIn.startAnimation(mViewInAnim);
		viewOut.startAnimation(mViewOutAnim);
		mCurView = viewIn;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mApp.stopBgMusic();
		//完全退出应用
		System.exit(0);
	}
	@Override
	protected void onStop() {
		super.onStop();
		//获取到系统的ActivityManager对象
		ActivityManager manager =
				(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		//获取当前系统运行的任务列表（只获取顶层那个）
		List<RunningTaskInfo> tasks = manager.getRunningTasks(1);
		//获取在前台运行的应用的包名
		String topPackageName = tasks.get(0).topActivity.getPackageName();
		if(!topPackageName.equals(getPackageName())){
			//如果当前在屏幕上显示的不是本应用，就暂停播放
			mApp.pauseBgMusic();
		}
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		mApp.playBgMusicContinue();
	}

}
