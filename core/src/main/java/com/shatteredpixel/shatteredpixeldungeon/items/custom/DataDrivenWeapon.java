package com.shatteredpixel.shatteredpixeldungeon.items.custom;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class DataDrivenWeapon extends MeleeWeapon {

	private static final String DEFINITION_ID = "definition_id";

	public String definitionId = "";
	public WeaponDefinition definition;

	public DataDrivenWeapon() {
		this(CustomItemRegistry.getRandomId());
	}

	public DataDrivenWeapon(String id) {
		super();
		this.definitionId = (id != null && !id.isEmpty()) ? id : "obsidian_blade";
		initFromDefinition(CustomItemRegistry.get(this.definitionId));
	}

	public void initFromDefinition(WeaponDefinition def) {
		this.definition = def;
		if (def != null) {
			this.tier = Math.max(1, def.tier);
			this.image = resolveSprite(def.image);
			this.hitSound = resolveHitSound(def.image);
			this.hitSoundPitch = 1.0f;
		} else {
			this.tier = 2;
			this.image = ItemSpriteSheet.SWORD;
			this.hitSound = Assets.Sounds.HIT_SLASH;
			this.hitSoundPitch = 1.0f;
		}
	}

	private static String resolveHitSound(String spriteName) {
		if (spriteName == null) return Assets.Sounds.HIT_SLASH;
		switch (spriteName.toUpperCase()) {
			case "MACE":
			case "GREATAXE":
				return Assets.Sounds.HIT_CRUSH;
			case "SPEAR":
			case "DAGGER":
				return Assets.Sounds.HIT_STAB;
			default:
				return Assets.Sounds.HIT_SLASH;
		}
	}

	private static int resolveSprite(String name) {
		if (name == null) return ItemSpriteSheet.SWORD;
		switch (name.toUpperCase()) {
			case "DAGGER": return ItemSpriteSheet.DAGGER;
			case "SHORTSWORD": return ItemSpriteSheet.SHORTSWORD;
			case "HAND_AXE": return ItemSpriteSheet.HAND_AXE;
			case "SPEAR": return ItemSpriteSheet.SPEAR;
			case "SWORD": return ItemSpriteSheet.SWORD;
			case "MACE": return ItemSpriteSheet.MACE;
			case "WHIP": return ItemSpriteSheet.WHIP;
			case "LONGSWORD": return ItemSpriteSheet.LONGSWORD;
			case "BATTLE_AXE": return ItemSpriteSheet.BATTLE_AXE;
			case "KATANA": return ItemSpriteSheet.KATANA;
			case "GREATSWORD": return ItemSpriteSheet.GREATSWORD;
			case "GREATAXE": return ItemSpriteSheet.GREATAXE;
			case "WAR_SCYTHE": return ItemSpriteSheet.WAR_SCYTHE;
			case "GLOVES": return ItemSpriteSheet.GLOVES;
			default: return ItemSpriteSheet.SWORD;
		}
	}

	@Override
	public String name() {
		return definition != null ? definition.name : "Obsidian Blade";
	}

	@Override
	public String desc() {
		return definition != null ? definition.description : "A keen dark blade.";
	}

	@Override
	public int min(int lvl) {
		if (definition != null && definition.minDamage > 0) {
			return definition.minDamage + lvl;
		}
		return super.min(lvl);
	}

	@Override
	public int max(int lvl) {
		if (definition != null && definition.maxDamage > 0) {
			return definition.maxDamage + lvl * (tier + 1);
		}
		return super.max(lvl);
	}

	@Override
	public int STRReq(int lvl) {
		if (definition != null && definition.strengthRequired > 0) {
			int req = definition.strengthRequired - (lvl / 3);
			if (masteryPotionBonus) req -= 2;
			return Math.max(1, req);
		}
		return super.STRReq(lvl);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		damage = super.proc(attacker, defender, damage);
		if (defender != null && defender.isAlive() && definition != null && definition.effect != null) {
			try {
				switch (definition.effect.toUpperCase()) {
					case "FROST":
						Buff.affect(defender, Chill.class, 3f);
						if (defender.sprite != null) {
							Splash.at(defender.sprite.center(), 0xFFB2D6FF, 4);
						}
						break;
					case "FIRE":
						Buff.affect(defender, Burning.class).reignite(defender);
						if (defender.sprite != null) {
							Splash.at(defender.sprite.center(), 0xFFFF6600, 4);
						}
						break;
					case "VAMPIRIC":
						if (attacker != null && attacker.isAlive() && damage > 0) {
							int heal = Math.max(1, damage / 5);
							attacker.HP = Math.min(attacker.HT, attacker.HP + heal);
							if (attacker.sprite != null) {
								attacker.sprite.emitter().burst(Speck.factory(Speck.HEALING), 3);
							}
						}
						break;
					case "SHOCK":
						Sample.INSTANCE.play(Assets.Sounds.LIGHTNING, 0.6f);
						if (defender.sprite != null) {
							Splash.at(defender.sprite.center(), 0xFFFFFF88, 5);
						}
						break;
				}
			} catch (Exception e) {
				// Safety fail-silent
			}
		}
		return damage;
	}

	@Override
	protected int baseChargeUse(Hero hero, Char target) {
		return 1;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(Sword.class, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		int dmgBoost = augment.damageFactor(5 + buffedLvl());
		Sword.cleaveAbility(hero, target, 1, dmgBoost, this);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DEFINITION_ID, definitionId);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		this.definitionId = bundle.getString(DEFINITION_ID);
		initFromDefinition(CustomItemRegistry.get(this.definitionId));
	}
}