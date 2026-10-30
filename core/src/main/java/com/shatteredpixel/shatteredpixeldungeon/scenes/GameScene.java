/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DemonSpawner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Snake;
import com.shatteredpixel.shatteredpixeldungeon.effects.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.*;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.Guidebook;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.InventoryScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.DimensionalSundial;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DiscardedItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTerrainTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonWallsTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.FogOfWar;
import com.shatteredpixel.shatteredpixeldungeon.tiles.GridTileMap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.RaisedTerrainTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.TerrainFeaturesTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.WallBlockingTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Banner;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.CharHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.GameLog;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventoryPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.LootIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.MenuPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ResumeIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.RightClickMenu;
import com.shatteredpixel.shatteredpixeldungeon.ui.StatusPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Tag;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toast;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toolbar;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGame;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHero;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoCell;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoMob;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoPlant;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoTrap;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndResurrect;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUpgrade;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Blending;
import com.watabou.input.ControllerHandler;
import com.watabou.input.KeyBindings;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.*;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PlatformSupport;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.RectF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public class GameScene extends PixelScene {

	static GameScene scene;

	private Group worldGroup;
	public static Group bloomGroup;

	private SkinnedBlock water;
	public SkinnedBlock waterBlock = water;
	private DungeonTerrainTilemap tiles;
	private GridTileMap visualGrid;
	private TerrainFeaturesTilemap terrainFeatures;
	private RaisedTerrainTilemap raisedTerrain;
	private DungeonWallsTilemap walls;
	private WallBlockingTilemap wallBlocking;
	private FogOfWar fog;
	private HeroSprite hero;

	private MenuPane menu;
	private StatusPane status;

	private BossHealthBar boss;

	private GameLog log;

	private static CellSelector cellSelector;

	private Group terrain;
	private Group customTiles;
	private Group levelVisuals;
	private Group levelWallVisuals;
	private Group customWalls;
	private Group ripples;
	private Group plants;
	private Group traps;
	private Group heaps;
	private Group mobs;
	private Group floorEmitters;
	private Group emitters;
	private Group effects;
	private Group gases;
	private Group spells;
	private Group statuses;
	private Group emoicons;
	private Group overFogEffects;
	private Group healthIndicators;

	private InventoryPane inventory;
	private static boolean invVisible = true;

	private Toolbar toolbar;
	private Toast prompt;

	private AttackIndicator attack;
	private LootIndicator loot;
	private ActionIndicator action;
	private ResumeIndicator resume;

	{
		inGameScene = true;
	}

	@Override
	public void create() {

		bloomObjects.clear();

		if (Dungeon.hero == null || Dungeon.level == null) {
			ShatteredPixelDungeon.switchNoFade(TitleScene.class);
			return;
		}

		Dungeon.level.playLevelMusic();

		SPDSettings.lastClass(Dungeon.hero.heroClass.ordinal());

		super.create();
		PostProcessingManager.instance.init(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Camera.main.zoom(GameMath.gate(minZoom, defaultZoom + SPDSettings.zoom(), maxZoom));
		Camera.main.edgeScroll.set(1);

		switch (SPDSettings.cameraFollow()) {
			case 4:
			default:
				Camera.main.setFollowDeadzone(0);
				break;
			case 3:
				Camera.main.setFollowDeadzone(0.2f);
				break;
			case 2:
				Camera.main.setFollowDeadzone(0.5f);
				break;
			case 1:
				Camera.main.setFollowDeadzone(0.9f);
				break;
		}

		RectF insets = getCommonInsets();
		float largeInsetTop = Game.platform.getSafeInsets(PlatformSupport.INSET_LRG).scale(1f / defaultZoom).top;

		scene = this;

		worldGroup = new Group();
		add(worldGroup);

		bloomGroup = new Group();
		worldGroup.add(bloomGroup);

		terrain = new Group();
		worldGroup.add(terrain);
		/*old
		water = new SkinnedBlock(
				Dungeon.level.width() * DungeonTilemap.SIZE,
				Dungeon.level.height() * DungeonTilemap.SIZE,
				Dungeon.level.waterTex()) {

			@Override
			protected NoosaScript script() {
				return NoosaScriptNoLighting.get();
			}

			@Override
			public void draw() {
				Blending.disable();
				super.draw();
				Blending.enable();
			}
		};
		 */
		water = new SkinnedBlock(
				Dungeon.level.width() * DungeonTilemap.SIZE,
				Dungeon.level.height() * DungeonTilemap.SIZE,
				Dungeon.level.waterTex()) {

			@Override
			protected NoosaScript script() {
				return com.watabou.noosa.WaterScript.get();
			}

			@Override
			public void draw() {
				Blending.enable();
				super.draw();
			}
		};
		water.autoAdjust = true;
		terrain.add(water);

		ripples = new Group();
		terrain.add(ripples);

		DungeonTileSheet.setupVariance(Dungeon.level.map.length, Dungeon.seedCurDepth());

		tiles = new DungeonTerrainTilemap();
		terrain.add(tiles);

		customTiles = new Group();
		terrain.add(customTiles);

		for (CustomTilemap visual : Dungeon.level.customTiles) {
			addCustomTile(visual);
		}

		visualGrid = new GridTileMap();
		terrain.add(visualGrid);

		terrainFeatures = new TerrainFeaturesTilemap(Dungeon.level.plants, Dungeon.level.traps);
		terrain.add(terrainFeatures);

		levelVisuals = Dungeon.level.addVisuals();
		worldGroup.add(levelVisuals); // worldGroup

		floorEmitters = new Group();
		worldGroup.add(floorEmitters); // worldGroup

		heaps = new Group();
		worldGroup.add(heaps); // worldGroup

		for (Heap heap : Dungeon.level.heaps.valueList()) {
			addHeapSprite(heap);
		}

		emitters = new Group();
		effects = new Group();
		healthIndicators = new Group();
		emoicons = new Group();
		overFogEffects = new Group();

		mobs = new Group();
		worldGroup.add(mobs); // worldGroup

		// --- MULTIPLAYER ---
		if (com.shatteredpixel.shatteredpixeldungeon.network.NetworkManager.instance().isMultiplayer &&
				com.shatteredpixel.shatteredpixeldungeon.network.NetworkManager.instance().localClient() != null &&
				com.shatteredpixel.shatteredpixeldungeon.network.NetworkManager.instance().localClient().snapshot() != null) {

			com.shatteredpixel.shatteredpixeldungeon.network.dto.state.GameStateDto state = com.shatteredpixel.shatteredpixeldungeon.network.NetworkManager.instance().localClient().snapshot().state;
			long myId = com.shatteredpixel.shatteredpixeldungeon.network.NetworkManager.instance().localPlayerId();

			int[] spawnOffsets = {1, -1, Dungeon.level.width(), -Dungeon.level.width()};
			java.util.HashSet<Integer> occupied = new java.util.HashSet<>();

			com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero realLocalHero = Dungeon.hero;
			int defaultPos = Dungeon.hero.pos;

			for (com.shatteredpixel.shatteredpixeldungeon.network.dto.state.HeroStateDto hs : state.heroes) {
				int spawnPos = hs.pos <= 0 ? defaultPos : hs.pos;

				if (occupied.contains(spawnPos)) {
					for (int offset : spawnOffsets) {
						int candidate = spawnPos + offset;
						if (candidate >= 0 && candidate < Dungeon.level.length() && Dungeon.level.passable[candidate] && !occupied.contains(candidate)) {
							spawnPos = candidate;
							break;
						}
					}
				}
				occupied.add(spawnPos);

				if (hs.ownerPlayerId == myId) {
					Dungeon.hero.pos = spawnPos;
					Dungeon.hero.playerId = myId;
					Dungeon.hero.syncMultiplayerPriority();
				} else {
					com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass remoteClass = com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass.WARRIOR;
					try {
						if (hs.heroClass != null) {
							remoteClass = com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass.valueOf(hs.heroClass);
						}
					} catch (Exception e) {
					}

					com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero remoteHero = new com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero();
					remoteHero.heroClass = remoteClass;
					remoteHero.playerId = hs.ownerPlayerId;
					remoteHero.syncMultiplayerPriority();

					Dungeon.hero = remoteHero;
					com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite remoteSprite = new com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite();
					Dungeon.hero = realLocalHero;

					remoteHero.pos = spawnPos;
					remoteSprite.link(remoteHero);
					remoteSprite.place(remoteHero.pos);
					remoteSprite.updateArmor();

					mobs.add(remoteSprite);
					com.shatteredpixel.shatteredpixeldungeon.actors.Actor.add(remoteHero);
				}
			}
		} else {
			Dungeon.hero.playerId = 0;
			Dungeon.hero.syncMultiplayerPriority();
		}
		// ----------------------

		hero = new HeroSprite();
		hero.place(Dungeon.hero.pos);
		hero.updateArmor();
		mobs.add(hero);

		for (Mob mob : Dungeon.level.mobs) {
			addMobSprite(mob);
		}

		raisedTerrain = new RaisedTerrainTilemap();
		worldGroup.add(raisedTerrain); // worldGroup

		walls = new DungeonWallsTilemap();
		worldGroup.add(walls); // worldGroup

		customWalls = new Group();
		worldGroup.add(customWalls); // worldGroup

		for (CustomTilemap visual : Dungeon.level.customWalls) {
			addCustomWall(visual);
		}

		levelWallVisuals = Dungeon.level.addWallVisuals();
		worldGroup.add(levelWallVisuals); // worldGroup

		wallBlocking = new WallBlockingTilemap();
		worldGroup.add(wallBlocking); // worldGroup

		worldGroup.add(emitters); // worldGroup
		worldGroup.add(effects); // worldGroup

		gases = new Group();
		worldGroup.add(gases); // worldGroup

		for (Blob blob : Dungeon.level.blobs.values()) {
			blob.emitter = null;
			addBlobSprite(blob);
		}


		fog = new FogOfWar(Dungeon.level.width(), Dungeon.level.height());
		worldGroup.add(fog); // worldGroup

		spells = new Group();
		worldGroup.add(spells); // worldGroup

		worldGroup.add(overFogEffects); // worldGroup

		statuses = new Group();
		worldGroup.add(statuses); // worldGroup

		worldGroup.add(healthIndicators); // worldGroup
		worldGroup.add(new TargetHealthIndicator()); // worldGroup

		worldGroup.add(emoicons); // worldGroup

		worldGroup.add(cellSelector = new CellSelector(tiles)); // worldGroup


		// --- UI ---
		int uiSize = SPDSettings.interfaceSize();
		float heroPaneExtraWidth = insets.left;
		float menuBarMaxLeft = uiCamera.width - insets.right - MenuPane.WIDTH;
		int hpBarMaxWidth = 50;
		float[] buffBarRowLimits = new float[9];
		float[] buffBarRowAdjusts = new float[9];

		if (largeInsetTop == 0 && insets.top > 0) {
			RectF cutout = Game.platform.getDisplayCutout().scale(1f / defaultZoom);
			if (cutout.top < 30 && cutout.left < 20 && cutout.right > 12) {
				heroPaneExtraWidth = Math.max(heroPaneExtraWidth, cutout.right - 12);
				heroPaneExtraWidth = Math.min(heroPaneExtraWidth, uiCamera.width - PixelScene.MIN_WIDTH_P);
			} else if (cutout.top < 20 && cutout.left < menuBarMaxLeft + MenuPane.WIDTH && cutout.right > menuBarMaxLeft) {
				menuBarMaxLeft = Math.min(menuBarMaxLeft, cutout.left - MenuPane.WIDTH);
				menuBarMaxLeft = Math.max(menuBarMaxLeft, PixelScene.MIN_WIDTH_P - MenuPane.WIDTH);
			} else if (cutout.left < 78 && cutout.top < 4 && cutout.right > 32) {
				hpBarMaxWidth = Math.round(cutout.left - 32 + 4);
				hpBarMaxWidth = Math.max(hpBarMaxWidth, 21);
			}
			if (cutout.left < 84 && cutout.top < 10 && cutout.right > 32 && cutout.bottom > 11) {
				int i = 1;
				int rowTop = 11;
				while (cutout.bottom > rowTop) {
					if (i == 1 || cutout.bottom > rowTop + 2) {
						buffBarRowLimits[i] = cutout.left - 32 + 3;
					} else {
						buffBarRowAdjusts[i] = cutout.bottom - rowTop + 1;
						rowTop += buffBarRowAdjusts[i];
					}
					i++;
					rowTop += 8;
				}
			}
		}

		float screentop = largeInsetTop;
		if (screentop == 0 && uiSize == 0) {
			screentop--;
		}

		menu = new MenuPane();
		menu.camera = uiCamera;
		menu.setPos(menuBarMaxLeft, screentop);
		add(menu);

		float extraRight = uiCamera.width - (menuBarMaxLeft + MenuPane.WIDTH);
		if (extraRight > 0) {
			SkinnedBlock bar = new SkinnedBlock(extraRight, 20, TextureCache.createSolid(0x88000000));
			bar.x = uiCamera.width - extraRight;
			bar.camera = uiCamera;
			add(bar);

			PointerArea blocker = new PointerArea(uiCamera.width - extraRight, 0, extraRight, 20);
			blocker.camera = uiCamera;
			add(blocker);
		}

		status = new StatusPane(SPDSettings.interfaceSize() > 0);
		status.camera = uiCamera;
		StatusPane.heroPaneExtraWidth = heroPaneExtraWidth;
		StatusPane.hpBarMaxWidth = hpBarMaxWidth;
		StatusPane.buffBarRowMaxWidths = buffBarRowLimits;
		StatusPane.buffBarRowAdjusts = buffBarRowAdjusts;
		status.setRect(insets.left, uiSize > 0 ? uiCamera.height - 39 - insets.bottom : screentop, uiCamera.width - insets.left - insets.right, 0);
		add(status);

		if (uiSize < 2 && largeInsetTop != 0) {
			SkinnedBlock bar = new SkinnedBlock(uiCamera.width, largeInsetTop, TextureCache.createSolid(0x88000000));
			bar.camera = uiCamera;
			add(bar);

			PointerArea blocker = new PointerArea(0, 0, uiCamera.width, largeInsetTop);
			blocker.camera = uiCamera;
			add(blocker);
		}

		boss = new BossHealthBar();
		boss.camera = uiCamera;
		boss.setPos((uiCamera.width - boss.width()) / 2, screentop + (landscape() ? 7 : 26));
		if (buffBarRowLimits[2] != 0) {
			boss.setPos(boss.left(), boss.top() + 7);
		} else if (buffBarRowAdjusts[2] != 0) {
			boss.setPos(boss.left(), boss.top() + buffBarRowAdjusts[2]);
		}
		add(boss);

		resume = new ResumeIndicator();
		resume.camera = uiCamera;
		add(resume);

		action = new ActionIndicator();
		action.camera = uiCamera;
		add(action);

		loot = new LootIndicator();
		loot.camera = uiCamera;
		add(loot);

		attack = new AttackIndicator();
		attack.camera = uiCamera;
		add(attack);

		log = new GameLog();
		log.camera = uiCamera;
		log.newLine();
		add(log);

		if (uiSize > 0) {
			bringToFront(status);
		}

		toolbar = new Toolbar();
		toolbar.camera = uiCamera;
		add(toolbar);

		if (uiSize == 2) {
			inventory = new InventoryPane();
			inventory.camera = uiCamera;
			inventory.setPos(uiCamera.width - inventory.width() - insets.right, uiCamera.height - inventory.height() - insets.bottom);
			add(inventory);

			toolbar.setRect(insets.left, uiCamera.height - toolbar.height() - inventory.height() - insets.bottom, uiCamera.width - insets.right, toolbar.height());
		} else {
			toolbar.setRect(insets.left, uiCamera.height - toolbar.height() - insets.bottom, uiCamera.width - insets.right, toolbar.height());
		}

		if (insets.bottom > 0) {
			SkinnedBlock bar = new SkinnedBlock(uiCamera.width, insets.bottom, TextureCache.createSolid(0x88000000));
			bar.camera = uiCamera;
			bar.y = uiCamera.height - insets.bottom;
			add(bar);

			PointerArea blocker = new PointerArea(0, uiCamera.height - insets.bottom, uiCamera.width, insets.bottom);
			blocker.camera = uiCamera;
			add(blocker);
		}

		layoutTags();

		switch (InterlevelScene.mode) {
			case RESURRECT:
				Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
				ScrollOfTeleportation.appearVFX(Dungeon.hero);
				SpellSprite.show(Dungeon.hero, SpellSprite.ANKH);
				new Flare(5, 16).color(0xFFFF00, true).show(hero, 4f);
				break;
			case RETURN:
				if (Dungeon.level.pit[Dungeon.hero.pos] && !Dungeon.hero.flying) {
					ShatteredPixelDungeon.runOnRenderThread(new Callback() {
						@Override
						public void call() {
							ScrollOfTeleportation.appearVFX(Dungeon.hero);
						}
					});
				} else {
					ScrollOfTeleportation.appearVFX(Dungeon.hero);
				}
				break;
			case DESCEND:
			case FALL:
				if (Dungeon.hero.isAlive()) {
					Badges.validateNoKilling();
				}
				break;
		}

		ArrayList<Item> dropped = Dungeon.droppedItems.get(Dungeon.depth);
		if (dropped != null) {
			for (Item item : dropped) {
				int pos = Dungeon.level.randomRespawnCell(null);
				if (pos == -1) pos = Dungeon.level.entrance();
				if (item instanceof Potion) {
					((Potion) item).shatter(pos);
				} else if (item instanceof Plant.Seed && !Dungeon.isChallenged(Challenges.NO_HERBALISM)) {
					Dungeon.level.plant((Plant.Seed) item, pos);
				} else if (item instanceof Honeypot) {
					Dungeon.level.drop(((Honeypot) item).shatter(null, pos), pos);
				} else {
					Dungeon.level.drop(item, pos);
				}
			}
			Dungeon.droppedItems.remove(Dungeon.depth);
		}

		Dungeon.hero.next();

		switch (InterlevelScene.mode) {
			case FALL:
			case DESCEND:
			case CONTINUE:
				Camera.main.snapTo(hero.center().x, hero.center().y - DungeonTilemap.SIZE * (defaultZoom / Camera.main.zoom));
				break;
			case ASCEND:
				Camera.main.snapTo(hero.center().x, hero.center().y + DungeonTilemap.SIZE * (defaultZoom / Camera.main.zoom));
				break;
			default:
				Camera.main.snapTo(hero.center().x, hero.center().y);
		}
		Camera.main.panTo(hero.center(), 2.5f);

		if (InterlevelScene.mode != InterlevelScene.Mode.NONE) {
			if (Dungeon.depth == Statistics.deepestFloor
					&& (InterlevelScene.mode == InterlevelScene.Mode.DESCEND || InterlevelScene.mode == InterlevelScene.Mode.FALL)) {
				GLog.h(Messages.get(this, "descend"), Dungeon.depth);
				Sample.INSTANCE.play(Assets.Sounds.DESCEND);

				for (Char ch : Actor.chars()) {
					if (ch instanceof DriedRose.GhostHero) {
						((DriedRose.GhostHero) ch).sayAppeared();
					}
				}

				int spawnersAbove = Statistics.spawnersAlive;
				if (spawnersAbove > 0 && Dungeon.depth <= 25) {
					for (Mob m : Dungeon.level.mobs) {
						if (m instanceof DemonSpawner && ((DemonSpawner) m).spawnRecorded) {
							spawnersAbove--;
						}
					}

					if (spawnersAbove > 0) {
						if (Dungeon.bossLevel()) {
							GLog.n(Messages.get(this, "spawner_warn_final"));
						} else {
							GLog.n(Messages.get(this, "spawner_warn"));
						}
					}
				}

			} else if (InterlevelScene.mode == InterlevelScene.Mode.RESET) {
				GLog.h(Messages.get(this, "warp"));
			} else if (InterlevelScene.mode == InterlevelScene.Mode.RESURRECT) {
				GLog.h(Messages.get(this, "resurrect"), Dungeon.depth);
			} else {
				GLog.h(Messages.get(this, "return"), Dungeon.depth);
			}

			if (Dungeon.hero.hasTalent(Talent.ROGUES_FORESIGHT)
					&& Dungeon.level instanceof RegularLevel && Dungeon.branch == 0) {
				int reqSecrets = Dungeon.level.feeling == Level.Feeling.SECRETS ? 2 : 1;
				for (Room r : ((RegularLevel) Dungeon.level).rooms()) {
					if (r instanceof SecretRoom) reqSecrets--;
				}

				Random.pushGenerator(Dungeon.seedCurDepth() + 1);
				if (reqSecrets <= 0 && Random.Int(4) < 2 + Dungeon.hero.pointsInTalent(Talent.ROGUES_FORESIGHT)) {
					GLog.p(Messages.get(this, "secret_hint"));
				}
				Random.popGenerator();
			}

			boolean unspentTalents = false;
			for (int i = 1; i <= Dungeon.hero.talents.size(); i++) {
				if (Dungeon.hero.talentPointsAvailable(i) > 0) {
					unspentTalents = true;
					break;
				}
			}
			if (unspentTalents) {
				GLog.newLine();
				GLog.w(Messages.get(Dungeon.hero, "unspent"));
				StatusPane.talentBlink = 10f;
				WndHero.lastIdx = 1;
			}

			switch (Dungeon.level.feeling) {
				case CHASM:
					GLog.w(Dungeon.level.feeling.desc());
					Notes.add(Notes.Landmark.CHASM_FLOOR);
					break;
				case WATER:
					GLog.w(Dungeon.level.feeling.desc());
					Notes.add(Notes.Landmark.WATER_FLOOR);
					break;
				case GRASS:
					GLog.w(Dungeon.level.feeling.desc());
					Notes.add(Notes.Landmark.GRASS_FLOOR);
					break;
				case DARK:
					GLog.w(Dungeon.level.feeling.desc());
					Notes.add(Notes.Landmark.DARK_FLOOR);
					break;
				case LARGE:
					GLog.w(Dungeon.level.feeling.desc());
					Notes.add(Notes.Landmark.LARGE_FLOOR);
					break;
				case TRAPS:
					GLog.w(Dungeon.level.feeling.desc());
					Notes.add(Notes.Landmark.TRAPS_FLOOR);
					break;
				case SECRETS:
					GLog.w(Dungeon.level.feeling.desc());
					Notes.add(Notes.Landmark.SECRETS_FLOOR);
					break;
			}

			for (Mob mob : Dungeon.level.mobs) {
				if (!mob.buffs(ChampionEnemy.class).isEmpty()) {
					GLog.w(Messages.get(ChampionEnemy.class, "warn"));
				}
			}

			if (Dungeon.hero.buff(AscensionChallenge.class) != null) {
				Dungeon.hero.buff(AscensionChallenge.class).saySwitch();
			}

			DimensionalSundial.sundialWarned = true;
			if (DimensionalSundial.spawnMultiplierAtCurrentTime() > 1) {
				GLog.w(Messages.get(DimensionalSundial.class, "warning"));
			} else {
				DimensionalSundial.sundialWarned = false;
			}

			InterlevelScene.mode = InterlevelScene.Mode.NONE;
		}

		if (SPDSettings.intro()) {
			if (Document.ADVENTURERS_GUIDE.isPageFound(Document.GUIDE_INTRO)) {
				GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_INTRO);
			} else if (ControllerHandler.isControllerConnected()) {
				GameLog.wipe();
				GLog.p(Messages.get(GameScene.class, "tutorial_move_controller"));
			} else if (SPDSettings.interfaceSize() == 0) {
				GameLog.wipe();
				GLog.p(Messages.get(GameScene.class, "tutorial_move_mobile"));
			} else {
				GameLog.wipe();
				GLog.p(Messages.get(GameScene.class, "tutorial_move_desktop"));
			}
			toolbar.visible = toolbar.active = false;
			status.visible = status.active = false;
			if (inventory != null) inventory.visible = inventory.active = false;
		}

		if (!SPDSettings.intro() &&
				Rankings.INSTANCE.totalNumber > 0 &&
				!Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_DIEING)) {
			GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_DIEING);
		}

		TrinketCatalyst cata = Dungeon.hero.belongings.getItem(TrinketCatalyst.class);
		if (cata != null && cata.hasRolledTrinkets()) {
			addToFront(new TrinketCatalyst.WndTrinket(cata));
		}

		if (!invVisible) toggleInvPane();
		fadeIn();

		if (!Dungeon.hero.isAlive()) {
			Ankh ankh = null;
			for (Ankh i : Dungeon.hero.belongings.getAllItems(Ankh.class)) {
				if (!i.isBlessed()) {
					ankh = i;
				}
			}
			if (ankh != null && GamesInProgress.gameExists(GamesInProgress.curSlot)) {
				add(new WndResurrect(ankh));
			} else {
				gameOver();
			}
		}
	}

	@Override
	public void drawWorld() {
		if (waterBlock != null) waterBlock.visible = false;

		worldGroup.draw();

		if (waterBlock != null) waterBlock.visible = true;
	}

	@Override
	public void drawWater() {
		if (waterBlock != null && waterBlock.exists && waterBlock.visible) {
			waterBlock.draw();
		}
	}

	@Override
	public void drawBloomObjects() {
		for (int i = 0; i < bloomObjects.size(); i++) {
			Gizmo g = bloomObjects.get(i);
			if (g != null && g.exists && g.visible) {
				g.draw();
			}
		}
	}

	@Override
	public void drawReflections() {
		if (mobs == null || mobs.members == null) return;

		synchronized (this) {
			for (com.watabou.noosa.Gizmo g : mobs.members) {
				if (g instanceof Visual && ((Visual) g).visible && ((Visual) g).exists) {
					com.watabou.noosa.Visual v = (com.watabou.noosa.Visual) g;

					float origScaleY = v.scale.y;
					float origY = v.y;
					float origX = v.x;
					float origAm = v.am;

					v.scale.y = -origScaleY;
					float refY = origY + com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap.SIZE * 1.9f;

					v.am = origAm * 0.60f;

					float blurOffset = 1f;

					v.x = origX - blurOffset;
					v.y = refY;
					v.draw();

					v.x = origX + blurOffset;
					v.draw();

					v.x = origX;
					v.y = refY - blurOffset;
					v.draw();

					v.y = refY + blurOffset;
					v.draw();

					v.y = refY;
					v.draw();

					v.scale.y = origScaleY;
					v.x = origX;
					v.y = origY;
					v.am = origAm;
				}
			}
		}
	}
	@Override
	public void drawUI() {
		boolean worldVis = worldGroup.visible;
		worldGroup.visible = false;
		super.draw();
		worldGroup.visible = worldVis;
	}

	private LightGrid lightGrid;
	private Image lightGridImage;

	private int currentLevelWidth = -1;
	private int currentLevelHeight = -1;

	@Override
	public void drawLightSources() {
		if (Dungeon.level == null) return;

		int levelW = Dungeon.level.width();
		int levelH = Dungeon.level.height();

		if (lightGrid == null || currentLevelWidth != levelW || currentLevelHeight != levelH) {
			currentLevelWidth = levelW;
			currentLevelHeight = levelH;

			lightGrid = new LightGrid();
			lightGrid.init(levelW, levelH);

			lightGridImage = new Image(lightGrid.getTexture());
			lightGridImage.scale.set(DungeonTilemap.SIZE, DungeonTilemap.SIZE);
		}

		// ambient
		lightGrid.clear(0.05f, 0.07f, 0.12f);

		collectAllLightSources(lightGrid);
		lightGrid.updateTexture();
		lightGridImage.draw();
	}

	private void collectAllLightSources(LightGrid grid) {
		if (Dungeon.level == null) return;

		// --- player light ---
		if (Dungeon.hero != null && Dungeon.hero.sprite != null) {
			float hx = (Dungeon.hero.sprite.x + Dungeon.hero.sprite.width / 2f) / DungeonTilemap.SIZE;
			float hy = (Dungeon.hero.sprite.y + Dungeon.hero.sprite.height / 2f) / DungeonTilemap.SIZE;

			// breathing fire
			float flicker = (float) Math.sin(Game.timeTotal * 6.0f) * 0.25f;
			float radius = 7.5f + flicker;

			// color
			grid.addLight(hx, hy, 1.0f, 0.85f, 0.55f, radius, 0.01f);
		}

		/* --- item light ---
		if (Dungeon.level.heaps != null) {
			for (Heap heap : Dungeon.level.heaps.values()) {
				if (heap != null && heap.pos >= 0 && heap.pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[heap.pos]) {
					float hx = (heap.pos % Dungeon.level.width()) + 0.5f;
					float hy = (heap.pos / Dungeon.level.width()) + 0.5f;

					grid.addLight(hx, hy, 0.9f, 0.74f, 0.2f, 2.5f, 0.4f);
				}
			}
		}

		// --- mob light ---
		if (Dungeon.level.mobs != null) {
			for (Mob mob : Dungeon.level.mobs) {
				if (mob != null && mob.sprite != null && mob.isAlive() && mob.pos >= 0 && mob.pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[mob.pos]) {
					float mx = (mob.sprite.x + mob.sprite.width / 2f) / DungeonTilemap.SIZE;
					float my = (mob.sprite.y + mob.sprite.height / 2f) / DungeonTilemap.SIZE;

					// Синеватый магический свет от врагов
					grid.addLight(mx, my, 0.3f, 0.6f, 1.0f, 3.0f, 0.35f);
				}
			}
		}
		 */

		// --- effects light ---
		if (bloomObjects != null) {

			for (int i = bloomObjects.size() - 1; i >= 0; i--) {
				Gizmo g = bloomObjects.get(i);
				if (g == null || !g.exists || !g.alive) {
					bloomObjects.remove(i);
				}
			}

			for (int i = 0; i < bloomObjects.size(); i++) {
				Gizmo g = bloomObjects.get(i);
				if (g == null || !g.exists || !g.alive || !g.visible) continue;

				if (g instanceof Emitter && !((Emitter) g).on) {
					continue;
				}

				float gx = 0, gy = 0;

				if (g instanceof Visual) {
					Visual v = (Visual) g;
					gx = (v.x + v.width() / 2f) / DungeonTilemap.SIZE;
					gy = (v.y + v.height() / 2f) / DungeonTilemap.SIZE;
				} else if (g instanceof Emitter) {
					Emitter e = (Emitter) g;
					gx = (e.x + e.width / 2f) / DungeonTilemap.SIZE;
					gy = (e.y + e.height / 2f) / DungeonTilemap.SIZE;
				}

				int cell = (int) gx + (int) gy * Dungeon.level.width();

				if (cell >= 0 && cell < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[cell]) {

					if (g instanceof Emitter) {
						Emitter e = (Emitter) g;

						if (e.factory == BlastParticle.FACTORY) {
							grid.addLight(gx, gy, 1.0f, 0.4f, 0.0f, 5.5f, 0.8f);       // Blast
						} else if (e.factory == ElmoParticle.FACTORY) {
							grid.addLight(gx, gy, 0.2f, 1.0f, 0.3f, 5.0f, 0.8f);     // Elmo
						} else if (e.factory == EnergyParticle.FACTORY) {
							grid.addLight(gx, gy, 0.0f, 0.9f, 1.0f, 5.0f, 0.8f);       // Energy
						} else if (e.factory == FlameParticle.FACTORY) {
							grid.addLight(gx, gy, 1.0f, 0.3f, 0.0f, 5.0f, 0.85f);      // Flame
						} else if (e.factory == PoisonParticle.MISSILE) {
							grid.addLight(gx, gy, 0.1f, 1.0f, 0.2f, 4.5f, 0.7f);       // Poison
						} else if (e.factory == PurpleParticle.BURST) {
							grid.addLight(gx, gy, 0.7f, 0.1f, 1.0f, 4.5f, 0.75f);      // Purple
						} else if (e.factory == RainbowParticle.BURST) {
							grid.addLight(gx, gy, 1.0f, 0.2f, 0.8f, 5.0f, 0.8f);       // Rainbow
						} else if (e.factory == SacrificialParticle.FACTORY) {
							grid.addLight(gx, gy, 1.0f, 0.0f, 0.2f, 4.5f, 0.8f);       // Sacrifice
						} else if (e.factory == ShadowParticle.UP) {
							grid.addLight(gx, gy, 0.4f, 0.0f, 0.8f, 4.0f, 0.6f);       // Shadow
						} else if (e.factory == SparkParticle.FACTORY) {
							grid.addLight(gx, gy, 1.0f, 1.0f, 0.2f, 4.5f, 0.85f);      // Spark
						}

					} else if (g instanceof Beam) {
						grid.addLight(gx, gy, 0.2f, 1.0f, 1.0f, 6.0f, 0.9f);          // Beam
					} else if (g instanceof Lightning) {
						grid.addLight(gx, gy, 0.3f, 0.6f, 1.0f, 5.5f, 0.85f);         // Lightning
					} else if (g instanceof MagicMissile) {
						grid.addLight(gx, gy, 0.4f, 0.8f, 1.0f, 4.5f, 0.75f);         // MagicMissile
					}
				}
			}
		}
		// --- blobs light ---
		if (Dungeon.level.blobs != null) {
			for (Blob blob : Dungeon.level.blobs.values()) {
				if (blob == null || blob.cur == null) continue;

				for (int cell = 0; cell < Dungeon.level.length(); cell++) {
					if (blob.cur[cell] > 0 && Dungeon.level.heroFOV[cell]) {
						float fx = (cell % Dungeon.level.width()) + 0.5f;
						float fy = (cell / Dungeon.level.width()) + 0.5f;

						if (blob instanceof Fire) {
							grid.addLight(fx, fy, 1.0f, 0.35f, 0.0f, 4.5f, 0.8f);
						} else if (blob instanceof Inferno) {
							grid.addLight(fx, fy, 1.0f, 0.2f, 0.0f, 6.0f, 0.95f);
						} else if (blob instanceof SacrificialFire) {
							grid.addLight(fx, fy, 1.0f, 0.0f, 0.2f, 5.0f, 0.85f);
						} else if (blob instanceof MagicalFireRoom.EternalFire) {
							grid.addLight(fx, fy, 0.2f, 1.0f, 0.3f, 4.5f, 0.8f);
						} else if (blob instanceof Electricity) {
							grid.addLight(fx, fy, 0.3f, 0.7f, 1.0f, 5.0f, 0.9f);

						} else if (blob instanceof WaterOfHealth) {
							grid.addLight(fx, fy, 0.1f, 1.0f, 0.4f, 3.5f, 0.6f);
						} else if (blob instanceof WaterOfAwareness) {
							grid.addLight(fx, fy, 0.2f, 0.8f, 1.0f, 3.5f, 0.6f);
						} else if (blob instanceof WellWater) {
							grid.addLight(fx, fy, 0.4f, 0.7f, 1.0f, 3.5f, 0.6f);

						} else if (blob instanceof Freezing || blob instanceof Blizzard) {
							grid.addLight(fx, fy, 0.3f, 0.7f, 1.0f, 4.0f, 0.5f);
						} else if (blob instanceof ConfusionGas) {
							grid.addLight(fx, fy, 0.9f, 0.3f, 0.9f, 3.5f, 0.4f);
						}
					}
				}
			}
		}
	}

	public ArrayList<Gizmo> bloomObjects = new ArrayList<>();

	public static void addBloomObject(Gizmo gizmo) {
		if (scene != null && gizmo != null && !scene.bloomObjects.contains(gizmo)) {
			scene.bloomObjects.add(gizmo);
		}
	}

	public static void bloomEffect(Visual effect) {
		if (scene != null) {
			scene.effects.add(effect);
			addBloomObject(effect);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	public static void endActorThread() {
		if (actorThread != null && actorThread.isAlive()) {
			Actor.keepActorThreadAlive = false;
			actorThread.interrupt();
		}
	}

	public boolean waitForActorThread(int msToWait, boolean interrupt) {
		if (actorThread == null || !actorThread.isAlive()) {
			return true;
		}
		synchronized (actorThread) {
			if (interrupt) actorThread.interrupt();
			try {
				actorThread.wait(msToWait);
			} catch (InterruptedException e) {
				ShatteredPixelDungeon.reportException(e);
			}
			return !Actor.processing();
		}
	}

	@Override
	public synchronized void onPause() {
		try {
			if (!Dungeon.hero.ready) waitForActorThread(500, false);
			Dungeon.saveAll();
			Badges.saveGlobal();
			Journal.saveGlobal();
		} catch (IOException e) {
			ShatteredPixelDungeon.reportException(e);
		}
	}

	private static Thread actorThread;

	private ArrayList<Gizmo> toDestroy = new ArrayList<>();
	private float notifyDelay = 1 / 60f;

	public static boolean updateItemDisplays = false;
	public static boolean tagDisappeared = false;
	public static boolean updateTags = false;
	private static float waterOfs = 0;

	@Override
	public synchronized void update() {
		lastOffset = null;

		PostProcessingManager ppm = PostProcessingManager.instance;
		ppm.update(Game.elapsed);

		if (Dungeon.hero != null && Dungeon.hero.isAlive()) {

			float delta = Game.elapsed;
			float lerpSpeed = delta * 3.5f;

			float hpPercent = (float) Dungeon.hero.HP / (float) Dungeon.hero.HT;

			// noise
			float targetNoise = 0f;
			if (hpPercent < 0.30f) {
				targetNoise = ((0.30f - hpPercent) / 0.30f) * 0.45f;
			}
			ppm.corrosionNoise += (targetNoise - ppm.corrosionNoise) * lerpSpeed;

			boolean isInvisible = Dungeon.hero.invisible > 0 || Dungeon.hero.buff(Invisibility.class) != null;
			boolean isPsychotropic = Dungeon.hero.buff(MindVision.class) != null;

			float targetSaturation = 1.0f;
			float targetTintAmount = 0.0f;
			float targetFisheye = isPsychotropic ? 0.04f : 0.0f; // 0.04f rounded corners

			if (isInvisible) {
				targetSaturation = 0.5f;
				targetTintAmount = 0.5f;
				ppm.tintR = 0.30f; ppm.tintG = 0.60f; ppm.tintB = 1.20f;
			} else if (isPsychotropic) {
				targetSaturation = 2.20f;
			} else if (hpPercent < 0.20f) {
				targetSaturation = hpPercent / 0.20f;
			}

			ppm.saturation += (targetSaturation - ppm.saturation) * lerpSpeed;
			ppm.tintAmount += (targetTintAmount - ppm.tintAmount) * lerpSpeed;
			ppm.fisheyeAmount += (targetFisheye - ppm.fisheyeAmount) * lerpSpeed;

			// poison
			boolean isPoisoned = Dungeon.hero.buff(Poison.class) != null;
			float targetPoisonDist = isPoisoned ? 0.002f : 0f;
			float poisonAberration = isPoisoned ? 0.035f : 0f;

			// amulet curse
			AscensionChallenge ascension = Dungeon.hero.buff(AscensionChallenge.class);
			float targetAberration = poisonAberration;

			if (ascension != null) {
				float stacks = ascension.getStacks();
				targetAberration += (stacks / 8.0f) * 0.08f;
			}

			ppm.vignetteIntensity += (0f - ppm.vignetteIntensity) * lerpSpeed;
			ppm.vignetteRed += (0f - ppm.vignetteRed) * lerpSpeed;
			ppm.chromaticDistortion += (targetAberration - ppm.chromaticDistortion) * lerpSpeed;
			ppm.poisonDistortion += (targetPoisonDist - ppm.poisonDistortion) * lerpSpeed;
		}
		if (updateItemDisplays) {
			updateItemDisplays = false;
			QuickSlotButton.refresh();
			InventoryPane.refresh();
			if (ActionIndicator.action instanceof MeleeWeapon.Charger) {
				ActionIndicator.refresh();
			}
		}

		if (Dungeon.hero == null || scene == null) {
			return;
		}

		super.update();

		if (notifyDelay > 0) notifyDelay -= Game.elapsed;

		if (!Emitter.freezeEmitters) {
			waterOfs -= 5 * Game.elapsed;
			water.offsetTo(0, waterOfs);
			waterOfs = water.offsetY();
		}

		if (!Actor.processing() && Dungeon.hero.isAlive()) {
			if (actorThread == null || !actorThread.isAlive()) {
				actorThread = new Thread() {
					@Override
					public void run() {
						Actor.process();
					}
				};

				if (Runtime.getRuntime().availableProcessors() == 1) {
					actorThread.setPriority(Thread.NORM_PRIORITY - 1);
				}
				actorThread.setName("SHPD Actor Thread");
				Thread.currentThread().setName("SHPD Render Thread");
				Actor.keepActorThreadAlive = true;
				actorThread.start();
			} else if (notifyDelay <= 0f) {
				notifyDelay += 1 / 60f;
				synchronized (actorThread) {
					actorThread.notify();
				}
			}
		}

		if (Dungeon.hero.ready && Dungeon.hero.paralysed == 0) {
			log.newLine();
		}

		if (updateTags) {
			tagAttack = attack.active;
			tagLoot = loot.visible;
			tagAction = action.visible;
			tagResume = resume.visible;
			layoutTags();
		} else if (tagAttack != attack.active || tagLoot != loot.visible || tagAction != action.visible || tagResume != resume.visible) {
			boolean tagAppearing = (attack.active && !tagAttack) || (loot.visible && !tagLoot) || (action.visible && !tagAction) || (resume.visible && !tagResume);
			tagAttack = attack.active;
			tagLoot = loot.visible;
			tagAction = action.visible;
			tagResume = resume.visible;
			if (tagAppearing) layoutTags();
			else tagDisappeared = true;
		}

		cellSelector.enable(Dungeon.hero.ready);

		if (!toDestroy.isEmpty()) {
			for (Gizmo g : toDestroy) {
				g.destroy();
			}
			toDestroy.clear();
		}
	}

	private static Point lastOffset = null;

	@Override
	public synchronized Gizmo erase(Gizmo g) {
		Gizmo result = super.erase(g);
		if (result instanceof Window) {
			lastOffset = ((Window) result).getOffset();
		}
		return result;
	}

	private boolean tagAttack = false;
	private boolean tagLoot = false;
	private boolean tagAction = false;
	private boolean tagResume = false;

	public static void layoutTags() {
		updateTags = false;
		if (scene == null) return;

		if (scene.inventory != null && scene.inventory.visible && (uiCamera.width < 460 && uiCamera.height < 300)) {
			Camera.main.setCenterOffset(0, Math.min(300 - uiCamera.height, 460 - uiCamera.width) / Camera.main.zoom);
		} else {
			Camera.main.setCenterOffset(0, 0);
		}

		RectF insets = Game.platform.getSafeInsets(PlatformSupport.INSET_ALL);
		insets = insets.scale(1f / uiCamera.zoom);

		boolean tagsOnLeft = SPDSettings.flipTags();
		float tagWidth = Tag.SIZE + (tagsOnLeft ? insets.left : insets.right);
		float tagLeft = tagsOnLeft ? 0 : uiCamera.width - tagWidth;

		float y = SPDSettings.interfaceSize() == 0 ? scene.toolbar.top() - 2 : scene.status.top() - 2;
		if (SPDSettings.interfaceSize() == 0) {
			if (tagsOnLeft) {
				scene.log.setRect(tagWidth, y, uiCamera.width - tagWidth - insets.right, 0);
			} else {
				scene.log.setRect(insets.left, y, uiCamera.width - tagWidth - insets.left, 0);
			}
		} else {
			if (tagsOnLeft) {
				scene.log.setRect(tagWidth, y, 160 - tagWidth, 0);
			} else {
				scene.log.setRect(insets.left, y, 160 - insets.left, 0);
			}
		}

		float pos = scene.toolbar.top();
		if (tagsOnLeft && SPDSettings.interfaceSize() > 0) {
			pos = scene.status.top();
		}

		if (scene.tagAttack) {
			scene.attack.setRect(tagLeft, pos - Tag.SIZE, tagWidth, Tag.SIZE);
			scene.attack.flip(tagsOnLeft);
			pos = scene.attack.top();
		}

		if (scene.tagLoot) {
			scene.loot.setRect(tagLeft, pos - Tag.SIZE, tagWidth, Tag.SIZE);
			scene.loot.flip(tagsOnLeft);
			pos = scene.loot.top();
		}

		if (scene.tagAction) {
			scene.action.setRect(tagLeft, pos - Tag.SIZE, tagWidth, Tag.SIZE);
			scene.action.flip(tagsOnLeft);
			pos = scene.action.top();
		}

		if (scene.tagResume) {
			scene.resume.setRect(tagLeft, pos - Tag.SIZE, tagWidth, Tag.SIZE);
			scene.resume.flip(tagsOnLeft);
		}
	}

	@Override
	protected void onBackPressed() {
		if (!cancel()) {
			add(new WndGame());
		}
	}

	public void addCustomTile(CustomTilemap visual) {
		customTiles.add(visual.create());
	}

	public void addCustomWall(CustomTilemap visual) {
		customWalls.add(visual.create());
	}

	private void addHeapSprite( Heap heap ) {
		ItemSprite sprite = heap.sprite = (ItemSprite)heaps.recycle( ItemSprite.class );
		sprite.revive();
		sprite.link( heap );
		heaps.add( sprite );
	}

	private void addDiscardedSprite(Heap heap) {
		heap.sprite = (DiscardedItemSprite) heaps.recycle(DiscardedItemSprite.class);
		heap.sprite.revive();
		heap.sprite.link(heap);
		heaps.add(heap.sprite);
	}

	private void addBlobSprite(final Blob gas) {
		if (gas.emitter == null) {
			BlobEmitter emitter = new BlobEmitter(gas);
			gases.add(emitter);

			// Добавляем в Bloom только выбранные магические и огненные блобы
			if (gas instanceof Fire ||
					gas instanceof Inferno ||
					gas instanceof SacrificialFire ||
					gas instanceof VaultFlameTraps ||
					gas instanceof Electricity ||
					gas instanceof Alchemy ||
					gas instanceof WaterOfHealth ||
					gas instanceof WaterOfAwareness ||
					gas instanceof MagicalFireRoom.EternalFire ||
					gas instanceof WellWater) {

				addBloomObject(emitter);
			}
		}
	}

	private synchronized void addMobSprite(Mob mob) {
		CharSprite sprite = mob.sprite();
		sprite.visible = Dungeon.level.heroFOV[mob.pos];
		mobs.add(sprite);
		sprite.link(mob);
		sortMobSprites();
	}

	public static void sortMobSprites() {
		if (scene != null) {
			synchronized (scene) {
				scene.mobs.sort(new Comparator() {
					@Override
					public int compare(Object a, Object b) {
						if (a instanceof Visual && b instanceof Visual) {
							return (int) Math.signum((((Visual) a).y + ((Visual) a).height()) - (((Visual) b).y + ((Visual) b).height()));
						} else if (a instanceof Visual) {
							return -1;
						} else if (b instanceof Visual) {
							return 1;
						} else {
							return 0;
						}
					}
				});
			}
		}
	}

	private synchronized void prompt(String text) {
		if (prompt != null) {
			prompt.killAndErase();
			toDestroy.add(prompt);
			prompt = null;
		}

		if (text != null) {
			prompt = new Toast(text) {
				@Override
				protected void onClose() {
					cancel();
				}
			};
			prompt.camera = uiCamera;
			prompt.setPos((uiCamera.width - prompt.width()) / 2, uiCamera.height - 60);

			if (inventory != null && inventory.visible && prompt.right() > inventory.left() - 10) {
				prompt.setPos(inventory.left() - prompt.width() - 10, prompt.top());
			}

			add(prompt);
		}
	}

	private void showBanner(Banner banner) {
		banner.camera = uiCamera;
		float offset = Camera.main.centerOffset.y;
		banner.x = align(uiCamera, (uiCamera.width - banner.width) / 2);
		banner.y = align(uiCamera, (uiCamera.height - banner.height) / 2 - 32 - offset);
		addToFront(banner);
	}

	public static void add(Blob gas) {
		Actor.add(gas);
		if (scene != null) {
			scene.addBlobSprite(gas);
		}
	}

	public static void add(Heap heap) {
		if (scene != null) {
			heap.autoExplored = true;
			scene.addHeapSprite(heap);
		}
	}

	public static void discard(Heap heap) {
		if (scene != null) {
			scene.addDiscardedSprite(heap);
		}
	}

	public static void add(Mob mob) {
		add(mob, 0);
	}

	public static void addSprite(Mob mob) {
		scene.addMobSprite(mob);
	}

	public static void add(Mob mob, float delay) {
		Dungeon.level.mobs.add(mob);
		delay = (float) Math.ceil(Actor.now() + delay) - Actor.now();
		if (scene != null) {
			scene.addMobSprite(mob);
			Actor.addDelayed(mob, delay);
			mob.spendToWhole();
		}
	}

	public static void add(EmoIcon icon) {
		scene.emoicons.add(icon);
	}

	public static void add(CharHealthIndicator indicator) {
		if (scene != null) scene.healthIndicators.add(indicator);
	}

	public static void add(CustomTilemap t, boolean wall) {
		if (scene == null) return;
		if (wall) {
			scene.addCustomWall(t);
		} else {
			scene.addCustomTile(t);
		}
	}

	public static void effect(Visual effect) {
		if (scene != null) scene.effects.add(effect);
	}

	public static void effectOverFog(Visual effect) {
		scene.overFogEffects.add(effect);
	}

	public static Ripple ripple(int pos) {
		if (scene != null) {
			Ripple ripple = (Ripple) scene.ripples.recycle(Ripple.class);
			ripple.reset(pos);
			return ripple;
		} else {
			return null;
		}
	}

	public static synchronized SpellSprite spellSprite() {
		return (SpellSprite) scene.spells.recycle(SpellSprite.class);
	}

	public static synchronized Emitter emitter() {
		if (scene != null) {
			Emitter emitter = (Emitter) scene.emitters.recycle(Emitter.class);
			emitter.revive();
			addBloomObject(emitter);
			return emitter;
		} else {
			return null;
		}
	}

	public static synchronized Emitter floorEmitter() {
		if (scene != null) {
			Emitter emitter = (Emitter) scene.floorEmitters.recycle(Emitter.class);
			emitter.revive();
			return emitter;
		} else {
			return null;
		}
	}

	public static FloatingText status() {
		return scene != null ? (FloatingText) scene.statuses.recycle(FloatingText.class) : null;
	}

	public static void pickUp(Item item, int pos) {
		if (scene != null) scene.toolbar.pickup(item, pos);
	}

	public static void pickUpJournal(Item item, int pos) {
		if (scene != null) scene.menu.pickup(item, pos);
	}

	public static void flashForDocument(Document doc, String page) {
		if (scene != null) {
			if (doc == Document.ADVENTURERS_GUIDE) {
				if (!page.equals(Document.GUIDE_INTRO)) {
					if (SPDSettings.interfaceSize() == 0) {
						GLog.p(Messages.get(Guidebook.class, "hint_mobile"));
					} else {
						GLog.p(Messages.get(Guidebook.class, "hint_desktop", KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(SPDAction.JOURNAL, ControllerHandler.isControllerConnected()))));
					}
				}
				Dungeon.hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(Guidebook.class, "hint_status"));
			}
			scene.menu.flashForPage(doc, page);
		}
	}

	public static void endIntro() {
		if (scene != null) {
			SPDSettings.intro(false);
			scene.add(new Tweener(scene, 2f) {
				@Override
				protected void updateValues(float progress) {
					if (progress <= 0.5f) {
						scene.status.alpha(2 * progress);
						scene.status.visible = scene.status.active = true;
						scene.toolbar.visible = scene.toolbar.active = false;
						if (scene.inventory != null) scene.inventory.visible = scene.inventory.active = false;
					} else {
						scene.status.alpha(1f);
						scene.status.visible = scene.status.active = true;
						scene.toolbar.alpha((progress - 0.5f) * 2);
						scene.toolbar.visible = scene.toolbar.active = true;
						if (scene.inventory != null) {
							scene.inventory.visible = scene.inventory.active = true;
							scene.inventory.alpha((progress - 0.5f) * 2);
						}
					}
				}
			});
			GameLog.wipe();
			if (SPDSettings.interfaceSize() == 0) {
				GLog.p(Messages.get(GameScene.class, "tutorial_ui_mobile"));
			} else {
				GLog.p(Messages.get(GameScene.class, "tutorial_ui_desktop",
						KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(SPDAction.HERO_INFO, ControllerHandler.isControllerConnected())),
						KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(SPDAction.INVENTORY, ControllerHandler.isControllerConnected()))));
			}

			for (int i = 0; i < Dungeon.level.length(); i++) {
				if (Dungeon.level.map[i] == Terrain.SECRET_DOOR) {
					Dungeon.level.discover(i);
					discoverTile(i, Terrain.SECRET_DOOR);
				}
			}
		}
	}

	public static void updateKeyDisplay() {
		if (scene != null && scene.menu != null) scene.menu.updateKeys();
	}

	public static void showlevelUpStars() {
		if (scene != null && scene.status != null) scene.status.showStarParticles();
	}

	public static void updateAvatar() {
		if (scene != null && scene.status != null) scene.status.updateAvatar();
	}

	public static void resetMap() {
		if (scene != null) {
			scene.tiles.map(Dungeon.level.map, Dungeon.level.width());
			scene.visualGrid.map(Dungeon.level.map, Dungeon.level.width());
			scene.terrainFeatures.map(Dungeon.level.map, Dungeon.level.width());
			scene.raisedTerrain.map(Dungeon.level.map, Dungeon.level.width());
			scene.walls.map(Dungeon.level.map, Dungeon.level.width());
		}
		updateFog();
	}

	public static void updateMap() {
		if (scene != null) {
			scene.tiles.updateMap();
			scene.visualGrid.updateMap();
			scene.terrainFeatures.updateMap();
			scene.raisedTerrain.updateMap();
			scene.walls.updateMap();
			updateFog();
		}
	}

	public static void updateMap(int cell) {
		if (scene != null) {
			scene.tiles.updateMapCell(cell);
			scene.visualGrid.updateMapCell(cell);
			scene.terrainFeatures.updateMapCell(cell);
			scene.raisedTerrain.updateMapCell(cell);
			scene.walls.updateMapCell(cell);
			updateFog(cell, 1);
		}
	}

	public static void plantSeed(int cell) {
		if (scene != null) {
			scene.terrainFeatures.growPlant(cell);
		}
	}

	public static void discoverTile(int pos, int oldValue) {
		if (scene != null) {
			scene.tiles.discover(pos, oldValue);
		}
	}

	public static void show(Window wnd) {
		if (scene != null) {
			cancel();
			if (scene.inventory != null && scene.inventory.visible) {
				Point offsetToInherit = null;
				for (Gizmo g : scene.members) {
					if (g instanceof Window) offsetToInherit = ((Window) g).getOffset();
				}
				if (lastOffset != null) {
					offsetToInherit = lastOffset;
				}
				if (offsetToInherit != null && !offsetToInherit.isZero()) {
					wnd.offset(offsetToInherit);
					wnd.boundOffsetWithMargin(3);
				}
			}
			scene.addToFront(wnd);
		}
	}

	public static boolean showingWindow() {
		if (scene == null) return false;
		for (Gizmo g : scene.members) {
			if (g instanceof Window) return true;
		}
		return false;
	}

	public static boolean interfaceBlockingHero() {
		if (scene == null) return false;
		if (showingWindow()) return true;
		if (scene.inventory != null && scene.inventory.isSelecting()) {
			return true;
		}
		return false;
	}

	public static void toggleInvPane() {
		if (scene != null && scene.inventory != null) {
			if (scene.inventory.visible) {
				scene.inventory.visible = scene.inventory.active = invVisible = false;
				scene.toolbar.setPos(scene.toolbar.left(), uiCamera.height - scene.toolbar.height());
			} else {
				scene.inventory.visible = scene.inventory.active = invVisible = true;
				scene.toolbar.setPos(scene.toolbar.left(), scene.inventory.top() - scene.toolbar.height());
			}
			layoutTags();
		}
	}

	public static void centerNextWndOnInvPane() {
		if (scene != null && scene.inventory != null && scene.inventory.visible) {
			lastOffset = new Point((int) scene.inventory.centerX() - uiCamera.width / 2, (int) scene.inventory.centerY() - uiCamera.height / 2);
		}
	}

	public static void updateFog() {
		if (scene != null) {
			scene.fog.updateFog();
			scene.wallBlocking.updateMap();
		}
	}

	public static void updateFog(int x, int y, int w, int h) {
		if (scene != null) {
			scene.fog.updateFogArea(x, y, w, h);
			scene.wallBlocking.updateArea(x, y, w, h);
		}
	}

	public static void updateFog(int cell, int radius) {
		if (scene != null) {
			scene.fog.updateFog(cell, radius);
			scene.wallBlocking.updateArea(cell, radius);
		}
	}

	public static void afterObserve() {
		if (scene != null) {
			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
				if (mob.sprite != null) {
					if (mob instanceof Mimic && mob.state == mob.PASSIVE && ((Mimic) mob).stealthy() && Dungeon.level.visited[mob.pos]) {
						mob.sprite.visible = true;
					} else {
						mob.sprite.visible = Dungeon.level.heroFOV[mob.pos];
					}
				}
				if (mob instanceof Ghoul) {
					for (Ghoul.GhoulLifeLink link : mob.buffs(Ghoul.GhoulLifeLink.class)) {
						link.updateVisibility();
					}
				}
			}
		}
	}

	public static void flash(int color) {
		flash(color, true);
	}

	public static void flash(int color, boolean lightmode) {
		if (scene != null) {
			ShatteredPixelDungeon.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					if (scene != null) {
						if (color > 0 && color < 0x01000000) {
							scene.fadeIn(0xFF000000 | color, lightmode);
						} else {
							scene.fadeIn(color, lightmode);
						}
					}
				}
			});
		}
	}

	public static void gameOver() {
		if (scene == null) return;
		Banner gameOver = new Banner(BannerSprites.get(BannerSprites.Type.GAME_OVER));
		gameOver.show(0x000000, 2f);
		scene.showBanner(gameOver);

		StyledButton restart = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(StartScene.class, "new"), 9) {
			@Override
			protected void onClick() {
				GamesInProgress.selectedClass = Dungeon.hero.heroClass;
				GamesInProgress.curSlot = GamesInProgress.firstEmpty();
				ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
			}

			@Override
			public void update() {
				alpha((float) Math.pow(gameOver.am, 2));
				super.update();
			}
		};
		restart.icon(Icons.get(Icons.ENTER));
		restart.alpha(0);
		restart.camera = uiCamera;
		float offset = Camera.main.centerOffset.y;
		restart.setSize(Math.max(80, restart.reqWidth()), 20);
		restart.setPos(
				align(uiCamera, (restart.camera.width - restart.width()) / 2),
				align(uiCamera, (restart.camera.height - restart.height()) / 2 + 8 - offset)
		);
		scene.add(restart);

		StyledButton menu = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(WndKeyBindings.class, "menu"), 9) {
			@Override
			protected void onClick() {
				GameScene.show(new WndGame());
			}

			@Override
			public void update() {
				alpha((float) Math.pow(gameOver.am, 2));
				super.update();
			}
		};
		menu.icon(Icons.get(Icons.PREFS));
		menu.alpha(0);
		menu.camera = uiCamera;
		menu.setSize(Math.max(80, menu.reqWidth()), 20);
		menu.setPos(
				align(uiCamera, (menu.camera.width - menu.width()) / 2),
				restart.bottom() + 2
		);
		scene.add(menu);
	}

	public static void bossSlain() {
		if (Dungeon.hero.isAlive()) {
			Banner bossSlain = new Banner(BannerSprites.get(BannerSprites.Type.BOSS_SLAIN));
			bossSlain.show(0xFFFFFF, 0.3f, 5f);
			scene.showBanner(bossSlain);
			Sample.INSTANCE.play(Assets.Sounds.BOSS);
		}
	}

	public static void handleCell(int cell) {
		cellSelector.select(cell, PointerEvent.LEFT);
	}

	public static void selectCell(CellSelector.Listener listener) {
		if (cellSelector.listener != null && cellSelector.listener != defaultCellListener) {
			cellSelector.listener.onSelect(null);
		}
		cellSelector.listener = listener;
		cellSelector.enabled = Dungeon.hero.ready;
		if (scene != null) {
			scene.prompt(listener.prompt());
		}
	}

	public static boolean cancelCellSelector() {
		if (cellSelector.listener != null && cellSelector.listener != defaultCellListener) {
			cellSelector.resetKeyHold();
			cellSelector.cancel();
			return true;
		} else {
			return false;
		}
	}

	public static WndBag selectItem(WndBag.ItemSelector listener) {
		cancel();
		if (scene != null) {
			if (scene.inventory != null && scene.inventory.visible && !showingWindow()) {
				scene.inventory.setSelector(listener);
				return null;
			} else {
				WndBag wnd = WndBag.getBag(listener);
				show(wnd);
				return wnd;
			}
		}
		return null;
	}

	private static WndBag.ItemSelector savedSelector;

	@Override
	public synchronized void saveWindows() {
		if (members == null) return;
		super.saveWindows();
		if (scene != null && scene.inventory != null && scene.inventory.getSelector() != null) {
			savedSelector = scene.inventory.getSelector();
		} else {
			for (Gizmo g : members.toArray(new Gizmo[0])) {
				if (g instanceof WndBag) {
					savedSelector = ((WndBag) g).getSelector();
				} else if (g instanceof InventoryScroll.WndConfirmCancel) {
					savedSelector = ((InventoryScroll.WndConfirmCancel) g).getItemSelector();
				} else if (g instanceof WndUpgrade) {
					savedSelector = ((WndUpgrade) g).getItemSelector();
				}
			}
		}
	}

	@Override
	public synchronized void restoreWindows() {
		super.restoreWindows();
		if (savedSelector != null) {
			if (scene != null && scene.inventory != null) {
				scene.inventory.setSelector(savedSelector);
			} else {
				addToFront(new WndBag(Dungeon.hero.belongings.backpack, savedSelector));
			}
			savedSelector = null;
		}
	}

	public static boolean cancel() {
		cellSelector.resetKeyHold();
		if (Dungeon.hero != null && (Dungeon.hero.curAction != null || Dungeon.hero.resting)) {
			Dungeon.hero.curAction = null;
			Dungeon.hero.resting = false;
			return true;
		} else {
			return cancelCellSelector();
		}
	}

	public static void ready() {
		selectCell(defaultCellListener);
		QuickSlotButton.cancel();
		InventoryPane.cancelTargeting();
		if (scene != null && scene.toolbar != null) scene.toolbar.examining = false;
		if (tagDisappeared) {
			tagDisappeared = false;
			updateTags = true;
		}
	}

	public static void checkKeyHold() {
		cellSelector.processKeyHold();
	}

	public static void resetKeyHold() {
		cellSelector.resetKeyHold();
	}

	public static void examineCell(Integer cell) {
		if (cell == null || cell < 0 || cell > Dungeon.level.length() || (!Dungeon.level.visited[cell] && !Dungeon.level.mapped[cell])) {
			return;
		}

		ArrayList<Object> objects = getObjectsAtCell(cell);
		if (objects.isEmpty()) {
			GameScene.show(new WndInfoCell(cell));
		} else if (objects.size() == 1) {
			examineObject(objects.get(0));
		} else {
			String[] names = getObjectNames(objects).toArray(new String[0]);
			GameScene.show(new WndOptions(Icons.get(Icons.INFO), Messages.get(GameScene.class, "choose_examine"), Messages.get(GameScene.class, "multiple_examine"), names) {
				@Override
				protected void onSelect(int index) {
					examineObject(objects.get(index));
				}
			});
		}
	}

	private static ArrayList<Object> getObjectsAtCell(int cell) {
		ArrayList<Object> objects = new ArrayList<>();
		if (cell == Dungeon.hero.pos) {
			objects.add(Dungeon.hero);
		} else if (Dungeon.level.heroFOV[cell]) {
			Mob mob = (Mob) Actor.findChar(cell);
			if (mob != null) objects.add(mob);
		}
		Heap heap = Dungeon.level.heaps.get(cell);
		if (heap != null && heap.seen) objects.add(heap);
		Plant plant = Dungeon.level.plants.get(cell);
		if (plant != null) objects.add(plant);
		Trap trap = Dungeon.level.traps.get(cell);
		if (trap != null && trap.visible) objects.add(trap);
		return objects;
	}

	private static ArrayList<String> getObjectNames(ArrayList<Object> objects) {
		ArrayList<String> names = new ArrayList<>();
		for (Object obj : objects) {
			if (obj instanceof Hero) names.add(((Hero) obj).className().toUpperCase(Locale.ENGLISH));
			else if (obj instanceof Mob) names.add(Messages.titleCase(((Mob) obj).name()));
			else if (obj instanceof Heap) names.add(Messages.titleCase(((Heap) obj).title()));
			else if (obj instanceof Plant) names.add(Messages.titleCase(((Plant) obj).name()));
			else if (obj instanceof Trap) names.add(Messages.titleCase(((Trap) obj).name()));
		}
		return names;
	}

	public static void examineObject(Object o) {
		if (o == Dungeon.hero) {
			GameScene.show(new WndHero());
		} else if (o instanceof Mob && ((Mob) o).isActive()) {
			GameScene.show(new WndInfoMob((Mob) o));
			if (o instanceof Snake && !Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_SURPRISE_ATKS)) {
				GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_SURPRISE_ATKS);
			}
		} else if (o instanceof Heap && !((Heap) o).isEmpty()) {
			GameScene.show(new WndInfoItem((Heap) o));
		} else if (o instanceof Plant) {
			GameScene.show(new WndInfoPlant((Plant) o));
			Bestiary.setSeen(o.getClass());
		} else if (o instanceof Trap) {
			GameScene.show(new WndInfoTrap((Trap) o));
			Bestiary.setSeen(o.getClass());
		} else {
			GameScene.show(new WndMessage(Messages.get(GameScene.class, "dont_know")));
		}
	}

	private static final CellSelector.Listener defaultCellListener = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (Dungeon.hero.handle(cell)) {
				Dungeon.hero.next();
			}
		}

		@Override
		public void onRightClick(Integer cell) {
			if (cell == null || cell < 0 || cell > Dungeon.level.length() || (!Dungeon.level.visited[cell] && !Dungeon.level.mapped[cell])) {
				return;
			}

			ArrayList<Object> objects = getObjectsAtCell(cell);
			ArrayList<String> textLines = getObjectNames(objects);

			String title = null;
			Image image = null;
			if (objects.isEmpty()) {
				title = WndInfoCell.cellName(cell);
				image = WndInfoCell.cellImage(cell);
			} else if (objects.size() > 1) {
				title = Messages.get(GameScene.class, "multiple");
				image = Icons.get(Icons.INFO);
			} else if (objects.get(0) instanceof Hero) {
				title = textLines.remove(0);
				image = HeroSprite.avatar((Hero) objects.get(0));
			} else if (objects.get(0) instanceof Mob) {
				title = textLines.remove(0);
				image = ((Mob) objects.get(0)).sprite();
			} else if (objects.get(0) instanceof Heap) {
				title = textLines.remove(0);
				image = new ItemSprite((Heap) objects.get(0));
			} else if (objects.get(0) instanceof Plant) {
				title = textLines.remove(0);
				image = TerrainFeaturesTilemap.tile(cell, Dungeon.level.map[cell]);
			} else if (objects.get(0) instanceof Trap) {
				title = textLines.remove(0);
				image = TerrainFeaturesTilemap.tile(cell, Dungeon.level.map[cell]);
			}

			if (objects.isEmpty()) {
				textLines.add(0, Messages.get(GameScene.class, "go_here"));
			} else if (objects.get(0) instanceof Hero) {
				textLines.add(0, Messages.get(GameScene.class, "go_here"));
			} else if (objects.get(0) instanceof Mob) {
				if (((Mob) objects.get(0)).alignment != Char.Alignment.ENEMY) {
					textLines.add(0, Messages.get(GameScene.class, "interact"));
				} else {
					textLines.add(0, Messages.get(GameScene.class, "attack"));
				}
			} else if (objects.get(0) instanceof Heap) {
				switch (((Heap) objects.get(0)).type) {
					case HEAP:
						textLines.add(0, Messages.get(GameScene.class, "pick_up"));
						break;
					case FOR_SALE:
						textLines.add(0, Messages.get(GameScene.class, "purchase"));
						break;
					default:
						textLines.add(0, Messages.get(GameScene.class, "interact"));
						break;
				}
			} else if (objects.get(0) instanceof Plant) {
				textLines.add(0, Messages.get(GameScene.class, "trample"));
			} else if (objects.get(0) instanceof Trap) {
				textLines.add(0, Messages.get(GameScene.class, "interact"));
			}

			if (objects.size() > 1) {
				textLines.add(0, "_" + textLines.remove(0) + ":_ " + textLines.get(0));
				for (int i = 1; i < textLines.size(); i++) {
					textLines.add(i, "_" + Messages.get(GameScene.class, "examine") + ":_ " + textLines.remove(i));
				}
			} else {
				textLines.add(0, "_" + textLines.remove(0) + "_");
				textLines.add(1, "_" + Messages.get(GameScene.class, "examine") + "_");
			}

			RightClickMenu menu = new RightClickMenu(image, title, textLines.toArray(new String[0])) {
				@Override
				public void onSelect(int index) {
					if (index == 0) {
						handleCell(cell);
					} else {
						if (objects.size() == 0) {
							GameScene.show(new WndInfoCell(cell));
						} else {
							examineObject(objects.get(index - 1));
						}
					}
				}
			};
			scene.addToFront(menu);
			menu.camera = PixelScene.uiCamera;
			PointF mousePos = PointerEvent.currentHoverPos();
			mousePos = menu.camera.screenToCamera((int) mousePos.x, (int) mousePos.y);
			menu.setPos(mousePos.x - 3, mousePos.y - 3);
		}

		@Override
		public String prompt() {
			return null;
		}
	};
}