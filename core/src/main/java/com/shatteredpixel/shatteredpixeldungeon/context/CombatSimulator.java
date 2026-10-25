package com.shatteredpixel.shatteredpixeldungeon.context;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

public class CombatSimulator {

	public static class SimulationResult {
		public int rounds;
		public boolean heroWon;
		public int heroHpRemaining;
		public int damageDealt;
		public int damageTaken;
	}

	/**
	 * Simulates a 1v1 combat engagement in headless isolation without
	 * affecting the active game world or rendering pipeline.
	 */
	public static SimulationResult simulate(DungeonSession session, Mob enemy, int maxRounds) {
		SimulationResult result = new SimulationResult();
		if (session == null || session.getHero() == null || enemy == null) {
			return result;
		}

		Hero hero = session.getHero();
		int heroHp = hero.HP;
		int enemyHp = enemy.HP;

		int round = 0;
		while (heroHp > 0 && enemyHp > 0 && round < maxRounds) {
			round++;

			// Hero attack
			int heroDmg = Math.max(1, hero.damageRoll());
			enemyHp -= heroDmg;
			result.damageDealt += heroDmg;

			if (enemyHp <= 0) break;

			// Enemy attack
			int enemyDmg = Math.max(0, enemy.damageRoll());
			heroHp -= enemyDmg;
			result.damageTaken += enemyDmg;
		}

		result.rounds = round;
		result.heroWon = enemyHp <= 0;
		result.heroHpRemaining = Math.max(0, heroHp);
		return result;
	}
}