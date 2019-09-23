package com.vane.game.pintu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

public class BackgroundView extends View{
	private Paint paint;
	/**扇形的颜色**/
	private static final int ARC_COLOR = 0xffA29E97;
	/**背景颜色**/
	private static final int BG_COLOR = 0xffE0D8CE;
	public BackgroundView(Context context) {
		super(context);
		init();
	}

	public BackgroundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	/**
	 * 初始化
	 */
	private void init() {
		paint = new Paint();
		paint.setColor(ARC_COLOR);
		paint.setAntiAlias(true);
		startRotate();
	}
	/**线程开关**/
	private boolean threadRun = true;
	private float degress;
	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			invalidate();
			return false;
		}
	});
	/**
	 * 开始旋转
	 */
	private void startRotate(){
		new Thread(){
			public void run() {
				while(threadRun){
					degress-=0.07f;
					handler.sendEmptyMessage(0);
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.rotate(degress, width/2f, height/2f);
		canvas.drawColor(BG_COLOR);
		canvas.drawArc(oval, 0, 25, true, paint);
		canvas.drawArc(oval, 45, 25, true, paint);
		canvas.drawArc(oval, 90, 25, true, paint);
		canvas.drawArc(oval, 135, 25, true, paint);
		canvas.drawArc(oval, 180, 25, true, paint);
		canvas.drawArc(oval, 225, 25, true, paint);
		canvas.drawArc(oval, 270, 25, true, paint);
		canvas.drawArc(oval, 315, 25, true, paint);
		canvas.drawCircle(width/2f, height/2f, 5, paint);
	}
	/**画扇形所参照的矩形**/
	RectF oval;
	private int width;
	private int height;
	public void layout(int l, int t, int r, int b) {
		super.layout(l, t, r, b);
		width = getWidth();
		height = getHeight();
		float radius = (float) Math.sqrt(Math.pow(width/2f, 2)+Math.pow(height/2f, 2));
		float x1 = width/2f - radius;
		float y1 = height/2f - radius;
		float x2 = width/2f + radius;
		float y2 = height/2f + radius;
		oval = new RectF(x1, y1, x2, y2);
	}
}
