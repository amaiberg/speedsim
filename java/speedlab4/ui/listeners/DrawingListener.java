package speedlab4.ui.listeners;

import java.util.ArrayList;

import speedlab4.model.BrushController;
import speedlab4.ui.LatticeView;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class DrawingListener implements OnTouchListener {
	
	LatticeView myView;
	BrushController controller;
	ArrayList<Point> touchedPoints;
	int temp;
	
	public DrawingListener(BrushController bc, LatticeView lv){
		super();
		myView = lv;
		controller = bc;
		touchedPoints = new ArrayList<Point>(50);
	}

	public boolean onTouch(View v, MotionEvent event) {
		
		Point point;
		
		//System.out.println("Action type: " + event.getAction());
		
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){
			int historySize = event.getHistorySize();
			//System.out.println("History size: "+historySize);
			int pointerCount = event.getPointerCount();
			//System.out.println("Pointer count: "+pointerCount);
			for (int h = 0; h < historySize; h++) {
				for (int p = 0; p < pointerCount; p++) {
					point = processPoint(event.getHistoricalX(p, h), event.getHistoricalY(p, h));
					touchedPoints.add(point);
				}
			}
			for (int p = 0; p < pointerCount; p++) {
				point = processPoint(event.getX(p), event.getY(p));
				touchedPoints.add(point);
			}

			// command controller to update lattice
			controller.setLatticeCell(touchedPoints, controller.getDrawState());
			touchedPoints.clear();
		}
		return true;
	}
	
	/*
	 * Takes x-y coord from touch event and creates
	 * and returns a Point object after converting it
	 * to lattice coordinates
	 */
	private Point processPoint(float x, float y){
		// translate x-y to grid coordinates
		int cellX = (int) ((x/myView.getWidth())*myView.getSimModel().getSize());
		int cellY = (int) ((y/myView.getHeight())*myView.getSimModel().getSize());
		
		// for some reason lattice coordinate system is diagonal mirror from this,
		// so correct for that
		return translate(cellX, cellY);
	}
	
	private Point translate(int x, int y){
		temp = x;
		x = y;
		y = temp;
		return new Point(x, y);
	}

}
