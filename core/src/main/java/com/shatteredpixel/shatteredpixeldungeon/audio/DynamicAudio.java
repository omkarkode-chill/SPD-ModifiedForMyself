package com.shatteredpixel.shatteredpixeldungeon.audio;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

/**
 * Lightweight, zero-lag dynamic audio engine.
 * Avoids streaming/reloading long background music tracks during combat transitions
 * (which causes frame hitches on Android MediaPlayer), but preserves instant RAM-cached
 * sound cues: heartbeat pulses, surprise attack impacts, and environmental ambiance.
 */
public class DynamicAudio {

	private static float heartbeatTimer = 0f;
	private static float ambientTimer = 0f;
	private static float nextAmbientDelay = 14f;

	public static void update() {
		// Only run dynamic audio during active dungeon gameplay
		if (!(Game.scene() instanceof GameScene)) {
			return;
		}

		if (Dungeon.hero == null || Dungeon.level == null || !Dungeon.hero.isAlive()) {
			return;
		}

		// Do not play ambiance during boss level
		if (Dungeon.bossLevel()) {
			return;
		}

		boolean hasVisibleEnemies = Dungeon.hero.visibleEnemies() > 0;
		boolean lowHealth = Dungeon.hero.HP <= (Dungeon.hero.HT * 0.35f);
		boolean inCombat = hasVisibleEnemies || lowHealth;

		// 1. Low-Health Heartbeat Pulse (instant SoundPool, zero lag)
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

		// 2. Environmental Atmosphere SFX (periodic subtle cues when peacefully exploring)
		if (!inCombat && !Statistics.amuletObtained) {
			ambientTimer += Game.elapsed;
			if (ambientTimer >= nextAmbientDelay) {
				ambientTimer = 0f;
				nextAmbientDelay = Random.Float(16f, 26f);
				playAtmosphericCue(Dungeon.depth);
			}
		} else {
			ambientTimer = 0f;
		}
	}

	private static void playAtmosphericCue(int depth) {
		try {
			if (depth >= 1 && depth <= 4) {
				Sample.INSTANCE.play(Assets.Sounds.WATER, 0.22f, Random.Float(0.85f, 1.15f));
			} else if (depth >= 6 && depth <= 9) {
				Sample.INSTANCE.play(Assets.Sounds.UNLOCK, 0.18f, Random.Float(0.70f, 0.90f));
			} else if (depth >= 11 && depth <= 14) {
				Sample.INSTANCE.play(Assets.Sounds.ROCKS, 0.22f, Random.Float(0.90f, 1.10f));
			} else if (depth >= 16 && depth <= 19) {
				Sample.INSTANCE.play(Assets.Sounds.BURNING, 0.20f, Random.Float(0.95f, 1.05f));
			} else if (depth >= 21 && depth <= 24) {
				Sample.INSTANCE.play(Assets.Sounds.CURSED, 0.22f, Random.Float(0.75f, 0.95f));
			}
		} catch (Exception e) {}
	}

	public static void playSurpriseImpact() {
		try {
			Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG, 0.85f, Random.Float(0.95f, 1.05f));
		} catch (Exception e) {}
	}

	public static void reset() {
		heartbeatTimer = 0f;
		ambientTimer = 0f;
	}
}
