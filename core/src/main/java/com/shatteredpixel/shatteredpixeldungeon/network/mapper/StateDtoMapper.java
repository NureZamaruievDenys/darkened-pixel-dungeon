package com.shatteredpixel.shatteredpixeldungeon.network.mapper;

import com.shatteredpixel.shatteredpixeldungeon.game.state.BuffState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.GameState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.HeapState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.HeroState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.ItemState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.LevelState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.MobState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.PlayerState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.TrapState;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.state.BuffStateDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.state.GameStateDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.state.HeapStateDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.state.HeroStateDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.state.ItemStateDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.state.LevelStateDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.state.MobStateDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.state.PlayerStateDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.state.TrapStateDto;

public class StateDtoMapper {

    public GameStateDto toDto(GameState state) {
        if (state == null) {
            return null;
        }

        GameStateDto dto = new GameStateDto();

        dto.gameId = state.gameId;
        dto.seed = state.seed;

        dto.depth = state.depth;
        dto.branch = state.branch;

        dto.globalTurn = state.globalTurn;
        dto.serverSequence = state.serverSequence;

        dto.players = new PlayerStateDto[state.players.size()];
        for (int i = 0; i < state.players.size(); i++) {
            dto.players[i] = toDto(state.players.get(i));
        }

        dto.heroes = new HeroStateDto[state.heroes.size()];
        for (int i = 0; i < state.heroes.size(); i++) {
            dto.heroes[i] = toDto(state.heroes.get(i));
        }

        dto.levels = new LevelStateDto[state.levels.size()];
        for (int i = 0; i < state.levels.size(); i++) {
            dto.levels[i] = toDto(state.levels.get(i));
        }

        return dto;
    }

    public PlayerStateDto toDto(PlayerState state) {
        if (state == null) {
            return null;
        }

        PlayerStateDto dto = new PlayerStateDto();

        dto.playerId = state.playerId;
        dto.name = state.name;

        dto.host = state.host;

        dto.controlledHeroId = state.controlledHeroId;

        dto.currentDepth = state.currentDepth;
        dto.currentBranch = state.currentBranch;

        dto.status = state.status == null ? null : state.status.name();

        dto.lastClientSequence = state.lastClientSequence;
        dto.lastAcknowledgedServerSequence = state.lastAcknowledgedServerSequence;

        return dto;
    }

    public HeroStateDto toDto(HeroState state) {
        if (state == null) {
            return null;
        }

        HeroStateDto dto = new HeroStateDto();

        dto.entityId = state.entityId;
        dto.ownerPlayerId = state.ownerPlayerId;

        dto.heroClass = state.heroClass;
        dto.heroSubClass = state.heroSubClass;

        dto.depth = state.depth;
        dto.branch = state.branch;
        dto.pos = state.pos;

        dto.hp = state.hp;
        dto.maxHp = state.maxHp;

        dto.level = state.level;
        dto.exp = state.exp;
        dto.strength = state.strength;

        dto.alive = state.alive;
        dto.ready = state.ready;

        dto.actionTime = state.actionTime;

        dto.inventoryId = state.inventoryId;

        dto.buffs = new BuffStateDto[state.buffs.size()];
        for (int i = 0; i < state.buffs.size(); i++) {
            dto.buffs[i] = toDto(state.buffs.get(i));
        }

        return dto;
    }

    public LevelStateDto toDto(LevelState state) {
        if (state == null) {
            return null;
        }

        LevelStateDto dto = new LevelStateDto();

        dto.levelId = state.levelId;

        dto.depth = state.depth;
        dto.branch = state.branch;

        dto.width = state.width;
        dto.height = state.height;

        dto.terrain = state.terrain == null ? null : state.terrain.clone();
        dto.visited = state.visited == null ? null : state.visited.clone();
        dto.mapped = state.mapped == null ? null : state.mapped.clone();

        dto.mobs = new MobStateDto[state.mobs.size()];
        for (int i = 0; i < state.mobs.size(); i++) {
            dto.mobs[i] = toDto(state.mobs.get(i));
        }

        dto.heaps = new HeapStateDto[state.heaps.size()];
        for (int i = 0; i < state.heaps.size(); i++) {
            dto.heaps[i] = toDto(state.heaps.get(i));
        }

        dto.traps = new TrapStateDto[state.traps.size()];
        for (int i = 0; i < state.traps.size(); i++) {
            dto.traps[i] = toDto(state.traps.get(i));
        }

        return dto;
    }

