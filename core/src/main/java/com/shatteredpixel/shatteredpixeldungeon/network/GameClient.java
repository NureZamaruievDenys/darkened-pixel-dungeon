package com.shatteredpixel.shatteredpixeldungeon.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import com.shatteredpixel.shatteredpixeldungeon.multiplayer.client.ClientStateStore;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.command.PlayerCommandDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.delta.StateDeltaDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.session.JoinRequestDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.session.JoinResponseDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.session.LobbyUpdateDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.snapshot.GameSnapshotDto;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Game;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameClient {

    private final ClientStateStore stateStore = new ClientStateStore();

    private GameServer localServer;
    private boolean connected;
    private long playerId;
    private GameSnapshotDto snapshot;
    private StateDeltaDto lastDelta;

    public Client kryoClient;

    public LobbyUpdateDto lastLobbyUpdate = null;

    //query of commands
    private final ConcurrentLinkedQueue<PlayerCommandDto> commandQueue = new ConcurrentLinkedQueue<>();

    public GameClient() {}

    // command of player
    public PlayerCommandDto popCommand(long forPlayerId) {
        for (PlayerCommandDto cmd : commandQueue) {
            if (cmd.playerId == forPlayerId) {
                commandQueue.remove(cmd);
                return cmd;
            }
        }
        return null;
    }

    // command receiving
    public void receiveCommand(PlayerCommandDto cmd) {
        System.out.println("[CLIENT] Получена команда с сервера для ID: " + cmd.playerId + ", Тип: " + cmd.type);
        commandQueue.add(cmd);
        Game.runOnRenderThread(GameScene::ready);
    }


    public JoinResponseDto connectLocal(GameServer server, String playerName) {
        this.localServer = server;
        JoinRequestDto request = new JoinRequestDto();
        request.playerName = playerName;
        request.requestedPlayerId = 0L;

        JoinResponseDto response = server.join(request);
        connected = response.accepted;
        if (response.accepted) {
            playerId = response.assignedPlayerId;
            snapshot = response.initialSnapshot;
            stateStore.applySnapshot(snapshot);
        }
        return response;
    }

    public boolean connectNetwork(String ip, int port, String playerName, String heroClass) {
        try {
            kryoClient = new Client(1048576, 1048576);
            GameServer.registerClasses(kryoClient.getKryo());
            kryoClient.start();

            kryoClient.addListener(new Listener() {
                @Override
                public void connected(Connection connection) {
                    JoinRequestDto request = new JoinRequestDto();
                    request.playerName = playerName;
                    request.heroClass = heroClass;
                    request.requestedPlayerId = 0L;
                    kryoClient.sendTCP(request);
                }

                @Override
                public void received(Connection connection, Object object) {
                    if (object instanceof JoinResponseDto) {
                        JoinResponseDto response = (JoinResponseDto) object;
                        connected = response.accepted;
                        if (response.accepted) {
                            playerId = response.assignedPlayerId;
                            snapshot = response.initialSnapshot;
                            stateStore.applySnapshot(snapshot);
                        }
                    } else if (object instanceof StateDeltaDto) {
                        lastDelta = (StateDeltaDto) object;
                        stateStore.applyDelta(lastDelta);
                    } else if (object instanceof GameSnapshotDto) {
                        snapshot = (GameSnapshotDto) object;
                        stateStore.applySnapshot(snapshot);
                    } else if (object instanceof LobbyUpdateDto) {
                        lastLobbyUpdate = (LobbyUpdateDto) object;
                    } else if (object instanceof PlayerCommandDto) {
                        receiveCommand((PlayerCommandDto) object);
                    }
                }

                @Override
                public void disconnected(Connection connection) {
                    connected = false;
                }
            });

            kryoClient.connect(5000, ip, port, port);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void attachHost(GameServer server, long playerId, GameSnapshotDto snapshot) {
        this.localServer = server;
        this.connected = server != null && server.isRunning();
        this.playerId = playerId;
        this.snapshot = snapshot;
        this.lastDelta = null;
        stateStore.applySnapshot(snapshot);
    }

    public ServerCommandResult sendCommand(PlayerCommandDto command) {
        if (kryoClient != null && kryoClient.isConnected()) {
            kryoClient.sendTCP(command);
            return ServerCommandResult.accepted(null);
        }
        if (!connected || localServer == null) {
            return ServerCommandResult.rejected("Client is not connected", null);
        }
        ServerCommandResult result = localServer.handleCommand(command);
        lastDelta = result.delta;
        stateStore.applyDelta(lastDelta);
        return result;
    }

    public void sendNetworkPacket(Object packet) {
        if (kryoClient != null && kryoClient.isConnected()) {
            if (packet instanceof PlayerCommandDto) {
                System.out.println("[CLIENT] Отправка команды по TCP (Клиент -> Сервер)");
            }
            kryoClient.sendTCP(packet);
        } else if (localServer != null) {
            if (packet instanceof PlayerCommandDto) {
                System.out.println("[CLIENT] Отправка команды ЛОКАЛЬНО (Хост -> Сервер)");
            }
            localServer.handleCommand((PlayerCommandDto) packet);
        } else {
            System.out.println("[CLIENT] ОШИБКА: Нет подключения для отправки пакета!");
        }
    }

    public void disconnect() {
        connected = false;
        localServer = null;
        playerId = 0L;
        snapshot = null;
        lastDelta = null;
        if (kryoClient != null) {
            kryoClient.stop();
            kryoClient = null;
        }
    }

    public boolean isConnected() { return connected; }
    public long playerId() { return playerId; }

    public GameSnapshotDto snapshot() {
        if (localServer != null) {
            return localServer.currentSnapshot();
        }
        return snapshot;
    }

    public StateDeltaDto lastDelta() { return lastDelta; }
    public ClientStateStore stateStore() { return stateStore; }
}