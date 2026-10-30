package com.shatteredpixel.shatteredpixeldungeon.network.dto.session;

public class JoinRequestDto {

    public String playerName;
    public String heroClass;

    public long requestedPlayerId;

    public int protocolVersion;
    public String gameVersion;

    public JoinRequestDto() {
    }
}