    public MobStateDto toDto(MobState state) {
        if (state == null) {
            return null;
        }

        MobStateDto dto = new MobStateDto();

        dto.entityId = state.entityId;

        dto.mobType = state.mobType;

        dto.depth = state.depth;
        dto.branch = state.branch;
        dto.pos = state.pos;

        dto.hp = state.hp;
        dto.maxHp = state.maxHp;

        dto.alive = state.alive;

        dto.actionTime = state.actionTime;

        dto.targetEntityId = state.targetEntityId;
        dto.lastKnownTargetPos = state.lastKnownTargetPos;

        dto.buffs = new BuffStateDto[state.buffs.size()];
        for (int i = 0; i < state.buffs.size(); i++) {
            dto.buffs[i] = toDto(state.buffs.get(i));
        }

        return dto;
    }

    public BuffStateDto toDto(BuffState state) {
        if (state == null) {
            return null;
        }

        BuffStateDto dto = new BuffStateDto();

        dto.buffId = state.buffId;

        dto.buffType = state.buffType;

        dto.targetEntityId = state.targetEntityId;
        dto.sourceEntityId = state.sourceEntityId;

        dto.duration = state.duration;
        dto.stacks = state.stacks;

        return dto;
    }

    public HeapStateDto toDto(HeapState state) {
        if (state == null) {
            return null;
        }

        HeapStateDto dto = new HeapStateDto();

        dto.heapId = state.heapId;

        dto.pos = state.pos;

        dto.items = new ItemStateDto[state.items.size()];
        for (int i = 0; i < state.items.size(); i++) {
            dto.items[i] = toDto(state.items.get(i));
        }

        return dto;
    }

    public ItemStateDto toDto(ItemState state) {
        if (state == null) {
            return null;
        }

        ItemStateDto dto = new ItemStateDto();

        dto.itemId = state.itemId;

        dto.itemType = state.itemType;

        dto.quantity = state.quantity;
        dto.level = state.level;

        dto.cursed = state.cursed;
        dto.identified = state.identified;

        return dto;
    }

    public TrapStateDto toDto(TrapState state) {
        if (state == null) {
            return null;
        }

        TrapStateDto dto = new TrapStateDto();

        dto.trapId = state.trapId;

        dto.trapType = state.trapType;

        dto.pos = state.pos;

        dto.active = state.active;
        dto.visible = state.visible;

        return dto;
    }

    public GameState fromDto(GameStateDto dto) {
        if (dto == null) {
            return null;
        }

        GameState state = new GameState();

        state.gameId = dto.gameId;
        state.seed = dto.seed;

        state.depth = dto.depth;
        state.branch = dto.branch;

        state.globalTurn = dto.globalTurn;
        state.serverSequence = dto.serverSequence;

        if (dto.players != null) {
            for (PlayerStateDto playerDto : dto.players) {
                PlayerState player = fromDto(playerDto);
                if (player != null) {
                    state.players.add(player);
                }
            }
        }

        if (dto.heroes != null) {
            for (HeroStateDto heroDto : dto.heroes) {
                HeroState hero = fromDto(heroDto);
                if (hero != null) {
                    state.heroes.add(hero);
                }
            }
        }

        if (dto.levels != null) {
            for (LevelStateDto levelDto : dto.levels) {
                LevelState level = fromDto(levelDto);
                if (level != null) {
                    state.levels.add(level);
                }
            }
        }

        return state;
    }

    public PlayerState fromDto(PlayerStateDto dto) {
        if (dto == null) {
            return null;
        }

        PlayerState state = new PlayerState();

        state.playerId = dto.playerId;
        state.name = dto.name;

        state.host = dto.host;

        state.controlledHeroId = dto.controlledHeroId;

        state.currentDepth = dto.currentDepth;
        state.currentBranch = dto.currentBranch;

        if (dto.status != null) {
            state.status = PlayerState.Status.valueOf(dto.status);
        }

        state.lastClientSequence = dto.lastClientSequence;
        state.lastAcknowledgedServerSequence = dto.lastAcknowledgedServerSequence;

        return state;
    }

