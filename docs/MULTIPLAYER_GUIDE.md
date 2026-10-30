# Руководство по реализации мультиплеера

Ниже — подробная документация и план по папкам `game`, `network`, `multiplayer` для проекта Shattered Pixel Dungeon. Содержит: архитектурные рекомендации, список полей/методов, структуры DTO, сценарии сообщений и приоритетный план работ.

---

## Краткий чек-лист (шаги реализации)

1. Спроектировать протокол обмена (сообщения, версия, сериализация).
2. Создать пакет `network` — низкоуровневый транспорт (WebSocket/TCP/UDP + надежность).
3. Создать пакет `multiplayer` — логика синхронизации, сервер/клиент, lobby, матчмейкинг.
4. Прописать DTO и сериализацию (protobuf/JSON/Kryo).
5. Интегрировать с `Dungeon` и Actor-системой: ввести snapshot/delta и командную модель.
6. Реализовать сервер (авторитетный) и простой референс-клиент.
7. Сделать тесты: симуляция сети, нагрузочное тестирование, replay.
8. Добавить UI (lobby, подключение, статус сети) и обработку ошибок.
9. Безопасность: аутентификация, проверка целостности, валидация команд.
10. Итеративно оптимизировать: пакеты, предикция, интерполяция, компрессия.

---

## Высокоуровневая архитектура (рекомендация)

- Сервер — авторитетный (server-authoritative): сервер хранит истинное состояние игры и применяет команды клиентов.
- Клиенты — отправляют input-команды (player actions). Клиенты применяют локальную предикцию отображения для плавности, получают от сервера подтверждение/корректировки.
- Синхронизация — гибрид: командная модель для действий (move/attack/use-item), периодические snapshots/дикты для полных состояний (каждые N тиков).
- Сетевой стек разделён на 3 уровня:
  - `network` — транспорт (socket, reconnect, NAT, ping/latency).
  - `multiplayer` — логика матчей, авторизация, lobby, комнаты.
  - `game` — адаптер, который переводит входные команды в вызовы `Dungeon`/Actor API и наоборот.

---

## Папка `network` — ответственность и API

Цель: обеспечить надёжный, расширяемый и тестируемый транспорт.

Рекомендуемая структура пакета `network` (Java):

- `network.transport` — низкоуровневые реализации
  - `Transport.java` — интерфейс
  - `TcpTransport.java` — TCP implementation (reliable)
  - `UdpTransport.java` — UDP + reliability layer (опционально)
  - `WebSocketTransport.java` — WebSocket (для веб / мобильных прокси)
- `network.serialization` — сериализация/версии
  - `Serializer.java` — интерфейс
  - `JsonSerializer.java` — (для отладки)
  - `ProtobufSerializer.java` — (рекомендуется для production)
- `network.conn` — управление подключениями
  - `Connection.java` — wrapper для socket + metadata (peerId, lastSeen, rtt)
  - `ConnectionManager.java` — список соединений, reconnect logic
- `network/messages` — базовые сетевые сообщения
  - `NetMessage` (base)
  - `Heartbeat` / `Ack` / `Disconnect` / `Error` / `Auth` / `Version` etc.

Основные интерфейсы и методы (подписи):

- Transport.java
  - void start() throws IOException
  - void stop()
  - void send(Connection conn, byte[] payload)
  - void broadcast(Collection<Connection> conns, byte[] payload)
  - void onReceive(Consumer<ReceivedPacket> handler)

- Connection.java
  - String id()
  - InetSocketAddress address()
  - void close()
  - boolean isConnected()
  - long lastSeen()

- Serializer.java
  - byte[] serialize(NetMessage msg)
  - NetMessage deserialize(byte[] payload)

Замечания:
- Транспорт должен поддерживать пинги/RTT, очереди сообщений и базовую QoS (reliable, unreliable).
- Логи низкого уровня (send/recv) должны иметь возможность включаться по флагу для отладки.

---

## Папка `multiplayer` — ответственность и API

Содержит сервера и клиента высокого уровня, матчмейкинг, синхронизацию состояния и replay.

Рекомендуемая структура пакета `multiplayer`:

