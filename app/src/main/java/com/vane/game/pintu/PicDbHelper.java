package com.vane.game.pintu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 *
 *这里使用数据库去存储图片信息。
 *表里面的字段有：图片文件的Assets路径 和 等级
 */
public class PicDbHelper extends SQLiteOpenHelper{

	public PicDbHelper(Context context) {
		super(context, "puzzle.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table t_pic(_id integer primary key autoincrement,path_img_small text,path_img_large text,level integer default 0,best_score_1 integer default -1,best_score_2 integer default -1,best_score_3 integer default -1,best_score_4 integer default -1)");
		//插入起始数据
		for (int i = 0; i < 24; i++) {
			ContentValues values = new ContentValues();
			values.put("path_img_small", "img/sp"+i+".jpg");
			values.put("path_img_large", "img/p"+i+".jpg");
			db.insert("t_pic", null, values );
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	/**
	 * 根据传过来的难度等级，返回它所对应的最高分成绩字段名字
	 * @param level
	 * @return
	 */
	public static String getBestScoreColumnName(int level){
		return "best_score_"+level;
	}
	/**
	 * 返回true代表数据更新了，false代表数据没更新
	 * @param _id 数据的_id
	 * @param score 当前完成拼图的分数
	 * @param level 当前完成拼图的难度等级
	 * @return
	 */
	public boolean updateScore(int _id,int score,int level){
		SQLiteDatabase db = getWritableDatabase();
		String selection = "_id = ?";
		String[] selectionArgs = {_id+""};
		Cursor cursor = db.query("t_pic", null, selection, selectionArgs, null, null, null);
		if(cursor.moveToNext()){
			int dbLevel = cursor.getInt(cursor.getColumnIndex("level"));
			if(dbLevel < level){
				//如果数据库里面存的等级小于刚完成的拼图等级
				ContentValues values = new ContentValues();
				values.put("level", level);
				values.put(getBestScoreColumnName(level), score);
				db.update("t_pic", values , selection, selectionArgs);
				return true;
			}else{
				//如果数据库里面存的等级>=刚完成的拼图等级
				//获取当前难度等级在数据库中对应的最高分
				int dbBestScore = cursor.getInt(cursor.getColumnIndex(getBestScoreColumnName(level)));
				if(dbBestScore > score){
					ContentValues values = new ContentValues();
					values.put(getBestScoreColumnName(level), score);
					db.update("t_pic", values , selection, selectionArgs);
					return true;
				}
			}
		}
		return false;
	}
}
