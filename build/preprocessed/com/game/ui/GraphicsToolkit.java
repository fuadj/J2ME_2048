package com.game.ui;

import com.game.geometry.*;
import javax.microedition.lcdui.*;

public class GraphicsToolkit {
	public static final int DEFAULT_FONT_STYLE = Font.STYLE_BOLD;
	public static final double DEFAULT_INSET = 0.87;

	public static void fillRectWithRoundEdge(Graphics g, Rect rect, double roundFactor) {
		double arcWidth;
		double arcHeight;
		
		g.setClip(rect.x, rect.y, rect.width, rect.height);
		arcWidth = rect.width * roundFactor;
		arcHeight = rect.height * roundFactor;
		g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, (int)arcWidth, (int)arcHeight);
	}

	public static void drawStringWithDefaults(Graphics g, Rect rect, String text) {
		drawStringInRectWithStyle(g, rect, text, DEFAULT_FONT_STYLE, DEFAULT_INSET);
	}

	public static void drawStringInRectWithStyle(Graphics g, Rect rect, String text, int style, double inset) {
		Point center = rect.locateCenter();
		double length = rect.width * inset;

		int face = Font.FACE_MONOSPACE;

		Font font = Font.getFont(face, style, Font.SIZE_LARGE);
		if (font.stringWidth(text) > length) {		// was too big for us
			font = Font.getFont(face, style, Font.SIZE_MEDIUM);
			if (font.stringWidth(text) > length)
				font = Font.getFont(face, style, Font.SIZE_SMALL);
		}
		
		g.setFont(font);

		int save_x = g.getClipX();
		int save_y = g.getClipY();
		int save_w = g.getClipWidth();
		int save_h = g.getClipHeight();

		g.setClip(rect.x, rect.y, rect.width, rect.height);
		center.y += font.getHeight() / 2;

		g.drawString(text, center.x, center.y, Graphics.HCENTER | Graphics.BOTTOM);

		g.setClip(save_x, save_y, save_w, save_h);
	}
}
