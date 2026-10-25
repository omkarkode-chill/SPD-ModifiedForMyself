package com.shatteredpixel.shatteredpixeldungeon.items.custom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
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

		// Ensure default fallback definition always exists
		WeaponDefinition fallback = new WeaponDefinition();
		fallback.id = "obsidian_blade";
		fallback.name = "Obsidian Blade";
		fallback.description = "A razor-sharp blade forged from deep volcanic obsidian glass.";
		fallback.tier = 2;
		fallback.minDamage = 8;
		fallback.maxDamage = 22;
		fallback.strengthRequired = 12;
		fallback.image = "SWORD";
		definitions.put(fallback.id, fallback);
		idList.add(fallback.id);

		try {
			FileHandle file = Gdx.files.internal("data/weapons.json");
			if (!file.exists()) {
				file = Gdx.files.internal("weapons.json");
			}

			if (file != null && file.exists()) {
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
					if (!idList.contains(def.id)) {
						idList.add(def.id);
					}
				}
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
		WeaponDefinition def = new WeaponDefinition();
		def.id = "default_blade";
		def.name = "Obsidian Blade";
		def.description = "A keen dark blade.";
		def.tier = 2;
		def.minDamage = 8;
		def.maxDamage = 22;
		def.strengthRequired = 12;
		def.image = "SWORD";
		return def;
	}

	public static String getRandomId() {
		if (idList.isEmpty()) return "obsidian_blade";
		return idList.get(Random.Int(idList.size()));
	}

	public static void injectIntoGenerator() {
		if (injected) return;
		injected = true;

		try {
			Generator.Category[] tiers = new Generator.Category[]{
					Generator.Category.WEP_T1,
					Generator.Category.WEP_T2,
					Generator.Category.WEP_T3,
					Generator.Category.WEP_T4,
					Generator.Category.WEP_T5
			};

			for (Generator.Category cat : tiers) {
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
		try {
			if (!(Game.scene() instanceof GameScene)) {
				return;
			}

			if (hero == null || Dungeon.level == null || hero.belongings == null || hero.belongings.backpack == null) {
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
		} catch (Exception e) {
			Game.reportException(e);
		}
	}
}