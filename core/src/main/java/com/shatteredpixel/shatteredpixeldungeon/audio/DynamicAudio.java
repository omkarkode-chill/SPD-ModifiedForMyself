package com.shatteredpixel.shatteredpixeldungeon.audio;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class DynamicAudio {

	private static boolean inCombat = false;
	private static float calmTimer = 0f;
	private static final float CALM_DELAY = 3.5f;

	private static float heartbeatTimer = 0f;
	private static boolean transitioning = false;

	public static void update() {
		// Only run dynamic audio during active dungeon gameplay
		if (!(Game.scene() instanceof GameScene)) {
			if (inCombat) {
				reset();
			}
			return;
		}

		if (Dungeon.hero == null || Dungeon.level == null || !Dungeon.hero.isAlive()) {
			return;
		}

		// Do not interrupt boss music or amulet ascension
		if (Dungeon.bossLevel() || Statistics.amuletObtained) {
			return;
		}

		boolean hasVisibleEnemies = Dungeon.hero.visibleEnemies() > 0;
		boolean lowHealth = Dungeon.hero.HP <= (Dungeon.hero.HT * 0.35f);
		boolean danger = hasVisibleEnemies || lowHealth;

		// 1. Adaptive Tension / Ambient Music Transition
		if (danger) {
			calmTimer = 0f;
			if (!inCombat && !transitioning) {
				inCombat = true;
				playTensionMusic();
			}
		} else {
			if (inCombat) {
				calmTimer += Game.elapsed;
				if (calmTimer >= CALM_DELAY && !transitioning) {
					inCombat = false;
					playAmbientMusic();
				}
			}
		}

		// 2. Low-Health Heartbeat Pulse
		if (Dungeon.hero.HP <= (Dungeon.hero.HT * 0.25f)) {
			heartbeatTimer += Game.elapsed;
			float pulseInterval = (Dungeon.hero.HP <= Dungeon.hero.HT * 0.15f) ? 0.8f : 1.3f;
			if (heartbeatTimer >= pulseInterval) {
				heartbeatTimer = 0f;
				String sfx = (Dungeon.hero.HP <= Dungeon.hero.HT * 0.15f) ?
						Assets.Sounds.HEALTH_CRITICAL : Assets.Sounds.HEALTH_WARN;
				Sample.INSTANCE.play(sfx, 0.45f);
			}
		} else {
			heartbeatTimer = 0f;
		}
	}

	private static void playTensionMusic() {
		final String tenseTrack = getTensionTrackForDepth(Dungeon.depth);
		if (tenseTrack == null) return;

		transitioning = true;
		Music.INSTANCE.fadeOut(0.6f, new Callback() {
			@Override
			public void call() {
				Music.INSTANCE.play(tenseTrack, true);
				transitioning = false;
			}
		});
	}

	private static void playAmbientMusic() {
		transitioning = true;
		Music.INSTANCE.fadeOut(0.8f, new Callback() {
			@Override
			public void call() {
				if (Dungeon.level != null) {
					Dungeon.level.playLevelMusic();
				}
				transitioning = false;
			}
		});
	}

	private static String getTensionTrackForDepth(int depth) {
		if (depth >= 1 && depth <= 4) {
			return Assets.Music.SEWERS_TENSE;
		} else if (depth >= 6 && depth <= 9) {
			return Assets.Music.PRISON_TENSE;
		} else if (depth >= 11 && depth <= 14) {
			return Assets.Music.CAVES_TENSE;
		} else if (depth >= 16 && depth <= 19) {
			return Assets.Music.CITY_TENSE;
		} else if (depth >= 21 && depth <= 24) {
			return Assets.Music.HALLS_TENSE;
		}
		return null;
	}

	public static void reset() {
		inCombat = false;
		calmTimer = 0f;
		heartbeatTimer = 0f;
		transitioning = false;
	}
}
