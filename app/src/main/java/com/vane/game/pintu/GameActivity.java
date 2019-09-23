package com.vane.game.pintu;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.vane.game.R;

public class GameActivity extends Activity implements OnClickListener {
	private GameView mGameView;
	private TextView mStepTv;
	private ImageView mShowImg;
	private Bitmap bitmap;
	private ImageView mBigStarImg;
	/**显示最高分的TextView**/
	private TextView mBestScoreTv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		Intent intent = getIntent();
		String largeImgPath = intent.getStringExtra("path_img_large");
		int level = intent.getIntExtra("level", 0);

		mGameView = (GameView) findViewById(R.id.gameView);
		AssetManager assetManager = getAssets();
		try {
			InputStream is = assetManager.open(largeImgPath);
			bitmap = BitmapFactory.decodeStream(is);
			mGameView.startGame(bitmap, level);
		} catch (IOException e) {
			e.printStackTrace();
		}

		mGameView.setOnGameViewListen(new GameView.OnGameViewListen() {

			@Override
			public void successed() {
				//如果拼图完成，就把大星星显示出来
				mBigStarImg.setVisibility(View.VISIBLE);
			}
			@Override
			public void onStepChange(int curStep) {
				mStepTv.setText("移动次数："+curStep);
			}
		});
		mStepTv = (TextView) findViewById(R.id.stepTv);
		mShowImg = (ImageView) findViewById(R.id.showImg);
		mShowImg.setImageBitmap(bitmap);
		mBigStarImg = (ImageView) findViewById(R.id.bigStarImg);
		mBigStarImg.setOnClickListener(this);
		mBestScoreTv = (TextView) findViewById(R.id.bestScoreTv);
		int bestScore = intent.getIntExtra("best_score", -1);
		if(bestScore != -1){
			mBestScoreTv.setText("最好成绩："+bestScore+"步");
		}else{
			mBestScoreTv.setText("最好成绩：无");
		}
	}
	@Override
	public void onClick(View v) {
		if(v == mBigStarImg){
			//既然点到了大星星，就说明拼图已完成
			Intent intent = getIntent();
			// 这样获取的Intent对象不仅包含了之前ChooseGameActivity
			//存的数据，还可以添加新的数据
			intent.putExtra("score", mGameView.getmStep());
			setResult(RESULT_OK, intent);
			finish();
		}
	}
}