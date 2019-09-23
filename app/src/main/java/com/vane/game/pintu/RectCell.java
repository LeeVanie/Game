package com.vane.game.pintu;

import android.graphics.Rect;

public class RectCell {

	private int position;
	private Rect rect;
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public Rect getRect() {
		return rect;
	}
	public void setRect(Rect rect) {
		this.rect = rect;
	}
	public RectCell(int position, Rect rect) {
		super();
		this.position = position;
		this.rect = rect;
	}
	public RectCell() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