- `multiplayer.server`
  - `GameServer.java` — главный класс сервера
  - `MatchManager.java` — управление матчами/комнатами
  - `Room.java` / `Lobby.java`
  - `PlayerSession.java` — метаданные игрока на сервере
  - `AuthoritativeGame.java` — держит текущую копию `Dungeon` либо мини-репрезентацию игры
- `multiplayer.client`
  - `GameClient.java` — клиентская логика подключения и синхронизации
  - `ClientStateManager.java` — локальное состояние, предикция, интерполяция
- `multiplayer.sync`
  - `CommandProcessor.java` — серверная валидация и применение команд
  - `SnapshotManager.java` — создание и диффинг snapshot'ов
  - `ReplayRecorder.java` — запись/воспроизведение матчей (для dev/test)
- `multiplayer.protocol`
  - DTOs, message handlers, routing

Ключевые классы и методы (подписи)

- GameServer.java
  - void start(int port)
  - void stop()
  - void onConnection(Connection conn)
  - void onMessage(Connection conn, NetMessage msg)
  - Room createRoom(RoomConfig config)

- AuthoritativeGame.java
  - void tick(float dt)  // вызывается в цикле сервера
  - void applyCommand(PlayerCommand cmd, PlayerSession session)
  - Snapshot createSnapshot()
  - void loadSnapshot(Snapshot s)

- GameClient.java
  - void connect(String host, int port)
  - void disconnect()
  - void sendCommand(PlayerCommand cmd)
  - void onSnapshot(Snapshot s)

- CommandProcessor.java
  - boolean validate(PlayerCommand cmd, ServerGameState state)
  - void execute(PlayerCommand cmd, ServerGameState state)

Замечания:
- `AuthoritativeGame` не обязательно должна держать полноценный объект `Dungeon`, лучше — минимальную сериализуемую репрезентацию мира (StateModel), если full-Dungeon тяжёл для сериализации.
- Однако при интеграции важно: все изменения состояния должны идти через `Dungeon` API (см. AGENTS.md). Серверный state может иметь адаптер, который маппит `Dungeon` ↔ NetworkState.

---

## Папка `game` — интерфейс между движком и мультиплеером

`game` — это слой адаптации: перевод событий из движка в сетевые команды и наоборот.

Рекомендуемая структура:

- `game.netadapter`
  - `NetworkedHeroController.java` — перехват input от игрока и формирование `PlayerCommand`
  - `NetworkedActorAdapter.java` — наблюдатель за `Actor` и сбор diffs
  - `GameStateSerializer.java` — преобразование `Dungeon`/Level/Actor → SnapshotDTO

- `game.sync`
  - `ClientPrediction.java` — локальная предикция для героя
  - `StateReconciler.java` — применение исправлений от сервера

Ключевые пункты интеграции:
- Замени прямые вызовы игровых методов в UI для сетевой партии на объекты `NetworkedHeroController`, которые посылают команды в `multiplayer.client`.
- Не создавайте локальную независимую `Dungeon`-копию — либо использовать глобальный Dungeon, но применять команды последовательно через `Actor` очередь, либо держать server-side мини-модель и на клиенте мапить snapshot → Dungeon.

---

## DTO / протоколы сообщений

Ниже — рекомендуемые DTO и protobuf-описания (пример). Используйте protobuf при production; JSON — для отладки и быстрых тестов.

Пример в Java (POJO) и .proto для основных типов сообщений.

### 1) Общие требования
- Каждое сообщение содержит `protocolVersion` и `messageId`.
- Использовать `tick`/`serverTick` поля для синхронизации и порядка.
- Игроки авторизуются через `AuthToken`/`PlayerId`.

### 2) Виды сообщений
- Handshake: `JoinRequest` / `JoinResponse`
- Input: `PlayerCommand` (move, attack, use)
- Server -> Clients: `CommandAck`, `Snapshot`, `DeltaUpdate`, `EntitySpawn`, `EntityRemove`
- System: `Ping`/`Pong`, `Disconnect`, `Error`
- Chat: `ChatMessage`


### Пример Java POJO (с комментариями)

