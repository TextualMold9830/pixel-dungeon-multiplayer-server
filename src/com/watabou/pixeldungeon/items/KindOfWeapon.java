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
package com.watabou.pixeldungeon.items;

import java.util.ArrayList;

import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.utils.Random;

abstract public class KindOfWeapon extends EquipableItem {

	private static final String TXT_EQUIP_CURSED	= "you wince as your grip involuntarily tightens around your %s";
	
	protected static final float TIME_TO_EQUIP = 1f;
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( isEquipped( hero ) ? AC_UNEQUIP : AC_EQUIP );
		return actions;
	}
	
	@Override
	public boolean isEquipped( Hero hero ) {
		return hero.belongings.getWeapon() == this;
	}
	
	@Override
	public boolean doEquip( Hero hero ) {
		
		detachAll( hero.belongings.backpack );
		
		if (hero.belongings.getWeapon() == null || hero.belongings.getWeapon().doUnequip( hero, true )) {
			
			hero.belongings.setWeapon(this);
			activate( hero );

			cursedKnown = true;
			if (cursed) {
				equipCursed( hero );
				GLog.n( TXT_EQUIP_CURSED, name() );
			}
			
			hero.spendAndNext( TIME_TO_EQUIP );
			return true;
			
		} else {
			
			collect( hero.belongings.backpack );
			return false;
		}
	}
	
	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		if (super.doUnequip( hero, collect, single )) {
			
			hero.belongings.setWeapon(null);
			return true;
			
		} else {
			
			return false;
			
		}
	}
	
	public void activate( Hero hero ) {
	}
	
	abstract public int min();
	abstract public int max();
	
	public int damageRoll( Hero owner ) {
		return Random.NormalIntRange( min(), max() );
	}
	
	public float acuracyFactor( Hero hero ) {
		return 1f;
	}
	
	public float speedFactor( Hero hero ) {
		return 1f;
	}
	
	public void proc( Char attacker, Char defender, int damage ) {
	}
	
}
