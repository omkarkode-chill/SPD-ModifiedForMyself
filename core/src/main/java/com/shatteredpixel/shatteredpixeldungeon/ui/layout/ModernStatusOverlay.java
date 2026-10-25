package com.shatteredpixel.shatteredpixeldungeon.ui.layout;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.Scene;
import com.watabou.noosa.ui.Component;

public class ModernStatusOverlay extends Component {

	private static ModernStatusOverlay instance;

	private NinePatch background;
	private FlexContainer flex;
	private RenderedTextBlock infoText;
	private RenderedTextBlock weaponText;
	private RenderedTextBlock dangerText;

	public ModernStatusOverlay() {
		super();
		this.camera = PixelScene.uiCamera;
	}

	@Override
	protected void createChildren() {
		background = Chrome.get(Chrome.Type.TOAST_TR);
		add(background);

		flex = new FlexContainer(FlexContainer.Direction.COLUMN, 1.5f);
		flex.setPadding(3.5f);

		infoText = PixelScene.renderTextBlock(5);
		weaponText = PixelScene.renderTextBlock(5);
		dangerText = PixelScene.renderTextBlock(5);

		flex.addComponent(infoText);
		flex.addComponent(weaponText);
		flex.addComponent(dangerText);

		add(flex);
	}

	public void updateData() {
		if (Dungeon.hero == null || !Dungeon.hero.isAlive() || PixelScene.uiCamera == null) {
			visible = false;
			return;
		}
		visible = true;

		infoText.text("D:" + Dungeon.depth + " | " + Dungeon.gold + "G");
		infoText.hardlight(0xCCCCCC);

		String wepName = Dungeon.hero.belongings.weapon != null ?
				Dungeon.hero.belongings.weapon.name() : "Unarmed";
		if (wepName.length() > 14) {
			wepName = wepName.substring(0, 12) + "..";
		}
		weaponText.text(wepName);
		weaponText.hardlight(0xFFFF99);

		boolean inCombat = Dungeon.hero.visibleEnemies() > 0;
		if (inCombat) {
			dangerText.text("COMBAT (" + Dungeon.hero.visibleEnemies() + ")");
			dangerText.hardlight(0xFF5555);
		} else {
			dangerText.text("EXPLORING");
			dangerText.hardlight(0x77DD77);
		}

		layout();
	}

	@Override
	protected void layout() {
		if (PixelScene.uiCamera == null) return;

		this.camera = PixelScene.uiCamera;
		background.camera = PixelScene.uiCamera;
		infoText.camera = PixelScene.uiCamera;
		weaponText.camera = PixelScene.uiCamera;
		dangerText.camera = PixelScene.uiCamera;
		flex.camera = PixelScene.uiCamera;

		this.width = Math.max(62, flex.width());
		this.height = flex.height();

		background.size(this.width, this.height);

		AnchorLayout.positionToCamera(this, PixelScene.uiCamera, Anchor.TOP_RIGHT, 3, 38);

		background.x = this.x;
		background.y = this.y;
		flex.setPos(this.x, this.y);
	}

	public static void updateOverlay() {
		Scene curScene = Game.scene();
		if (curScene instanceof GameScene && Dungeon.hero != null && Dungeon.hero.isAlive()) {
			if (instance == null || instance.parent != curScene) {
				instance = new ModernStatusOverlay();
				curScene.add(instance);
			}
			instance.updateData();
		} else {
			if (instance != null) {
				if (instance.parent != null) {
					instance.parent.remove(instance);
				}
				instance.destroy();
				instance = null;
			}
		}
	}
}