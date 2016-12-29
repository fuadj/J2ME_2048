package com.game.geometry;

import com.game.geometry.Point;

public class Rect {
	public int x, y;
	
	public int width, height;
	
	public Rect() {
		this(0, 0, 0, 0);
	}
	
	public Rect(Rect other) {
		this(other.x, other.y, other.width, other.height);
	}

	public Rect(int w, int h) {
		this(0, 0, w, h);
	}
	
	public Rect(int x, int y, int w, int h) {
		this.x = x; this.y = y;
		this.width = w;
		this.height = h;
	}
		
	public boolean containsPoint(Point pt) {
		return (pt.x >= x && pt.y >= y) &&
				(( x + width >= pt.x) && ( y + height >= height));
	}
	
	public void moveBy(int dx, int dy) {
		x += dx;
		y += dy;
	}
	
	public void centerAt(Point pt) {
		int dx = width / 2;
		int dy = height / 2;
		
		x = pt.x - dx;
		y = pt.y - dy;
	}
	
	public Point locateCenter() {
		Point center = new Point();
		
		center.x = x + width / 2;
		center.y = y + height / 2;
		
		return center;
	}
	
	public Rect scaleBy(double scaleFactor) {
		Rect copy = new Rect((int)(width * scaleFactor), (int)(height * scaleFactor));
		
		copy.centerAt(this.locateCenter());
		return copy;
	}

	/**
	 * It doesn't affect the origins.
	 * @param scaleFactor
	 * @return 
	 */
	public Rect scaleWidthBy(double scaleFactor) {
		Rect copy = new Rect(this);

		copy.width = (int)(scaleFactor * this.width);
		return copy;
	}

	/**
	 * It doesn't affect the origins.
	 * @param scaleFactor
	 * @return 
	 */
	public Rect scaleHeightBy(double scaleFactor) {
		Rect copy = new Rect(this);

		copy.height = (int)(scaleFactor * height);
		return copy;
	}
}
