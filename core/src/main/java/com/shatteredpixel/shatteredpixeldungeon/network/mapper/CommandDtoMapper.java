package com.shatteredpixel.shatteredpixeldungeon.network.mapper;

import com.shatteredpixel.shatteredpixeldungeon.game.command.AttackCommand;
import com.shatteredpixel.shatteredpixeldungeon.game.command.MoveCommand;
import com.shatteredpixel.shatteredpixeldungeon.game.command.PlayerCommand;
import com.shatteredpixel.shatteredpixeldungeon.game.command.WaitCommand;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.command.PlayerCommandDto;

public class CommandDtoMapper {

    public PlayerCommandDto toDto(PlayerCommand command) {
        if (command == null) {
            return null;
        }

        PlayerCommandDto dto = new PlayerCommandDto();

        dto.commandId = command.commandId;
        dto.playerId = command.playerId;
        dto.actorEntityId = command.actorEntityId;
        dto.clientSequence = command.clientSequence;
        dto.baseServerSequence = command.baseServerSequence;
        dto.type = command.type();

        if (command instanceof MoveCommand) {
            MoveCommand move = (MoveCommand) command;
            dto.targetCell = move.targetCell;
        } else if (command instanceof AttackCommand) {
            AttackCommand attack = (AttackCommand) command;
            dto.targetCell = attack.targetCell;
            dto.targetEntityId = (int) attack.targetEntityId; // ИСПРАВЛЕНО: Приведение к int
        }

        return dto;
    }

    public PlayerCommand fromDto(PlayerCommandDto dto) {
        if (dto == null || dto.type == null) {
            return null;
        }

        PlayerCommand command;

        switch (dto.type) {
            case "MOVE":
                command = new MoveCommand(dto.playerId, dto.actorEntityId, dto.targetCell);
                break;
            case "ATTACK":
                command = new AttackCommand(dto.playerId, dto.actorEntityId, dto.targetCell, dto.targetEntityId);
                break;
            case "WAIT":
                command = new WaitCommand(dto.playerId, dto.actorEntityId);
                break;
            default:
                return null;
        }

        command.commandId = dto.commandId;
        command.clientSequence = dto.clientSequence;
        command.baseServerSequence = dto.baseServerSequence;

        return command;
    }
}