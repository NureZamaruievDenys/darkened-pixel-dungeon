package com.shatteredpixel.shatteredpixeldungeon.network.mapper;

import com.shatteredpixel.shatteredpixeldungeon.game.event.CommandRejectedEvent;
import com.shatteredpixel.shatteredpixeldungeon.game.event.EntityMovedEvent;
import com.shatteredpixel.shatteredpixeldungeon.game.event.GameEvent;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.event.GameEventDto;

public class EventDtoMapper {

    public GameEventDto toDto(GameEvent event) {
        if (event == null) {
            return null;
        }

        GameEventDto dto = new GameEventDto();

        dto.eventId = event.eventId;
        dto.serverSequence = event.serverSequence;
        dto.turn = event.turn;

        dto.type = event.type();

        if (event instanceof EntityMovedEvent) {
            EntityMovedEvent moved = (EntityMovedEvent) event;
            dto.entityId = moved.entityId;
            dto.fromCell = moved.fromCell;
            dto.toCell = moved.toCell;
        } else if (event instanceof CommandRejectedEvent) {
            CommandRejectedEvent rejected = (CommandRejectedEvent) event;
            dto.commandId = rejected.commandId;
            dto.playerId = rejected.playerId;
            dto.reason = rejected.reason;
        }

        return dto;
    }

    public GameEvent fromDto(GameEventDto dto) {
        if (dto == null || dto.type == null) {
            return null;
        }

        GameEvent event;

        switch (dto.type) {
            case "ENTITY_MOVED":
                event = new EntityMovedEvent(dto.entityId, dto.fromCell, dto.toCell);
                break;
            case "COMMAND_REJECTED":
                event = new CommandRejectedEvent(dto.commandId, dto.playerId, dto.reason);
                break;
            default:
                return null;
        }

        event.eventId = dto.eventId;
        event.serverSequence = dto.serverSequence;
        event.turn = dto.turn;

        return event;
    }
}