package com.shatteredpixel.shatteredpixeldungeon.network.dto.state;

/**
 * Network DTO for PlayerState.
 *
 * Kryo-friendly:
 * - public fields
 * - no logic
 * - no final collections
 * - no complex domain references
 */
public class PlayerStateDto {

    public long playerId;
    public String name;

    public boolean host;

    public long controlledHeroId;

    public int currentDepth;
    public int currentBranch;

    public String status;

    public int lastClientSequence;
    public long lastAcknowledgedServerSequence;

    public PlayerStateDto() {
    }
}