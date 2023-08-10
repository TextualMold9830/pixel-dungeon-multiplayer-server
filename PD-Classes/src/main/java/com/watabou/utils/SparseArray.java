/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
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

package com.watabou.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

//I think everything works good now
// Everything works. Now we need BitMap
public class SparseArray<T> extends HashMap<Integer, T> {

	public int[] keyArray() {
		Set<Integer> keySet = keySet();
		int[] keys = new int[keySet.size()];
		int i = 0;
		for (Integer key : keySet){
			keys[i] = key;
			i++;
		}
		return  keys;
	}
	
	public List<T> values() {
		return new ArrayList<T>(super.values());
	}
}
