package com.shatteredpixel.shatteredpixeldungeon.network.dto.session;

import java.util.ArrayList;

public class LobbyUpdateDto {
    public ArrayList<LobbyPlayerDto> players = new ArrayList<>();
    public boolean allReadyToStart = false; // Флаг, что игра начинается!

    public boolean launchGame = false;

    public GameSettingsDto settings = new GameSettingsDto();

    public long dungeonSeed = 0;
}
