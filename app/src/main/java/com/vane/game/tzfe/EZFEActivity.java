package com.vane.game.tzfe;

import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.vane.game.R;


/**
 * 2048
 */
public class EZFEActivity extends Activity {

	private TextView scoreTextView,bestScoreTextView;
	private Button buttoNewGame;
	
	private GameView gameView = null;
	
	private Score score;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tzfe);
		
		buttoNewGame = (Button) findViewById(R.id.btnNewGame);
		scoreTextView = (TextView) findViewById(R.id.score);
		bestScoreTextView = (TextView) findViewById(R.id.bestScore);
		
		gameView = (GameView) findViewById(R.id.gameView);

		score = new Score();
		gameView.setScore(score);
		
		buttoNewGame.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startNewGame();
			}
		});
		
	}
	
	
	private void startNewGame()
	{
		score.clearScore();
		showScore();
		showBestScore();
		gameView.startGame();
	}
	
	private void showBestScore(){
		bestScoreTextView.setText(String.valueOf(score.getBestScore()));
	}
	
	private void showScore(){
		scoreTextView.setText(String.valueOf(score.getScore()));
	}
	
	
	class Score 
	{
		private int score=0;
		private static final String SP_KEY_BEST_SCORE = "bestScore";
		
		public void clearScore(){
			score=0;
		}
		
		public int getScore()
		{
			return score;
		}
		
		public void addScore(int s)
		{
			score+=s;
			showScore();

			saveBestScore(Math.max(score, getBestScore()));
			showBestScore();
		}
		
		public void saveBestScore(int s){
			Editor e = getPreferences(MODE_PRIVATE).edit();
			e.putInt(SP_KEY_BEST_SCORE, s);
			e.commit();
		}

		public int getBestScore(){
			return getPreferences(MODE_PRIVATE).getInt(SP_KEY_BEST_SCORE, 0);
		}
		
		
	}

}
