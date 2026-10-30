package com.shatteredpixel.shatteredpixeldungeon.network.mapper;

import com.shatteredpixel.shatteredpixeldungeon.game.snapshot.GameSnapshot;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.snapshot.GameSnapshotDto;

public class SnapshotDtoMapper {

    private final StateDtoMapper stateMapper = new StateDtoMapper();

    public GameSnapshotDto toDto(GameSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }

        GameSnapshotDto dto = new GameSnapshotDto();

        dto.snapshotId = snapshot.snapshotId;
        dto.sequence = snapshot.sequence;
        dto.turn = snapshot.turn;

        dto.state = stateMapper.toDto(snapshot.state);

        return dto;
    }

    public GameSnapshot fromDto(GameSnapshotDto dto) {
        if (dto == null) {
            return null;
        }

        GameSnapshot snapshot = new GameSnapshot();

        snapshot.snapshotId = dto.snapshotId;
        snapshot.sequence = dto.sequence;
        snapshot.turn = dto.turn;

        snapshot.state = stateMapper.fromDto(dto.state);

        return snapshot;
    }
}