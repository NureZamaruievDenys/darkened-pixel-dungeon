package com.shatteredpixel.shatteredpixeldungeon.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import com.shatteredpixel.shatteredpixeldungeon.game.command.PlayerCommand;
import com.shatteredpixel.shatteredpixeldungeon.game.save.LegacySnapshotBuilder;
import com.shatteredpixel.shatteredpixeldungeon.game.simulation.SimulationResult;
import com.shatteredpixel.shatteredpixeldungeon.game.snapshot.GameSnapshot;
import com.shatteredpixel.shatteredpixeldungeon.game.state.GameState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.HeroState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.PlayerState;
import com.shatteredpixel.shatteredpixeldungeon.multiplayer.MultiplayerSession;
import com.shatteredpixel.shatteredpixeldungeon.multiplayer.ServerGameLoop;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.command.PlayerCommandDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.delta.StateDeltaDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.session.JoinRequestDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.session.JoinResponseDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.session.LobbyPlayerDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.session.LobbyUpdateDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.session.PlayerReadyDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.snapshot.GameSnapshotDto;
import com.shatteredpixel.shatteredpixeldungeon.network.mapper.CommandDtoMapper;
import com.shatteredpixel.shatteredpixeldungeon.network.mapper.DeltaDtoMapper;
import com.shatteredpixel.shatteredpixeldungeon.network.mapper.SnapshotDtoMapper;

import java.io.IOException;

public class GameServer {

    private boolean running = false;
    private ServerGameLoop gameLoop;
    private MultiplayerSession session;

    private CommandDtoMapper commandMapper = new CommandDtoMapper();
    private DeltaDtoMapper deltaMapper = new DeltaDtoMapper();
    private SnapshotDtoMapper snapshotMapper = new SnapshotDtoMapper();

    private StateDeltaDto lastDelta;
    private Server kryoServer;

    public GameServer() {}

    public GameSnapshotDto startFromCurrentDungeon(long hostPlayerId, String hostPlayerName) {
        LegacySnapshotBuilder builder = new LegacySnapshotBuilder();
        GameSnapshot initialSnapshot = builder.buildLocalSnapshot(hostPlayerId, hostPlayerName);
        GameState initialState = initialSnapshot.state;

        session = new MultiplayerSession();
        session.sessionId = initialSnapshot.snapshotId;
        session.gameState = initialState;

        PlayerState hostState = initialState.findPlayer(hostPlayerId);
        if (hostState == null) {
            hostState = new PlayerState();
            hostState.playerId = hostPlayerId;
            hostState.name = hostPlayerName;
            hostState.host = true;

            if (!initialState.heroes.isEmpty()) {
                hostState.controlledHeroId = initialState.heroes.get(0).entityId;
                initialState.heroes.get(0).ownerPlayerId = hostPlayerId;

                // ИСПРАВЛЕНИЕ: Явно сохраняем класс Хоста в сетевое состояние!
                if (com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero != null) {
                    initialState.heroes.get(0).heroClass = com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero.heroClass.name();
                }
            }
            session.gameState.players.add(hostState);
        }

        session.addPlayerFromState(hostState);
        gameLoop = new ServerGameLoop(session.gameState);
        running = true;

        System.out.println("[СЕРВЕР] Состояние успешно загружено из текущего подземелья!");
        return currentSnapshot();
    }

    public boolean isRunning() { return running; }

    public JoinResponseDto join(JoinRequestDto request) {
        JoinResponseDto response = new JoinResponseDto();
        if (!running || session == null || session.gameState == null) {
            response.accepted = false;
            return response;
        }

        response.accepted = true;
        response.assignedPlayerId = generatePlayerId();

        PlayerState newPlayer = new PlayerState();
        newPlayer.playerId = response.assignedPlayerId;
        newPlayer.name = request.playerName;
        newPlayer.host = false;

        HeroState newHero = new HeroState();
        newHero.entityId = generateEntityId();
        newHero.ownerPlayerId = newPlayer.playerId;
        newHero.heroClass = request.heroClass != null ? request.heroClass : "WARRIOR";
        newHero.hp = 20;
        newHero.maxHp = 20;
        newHero.level = 1;
        newHero.strength = 10;
        newHero.alive = true;

        HeroState hostHero = session.gameState.heroes.isEmpty() ? null : session.gameState.heroes.get(0);
        if (hostHero != null) {
            newHero.depth = hostHero.depth;
            newHero.branch = hostHero.branch;
            newHero.pos = hostHero.pos;
        }

        newPlayer.controlledHeroId = newHero.entityId;

        session.gameState.players.add(newPlayer);
        session.gameState.heroes.add(newHero);
        session.addPlayerFromState(newPlayer);

        if (NetworkManager.instance().localClient() != null) {
            NetworkManager.instance().localClient().attachHost(this, NetworkManager.instance().localPlayerId(), currentSnapshot());
        }

        if (kryoServer != null) {
            kryoServer.sendToAllTCP(currentSnapshot());
        }

        response.initialSnapshot = currentSnapshot();
        return response;
    }