```java
public class NetMessage {
    public String type;           // Тип сообщения (enum в runtime)
    public int protocolVersion;   // Версия протокола
    public long messageId;        // Уникальный id сообщения (для ack)
}

public class JoinRequest extends NetMessage {
    public String playerName;
    public String authToken; // optional
}

public class JoinResponse extends NetMessage {
    public boolean accepted;
    public String reason; // если rejected
    public String playerId;
    public int serverTick; // sync
}

public class PlayerCommand extends NetMessage {
    public String playerId;
    public long clientSeq; // последовательность команд от клиента
    public int tick; // client tick when command was generated
    public CommandType cmdType; // MOVE, ATTACK, USE
    // payload
    public int targetCellX, targetCellY; // для MOVE/ATTACK
    public String itemId; // для USE
}

public class Snapshot extends NetMessage {
    public int serverTick; // tick на сервере
    public List<EntityState> entities; // полный стейт
}

public class EntityState {
    public String id; // e.g. "hero-1" or mob id
    public EntityType type; // HERO, MOB, ITEM, BLOB
    public int x, y; // позиция (tile)
    public int hp, maxHp;
    public float time; // actor time (для очереди)
    public Map<String, String> extras; // расширяемые поля
}
```


### Пример protobuf (.proto)

```proto
syntax = "proto3";
package spd.network;

message NetHeader {
  uint32 protocolVersion = 1;
  uint64 messageId = 2;
}

message JoinRequest {
  NetHeader header = 1;
  string playerName = 2;
  string authToken = 3;
}

message JoinResponse {
  NetHeader header = 1;
  bool accepted = 2;
  string reason = 3;
  string playerId = 4;
  uint32 serverTick = 5;
}

message PlayerCommand {
  NetHeader header = 1;
  string playerId = 2;
  uint64 clientSeq = 3;
  uint32 tick = 4;
  oneof body {
    MoveCommand move = 11;
    AttackCommand attack = 12;
    UseItemCommand use = 13;
  }
}

message MoveCommand {
  int32 x = 1;
  int32 y = 2;
}

message AttackCommand {
  int32 x = 1;
  int32 y = 2;
}

message UseItemCommand {
  string itemId = 1;
}

message Snapshot {
  NetHeader header = 1;
  uint32 serverTick = 2;
  repeated EntityState entities = 3;
}

message EntityState {
  string id = 1;
  string type = 2;
  int32 x = 3;
  int32 y = 4;
  int32 hp = 5;
  int32 maxHp = 6;
  float time = 7;
  map<string,string> extras = 8;
}
```

---
 
## Документация по текущему проекту (файлы в репозитории)

Ниже — актуальное состояние файлов в репозитории и краткое описание полей/методов. Это отражает реальную реализацию в `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon`.

Пакеты описаны отдельно: `game`, `network`, `multiplayer`.

### game

Содержит модель команд, snapshot и сериализуемое состояние игры (минимальная state-модель). Ключевые подпакеты и классы:

- `game.command`
  - `PlayerCommand` (abstract)
    - поля: `commandId`, `playerId`, `actorEntityId`, `clientSequence`, `baseServerSequence`
    - метод: `String type()` (abstract)
  - `MoveCommand` extends `PlayerCommand`
    - поле: `targetCell` (int)
    - type() = "MOVE"
  - `AttackCommand` extends `PlayerCommand`
    - поля: `targetCell` (int), `targetEntityId` (long)
    - type() = "ATTACK"
  - `WaitCommand` extends `PlayerCommand`
    - type() = "WAIT"

- `game.snapshot`
  - `GameSnapshot`
    - поля: `snapshotId`, `sequence`, `turn`, `GameState state`

- `game.state`
  - `GameState`
    - поля: `gameId`, `seed`, `depth`, `branch`, `globalTurn`, `serverSequence`
    - коллекции: `players: List<PlayerState>`, `heroes: List<HeroState>`, `levels: List<LevelState>`
    - методы поиска: `findPlayer(long)`, `findHero(long)`, `findLevel(int depth, int branch)`
  - Остальные DTO-представления: `HeroState`, `PlayerState`, `LevelState`, `MobState`, `ItemState`, `BuffState`, `HeapState`, `TrapState` — используются в snapshot/state и доступны в `game.state`.

- `game.simulation`
  - `SimulationResult`
    - поля: `accepted` (boolean), `rejectionReason` (String), `StateDelta delta`
    - статические конструкторы: `accepted(StateDelta)`, `rejected(String)`

