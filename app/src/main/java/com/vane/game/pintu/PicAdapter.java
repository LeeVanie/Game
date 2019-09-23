package com.vane.game.pintu;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vane.game.R;

public class PicAdapter extends BaseAdapter{
	//数据源
	private Cursor cursor;
	//用来加载布局的对象
	private LayoutInflater inflater;
	//用来处理Assets文件的对象
	private AssetManager assetManager;

	private HashMap<Integer, Bitmap> bitmaps;
	public PicAdapter(Context context,Cursor cursor) {
		super();
		this.cursor = cursor;
		inflater = LayoutInflater.from(context);
		assetManager = context.getAssets();
		bitmaps = new HashMap<Integer, Bitmap>();
	}

	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public Cursor getItem(int position) {
		cursor.moveToPosition(position);
		return cursor;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			//加载Item布局
			convertView = inflater.inflate(R.layout.pic_item, null);
			holder = new ViewHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.item_img);
			LinearLayout layout =
					(LinearLayout) convertView.findViewById(R.id.starLayout);
			holder.starImgs = new ImageView[layout.getChildCount()];
			for (int i = 0; i < layout.getChildCount(); i++) {
				holder.starImgs[i] = (ImageView) layout.getChildAt(i);
			}
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		cursor.moveToPosition(position);
		//设置星星颜色
		int level = cursor.getInt(cursor.getColumnIndex("level"));
		for (int i = 0; i < holder.starImgs.length; i++) {
			if(i < level){
				holder.starImgs[i].setImageResource(R.drawable.ic_star);
			}else{
				holder.starImgs[i].setImageResource(R.drawable.ic_star_gray);
			}
		}

		if(!bitmaps.containsKey(position)){
			String imgAssetPath = cursor.getString(
					cursor.getColumnIndex("path_img_small"));
			InputStream is = null;
			try {
				is = assetManager.open(imgAssetPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			bitmaps.put(position, bitmap);
		}

		holder.img.setImageBitmap(bitmaps.get(position));

		return convertView;
	}
	private class ViewHolder{
		ImageView img;
		ImageView[] starImgs;
	}
	public void resetDatas(Cursor cursor) {
		this.cursor = cursor;
		//Adapter刷新方法，执行这个方法后Adapter会
		//重写调用getCount，getView方法
		notifyDataSetChanged();
	}
}

