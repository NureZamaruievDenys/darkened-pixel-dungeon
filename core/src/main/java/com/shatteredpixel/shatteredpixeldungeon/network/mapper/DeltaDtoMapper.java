package com.shatteredpixel.shatteredpixeldungeon.network.mapper;

import com.shatteredpixel.shatteredpixeldungeon.game.delta.EntityDelta;
import com.shatteredpixel.shatteredpixeldungeon.game.delta.StateDelta;
import com.shatteredpixel.shatteredpixeldungeon.game.event.GameEvent;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.delta.EntityDeltaDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.delta.StateDeltaDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.event.GameEventDto;

public class DeltaDtoMapper {

    private final EventDtoMapper eventMapper = new EventDtoMapper();

    public StateDeltaDto toDto(StateDelta delta) {
        if (delta == null) {
            return null;
        }

        StateDeltaDto dto = new StateDeltaDto();

        dto.baseSequence = delta.baseSequence;
        dto.sequence = delta.sequence;
        dto.turn = delta.turn;

        dto.entityDeltas = new EntityDeltaDto[delta.entityDeltas.size()];
        for (int i = 0; i < delta.entityDeltas.size(); i++) {
            dto.entityDeltas[i] = toDto(delta.entityDeltas.get(i));
        }

        dto.events = new GameEventDto[delta.events.size()];
        for (int i = 0; i < delta.events.size(); i++) {
            dto.events[i] = eventMapper.toDto(delta.events.get(i));
        }

        return dto;
    }

    public EntityDeltaDto toDto(EntityDelta delta) {
        if (delta == null) {
            return null;
        }

        EntityDeltaDto dto = new EntityDeltaDto();

        dto.entityId = delta.entityId;

        dto.pos = delta.pos;
        dto.hp = delta.hp;
        dto.alive = delta.alive;

        return dto;
    }

    public StateDelta fromDto(StateDeltaDto dto) {
        if (dto == null) {
            return null;
        }

        StateDelta delta = new StateDelta();

        delta.baseSequence = dto.baseSequence;
        delta.sequence = dto.sequence;
        delta.turn = dto.turn;

        if (dto.entityDeltas != null) {
            for (EntityDeltaDto entityDeltaDto : dto.entityDeltas) {
                EntityDelta entityDelta = fromDto(entityDeltaDto);
                if (entityDelta != null) {
                    delta.entityDeltas.add(entityDelta);
                }
            }
        }

        if (dto.events != null) {
            for (GameEventDto eventDto : dto.events) {
                GameEvent event = eventMapper.fromDto(eventDto);
                if (event != null) {
                    delta.events.add(event);
                }
            }
        }

        return delta;
    }

    public EntityDelta fromDto(EntityDeltaDto dto) {
        if (dto == null) {
            return null;
        }

        EntityDelta delta = new EntityDelta();

        delta.entityId = dto.entityId;

        delta.pos = dto.pos;
        delta.hp = dto.hp;
        delta.alive = dto.alive;

        return delta;
    }
}