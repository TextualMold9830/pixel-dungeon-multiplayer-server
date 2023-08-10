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
package com.watabou.pixeldungeon.items.wands;

import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.nikita22007.multiplayer.noosa.tweeners.AlphaTweener;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.effects.MagicMissile;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.mechanics.Ballistica;
import com.watabou.utils.Callback;

public class WandOfBlink extends Wand {

	{
		name = "Wand of Blink";
	}
	
	@Override
	protected void onZap( int cell ) {

		int level = power();
		
		if (Ballistica.distance > level + 4) {
			cell = Ballistica.trace[level + 3];
		} else if (Actor.findChar( cell ) != null && Ballistica.distance > 1) {
			cell = Ballistica.trace[Ballistica.distance - 2];
		}
		
		curUser.getSprite().visible = true;
		appear( curUser, cell );
		Dungeon.observeAll();
	}
	
	@Override
	protected void fx( int cell, Callback callback ) {
		MagicMissile.whiteLight( curUser.getSprite().parent, curUser.pos, cell, callback );
		Sample.INSTANCE.play( Assets.SND_ZAP );
		curUser.getSprite().visible = false;
	}
	
	public static void appear( Char ch, int pos ) {
		
		ch.move( pos );
		ch.getSprite().place( pos );
		
		if (ch.invisible == 0) {
			ch.getSprite().alpha( 0 );
			AlphaTweener.showAlphaTweener(ch.getSprite(), 1, 0.4f );
		}
		
		ch.getSprite().emitter().start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );
		Sample.INSTANCE.play( Assets.SND_TELEPORT );
	}
	
	@Override
	public String desc() {
		return
			"This wand will allow you to teleport in the chosen direction. " +
			"Creatures and inanimate obstructions will block the teleportation.";
	}
}
