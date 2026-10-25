package com.shatteredpixel.shatteredpixeldungeon.ui.layout;

import com.watabou.noosa.Camera;
import com.watabou.noosa.ui.Component;

public class AnchorLayout {

	public static void position(Component target, float containerX, float containerY,
	                             float containerWidth, float containerHeight,
	                             Anchor anchor, float marginX, float marginY) {
		float targetX = containerX;
		float targetY = containerY;

		switch (anchor) {
			case TOP_LEFT:
				targetX = containerX + marginX;
				targetY = containerY + marginY;
				break;
			case TOP_CENTER:
				targetX = containerX + (containerWidth - target.width()) / 2 + marginX;
				targetY = containerY + marginY;
				break;
			case TOP_RIGHT:
				targetX = containerX + containerWidth - target.width() - marginX;
				targetY = containerY + marginY;
				break;
			case CENTER_LEFT:
				targetX = containerX + marginX;
				targetY = containerY + (containerHeight - target.height()) / 2 + marginY;
				break;
			case CENTER:
				targetX = containerX + (containerWidth - target.width()) / 2 + marginX;
				targetY = containerY + (containerHeight - target.height()) / 2 + marginY;
				break;
			case CENTER_RIGHT:
				targetX = containerX + containerWidth - target.width() - marginX;
				targetY = containerY + (containerHeight - target.height()) / 2 + marginY;
				break;
			case BOTTOM_LEFT:
				targetX = containerX + marginX;
				targetY = containerY + containerHeight - target.height() - marginY;
				break;
			case BOTTOM_CENTER:
				targetX = containerX + (containerWidth - target.width()) / 2 + marginX;
				targetY = containerY + containerHeight - target.height() - marginY;
				break;
			case BOTTOM_RIGHT:
				targetX = containerX + containerWidth - target.width() - marginX;
				targetY = containerY + containerHeight - target.height() - marginY;
				break;
		}

		target.setPos(targetX, targetY);
	}

	public static void positionToCamera(Component target, Camera camera, Anchor anchor, float marginX, float marginY) {
		if (camera == null) return;
		position(target, 0, 0, camera.width, camera.height, anchor, marginX, marginY);
	}
}