package com.vane.game;

import java.io.IOException;

import android.app.Application;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.util.SparseIntArray;

public class MyApplication extends Application{
	private SoundPool soundPool;
	private SparseIntArray soundIds;
	private AssetManager assetManager;
	private MediaPlayer player;
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("Application", "APP_onCreate");
		assetManager = getAssets();
		soundIds = new SparseIntArray();
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);
		try {
			AssetFileDescriptor afd = assetManager.openFd("sound/click.ogg");
			soundIds.put(1, soundPool.load(afd, 1));
		} catch (IOException e) {
			e.printStackTrace();
		}
		player = new MediaPlayer();
		//����ѭ������
		player.setLooping(true);
	}
	/**
	 * ���Ű�ť�����Ч
	 */
	public void playClickSound(){
		soundPool.play(soundIds.get(1), 1, 1, 1, 0, 1);
	}
	/**
	 * ���Ų˵��µı�������
	 */
	public void playMenuBgSound(){
		try {
			AssetFileDescriptor afd = assetManager.openFd("sound/menu.mp3");
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			player.prepare();
			player.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * ��ͣ��������
	 */
	public void pauseBgMusic() {
		if(player != null && player.isPlaying()){
			player.pause();
		}
	}
	/**
	 * �������ű�������(��ͣ�����)
	 */
	public void playBgMusicContinue(){
		player.start();
	}
	/**
	 * ֹͣ���ű�������
	 */
	public void stopBgMusic() {
		player.stop();
		player.release();
		player = null;
	}
}
