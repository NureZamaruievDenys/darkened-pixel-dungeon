package com.shatteredpixel.shatteredpixeldungeon.network;

import com.shatteredpixel.shatteredpixeldungeon.network.dto.snapshot.GameSnapshotDto;

public class NetworkManager {

    private static final NetworkManager INSTANCE = new NetworkManager();

    public static NetworkManager instance() {
        return INSTANCE;
    }

    private GameServer server;
    private GameClient localClient;

    private boolean host;
    private long localPlayerId;

    public boolean isMultiplayer = false;

    private NetworkManager() {
    }

    public GameSnapshotDto startHostFromCurrentDungeon(String hostPlayerName) {
        if (server == null) {
            server = new GameServer();
        }

        localPlayerId = 1L;
        host = true;
        isMultiplayer = true;

        server.startFromCurrentDungeon(localPlayerId, hostPlayerName);
        server.startNetworkServer(54555);

        GameSnapshotDto snapshot = server.currentSnapshot();

        if (localClient == null) {
            localClient = new GameClient();
        }

        localClient.attachHost(server, localPlayerId, snapshot);
        return snapshot;
    }

    public boolean joinAsClient(String ipAddress, String playerName, String heroClass) {
        if (localClient == null) {
            localClient = new GameClient();
        }

        host = false;
        isMultiplayer = true;

        boolean success = localClient.connectNetwork(ipAddress, 54555, playerName, heroClass);
        if (success) {
            localPlayerId = localClient.playerId();
        }
        return success;
    }

    public void stopHost() {
        if (server != null) {
            server.stopNetworkServer();
            server = null;
        }

        if (localClient != null) {
            localClient.disconnect();
            localClient = null;
        }

        host = false;
        localPlayerId = 0L;
        isMultiplayer = false;
    }

    public boolean isHost() { return host; }
    public boolean isServerRunning() { return server != null && server.isRunning(); }

    public long localPlayerId() {
        if (host) {
            return localPlayerId;
        } else if (localClient != null) {
            return localClient.playerId();
        }
        return 0L;
    }

    public GameServer server() { return server; }
    public GameClient localClient() { return localClient; }

    public GameSnapshotDto currentSnapshot() {
        if (server == null) return null;
        return server.currentSnapshot();
    }
}