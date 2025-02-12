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
package com.watabou.pixeldungeon.items.armor.glyphs;

import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.buffs.Hunger;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.items.armor.Armor;
import com.watabou.pixeldungeon.items.armor.Armor.Glyph;
import com.watabou.pixeldungeon.sprites.CharSprite;
import com.watabou.pixeldungeon.sprites.ItemSprite;
import com.watabou.pixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.pixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Random;

import static com.watabou.pixeldungeon.network.SendData.sendBuff;

public class Metabolism extends Glyph {

	private static final String TXT_METABOLISM	= "%s of metabolism";
	
	private static Glowing RED = new Glowing( 0xCC0000 );
	
	@Override
	public int proc( Armor armor, Char attacker, Char defender, int damage) {

		int level = Math.max( 0, armor.effectiveLevel() );
		if (Random.Int( level / 2 + 5 ) >= 4) {
			
			int healing = Math.min( defender.getHT() - defender.getHP(), Random.Int( 1, defender.getHT() / 5 ) );
			if (healing > 0) {
				
				Hunger hunger = defender.buff( Hunger.class );
				
				if (hunger != null && !hunger.isStarving()) {
					
					hunger.satisfy( -Hunger.STARVING / 10 );
					
					defender.setHP(defender.getHP() + healing);
					defender.getSprite().emitter().burst( Speck.factory( Speck.HEALING ), 1 );
					defender.getSprite().showStatus( CharSprite.POSITIVE, Integer.toString( healing ) );
				}
			}

		}
		
		return damage;
	}
	
	@Override
	public String name( String weaponName) {
		return String.format( TXT_METABOLISM, weaponName );
	}

	@Override
	public Glowing glowing() {
		return RED;
	}
}
