package com.shatteredpixel.shatteredpixeldungeon.items.custom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.watabou.noosa.Game;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CustomItemRegistry {

	private static final HashMap<String, WeaponDefinition> definitions = new HashMap<>();
	private static final ArrayList<String> idList = new ArrayList<>();
	private static boolean starterGiven = false;
	private static boolean injected = false;

	public static void load() {
		definitions.clear();
		idList.clear();

		try {
			FileHandle file = Gdx.files.internal("data/weapons.json");
			if (!file.exists()) {
				return;
			}

			JsonValue root = new JsonReader().parse(file);
			for (JsonValue entry = root.child; entry != null; entry = entry.next) {
				WeaponDefinition def = new WeaponDefinition();
				def.id = entry.has("id") ? entry.getString("id") : "custom_weapon";
				def.name = entry.has("name") ? entry.getString("name") : "Custom Weapon";
				def.description = entry.has("description") ? entry.getString("description") : "";
				def.tier = entry.has("tier") ? entry.getInt("tier") : 1;
				def.minDamage = entry.has("minDamage") ? entry.getInt("minDamage") : 1;
				def.maxDamage = entry.has("maxDamage") ? entry.getInt("maxDamage") : 5;
				def.strengthRequired = entry.has("strengthRequired") ? entry.getInt("strengthRequired") : 10;
				def.image = entry.has("image") ? entry.getString("image") : "SWORD";

				definitions.put(def.id, def);
				idList.add(def.id);
			}
		} catch (Exception e) {
			Game.reportException(e);
		}
	}

	public static WeaponDefinition get(String id) {
		if (id != null && definitions.containsKey(id)) {
			return definitions.get(id);
		}
		if (!idList.isEmpty()) {
			return definitions.get(idList.get(0));
		}
		return null;
	}

	public static String getRandomId() {
		if (idList.isEmpty()) return "";
		return idList.get(Random.Int(idList.size()));
	}

	public static void injectIntoGenerator() {
		if (injected) return;
		injected = true;

		try {
			for (Generator.Category cat : Generator.weaponTiers) {
				if (cat.classes != null) {
					Class<?>[] newClasses = Arrays.copyOf(cat.classes, cat.classes.length + 1);
					newClasses[cat.classes.length] = DataDrivenWeapon.class;
					cat.classes = newClasses;

					if (cat.probs != null) {
						float[] newProbs = Arrays.copyOf(cat.probs, cat.probs.length + 1);
						newProbs[cat.probs.length] = 1.0f;
						cat.probs = newProbs;
					}
					if (cat.defaultProbs != null) {
						float[] newDefaultProbs = Arrays.copyOf(cat.defaultProbs, cat.defaultProbs.length + 1);
						newDefaultProbs[cat.defaultProbs.length] = 1.0f;
						cat.defaultProbs = newDefaultProbs;
					}
				}
			}
		} catch (Exception e) {
			Game.reportException(e);
		}
	}

	public static void checkStartingBonus(Hero hero) {
		if (hero == null || Dungeon.level == null) {
			return;
		}

		if (Dungeon.depth == 1) {
			if (!starterGiven) {
				starterGiven = true;
				DataDrivenWeapon starter = new DataDrivenWeapon("obsidian_blade");
				starter.identify();
				starter.collect();
			}
		} else {
			starterGiven = false;
		}
	}
}
