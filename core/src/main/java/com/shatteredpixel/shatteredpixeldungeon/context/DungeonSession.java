package com.shatteredpixel.shatteredpixeldungeon.context;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;

public class DungeonSession {

	private Hero hero;
	private Level level;
	private int depth;
	private int branch;
	private int gold;
	private int energy;
	private int challenges;
	private long seed;

	public DungeonSession() {
		this.depth = 1;
		this.branch = 0;
		this.gold = 0;
		this.energy = 0;
	}

	public DungeonSession(Hero hero, Level level, int depth) {
		this.hero = hero;
		this.level = level;
		this.depth = depth;
	}

	public void syncFromDungeon() {
		this.hero = Dungeon.hero;
		this.level = Dungeon.level;
		this.depth = Dungeon.depth;
		this.branch = Dungeon.branch;
		this.gold = Dungeon.gold;
		this.energy = Dungeon.energy;
		this.challenges = Dungeon.challenges;
		this.seed = Dungeon.seed;
	}

	public void syncToDungeon() {
		Dungeon.hero = this.hero;
		Dungeon.level = this.level;
		Dungeon.depth = this.depth;
		Dungeon.branch = this.branch;
		Dungeon.gold = this.gold;
		Dungeon.energy = this.energy;
		Dungeon.challenges = this.challenges;
		Dungeon.seed = this.seed;
	}

	public Hero getHero() { return hero; }
	public void setHero(Hero hero) { this.hero = hero; }

	public Level getLevel() { return level; }
	public void setLevel(Level level) { this.level = level; }

	public int getDepth() { return depth; }
	public void setDepth(int depth) { this.depth = depth; }

	public int getBranch() { return branch; }
	public void setBranch(int branch) { this.branch = branch; }

	public int getGold() { return gold; }
	public void setGold(int gold) { this.gold = gold; }

	public int getEnergy() { return energy; }
	public void setEnergy(int energy) { this.energy = energy; }

	public int getChallenges() { return challenges; }
	public void setChallenges(int challenges) { this.challenges = challenges; }

	public long getSeed() { return seed; }
	public void setSeed(long seed) { this.seed = seed; }

	public boolean hasHero() {
		return hero != null;
	}

	public boolean isHeroAlive() {
		return hero != null && hero.isAlive();
	}

	public boolean isChallenged(int mask) {
		return (challenges & mask) != 0;
	}

	public DungeonSession createIsolatedCopy() {
		DungeonSession copy = new DungeonSession();
		copy.hero = this.hero;
		copy.level = this.level;
		copy.depth = this.depth;
		copy.branch = this.branch;
		copy.gold = this.gold;
		copy.energy = this.energy;
		copy.challenges = this.challenges;
		copy.seed = this.seed;
		return copy;
	}
}