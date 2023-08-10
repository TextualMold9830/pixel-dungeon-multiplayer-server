/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pixeldungeon.windows;

import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.armor.Armor;
import com.watabou.pixeldungeon.items.bags.Bag;
import com.watabou.pixeldungeon.items.wands.Wand;
import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon;
import com.watabou.pixeldungeon.items.weapon.missiles.Boomerang;
import com.watabou.pixeldungeon.network.SendData;
import com.watabou.pixeldungeon.network.SpecialSlot;
import com.watabou.pixeldungeon.plants.Plant.Seed;
import com.watabou.pixeldungeon.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WndBag extends WndTabbed {
	
	public static enum Mode {
		ALL,
		UNIDENTIFED,
		UPGRADEABLE,
		QUICKSLOT,
		FOR_SALE,
		WEAPON,
		ARMOR,
		ENCHANTABLE,
		WAND,
		SEED
	}
	
	private Listener listener;
	private WndBag.Mode mode;
	private String title;

	protected int count;

	protected static boolean IsItemEnable(Mode mode, Item item, Hero hero) {
		return (
				mode == Mode.QUICKSLOT && (item.defaultAction != null) ||
				mode == Mode.FOR_SALE && (item.price() > 0) && (!item.isEquipped(hero) || !item.cursed) ||
				mode == Mode.UPGRADEABLE && item.isUpgradable() ||
				mode == Mode.UNIDENTIFED && !item.isIdentified() ||
				mode == Mode.WEAPON && (item instanceof MeleeWeapon || item instanceof Boomerang) ||
				mode == Mode.ARMOR && (item instanceof Armor) ||
				mode == Mode.ENCHANTABLE && (item instanceof MeleeWeapon || item instanceof Boomerang || item instanceof Armor) ||
				mode == Mode.WAND && (item instanceof Wand) ||
				mode == Mode.SEED && (item instanceof Seed) ||
				mode == Mode.ALL
		);
	}

	protected static List<List<Integer>> AllowedItems(Hero hero, Mode mode) {
		List<List<Integer>> result = new ArrayList<List<Integer>>(3);
		for (Bag bag : hero.belongings.getBags()) {
			if (bag == null){
				continue;
			}
			for (Item item : bag.items) {
				if (item == null) {
					continue;
				}
				if (IsItemEnable(mode, item, hero)) {
					result.add(item.getSlot(hero));
				}
			}
		}
		for (SpecialSlot slot : hero.belongings.getSpecialSlots()) {
		    if (slot == null){
		        continue;
            }
            if (slot.item == null) {
                continue;
            }
			if (IsItemEnable(mode, slot.item, hero)) {
				List<Integer> res = new ArrayList<>(1);
				res.add(slot.path());
				result.add(res);
			}
		}
		return result;
	}

	protected static JSONArray ListToJsonArray(List<List<Integer>> arg) {
		JSONArray result = new JSONArray();
		for (int i = 0; i < arg.size(); i++) {
			List<Integer> curr_arr = arg.get(i);
			JSONArray cur_json_arr = new JSONArray();
			for (int j = 0; j < curr_arr.size(); j++) {
				cur_json_arr.put(curr_arr.get(j));
			}
			result.put(cur_json_arr);
		}
		return result;
	}

	public WndBag( Hero owner, Listener listener, Mode mode, String title ) {

		super();
		attachToHero(owner);

		this.listener = listener;
		this.mode = mode;  // internal
		this.title = title;

		JSONObject wnd_obj = new JSONObject();
		try {
			wnd_obj.put("title", title);
			wnd_obj.put("allowed_items", ListToJsonArray(AllowedItems(owner, mode)));
			wnd_obj.put("has_listener", listener != null);
		} catch (JSONException ignored) {
		}

		SendData.sendWindow(owner.networkID, "wnd_bag", getId(), wnd_obj);
	}

	@Override
	public void onSelect(int button, JSONObject args) {
		try {
			selectItem(getOwnerHero().belongings.getItemInSlot(Utils.JsonArrayToListInteger(args.getJSONArray("item_path"))));
		} catch (JSONException e) {
			e.printStackTrace();
			assert false;
		}
		hide();
	}

	public void selectItem(Item item){
		if (listener!=null){
			listener.onSelect(item);
		}
	}

	public interface Listener {
		void onSelect( Item item );
	}
}
