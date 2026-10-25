package com.shatteredpixel.shatteredpixeldungeon.ui.layout;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
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

	private String lastInfoStr = "";
	private String lastWeaponStr = "";
	private String lastDangerStr = "";
	private boolean inLayout = false;

	public ModernStatusOverlay() {
		super();
		this.camera = PixelScene.uiCamera;
	}

	@Override
	protected void createChildren() {
		try {
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
		} catch (Exception e) {
			Game.reportException(e);
		}
	}

	public void updateData() {
		try {
			if (Dungeon.hero == null || !Dungeon.hero.isAlive() || PixelScene.uiCamera == null) {
				visible = false;
				return;
			}
			visible = true;

			// Line 1: Floor depth and player HP
			String infoStr = "D:" + Dungeon.depth + " | " + Dungeon.hero.HP + "/" + Dungeon.hero.HT + " HP";
			if (!infoStr.equals(lastInfoStr)) {
				lastInfoStr = infoStr;
				infoText.text(infoStr);
				int hpColor = (Dungeon.hero.HP <= Dungeon.hero.HT * 0.3f) ? 0xFF5555 : 0xCCCCCC;
				infoText.hardlight(hpColor);
			}

			// Line 2: Gold and equipped weapon
			String wepName = (Dungeon.hero.belongings != null && Dungeon.hero.belongings.weapon != null) ?
					Dungeon.hero.belongings.weapon.name() : "Unarmed";
			if (wepName.length() > 13) {
				wepName = wepName.substring(0, 11) + "..";
			}
			String weaponStr = "G:" + Dungeon.gold + " | " + wepName;
			if (!weaponStr.equals(lastWeaponStr)) {
				lastWeaponStr = weaponStr;
				weaponText.text(weaponStr);
				weaponText.hardlight(0xFFFF99);
			}

			// Line 3: Combat state and dropped keys notification
			int enemies = Dungeon.hero.visibleEnemies();
			int uncollectedKeys = countKeysOnFloor();
			String dangerStr;
			int dangerColor;
			if (enemies > 0) {
				dangerStr = "COMBAT (" + enemies + ")";
				dangerColor = 0xFF5555;
			} else if (uncollectedKeys > 0) {
				dangerStr = "EXPLORING (KEY:" + uncollectedKeys + ")";
				dangerColor = 0xFFDD44;
			} else {
				dangerStr = "EXPLORING";
				dangerColor = 0x77DD77;
			}

			if (!dangerStr.equals(lastDangerStr)) {
				lastDangerStr = dangerStr;
				dangerText.text(dangerStr);
				dangerText.hardlight(dangerColor);
			}

			layout();
		} catch (Exception e) {
			Game.reportException(e);
		}
	}

	private int countKeysOnFloor() {
		if (Dungeon.level == null || Dungeon.level.heaps == null) return 0;
		int keyCount = 0;
		for (Heap heap : Dungeon.level.heaps.valueList()) {
			if (heap != null && heap.seen && heap.items != null) {
				for (Item item : heap.items) {
					if (item instanceof Key) {
						keyCount++;
						break;
					}
				}
			}
		}
		return keyCount;
	}

	@Override
	protected void layout() {
		if (inLayout || PixelScene.uiCamera == null || flex == null || background == null) return;
		inLayout = true;
		try {
			this.camera = PixelScene.uiCamera;
			background.camera = PixelScene.uiCamera;
			if (infoText != null) infoText.camera = PixelScene.uiCamera;
			if (weaponText != null) weaponText.camera = PixelScene.uiCamera;
			if (dangerText != null) dangerText.camera = PixelScene.uiCamera;
			flex.camera = PixelScene.uiCamera;

			this.width = Math.max(68, flex.width());
			this.height = Math.max(26, flex.height());

			background.size(this.width, this.height);

			this.x = PixelScene.uiCamera.width - this.width - 3;
			this.y = 38;

			background.x = this.x;
			background.y = this.y;
			flex.setPos(this.x, this.y);
		} finally {
			inLayout = false;
		}
	}

	public static void updateOverlay() {
		try {
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
		} catch (Exception e) {
			Game.reportException(e);
		}
	}
}