Multiplayer prototype skeleton for Shattered Pixel Dungeon

Created files (skeletons):
 - core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/network/NetworkManager.java
 - core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/network/GameServer.java
 - core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/network/GameClient.java
 - core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/network/MultiplayerMode.java
 - core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/network/messages/*.java (JoinRequest/JoinResponse/Command/StateDelta/TickStart)

What this skeleton provides
 - A central `NetworkManager` singleton to choose mode, start/stop server or client.
 - Message POJOs intended for Kryo/Kryonet serialization (public fields, no logic).
 - Empty server/client skeletons where you'll plug transport and message handlers.

Next steps to produce a working prototype
 1) Add Kryonet dependency (recommended for libGDX projects):

    In `../core/build.gradle` dependencies block add:

    ```groovy
    implementation "com.esotericsoftware:kryonet:2.24.0"
    implementation "com.esotericsoftware:kryo:4.0.2"
    ```

 2) Implement `GameServer` using Kryonet `Server`:
    - register message classes with Kryo
    - accept connections, create PlayerSession for each connection
    - host authoritative Dungeon instance and run simulation loop on server
    - send `StateDelta` and `JoinResponse`

 3) Implement `GameClient` using Kryonet `Client`:
    - register message classes with Kryo
    - connect, send `JoinRequest`, send `Command`, receive `StateDelta`
    - integrate client-side prediction or simple apply-on-confirm behavior depending on chosen mode

 4) UI integration (Title/HeroSelect scenes):
    - Add a new button on `TitleScene` or inside `HeroSelectScene` to open multiplayer menu.
    - See `TitleScene.java` for existing buttons (where to add yours). You can add a new `StyledButton` similar to `btnJournal`.

 5) Implement server authoritative state mapping:
    - On server side, `Dungeon` should be adapted (or a server-only `DungeonServer` wrapper) to hold `Map<UUID, Hero>` while keeping single-player interface intact in client code.

How to hook a button into `TitleScene` (example)
 - Open `../core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/TitleScene.java`
 - Create a `StyledButton` and in `onClick()` open a new window `WndMultiplayer` (you'll create it under `core/src/main/java/.../windows/WndMultiplayer.java`).
 - The `WndMultiplayer` window should let you pick `MultiplayerMode`, start server, join server (host/ip/port), and show local player id.

Notes on keeping singleplayer intact
 - Keep existing `Dungeon` and singleplayer entry points untouched. Only when `NetworkManager` starts a multiplayer session should code paths consult the network and/or use server authoritative APIs.
 - Implement server-side `DungeonServer` adapter so server can run simulation while clients remain mostly unchanged.

If you want, I can now:
 - implement Kryonet-based `GameServer`/`GameClient` skeleton (requires adding dependencies to Gradle),
 - create `WndMultiplayer` UI window and show a patched `TitleScene` with a new button.

Tell me which of the two you want me to do next: add Kryonet transport code, or add UI window and patch `TitleScene` to show the multiplayer menu.