Эта часть используется серверной симуляцией (`GameSimulation`) для применения `PlayerCommand` и получения `StateDelta`.

### network

Пакет реализует транспорт-нейтральный фасад сервера/клиента и набор DTO + мапперов для перевода между POJO и сетевыми DTO.

Ключевые классы:

- `NetworkManager` — singleton фасад, методы:
  - `startHostFromCurrentDungeon(String hostPlayerName) : GameSnapshotDto`
  - `stopHost()`
  - `isHost()`, `isServerRunning()`, `localPlayerId()`, `server()`, `localClient()`, `currentSnapshot()`

- `GameServer` — transport-neutral авторитетный сервер (in-memory на данный момент)
  - поля: `MultiplayerSession session`, `ServerGameLoop gameLoop`, `boolean running`, `StateDeltaDto lastDelta`
  - методы:
    - `startFromCurrentDungeon(long hostPlayerId, String hostPlayerName) : GameSnapshotDto` — строит `LegacySnapshotBuilder`, создает `MultiplayerSession` и `ServerGameLoop`
    - `stop()`
    - `isRunning()`
    - `join(JoinRequestDto) : JoinResponseDto` — обрабатывает запросы на присоединение (текущая стадия: принимает только если сервер запущен)
    - `handleCommand(PlayerCommandDto) : ServerCommandResult` — маппит DTO -> `PlayerCommand`, вызывает `gameLoop.applyCommand(...)`, формирует `StateDeltaDto` и возвращает результат
    - `currentSnapshot() : GameSnapshotDto` — экспортирует snapshot из `MultiplayerSession`

- `GameClient` — transport-neutral клиент (на текущем этапе поддерживает подключение к локальному GameServer)
  - поля: `ClientStateStore stateStore`, `GameServer localServer`, `boolean connected`, `long playerId`, `GameSnapshotDto snapshot`, `StateDeltaDto lastDelta`
  - методы:
    - `connectLocal(GameServer, String playerName) : JoinResponseDto`
    - `attachHost(GameServer, long playerId, GameSnapshotDto snapshot)`
    - `sendCommand(PlayerCommandDto) : ServerCommandResult` — пересылает команду на `localServer.handleCommand`, применяет `lastDelta` к `stateStore`
    - `disconnect()`, геттеры для состояния

- DTO и mapper'ы (подпакет `network.dto` и `network.mapper`):
  - DTO: `network.dto.command.PlayerCommandDto` (поля: `commandId`, `playerId`, `actorEntityId`, `clientSequence`, `baseServerSequence`, `type`, `targetCell`, `targetEntityId`)
  - `network.dto.snapshot.GameSnapshotDto` (поля: `snapshotId`, `sequence`, `turn`, `GameStateDto state`)
  - `network.dto.delta.StateDeltaDto` (поля: `baseSequence`, `sequence`, `turn`, `EntityDeltaDto[] entityDeltas`, `GameEventDto[] events`)
  - `network.dto.state.*` — `GameStateDto`, `PlayerStateDto`, `HeroStateDto`, `LevelStateDto` и т.д. (массивы/примитивные поля)
  - Mapper'ы: `CommandDtoMapper`, `SnapshotDtoMapper`, `DeltaDtoMapper` — переводят между `game.*` классами и `network.dto.*`.

Примеры мапперов:
- `CommandDtoMapper.fromDto(PlayerCommandDto)` возвращает соответствующий `PlayerCommand` (`MoveCommand`, `AttackCommand`, `WaitCommand`) и копирует `commandId`, `clientSequence`, `baseServerSequence`.
- `DeltaDtoMapper.toDto(StateDelta)` и обратно: маппит `EntityDelta`s и `GameEvent`s.

### multiplayer

Содержит серверные сессии и цикл симуляции.

- `MultiplayerSession` — серверный контейнер с полями:
  - `long sessionId`
  - `GameState gameState`
  - `List<PlayerSession> players`
  - методы: `findPlayer(long)`, `addPlayerFromState(PlayerState)` — добавляет `PlayerSession` скопировав данные из `PlayerState` (playerId, name, host, controlledHeroId, lastClientSequence, lastAcknowledgedServerSequence)