    public HeroState fromDto(HeroStateDto dto) {
        if (dto == null) {
            return null;
        }

        HeroState state = new HeroState();

        state.entityId = dto.entityId;
        state.ownerPlayerId = dto.ownerPlayerId;

        state.heroClass = dto.heroClass;
        state.heroSubClass = dto.heroSubClass;

        state.depth = dto.depth;
        state.branch = dto.branch;
        state.pos = dto.pos;

        state.hp = dto.hp;
        state.maxHp = dto.maxHp;

        state.level = dto.level;
        state.exp = dto.exp;
        state.strength = dto.strength;

        state.alive = dto.alive;
        state.ready = dto.ready;

        state.actionTime = dto.actionTime;

        state.inventoryId = dto.inventoryId;

        if (dto.buffs != null) {
            for (BuffStateDto buffDto : dto.buffs) {
                BuffState buff = fromDto(buffDto);
                if (buff != null) {
                    state.buffs.add(buff);
                }
            }
        }

        return state;
    }

    public LevelState fromDto(LevelStateDto dto) {
        if (dto == null) {
            return null;
        }

        LevelState state = new LevelState();

        state.levelId = dto.levelId;

        state.depth = dto.depth;
        state.branch = dto.branch;

        state.width = dto.width;
        state.height = dto.height;

        state.terrain = dto.terrain == null ? null : dto.terrain.clone();
        state.visited = dto.visited == null ? null : dto.visited.clone();
        state.mapped = dto.mapped == null ? null : dto.mapped.clone();

        if (dto.mobs != null) {
            for (MobStateDto mobDto : dto.mobs) {
                MobState mob = fromDto(mobDto);
                if (mob != null) {
                    state.mobs.add(mob);
                }
            }
        }

        if (dto.heaps != null) {
            for (HeapStateDto heapDto : dto.heaps) {
                HeapState heap = fromDto(heapDto);
                if (heap != null) {
                    state.heaps.add(heap);
                }
            }
        }

        if (dto.traps != null) {
            for (TrapStateDto trapDto : dto.traps) {
                TrapState trap = fromDto(trapDto);
                if (trap != null) {
                    state.traps.add(trap);
                }
            }
        }

        return state;
    }

    public MobState fromDto(MobStateDto dto) {
        if (dto == null) {
            return null;
        }

        MobState state = new MobState();

        state.entityId = dto.entityId;

        state.mobType = dto.mobType;

        state.depth = dto.depth;
        state.branch = dto.branch;
        state.pos = dto.pos;

        state.hp = dto.hp;
        state.maxHp = dto.maxHp;

        state.alive = dto.alive;

        state.actionTime = dto.actionTime;

        state.targetEntityId = dto.targetEntityId;
        state.lastKnownTargetPos = dto.lastKnownTargetPos;

        if (dto.buffs != null) {
            for (BuffStateDto buffDto : dto.buffs) {
                BuffState buff = fromDto(buffDto);
                if (buff != null) {
                    state.buffs.add(buff);
                }
            }
        }

        return state;
    }

    public BuffState fromDto(BuffStateDto dto) {
        if (dto == null) {
            return null;
        }

        BuffState state = new BuffState();

        state.buffId = dto.buffId;

        state.buffType = dto.buffType;

        state.targetEntityId = dto.targetEntityId;
        state.sourceEntityId = dto.sourceEntityId;

        state.duration = dto.duration;
        state.stacks = dto.stacks;

        return state;
    }

    public HeapState fromDto(HeapStateDto dto) {
        if (dto == null) {
            return null;
        }

        HeapState state = new HeapState();

        state.heapId = dto.heapId;

        state.pos = dto.pos;

        if (dto.items != null) {
            for (ItemStateDto itemDto : dto.items) {
                ItemState item = fromDto(itemDto);
                if (item != null) {
                    state.items.add(item);
                }
            }
        }

        return state;
    }

    public ItemState fromDto(ItemStateDto dto) {
        if (dto == null) {
            return null;
        }

        ItemState state = new ItemState();

        state.itemId = dto.itemId;

        state.itemType = dto.itemType;

        state.quantity = dto.quantity;
        state.level = dto.level;

        state.cursed = dto.cursed;
        state.identified = dto.identified;

        return state;
    }

    public TrapState fromDto(TrapStateDto dto) {
        if (dto == null) {
            return null;
        }

        TrapState state = new TrapState();

        state.trapId = dto.trapId;

        state.trapType = dto.trapType;

        state.pos = dto.pos;

        state.active = dto.active;
        state.visible = dto.visible;

        return state;
    }
}