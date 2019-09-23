package com.vane.game.pintu;

import com.vane.game.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ChooseGameActivity extends Activity implements OnItemClickListener, OnClickListener{
	private GridView mGridView;
	private PicAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_game);
		initGridView();

	}
	/**
	 * 初始化GridView
	 */
	private void initGridView() {
		mGridView = (GridView) findViewById(R.id.gridView);
		dbHelper = new PicDbHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		//查询t_pic表，获取到数据源Cursor对象
		Cursor cursor = db.query("t_pic", null, null, null, null, null, null);
		mAdapter = new PicAdapter(this, cursor);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
		db.close();
	}
	private AlertDialog dialog;
	/**对话框里面的Button数组**/
	private Button[] mDialogBtns;
	/**
	 * 初始化Dialog
	 */
	private void initDialog(){
		dialog = new AlertDialog.Builder(this).create();
		dialog.show();
		View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_choose_level, null);
		dialog.setContentView(view);

		//获取到Dialog的窗口对象
		Window window = dialog.getWindow();
		//获取窗口的布局参数
		LayoutParams params = window.getAttributes();
		//将Dialog窗口的宽设置为GridView宽的一半
		params.width = mGridView.getWidth()/2;
		params.height = LayoutParams.WRAP_CONTENT;
		window.setAttributes(params);

		mDialogBtns = new Button[4];
		int[] btnIds = {R.id.dialogEasyBtn,R.id.dialogNormalBtn,R.id.dialogHardBtn,R.id.dialogBtBtn};
		for (int i = 0; i < btnIds.length; i++) {
			mDialogBtns[i] = (Button) view.findViewById(btnIds[i]);
			mDialogBtns[i].setOnClickListener(this);
		}
		//找到view里面的关闭按钮，并监听
		view.findViewById(R.id.dialogCloseBtn).setOnClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		if(dialog == null){
			initDialog();
		}
		//对Dialog里面的几个按钮进行设置
		Cursor cursor = mAdapter.getItem(position);
		int level = cursor.getInt(cursor.getColumnIndex("level"));
		//level      0 1 2 3 4
		//按钮可点数    1 2 3 4 4
//		for (int i = 0; i < mDialogBtns.length; i++) {
//			if(i <= level){
//				//设置按钮可以被点击
//				mDialogBtns[i].setBackgroundResource(R.drawable.selector_btn_red);
//				mDialogBtns[i].setEnabled(true);
//			}else{
//				mDialogBtns[i].setBackgroundResource(R.drawable.selector_btn_gray);
//				mDialogBtns[i].setEnabled(false);
//			}
//		}

		dialog.show();
		mLastClickItemPos = position;
	}
	/**最后一次点击的item位置**/
	private int mLastClickItemPos;
	private PicDbHelper dbHelper;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.dialogEasyBtn:
				startGameAty(1);
				break;
			case R.id.dialogNormalBtn:
				startGameAty(2);
				break;
			case R.id.dialogHardBtn:
				startGameAty(3);
				break;
			case R.id.dialogBtBtn:
				startGameAty(4);
				break;
			case R.id.dialogCloseBtn:
				//取消dialog
				dialog.dismiss();
				break;
		}
	}
	/**
	 * 跳转到游戏界面
	 * @param level 代表难度
	 */
	private void startGameAty(int level) {
		Intent intent = new Intent(this, GameActivity.class);
		//存放所选难度
		intent.putExtra("level", level);
		Cursor cursor = mAdapter.getItem(mLastClickItemPos);
		String largeImgPath = cursor.getString(cursor.getColumnIndex("path_img_large"));
		//存放大图的Asset路径
		intent.putExtra("path_img_large", largeImgPath);
		//存放_id
		int _id = cursor.getInt(cursor.getColumnIndex("_id"));
		intent.putExtra("_id", _id);
		//获取到所选难度等级对应的最好成绩
		int bestScore = cursor.getInt(cursor.getColumnIndex(PicDbHelper.getBestScoreColumnName(level)));
		intent.putExtra("best_score", bestScore);
		startActivityForResult(intent, 1);
		dialog.dismiss();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1 && resultCode == RESULT_OK){
			//说明游戏界面拼图完成了
			int _id = data.getIntExtra("_id", -1);
			int score = data.getIntExtra("score", -1);
			int level = data.getIntExtra("level", 0);
			if(dbHelper.updateScore(_id, score, level)){
				//说明数据发生了更新，那么界面也应该更新
				Cursor cursor = dbHelper.getWritableDatabase().query("t_pic", null, null, null, null, null, null);
//				mAdapter = new PicAdapter(this, cursor);
//				mGridView.setAdapter(mAdapter);
				mAdapter.resetDatas(cursor);
			}
		}
	}
}
