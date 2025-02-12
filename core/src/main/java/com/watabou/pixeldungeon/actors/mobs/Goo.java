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
package com.watabou.pixeldungeon.actors.mobs;

import java.util.HashSet;

import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.Statistics;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.blobs.ToxicGas;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Ooze;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.items.LloydsBeacon;
import com.watabou.pixeldungeon.items.keys.SkeletonKey;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.watabou.pixeldungeon.items.weapon.enchantments.Death;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.SewerBossLevel;
import com.watabou.pixeldungeon.mechanics.Ballistica;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.sprites.CharSprite;
import com.watabou.pixeldungeon.sprites.GooSprite;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Goo extends Mob {

	private static final float PUMP_UP_DELAY	= 2f;
	
	{
		name = Dungeon.depth == Statistics.deepestFloor ? "Goo" : "spawn of Goo";
		
		setHP(setHT(80));
		EXP = 10;
		defenseSkill = 12;
		spriteClass = GooSprite.class;
		
		loot = new LloydsBeacon();
		lootChance = 0.333f;
	}
	
	private boolean pumpedUp	= false;
	private boolean jumped		= false;
	
	@Override
	public int damageRoll() {
		if (pumpedUp) {
			return Random.NormalIntRange( 5, 30 );
		} else {
			return Random.NormalIntRange( 2, 12 );
		}
	}
	
	@Override
	public int attackSkill( Char target ) {
		return pumpedUp && !jumped ? 30 : 15;
	}
	
	@Override
	public int dr() {
		return 2;
	}
	
	@Override
	public boolean act() {
		
		if (Level.water[pos] && getHP() < getHT()) {
			getSprite().emitter().burst( Speck.factory( Speck.HEALING ), 1 );
			setHP(getHP() + 1);
		}
		
		return super.act();
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return pumpedUp ? distance( enemy ) <= 2 : super.canAttack( enemy );
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		if (Random.Int( 3 ) == 0) {
			Buff.affect( enemy, Ooze.class );
			enemy.getSprite().burst( 0x000000, 5 );
		}
		
		return damage;
	}
	
	@Override
	protected boolean doAttack( final Char enemy ) {		
		if (pumpedUp) {
			
			if (Level.adjacent( pos, enemy.pos )) {
				
				// Pumped up attack WITHOUT accuracy penalty
				jumped = false;
				return super.doAttack( enemy );
				
			} else {
				
				// Pumped up attack WITH accuracy penalty
				jumped = true;
				if (Ballistica.cast( pos, enemy.pos, false, true ) == enemy.pos) {
					final int dest = Ballistica.trace[Ballistica.distance - 2];
					
					Callback afterJump = new Callback() {
						@Override
						public void call() {
							move( dest );
							Dungeon.level.mobPress( Goo.this );
							Goo.super.doAttack( enemy );
						}
					};
					
					if (Dungeon.visible[pos] || Dungeon.visible[dest]) {
						
						getSprite().jump( pos, dest, afterJump );
						return false;
						
					} else {
						
						afterJump.call();
						return true;
						
					}
				} else {
					
					getSprite().idle();
					pumpedUp = false;
					return true;
				}
			}
			
		} else if (Random.Int( 3 ) > 0) {
		
			// Normal attack
			return super.doAttack( enemy );

		} else {
			
			// Pumping up
			pumpedUp = true;
			spend( PUMP_UP_DELAY );
			
			((GooSprite) getSprite()).pumpUp();
			
			if (Dungeon.visible[pos]) {
				getSprite().showStatus( CharSprite.NEGATIVE, "!!!" );
				GLog.n( "Goo is pumping itself up!" );
			}
				
			return true;
		}
	}
	
	@Override
	public boolean attack( Char enemy ) {
		boolean result = super.attack( enemy );
		pumpedUp = false;
		return result;
	}
	
	@Override
	protected boolean getCloser( int target ) {
		pumpedUp = false;
		return super.getCloser( target );
	}
	
	@Override
	public void move( int step ) {
		((SewerBossLevel)Dungeon.level).seal();
		super.move( step );
	}
	
	@Override
	public void die( Object cause ) {
		
		super.die( cause );
		
		((SewerBossLevel)Dungeon.level).unseal();
		
		GameScene.bossSlain();
		Dungeon.level.drop( new SkeletonKey(), pos );
		
		Badges.validateBossSlain();
		
		yell( "glurp... glurp..." );
	}
	
	@Override
	public void notice() {
		super.notice();
		yell( "GLURP-GLURP!" );
	}
	
	@Override
	public String description() {
		return
			"Little known about The Goo. It's quite possible that it is not even a creature, but rather a " +
			"conglomerate of substances from the sewers that gained rudiments of free will.";
	}
	
	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add( ToxicGas.class );
		RESISTANCES.add( Death.class );
		RESISTANCES.add( ScrollOfPsionicBlast.class );
	}
	
	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
}
