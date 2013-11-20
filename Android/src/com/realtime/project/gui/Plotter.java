package com.realtime.project.gui;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

public class Plotter extends View {
	
	private static float X_MAX;
	private static float Y_MAX;
	private static float X_MIN;
	private static float Y_MIN;
	private static PointF ORIGO;
	private float yscale, xscale;
	
	private Canvas canvas;

	private List<PointF> u, y, yref;
	
	public Plotter(Context context) {
		super(context);
		X_MIN = 50f;
		Y_MIN = 50f;
		u = new ArrayList<PointF>();
		y = new ArrayList<PointF>();
		yref = new ArrayList<PointF>();
	}
	
	public synchronized void addU(PointF p) {
		if (u.size() >= 100) {
			u = moveXcoordinates(u);
		}
		u.add(p);
	}
	
	public synchronized void addY(PointF p) {
		if (y.size() >= 100) {
			y = moveXcoordinates(y);
		}
		y.add(p);
	}
	
	public synchronized void addYref(PointF p) {
		if (yref.size() >= 100) {
			yref = moveXcoordinates(yref);
		}
		yref.add(p);
	}
	
	public synchronized List<PointF> moveXcoordinates(List<PointF> l) {
		List<PointF> newList = new ArrayList<PointF>();
		l.remove(0);
		for (PointF p : l) {
			PointF np = new PointF(p.x-1, p.y);
			newList.add(np);
		}
		return newList;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		this.canvas = canvas;
		
		X_MAX = getMeasuredWidth() - X_MIN;
		Y_MAX = getMeasuredHeight() - Y_MIN;
		ORIGO = new PointF(X_MIN, Y_MAX/2);
		yscale = (getMeasuredHeight()/2 - 50) / 10;
		xscale = (getMeasuredWidth() - 50) / 100;
		
		if (u.isEmpty()) return;
		
		for (PointF us : u) {
			drawPoint(us, Color.RED);
		}
		for (PointF ys : y) {
			drawPoint(ys, Color.BLUE);
		}
		for (PointF yrefs : yref) {
			drawPoint(yrefs, Color.GREEN);
		}

	}
	
	private synchronized void drawPoint(float x, float y, int color) {
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setStrokeWidth(5);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		
		float xx = ORIGO.x + x*xscale;
		float yy = (ORIGO.y - y*yscale);
		
		canvas.drawPoint(xx, yy, paint);
	}
	
	private synchronized void drawPoint(PointF p, int color) {
		drawPoint(p.x, p.y, color);
	}

}