- `PlayerSession` — структура игрока (в `multiplayer` пакете) — содержит `playerId`, `name`, `host` флаг и поля управления (см. `PlayerSession.java` в репозитории).

- `ServerGameLoop` — прокладка к `GameSimulation`
  - конструктор `ServerGameLoop(GameState state)`
  - `SimulationResult applyCommand(PlayerCommand command)` — вызывает `GameSimulation.apply(command)`
  - `GameState state()`

Эти классы реализуют текущую упрощённую модель: команды применяются немедленно в том же потоке, нет сетевого транспорта или фонового тика.

---

## Сериализация / оптимизация

- Protobuf или Kryo (Kryo + Kryonet) — компактно и быстро.
- Компрессия (zlib/gzip) — опционально для больших snapshot'ов.
- Диффы: отправлять только изменившиеся entity (EntityDelta) чаще, и полный Snapshot реже (каждые N тиков).
- Использовать идентификаторы вместо строк в горячих полях (enum ids).

---

## Взаимодействие с `Dungeon` и Actor-системой

Ключевые соображения (в соответствии с `AGENTS.md`):

1. Dungeon — источник истины для game state. Все изменения в состоянии игры должны происходить через `Dungeon` API.
2. Серверу можно хранить минимальную сериализуемую модель `ServerGameState`, которая синхронизируется с `Dungeon` через адаптеры:
   - `DungeonAdapter.toServerState(Dungeon)`
   - `DungeonAdapter.applyServerState(ServerGameState)`
3. Для применения входных команд на сервере используйте `CommandProcessor`, который вызывает соответствующие методы (например `Hero.moveTo(cell)`) и вызывает `hero.spend(time)` — таким образом Actor-очередь обработает побочные эффекты.
4. Для клиента: при получении Snapshot преобразуйте в локальный `ServerGameState` и применяйте через `StateReconciler`.

Примеры методов-адаптеров:

```java
public class DungeonAdapter {
    public static ServerGameState fromDungeon(Dungeon d) { /* собираем EntityState */ }
    public static void applyToDungeon(ServerGameState state) { /* маппим */ }
}
```

Замечание: не сериализуйте всю JVM-объектную графу Dungeon напрямую — это приведёт к тяжёлой и нестабильной сериализации. Лучше — собирать компактную DTO-репрезентацию.

---

## Полный список DTO полей и методов (рекомендовано)

1) PlayerCommand
- Поля: `playerId`, `clientSeq`, `tick`, `cmdType` (enum), payload — `targetX`, `targetY`, `itemId`, `abilityId`.
- Методы: `validate(ServerGameState) : boolean`, `apply(ServerGameState) : CommandResult`.

2) EntityState
- Поля: `id`, `type`, `x`, `y`, `hp`, `maxHp`, `time`, `statusFlags` (bitmask), `level` (для weapon/armor), `ownerId` (для items on ground), `extras`.
- Методы: `merge(EntityDelta)`, `clone()`.

3) Snapshot
- Поля: `serverTick`, `entities[]`, `checksum`, `deltaFromTick` (опция).
- Методы: `diff(Snapshot other) : SnapshotDelta`, `applyDelta(SnapshotDelta)`.

4) SnapshotDelta
- Поля: `added[]`, `updated[]`, `removedIds[]`, `baseTick`.

5) JoinRequest / JoinResponse
- Поля: как в примере.

6) Heartbeat / Ack
- Поля: `messageId`, `serverTick`, `rtt`.

7) ChatMessage
- Поля: `fromPlayerId`, `message`, `time`.

8) CommandResult
- Поля: `success`, `message`, `correction` (если действие отклонено), `serverTick`.

---

## Сервер vs Клиент: валидация и безопасность

- Никогда не доверяйте клиенту: валидация всех входных команд на сервере (достаточно ли энергии/времени, дистанция до цели, cooldowns, ownership предметов).
- Anti-cheat: сервер применяет deterministic rules и не выполняет доверительные проверки от клиента.
- Аутентификация: хотя в локальной сети это опционально, добавьте базовую систему авторизации через `AuthToken` и подпись сообщений (HMAC) при публичном распространении.

---

## Сценарии синхронизации (варианты)

