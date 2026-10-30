package com.shatteredpixel.shatteredpixeldungeon.network.dto.session;

public class GameSettingsDto {
    public int syncMode = 0; // 0 - Пошагово вместе, 1 - Параллельно, 2 - Порознь
    // В будущем сюда можно добавить seed и challenges
}