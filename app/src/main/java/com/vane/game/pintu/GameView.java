package com.vane.game.pintu;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class GameView extends View{

	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GameView(Context context) {
		super(context);
	}

	public void startGame(Bitmap srcBitmap,int level){
		mSrcBitmap = srcBitmap;
		//等级1 --> 3 x 3
		//等级2 --> 4 x 4
		mLine = level+2;
		mRow = level+2;
	}
	/**View的宽**/
	private int width;
	/**View的高**/
	private int height;
	/**行**/
	private int mLine;
	/**列**/
	private int mRow;
	/**图片上的点二维数组**/
	private Point[][] mBitmapPoints;
	/**View上的点二维数组**/
	private Point[][] mViewPoints;
	/**图片上的矩形数组**/
	private Rect[][] mBitmapRects;
	/**View上的矩形数组**/
	private Rect[][] mViewRects;
	/**原始图片的Bitmap**/
	private Bitmap mSrcBitmap;

	private RectCell[][] mRectCells;
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(mSrcBitmap != null){
			for (int i = 0; i < mLine; i++) {
				for (int j = 0; j < mRow; j++) {
					if(i == mBlankPoint.x &&
							j == mBlankPoint.y){
						continue;
					}
					canvas.drawBitmap(mSrcBitmap,
							mRectCells[i][j].getRect(),
							mViewRects[i][j],null);
				}
			}
		}
	}
	/**
	 * 在这里要告诉父容器，View需要多大的空间
	 * widthMeasureSpec是size和mode的结合
	 * heightMeasureSpec是size和mode的结合
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//调用
		//		int specMode = MeasureSpec.getMode(heightMeasureSpec);
		int specSize = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(specSize, specSize);
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
							int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		//获取View宽高
		width = getWidth();
		height = getHeight();
		//创建Bitmap上点的二维数组，并赋值
		mBitmapPoints = new Point[mLine+1][mRow+1];
		for (int i = 0; i < mBitmapPoints.length; i++) {
			for (int j = 0; j < mBitmapPoints[i].length; j++) {
				mBitmapPoints[i][j] = new Point(j*(mSrcBitmap.getWidth()/mRow),
						i*(mSrcBitmap.getHeight()/mLine));
			}
		}
		//创建Bitmap上矩形的二维数组，并赋值
		mBitmapRects = new Rect[mLine][mRow];
		for (int i = 0; i < mBitmapRects.length; i++) {
			for (int j = 0; j < mBitmapRects[i].length; j++) {
				mBitmapRects[i][j] = new Rect(
						mBitmapPoints[i][j].x, mBitmapPoints[i][j].y,
						mBitmapPoints[i+1][j+1].x, mBitmapPoints[i+1][j+1].y);
			}
		}
		//创建View上点的二维数组，并赋值
		mViewPoints = new Point[mLine+1][mRow+1];
		for (int i = 0; i < mViewPoints.length; i++) {
			for (int j = 0; j < mViewPoints[i].length; j++) {
				mViewPoints[i][j] = new Point(j*(width/mRow),
						i*(height/mLine));

			}
		}
		//创建View上矩形的二维数组，并赋值
		mViewRects = new Rect[mLine][mRow];
		for (int i = 0; i < mViewRects.length; i++) {
			for (int j = 0; j < mViewRects[i].length; j++) {
				mViewRects[i][j] = new Rect(
						mViewPoints[i][j].x, mViewPoints[i][j].y,
						mViewPoints[i+1][j+1].x, mViewPoints[i+1][j+1].y);
			}
		}
		mBlankPoint = new Point(mLine-1, mRow-1);
		mRectCells = new RectCell[mLine][mRow];
		//0 1 2 3
		//4 5 6 7
		for (int i = 0; i < mLine; i++) {
			for (int j = 0; j < mRow; j++) {
				mRectCells[i][j] =
						new RectCell(i*mRow+j, mBitmapRects[i][j]);
			}
		}
		breakOrder();

	}
	private Point mBlankPoint;
	/**
	 * 重写View的事件处理
	 * 一个完整的事件由：
	 *   1个down事件，n个move事件，1个up事件组成
	 *
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// 300*300 3*3   (150,50)  0,1
				Point point = new Point();
				point.x = (int) (event.getY()/(height/mLine));
				point.y = (int) (event.getX()/(width/mRow));
				if(canMove(point)){
					exchange(point);//移动(交换)
					mStep++;//移动次数+1
					if(onGameViewListen != null){
						//回调
						onGameViewListen.onStepChange(mStep);
					}
					if(success()){
						//如果监听器不为空，就回调
						if(onGameViewListen != null){
							onGameViewListen.successed();
						}
						mBlankPoint.x = -1;
						mBlankPoint.y = -1;
						invalidate();
					}
				}else{
					Toast.makeText(getContext(), "不要点这里啦！", 0).show();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				break;
		}
		return true;
	}
	/**已移动的次数**/
	private int mStep = 0;

	public int getmStep() {
		return mStep;
	}
	/**
	 * 把传过去的区域和空白区域的矩形进行交换
	 * @param point
	 */
	private void exchange(Point point) {
		RectCell rectTag = mRectCells[point.x][point.y];
		mRectCells[point.x][point.y] =
				mRectCells[mBlankPoint.x][mBlankPoint.y];
		mRectCells[mBlankPoint.x][mBlankPoint.y]= rectTag;
		mBlankPoint = point;
		invalidate();
	}
	/**
	 * 判断拼图是否完成
	 * @return
	 */
	private boolean success(){
		for (int i = 0; i < mLine; i++) {
			for (int j = 0; j < mRow; j++) {
				if(mRectCells[i][j].getPosition() != i*mRow+j){
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * 打乱顺序
	 */
	private void breakOrder(){
		//可以与空白区域交换的区域(用Point表示)
		List<Point> points= new ArrayList<Point>();
		for (int i = 0; i < 300; i++) {
			//清空集合里面的对象
			points.clear();
			if(mBlankPoint.x != 0){
				points.add(new Point(mBlankPoint.x-1, mBlankPoint.y));
			}
			if(mBlankPoint.x != mLine-1){
				points.add(new Point(mBlankPoint.x+1, mBlankPoint.y));
			}
			if(mBlankPoint.y != 0){
				points.add(new Point(mBlankPoint.x, mBlankPoint.y-1));
			}
			if(mBlankPoint.y != mRow-1){
				points.add(new Point(mBlankPoint.x, mBlankPoint.y+1));
			}
			Point point = points.get((int) (Math.random()*points.size()));
			exchange(point);
		}
	}
	/**
	 * 判断传进来的Point所代表的区域是否可以移动
	 * @param point
	 * @return
	 */
	private boolean canMove(Point point){
		/**
		 * 如果传进来的区域正好是空白区域 左邻居的话
		 */
		if(point.x == mBlankPoint.x
				&& point.y == mBlankPoint.y-1){
			return true;
		}
		/**
		 * 如果传进来的区域正好是空白区域 右邻居的话
		 */
		if(point.x == mBlankPoint.x
				&& point.y == mBlankPoint.y+1){
			return true;
		}
		/**
		 * 如果传进来的区域正好是空白区域 下邻居的话
		 */
		if(point.y == mBlankPoint.y
				&& point.x == mBlankPoint.x+1){
			return true;
		}
		/**
		 * 如果传进来的区域正好是空白区域 上邻居的话
		 */
		if(point.y == mBlankPoint.y
				&& point.x == mBlankPoint.x-1){
			return true;
		}
		return false;
	}

	public interface OnGameViewListen{
		/**GameView移动了多少次**/
		void onStepChange(int curStep);
		/**拼图的成功的回调方法**/
		void successed();
	}
	private OnGameViewListen onGameViewListen;
	public void setOnGameViewListen(OnGameViewListen onGameViewListen) {
		this.onGameViewListen = onGameViewListen;
	}
}
