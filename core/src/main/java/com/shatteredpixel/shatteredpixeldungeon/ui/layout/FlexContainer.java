package com.shatteredpixel.shatteredpixeldungeon.ui.layout;

import com.watabou.noosa.ui.Component;
import java.util.ArrayList;

public class FlexContainer extends Component {

	public enum Direction {
		ROW,
		COLUMN
	}

	public enum Align {
		START,
		CENTER,
		END
	}

	private Direction direction = Direction.COLUMN;
	private Align alignment = Align.START;
	private float gap = 2f;
	private float padding = 2f;

	private final ArrayList<Component> items = new ArrayList<>();

	public FlexContainer() {
		super();
	}

	public FlexContainer(Direction dir, float gap) {
		super();
		this.direction = dir;
		this.gap = gap;
	}

	public FlexContainer addComponent(Component item) {
		if (item != null && !items.contains(item)) {
			items.add(item);
			add(item);
			layout();
		}
		return this;
	}

	public FlexContainer removeComponent(Component item) {
		if (item != null && items.contains(item)) {
			items.remove(item);
			remove(item);
			layout();
		}
		return this;
	}

	public FlexContainer setGap(float gap) {
		this.gap = gap;
		layout();
		return this;
	}

	public FlexContainer setPadding(float padding) {
		this.padding = padding;
		layout();
		return this;
	}

	public FlexContainer setDirection(Direction direction) {
		this.direction = direction;
		layout();
		return this;
	}

	public FlexContainer setAlignment(Align align) {
		this.alignment = align;
		layout();
		return this;
	}

	@Override
	protected void layout() {
		if (items.isEmpty()) {
			return;
		}

		float curX = this.x + padding;
		float curY = this.y + padding;
		float maxCross = 0f;

		if (direction == Direction.COLUMN) {
			for (Component item : items) {
				float itemX = curX;
				if (alignment == Align.CENTER && this.width > 0) {
					itemX = this.x + (this.width - item.width()) / 2;
				} else if (alignment == Align.END && this.width > 0) {
					itemX = this.x + this.width - item.width() - padding;
				}
				item.setPos(itemX, curY);
				curY += item.height() + gap;
				maxCross = Math.max(maxCross, item.width());
			}
			this.width = maxCross + padding * 2;
			this.height = (curY - gap) - this.y + padding;
		} else {
			for (Component item : items) {
				float itemY = curY;
				if (alignment == Align.CENTER && this.height > 0) {
					itemY = this.y + (this.height - item.height()) / 2;
				} else if (alignment == Align.END && this.height > 0) {
					itemY = this.y + this.height - item.height() - padding;
				}
				item.setPos(curX, itemY);
				curX += item.width() + gap;
				maxCross = Math.max(maxCross, item.height());
			}
			this.width = (curX - gap) - this.x + padding;
			this.height = maxCross + padding * 2;
		}
	}
}