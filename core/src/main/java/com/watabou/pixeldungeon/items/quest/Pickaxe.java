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
package com.watabou.pixeldungeon.items.quest;

import java.util.ArrayList;

import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.buffs.Hunger;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.mobs.Bat;
import com.watabou.pixeldungeon.effects.CellEmitter;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.items.weapon.Weapon;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.Terrain;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.pixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

public class Pickaxe extends Weapon {
	
	public static final String AC_MINE	= "MINE";
	
	public static final float TIME_TO_MINE = 2;
	
	private static final String TXT_NO_VEIN = "There is no dark gold vein near you to mine";
	
	private static final Glowing BLOODY = new Glowing( 0x550000 );
	
	{
		name = "pickaxe";
		image = ItemSpriteSheet.PICKAXE;
		
		unique = true;
		
		defaultAction = AC_MINE;
		
		STR = 14;
	}
	
	private boolean bloodStained = false;
	
	@Override
	public int min() {
		return 3;
	}
	
	@Override
	public int max() {
		return 12;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_MINE );
		return actions;
	}
	
	@Override
	public void execute( final Hero hero, String action ) {
		
		if (action == AC_MINE) {
			
			if (Dungeon.depth < 11 || Dungeon.depth > 15) {
				GLog.w( TXT_NO_VEIN );
				return;
			}
			
			for (int i=0; i < Level.NEIGHBOURS8.length; i++) {
				
				final int pos = hero.pos + Level.NEIGHBOURS8[i];
				if (Dungeon.level.map[pos] == Terrain.WALL_DECO) {
				
					hero.spend( TIME_TO_MINE );
					hero.busy();
					
					hero.getSprite().attack( pos, new Callback() {
						
						@Override
						public void call() {

							CellEmitter.center( pos ).burst( Speck.factory( Speck.STAR ), 7 );
							Sample.INSTANCE.play( Assets.SND_EVOKE );
							
							Level.set( pos, Terrain.WALL );
							GameScene.updateMap( pos );
							
							DarkGold gold = new DarkGold();
							if (gold.doPickUp( hero )) {
								GLog.i( Hero.TXT_YOU_NOW_HAVE, gold.name() );
							} else {
								Dungeon.level.drop( gold, hero.pos );
							}
							
							Hunger hunger = hero.buff( Hunger.class );
							if (hunger != null && !hunger.isStarving()) {
								hunger.satisfy( -Hunger.STARVING / 10 );
							}
							
							hero.onOperateComplete();
						}
					} );
					
					return;
				}
			}
			
			GLog.w( TXT_NO_VEIN );
			
		} else {
			
			super.execute( hero, action );
			
		}
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public void proc( Char attacker, Char defender, int damage ) {
		if (!isBloodStained() && defender instanceof Bat && (defender.getHP() <= damage)) {
			setBloodStained(true);
			SendSelfUpdate(attacker instanceof Hero? (Hero) attacker: null);
		}
	}
	
	private static final String BLOODSTAINED = "bloodStained";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		
		bundle.put( BLOODSTAINED, isBloodStained());
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		
		setBloodStained(bundle.getBoolean( BLOODSTAINED ));
	}
	

	protected void updateGlowing() {
		setGlowing(isBloodStained() ? BLOODY : null);
	}
	
	@Override
	public String info() {
		return
			"This is a large and sturdy tool for breaking rocks. Probably it can be used as a weapon.";
	}

	public boolean isBloodStained() {
		return bloodStained;
	}

	protected void setBloodStained(boolean bloodStained) {
		this.bloodStained = bloodStained;
		updateGlowing();
	}
}
