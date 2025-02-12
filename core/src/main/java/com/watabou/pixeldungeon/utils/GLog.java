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
package com.watabou.pixeldungeon.utils;

import com.watabou.pixeldungeon.network.SendData;
import com.watabou.pixeldungeon.sprites.CharSprite;

import com.nikita22007.multiplayer.server.desktop.Log;

import java.util.regex.Pattern;

public class GLog {

	public static final String TAG = "GAME";

	public static final String POSITIVE		= "++ ";
	public static final String NEGATIVE		= "-- ";
	public static final String WARNING		= "** ";
	public static final String HIGHLIGHT	= "@@ ";

	private static final Pattern PUNCTUATION = Pattern.compile( ".*[.,;?! ]$" );

	public static void i( String text, Object... args ) {
		
		if (args.length > 0) {
			text = Utils.format( text, args );
		}
		
		Log.i( TAG, text );
		sendMessage(text);
	}
	
	public static void p( String text, Object... args ) {
		i( POSITIVE + text, args );
	}
	
	public static void n( String text, Object... args ) {
		i( NEGATIVE + text, args );
	}
	
	public static void w( String text, Object... args ) {
		i( WARNING + text, args );
	}
	
	public static void h( String text, Object... args ) {
		i( HIGHLIGHT + text, args );
	}

	protected static void sendMessage(String text) {
		int color = CharSprite.DEFAULT;
		if (text.startsWith(GLog.POSITIVE)) {
			text = text.substring(GLog.POSITIVE.length());
			color = CharSprite.POSITIVE;
		} else if (text.startsWith(GLog.NEGATIVE)) {
			text = text.substring(GLog.NEGATIVE.length());
			color = CharSprite.NEGATIVE;
		} else if (text.startsWith(GLog.WARNING)) {
			text = text.substring(GLog.WARNING.length());
			color = CharSprite.WARNING;
		} else if (text.startsWith(GLog.HIGHLIGHT)) {
			text = text.substring(GLog.HIGHLIGHT.length());
			color = CharSprite.NEUTRAL;
		}

		text = Utils.capitalize(text) +
				(PUNCTUATION.matcher(text).matches() ? "" : ".");

		SendData.sendMessageToAll(text, color);

	}

	public static void wipe() {
		//todo
	}
}