1. Lockstep (deterministic)
   - Подходит для полностью детерминированных игр.
   - Требует одинакового RNG и генерации — уязвимо к дрейфу, сложен для PoC с существующим движком.
2. Server-authoritative + Commands + Snapshot
   - Рекомендуется для SPD. Клиент шлёт команды, сервер применяет и рассылает snapshot/deltas.
3. Peer-to-peer (P2P)
   - Не рекомендуется (сложности безопасности, NAT, authority).

---

## План работ (пошаговый, приоритеты)

Фаза 0 — подготовка (1-2 дня)
- Создать `network` и `multiplayer` модули/пакеты.
- Прописать формат сообщений и .proto файлы.
- Настроить тестовый транспорт (локальные сокеты или loopback).

Фаза 1 — базовая связь и lobby (2-4 дня)
- Реализовать `Transport` и `ConnectionManager`.
- Сделать `JoinRequest/Response` и базовую авторизацию.
- UI: окно подключения и список игроков.

Фаза 2 — командная модель и сервер (4-7 дней)
- Реализовать `PlayerCommand` и `CommandProcessor`.
- Сервер: `AuthoritativeGame` с минимальной state model (hero position, mobs hp).
- Клиент: `GameClient` отправляет команды, принимает Snapshots.
- Тест: локальный матч 1v1 / co-op с симуляцией лагов.

Фаза 3 — полная интеграция с `Dungeon` (7-14 дней)
- Написать `DungeonAdapter` и `GameStateSerializer`.
- Реализовать snapshot/delta для полных уровней/карт.
- Обновить `GameScene` и input handlers на использование `NetworkedHeroController`.

Фаза 4 — оптимизация и фичи (непрерывно)
- Prediction, reconciliation, interpolation.
- Compression, binary packing, dedup.
- Replay & debugging tools.

---

## Практические советы и подводные камни

- Сначала реализуйте минимально воспроизводимую модель: только герой(и), движение, атака и HP. Не пытайтесь сериализовать всё сразу.
- Частые изменения: не отправляйте полный остров каждой милисекунды. Используйте delta + частый small tick для команд.
- Unit tests: эмулируйте сеть (packet loss, reorder, latency) и запускайте сервер и несколько клиентов в одном JVM для интеграционных тестов.
- Логи: выводите human-readable dump snapshot при debug.
- Версионирование: добавьте `protocolVersion` и умеющие мигрировать `.proto` сообщения.

---

## Примеры: небольшой workflow для команды "move"

1. Клиент нажимает на клетку — `NetworkedHeroController` формирует `PlayerCommand{MOVE, targetX, targetY, clientSeq, tick}` и посылает в `GameClient.sendCommand()`.
2. Клиент локально выполняет предиктивное `hero.moveTo()` (визуально) и сохраняет команду в buffer.
3. Сервер получает `PlayerCommand`, `CommandProcessor.validate()` — проверяет дистанцию, время действия и т.п., вызывает `AuthoritativeGame.applyCommand()` (внутри вызывает `Hero.moveTo()` и `hero.spend(cost)`).
4. Сервер собирает `Snapshot` и рассылает его всем игрокам (или только тем, кому нужно).
5. Клиент получает `Snapshot`, `StateReconciler` сравнивает его с локальной предикцией, если расхождение — применяет коррекцию (плавно интерполирует позицию).

---

## Recommended tools / libs

- KryoNet (Java) — прост для prototyping, использует Kryo для сериализации.
- Netty — production-ready transport.
- Protobuf / FlatBuffers — быстрые и компактные сериализации.
- JUnit + embedded network mocks — для тестов.

---

## Check-list для Pull Request

- [ ] Добавлены `.proto` (если использованы) + генерация классов в build.gradle
- [ ] Добавлены юнит/интеграционные тесты (network simulations)
- [ ] Документация: this file + описания public API
- [ ] Performance: snapshot size & tick rate измерены
- [ ] Безопасность: basic auth & validation implemented

---

Если хочешь, могу:
- Сгенерировать начальные скелеты классов в `core/src/main/java/...` (`network`, `multiplayer`, `game/netadapter`).
- Сгенерировать `.proto` файл и примеры POJO/serializers.

Скажи, что сделать дальше: "сгенерировать скелеты классов" или "создать .proto + build.gradle интеграцию" и я это выполню.