    public void handlePlayerReady(PlayerReadyDto dto) {
        if (session == null || session.gameState == null) return;
        PlayerState ps = session.gameState.findPlayer(dto.playerId);
        if (ps != null) {
            HeroState hs = session.gameState.findHero(ps.controlledHeroId);
            if (hs != null) {
                hs.ready = dto.ready;
            }
        }
        broadcastLobbyUpdate();
    }

    public void launchGame() {
        if (session == null || session.gameState == null) return;
        LobbyUpdateDto update = new LobbyUpdateDto();
        update.launchGame = true;

        update.dungeonSeed = com.shatteredpixel.shatteredpixeldungeon.Dungeon.seed;

        if (kryoServer != null) {
            kryoServer.sendToAllTCP(currentSnapshot());
            kryoServer.sendToAllTCP(update);
        }
        if (NetworkManager.instance().localClient() != null) {
            NetworkManager.instance().localClient().lastLobbyUpdate = update;
        }
    }

    public void broadcastLobbyUpdate() {
        if (session == null || session.gameState == null) return;

        LobbyUpdateDto update = new LobbyUpdateDto();
        update.dungeonSeed = com.shatteredpixel.shatteredpixeldungeon.Dungeon.seed;
        boolean allReady = true;

        for (PlayerState p : session.gameState.players) {
            LobbyPlayerDto lp = new LobbyPlayerDto();
            lp.name = p.name;
            lp.isHost = p.host;

            HeroState h = session.gameState.findHero(p.controlledHeroId);
            lp.heroClass = (h != null && h.heroClass != null) ? h.heroClass : "WARRIOR";
            lp.ready = (h != null) ? h.ready : false;

            if (!lp.ready) allReady = false;
            update.players.add(lp);
        }

        if (update.players.size() > 1 && allReady) {
            update.allReadyToStart = true;
        }

        if (kryoServer != null) {
            kryoServer.sendToAllTCP(update);
        }

        if (NetworkManager.instance().localClient() != null) {
            NetworkManager.instance().localClient().lastLobbyUpdate = update;
        }
    }

    public void startNetworkServer(int port) {
        try {
            kryoServer = new Server(1048576, 1048576);
            registerClasses(kryoServer.getKryo());
            kryoServer.start();
            kryoServer.bind(port, port);

            kryoServer.addListener(new Listener() {
                @Override
                public void received(Connection connection, Object object) {
                    if (object instanceof JoinRequestDto) {
                        JoinResponseDto response = join((JoinRequestDto) object);
                        connection.sendTCP(response);
                        if (response.accepted) {
                            broadcastLobbyUpdate();
                        }
                    } else if (object instanceof PlayerCommandDto) {
                        PlayerCommandDto cmd = (PlayerCommandDto) object;
                        System.out.println("[SERVER] Сервер получил команду по TCP от Клиента. Рассылаем всем...");
                        kryoServer.sendToAllTCP(object);

                        if (NetworkManager.instance().localClient() != null) {
                            NetworkManager.instance().localClient().receiveCommand(cmd);
                        }
                    } else if (object instanceof PlayerReadyDto) {
                        handlePlayerReady((PlayerReadyDto) object);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopNetworkServer() {
        if (kryoServer != null) {
            kryoServer.stop();
            kryoServer = null;
        }
    }

    public static void registerClasses(Kryo kryo) {
        kryo.setRegistrationRequired(false);
        kryo.register(java.util.ArrayList.class);
        kryo.register(java.util.HashMap.class);
        kryo.register(JoinRequestDto.class);
        kryo.register(JoinResponseDto.class);
        kryo.register(PlayerCommandDto.class);
        kryo.register(StateDeltaDto.class);
        kryo.register(GameSnapshotDto.class);
        kryo.register(GameState.class);
        kryo.register(PlayerState.class);
        kryo.register(HeroState.class);
    }

    public ServerCommandResult handleCommand(PlayerCommandDto dto) {
        if (!running) return ServerCommandResult.rejected("Server is not running", null);

        System.out.println("[SERVER] Сервер получил ЛОКАЛЬНУЮ команду от Хоста. Рассылаем всем...");
        if (kryoServer != null) {
            kryoServer.sendToAllTCP(dto);
        }

        if (NetworkManager.instance().localClient() != null) {
            NetworkManager.instance().localClient().receiveCommand(dto);
        }

        return ServerCommandResult.accepted(null);
    }

    public GameSnapshotDto currentSnapshot() {
        if (!running || session == null || session.gameState == null) return null;
        GameSnapshot snapshot = new GameSnapshot();
        snapshot.snapshotId = session.sessionId;
        snapshot.sequence = session.gameState.serverSequence;
        snapshot.turn = session.gameState.globalTurn;
        snapshot.state = session.gameState;
        return snapshotMapper.toDto(snapshot);
    }

    private long generatePlayerId() {
        long max = 0L;
        if (session != null && session.gameState != null) {
            for (PlayerState p : session.gameState.players) max = Math.max(max, p.playerId);
        }
        return max + 1L;
    }

    private long generateEntityId() {
        long max = 0L;
        if (session != null && session.gameState != null) {
            for (HeroState h : session.gameState.heroes) max = Math.max(max, h.entityId);
        }
        return max + 1000L;
    }
}