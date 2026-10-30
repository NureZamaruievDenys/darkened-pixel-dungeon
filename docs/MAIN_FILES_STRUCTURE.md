# Структура Главных Файлов Игровой Логики
## Shattered Pixel Dungeon - Альбом Файлов (33.8 KB)

---

## 📋 Главный пакет: `com.shatteredpixel.shatteredpixeldungeon`

### **1. ShatteredPixelDungeon.java** (4 KB)
**Тип:** Entry Point Application | LibGDX Game Subclass

**Отвественность:**
- Инициализация приложения при запуске
- Управление сценами приложения (переходы между экранами)
- Инициализация аудиосистемы
- Обработка выхода из приложения (特собые правила для iOS)

**Ключевые методы:**
```java
public ShatteredPixelDungeon(PlatformSupport platform)
    // Инициализация Game с начальной сценой (WelcomeScene)
    
public void create()
    // вызывается один раз при запуске
    // - updateSystemUI()
    // - SPDAction.loadBindings() - загрузка управления
    // - инициализация звука (Music.INSTANCE, Sample.INSTANCE)
    // - загрузка всех звуковых ассетов
    
public void finish()
    // вызывается при завершении приложения
    // - На iOS переводит на TitleScene (нельзя выходить)
    // - На других платформах вызывает super.finish()
    
public static void switchNoFade(Class<? extends PixelScene> c)
    // Переход на новую сцену БЕЗ затухания
    
public static void seamlessResetScene(SceneChangeCallback callback)
    // Перезагружает текущую сцену (сохраняет состояние окон)
```

**Статические переменные:**
```java
public static Class<? extends PixelScene> sceneClass  // Текущая запланированная сцена
public static final int v3_3_0 = 883                  // Версии для миграции сохранений
```

---

### **2. Dungeon.java** (33 KB) ⭐ ГЛАВНЫЙ КЛАСС
**Тип:** Game State Manager | Singleton static class

**САМЫЙ ВАЖНЫЙ КЛАСС ИГРЫ!** Содержит ВСЕ состояние текущей игры.

**Глобальные игровые переменные:**
```java
public static Hero hero;                    // Главный персонаж
public static Level level;                  // Текущий уровень карты
public static int depth;                    // Глубина подземелья (1-26)
public static int branch;                   // Ветвь маршрута (0=основной путь, 1=квесты)
public static ArrayList<Integer> generatedLevels;  // Какие уровни уже сгенерированы
public static int gold;                     // Количество золота
public static int energy;                   // Мана/энергия волшебства
public static long seed;                    // Сид для генерации (детерминированная случайность)
public static String customSeedText;        // Текстовое представление сида
public static int challenges;               // Битовая маска активных челленджей
public static float mobsToChampion;        // Счётчик врагов до champion спавна
```

**Счётчики предметов (LimitedDrops enum):**
```java
public static enum LimitedDrops {
    // Ограничивает спавн редких предметов
    STRENGTH_POTIONS,    // Зелья силы
    UPGRADE_SCROLLS,     // Свитки прокачки
    ARCANE_STYLI,        // Магические стили
    // ... и ещё 15 типов
}
```

**Ключевые методы:**
```java
public static void init()
    // Инициализация НОВОЙ игры
    // 1. Сброс всех статических переменных
    // 2. Инициализация Random с заданным сидом
    // 3. Инициализация Scroll, Potion, Ring (их типов)
    // 4. Спаун специальных и скрытых комнат
    // 5. Инициализация Generator (система лута)
    // 6. Сброс Statistics, Notes, GamesInProgress
    // 7. Создание Hero и его инициализация по классу
    // 8. Сброс достижений и квестов NPC
    
public static void initSeed()
    // Инициализация сида ОТДЕЛЬНО
    // - Daily mode: сид = день (для ежедневного челленджа)
    // - Custom seed: сид = пользовательский текст
    // - Normal: случайный сид

public static Level newLevel()
    // Создание НОВОГО уровня при переходе
    // 1. Сброс Actor.clear()
    // 2. Выбор класса уровня по depth:
    //    depth 1-2 → SewerLevel
    //    depth 3-4 → PrisonLevel
    //    depth 5-6 → CavesLevel
    //    ... и т.д.
    // 3. Level.create() - генерация карты
    // 4. Hero размещается на уровне
    // 5. Инициализация UI (Toolbar, HealthBar)
    
public static boolean levelHasBeenGenerated(int depth, int branch)
    // Проверка была ли уже сгенерирована этот уровень?
    // (нужно для кэширования и восстановления из сохранения)
    
public static void switchLevel(Class<? extends Level> nextLevel)
    // Переход из текущего уровня в новый
    // 1. Сохранение текущего уровня
    // 2. Линовка depth/branch для нового уровня
    // 3. Загрузка ИЛИ создание нового уровня
    // 4. Сохранение игры
    
public static void saveAll()
    // Сохранение ВСЕГО состояния игры в FileUtils
    // Упаковывает все переменные в Bundle

public static void loadAll()
    // Загрузка состояния игры из сохранения
    
public static int isChallenged(int mask)
    // Проверка включен ли челлендж по маске битов
```

**Структура LimitedDrops (15+ типов):**
```
STRENGTH_POTIONS    - Зелье Силы (максимум 6 за игру)
UPGRADE_SCROLLS     - Свитки прокачки письма
ARCANE_STYLI        - Магические маркеры
ENCH_STONE          - Камни зачаровывания
INT_STONE           - Камни интуиции
TRINKET_CATA        - Кулоны катализаторов
LAB_ROOM            - Лаборатория (комната)
SWARM_HP            - Здоровье от Swarm врагов
NECRO_HP            - Здоровье от Necromancer
BAT_HP              - Здоровье от Bat
... и ещё много
```

---

### **3. Statistics.java** (10 KB)
**Тип:** Game Session Statistics Tracker

**Отвественность:**
- Отслеживание статистики ТЕКУЩЕЙ игры
- Логирование действий игрока для достижений

**Ключевые переменные:**
```java
public static int enemiesSlain;        // Враги убиты
public static int foodEaten;           // Еды съедено
public static int goldCollected;       // Золота собрано
public static int potionsCooked;       // Зелий готово в алхимии
public static int scrollsUsed;         // Свитков использовано
public static int trapsDisarmed;       // Ловушек обезврежено
public static int treasuresFound;      // Сокровищ найдено
public static int wandsObtained;       // Волшебных палочек получено
```

**Методы:**
```java
public static void reset()     // Сброс всей статистики при новой игре
public static void save()      // Сохранение в памяти девайса
public static void load()      // Загрузка из памяти девайса
```

---

### **4. Rankings.java** (18 KB)
**Тип:** High Scores / Game Results Persistence

**Отвественность:**
- Хранение информации о ВСЕХ завершённых играх
- Рейтинг лучших результатов
- Информация об умерших героях

**Ключевые классы:**
```java
public static class Record implements Comparable {
    public int score;           // Финальный счёт
    public String title;        // Название ("Victory", "Slain by...")
    public String desc;         // Описание ("Defeated DwarfKing on level 20")
    public Statistics stats;    // Статистика игры
    public long gameID;         // ID игры для отследивания
    public Date date;           // Когда была сыграна игра
}
```

**Методы:**
```java
public static ArrayList<Record> records()  // Получить все результаты
public static void submit(int score, String title, String desc)
    // Добавить новый результат в список
public static void save()    // Сохранить рейтинги
public static void load()    // Загрузить рейтинги
```

---

### **5. SPDSettings.java** (12 KB)
**Тип:** Player Preferences Persistence

**Отвественность:**
- Сохранение пользовательских настроек между запусками
- Неверсионные данные (не сохраняются между версиями)

**Ключевые настройки:**
```java
public static boolean music()           // Включена ли музыка?
public static float musicVol()          // Громкость музыки (0-100)
public static boolean soundFx()         // Включены ли звуковые эффекты?
public static float SFXVol()           // Громкость SFX (0-100)
public static boolean fullscreen()      // Полный экран?
public static String controlScheme()    // Схема управления
public static String lastDaily()        // Последний ежедневный челлендж
public static String customSeed()       // Сохранённый пользовательский сид
public static int brightness()          // Яркость экрана
public static boolean screenShake()     // Тряска экрана?
public static int language()            // Выбранный язык
```

**Методы:**
```java
public static void set(String key, Object value)  // Установить значение
public static String getString(String key)         // Получить строку
public static float getFloat(String key)          // Получить float
public static void addListener(SettingsListener)  // Подписка на изменения
```

---

### **6. Badges.java** (42 KB)
**Тип:** Achievement System

**Отвественность:**
- Система достижений
- Отслеживание разблокированных медалей

**Ключевые достижения (enum Badge):**
```java
// Прохождение
VICTORY,              // Победа
// Враги
MASTERY(enemy type),  // Убить 10 врагов типа X
// Предметы
COLLECTOR,            // Собрать все типы предметов
// Боевые
DUEL,                // Победить босса без бафов
// Экспедиция
SPEEDRUN,            // Финиш быстро
```

**Методы:**
```java
public static void unlock(Badge badge)  // Разблокировать достижение
public static boolean isUnlocked(Badge badge)  // Проверить разблокировано ли
public static void save()        // Сохранить достижения
public static void load()        // Загрузить достижения
```

---

### **7. Assets.java** (18 KB)
**Тип:** Asset Registry / Resource Manager

**Отвественность:**
- Централизованный реестр всех графических и аудио ресурсов
- Пути к файлам в папке assets

**Структура:**
```java
public class Images {           // Графические ресурсы
    public static final String HEROES = "heroes.png";
    public static final String MOBS = "mobs.png";
    public static final String ITEMS = "items.png";
    // ... и многое другое
}

public class Sounds {           // Звуковые эффекты
    public static final String CLICK = "sounds/click.mp3";
    public static final String ATTACK = "sounds/attack.mp3";
    public static final String HIT = "sounds/hit.mp3";
    public static final String OPEN = "sounds/open.mp3";
    public static final String CLOSE = "sounds/close.mp3";
    // ... и многое другое
}

public class Music {            // Музыка
    public static final String THEME = "music/theme.mp3";
    public static final String SEWERS_1 = "music/sewers_1.mp3";
    public static final String PRISON_1 = "music/prison_1.mp3";
    // ... для каждого уровня
}
```

---

### **8. Chrome.java** (2.3 KB)
**Тип:** UI Theme System

**Отвественность:**
- Цвета и стили для UI элементов
- Консистентный внешний вид интерфейса

**Ключевые элементы styling:**
```java
public static int DEFAULT_COLOR;      // Цвет по умолчанию
public static int HIGHLIGHTED_COLOR;  // Подсвеченный цвет
public static int DISABLED_COLOR;     // Отключённый цвет
public static int NEGATIVE_COLOR;     // Плохие эффекты
public static int POSITIVE_COLOR;     // Хорошие эффекты
public static int WARNING_COLOR;      // Предупреждение/опасность
```

---

### **9. Challenges.java** (2.3 KB)
**Тип:** Difficulty Modifiers Enum

**Отвественность:**
- Определение доступных челленджей (усложнений игры)

**Доступные челленджи:**
```java
public static final int CURSED    = 1;   // Все предметы проклюты
public static final int DARKNESS  = 2;   // Без света
public static final int FORBIDDEN = 4;   // Нет определённого типа оружия
public static final int HARDER    = 8;   // Враги сильнее
public static final int LONELY    = 16;  // Нет союзников
public static final int BARREN    = 32;  // Меньше предметов
```

---

### **10. GamesInProgress.java** (5.9 KB)
**Тип:** Save Game Management

**Отвественность:**
- Управление текущими незавершёнными играми в прогрессе
- Список героев в текущих сохранениях

**Ключевая структура:**
```java
public static class Info {
    public HeroClass heroClass;   // Класс героя
    public int depth;             // На каком уровне
    public boolean challenges;    // Челленджи активны?
    public long lastPlayed;       // Когда был открыт последний раз
}

public static var Info gameInfo[]  // Массив текущих игр (до 3 слотов сохранения)
```

---

### **11. Bones.java** (7.1 KB)
**Тип:** Dead Hero Remains System

**Отвественность:**
- Система "костей" - остатков мёртвых героев
- При смерти герой оставляет скелет с предметами для других игроков

**Структура:**
```java
public class Bones implements Bundlable {
    public int level;           // На каком уровне умер герой
    public String heroClass;    // Какой класс был
    public ArrayList<Item> items;  // Какие предметы оставил
    public int depth;           // Глубина смерти
}
```

---

### **12. QuickSlot.java** (4.2 KB)
**Тип:** Quick Access Slot System

**Отвественность:**
- Система быстрых слотов для избранных предметов
- Позволяет быстро использовать частые предметы (зелья, свитки)

**Структура:**
```java
public static int[] slots;    // ID предметов в быстрых слотах
    // slots[0-3] - основные быстрые слоты
    // slots[4-7] - резервные слоты

public static void assignSlot(int slot, Item item)   // Назначить предмет слоту
public static Item getItem(int slot)                 // Получить предмет из слота
```

---

### **13. SPDAction.java** (18 KB)
**Тип:** Input / Control Mapping System

**Отвественность:**
- Система управления входом (клавиши, кнопки, сенсор)
- Маппинг действий к кнопкам управления

**Основные действия (enum Action):**
```java
MOVE, ATTACK, USE, THROW
QUICK_SLOT_1, QUICK_SLOT_2, QUICK_SLOT_3, QUICK_SLOT_4
INVENTORY, JOURNAL, OPTIONS
```

**Методы:**
```java
public static void bindKey(int keyCode, Action action)  // Привязать кнопку
public static void bindTouch(int action, Zone zone)     // Добавить сенсор зону
public static Action getAction(int input)               // Получить действие по входу
```

---

## 📦 Вспомогательные классы

### **14. Challenges.java** ✓
(уже описано выше)

### **15. Chrome.java** ✓
(уже описано выше)

### **16. GamesInProgress.java** ✓
(уже описано выше)

---

## 🎮 ВЗАИМОДЕЙСТВИЕ ФАЙЛОВ

### **Инициализация игры:**
```
1. ShatteredPixelDungeon.create()
   └─→ SPDAction.loadBindings()
   └─→ Assets инициализируются
   └─→ SPDSettings загружаются
   └─→ первая сцена = WelcomeScene

2. WelcomeScene → TitleScene

3. TitleScene [New Game] → HeroSelectScene
   └─→ GamesInProgress.selectedClass = выбранный класс

4. HeroSelectScene → InterlevelScene
   └─→ Dungeon.init()      ← инициализирует все статические переменные
   └─→ Dungeon.newLevel()  ← создаёт первый уровень
   └─→ Statistics.reset()
   └─→ Badges.load()
   └─→ Rankings.load()

5. InterlevelScene → GameScene
   └─→ GameScene.create() отрисовывает уровень
```

### **Сохранение/Загрузка:**
```
Автосохранение при переходе уровня:
1. GameScene.update() обнаружает переход уровня
2. Dungeon.saveAll() упаковывает в Bundle:
   - hero
   - level
   - depth, branch
   - chapters, gold, energy
   - все предметы (LimitedDrops)
3. Уровень сохраняется в FileUtils
4. Statistics.save()
5. Переход на новый уровень

При загрузке сохранения:
1. GamesInProgress показывает список сохранений
2. Dungeon.load() распаковывает Bundle
3. Level.load() восстанавливает карту
4. Hero.load() восстанавливает персонажа
5. GameScene.create() инициализирует отрисовку
```

---

## 🔄 ГЛАВНЫЙ ЦИКЛ ОБНОВЛЕНИЯ

```java
// Каждый frame в GameScene.update():

1️⃣ Обработка USER INPUT
   └─→ SPDAction.processInput()
   └─→ если клик на клетку → hero.move() или hero.attack()
   └─→ hero.spend(time) → потратить время действия

2️⃣ ACTOR QUEUE PROCESSING
   while (Actor.hasPending()) {
       Actor next = Actor.next();   // Получить с наименьшей time
       if (next.act()) {            // Выполнить действие
           // next.spend() был вызван в act()
       }
   }
   
   ▶ Порядок обработки (по actPriority):
     1. VFX_PRIO        (100)   - эффекты
     2. HERO_PRIO       (0)     - герой
     3. BLOB_PRIO       (-10)   - газы/эффекты
     4. MOB_PRIO        (-20)   - враги
     5. BUFF_PRIO       (-30)   - баффы

3️⃣ Проверка УСЛОВИЙ КОНЦА:
   ├─ hero.isDead()         → GameOverScene
   ├─ hero.finishGame()     → AmuletScene (победа)
   └─ hero.levelChanged     → InterlevelScene

4️⃣ ОТРИСОВКА кадра (render):
   ├─ Dungeon.level.draw()
   ├─ hero.sprite.draw()
   ├─ mob.sprite.draw()
   ├─ effect.draw()
   └─ UI компоненты
```

---

## 📊 Диаграмма зависимостей

```
ShatteredPixelDungeon (Game point)
         ↓
      PixelScene (базовая сцена)
         ↓
    ┌─────────────┐
    │ GameScene   │
    └──────┬──────┘
           ├── Dungeon (static state manager)
           │   ├─→ Hero
           │   ├─→ Level
           │   ├─→ Actor (очередь действий)
           │   │   ├─→ Hero.act()
           │   │   ├─→ Mob.act()
           │   │   └─→ Blob.act()
           │   │
           │   ├─→ Statistics
           │   ├─→ SPDSettings
           │   └─→ Dungeon.seed
           │
           ├── UI Components
           │   ├─→ Toolbar
           │   ├─→ HealthBar
           │   ├─→ BuffIndicator
           │   └─→ Other UI
           │
           └── Input Handler
               └─→ SPDAction
```

---

## 💾 Сохранение/Загрузка архитектуры

**Bundlable Pattern:**
```java
interface Bundlable {
    void storeInBundle(Bundle bundle);     // Сохранить в Bundle
    void restoreFromBundle(Bundle bundle); // Загрузить из Bundle
}

// Bundle = HashMap<String, Object>
// Все saveстемные классы реализуют Bundlable
```

**Что сохраняется:**
```
Dungeon.saveAll() упаковывает:
├── Hero (с инвентарём и баффами)
├── Level (карта, враги, предметы, ловушки)
├── depth, branch, gold, energy
├──章章 (chapters - квестовые данные)
├── LimitedDrops (счётчики предметов)
├── droppedItems (предметы между уровнями)
└── NPC квесты (Ghost, Wandmaker и т.д.)

+ отдельно сохраняются:
├── Statistics.save()
├── SPDSettings (в SharedPreferences)
├── Rankings.save()
├── GamesInProgress.save()
└── Badges.save()
```

---

## 🎯 Выводы

Все 13 главных файлов работают вместе для управления:
1. **ShatteredPixelDungeon** - Инициализация приложения
2. **Dungeon** - Состояние игры (ГЛАВНЫЙ КЛАСС)
3. **Statistics** - Статистика текущей игры
4. **Rankings** - История лучших результатов
5. **SPDSettings** - Пользовательские настройки
6. **Badges** - Система достижений
7. **Assets** - Реестр ресурсов
8. **Chrome** - UI стили
9. **Challenges** - Усложнения
10. **GamesInProgress** - Управление сохранениями
11. **Bones** - Остатки мёртвых героев
12. **QuickSlot** - Быстрые слоты предметов
13. **SPDAction** - Система управления

**Центральная идея:** Dungeon.java - это статический синглтон, который хранит ВСЕ состояние текущей игры и управляет взаимодействием между Actor (очередь действий), Items (система предметов) и Level (карта подземелья).

---

## 📂 Подпакеты: `com.shatteredpixel.shatteredpixeldungeon.*`

В проекте основная логика распределена по подпакетам для читаемости и модульности. Ниже — краткое описание назначения и структуры ключевых подпакетов (где искать код и зачем он нужен).

1) `actors/` — поведение сущностей
   - Назначение: реализации всех акторов (базовый `Actor`, `Hero`, `Mob`), их состояния и логики действий.
   - Типичные файлы: `Actor.java`, `Hero.java`, `Mob.java`, конкретные монстры (`Rat.java`, `Dwarf.java`).

2) `items/` — предметы и их система
   - Назначение: модели предметов, их эффекты, генерация и хранение (инвентарь, мешки, стаки).
   - Типичные файлы: `Item.java`, `Weapon.java`, `Armor.java`, `Potion.java`, `Scroll.java`, `Generator.java`, `Heap.java`.

3) `levels/` — генерация и логика уровней
   - Назначение: классы генераторов карт, описание комнат, спавн мобов и переходы между уровнями.
   - Типичные файлы: `Level.java`, `SewerLevel.java`, `PrisonLevel.java`, `Room.java`, `LevelGenerator.java`.

4) `scenes/` — экраны/сцены (PixelScene-наследники)
   - Назначение: экранные сцены приложения (Welcome, Title, GameScene, HeroSelect, Interlevel и т.д.).
   - Типичные файлы: `PixelScene.java`, `GameScene.java`, `TitleScene.java`, `InterlevelScene.java`.

5) `ui/` — визуальные компоненты интерфейса
   - Назначение: повторно используемые UI-элементы — панели, иконки, тулбары, индикаторы.
   - Типичные файлы: `Toolbar.java`, `HealthBar.java`, `BuffIndicator.java`, `Icons.java`.

6) `windows/` — диалоговые окна
   - Назначение: все модальные и немодальные окна (инвентарь, журнал, настройки и прочие диалоги).
   - Типичные файлы: `WndInventory.java`, `WndJournal.java`, `WndOptions.java`.

7) `effects/` и `sprites/` — VFX и работа со спрайтами
   - Назначение: частицы, визуальные эффекты, анимации и управление текстурами/атласами.
   - Типичные файлы: `Fx.java`, `Particle.java`, `Sprite.java`, `Atlas.java`.

8) `tiles/` — тайловая система
   - Назначение: определения тайлов, их свойства (проходимость, освещаемость) и рендеринг карты на основе тайлов.
   - Типичные файлы: `Tile.java`, `Terrain.java`, `TileMapRenderer.java`.

9) `mechanics/` — игровые правила и расчёты
   - Назначение: логика боёв, баффов/дебаффов, расчёты урона и взаимодействие эффектов (например, система `Blob`).
   - Типичные файлы: `Buff.java`, `Debuff.java`, `Combat.java`, `Blob.java`.

10) `journal/` и `messages/` — заметки и сообщения
   - Назначение: хранение записей журнала/квестов и централизованное форматирование игровых сообщений/локализация.
   - Типичные файлы: `Journal.java`, `Entry.java`, `Messages.java`.

11) `plants/` — растения и семена
   - Назначение: игровые растения (семена/растения) и их поведение на уровне (сбор урожаев, эффекты).

12) `services/` и `utils/` — платформенные сервисы и утилиты
   - `services/`: адаптеры для платформенной функциональности (звук, файловая система, обновления).
   - `utils/`: общие утилиты — `Bundle`, `FileUtils`, `Random`, `PathFinder`, логирование, сериализация.

Где что искать — краткая шпаргалка:
- Поведение врагов и акторов — `actors/`.
- Предметы и генерация лута — `items/`.
- Логика карт и генерация уровней — `levels/`.
- Всё, что рисует и управляет экранами — `scenes/`, `ui/`, `windows/`.
- Визуальные эффекты — `effects/` и `sprites/`.
- Общая механика и расчёты — `mechanics/`.

Такая модульная организация упрощает поддержку: изменения в одной подсистеме обычно не затрагивают остальные (например, переписывая генератор уровней вы редко меняете UI или модель предметов).

---

## 🔍 Детализация по подпапкам (файлы, структура, назначение)

Ниже — расширенная, практически полная детализация по ключевым подпакетам в `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon`. Для каждой папки приведён набор основных файлов/подпапок и краткое объяснение их назначения.

1) `actors/` — поведение сущностей (Actor, Hero, Mob)
   - Основные файлы в корне: `Actor.java`, `Char.java`.
   - Подпапки:
     - `blobs/` — определения типов газа/эффектов на тайле (например `Fire.java`, `ToxicGas.java`, `Regrowth.java`, `Web.java`). Реализует систему зон-эффектов, распространяющихся по карте.
     - `buffs/` — набор баффов/дебаффов (`Buff.java` и десятки реализаций: `Poison.java`, `Haste.java`, `Regeneration.java`, `Invisibility.java` и т.д.).
     - `hero/` — `Hero.java`, `Belongings.java`, `HeroClass.java`, `HeroSubClass.java`, способности и подпапки `abilities/` и `spells/` (которые содержат классы типа `ArmorAbility.java`, `DeathMark.java`, `AuraOfProtection.java` и т.д.).
     - `mobs/` — `Mob.java` и реализации монстров (полный список в проекте: `Rat.java`, `Bat.java`, `DwarfKing.java`, `Necromancer.java`, `YogDzewa.java` и т.д.), а также поддиректория `npcs/` (торговцы/спутники: `Shopkeeper.java`, `Wandmaker.java`, `Ghost.java`).

   - Назначение: вся игровая логика для активных сущностей — их очередь действий, обработка act(), состояния, баффы/дебаффы, специальные поведения и ИИ.

2) `effects/` — визуальные эффекты
   - Файлы: `Effects.java`, `Fireball.java`, `Lightning.java`, `SpellSprite.java`, `FloatingText.java` и др.
   - Подпапка `particles/` — примитивы частиц (`BlastParticle.java`, `SmokeParticle.java`, `SparkParticle.java` и др.).
   - Назначение: VFX-план для отображения атак, частиц, анимированных эффектов и их эмиттеров (CellEmitter, BlobEmitter и т.д.).

3) `items/` — предметы и связанные подсистемы
   - Корневые файлы: `Item.java`, `Generator.java`, `Heap.java`, `Gold.java`, `ItemStatusHandler.java`, `EquipableItem.java`.
   - Подпапки и их назначение:
     - `armor/` — разновидности брони (например `PlateArmor.java`, `LeatherArmor.java`) и вспомогательные объекты (`curses/`, `glyphs/`).
     - `artifacts/` — уникальные артефакты (`Artifact.java`, `HornOfPlenty.java`, `TimekeepersHourglass.java`).
     - `bags/` — сумки/холстеры (`Bag.java`, `PotionBandolier.java`).
     - `bombs/`, `food/`, `potions/` (и внутри `potions/` подпапки `brews/`, `elixirs/`), `scrolls/` (есть `exotic/`), `wands/`, `rings/`, `stones/`, `trinkets/`, `weapon/` (и внутри `melee/`, `missiles/`).
     - `journal/`, `keys/`, `quest/`, `remains/` — предметы, связанные с квестами, ключами и останками.

   - Назначение: предметная модель (создание, эффекты при использовании, механика экипировки, стеков, дропа на карте). Генератор `Generator.java` отвечает за лут/спаун предметов.

4) `levels/` — генерация и объекты уровня
   - Основные файлы: `Level.java`, `Terrain.java`, `RegularLevel.java`, конкретные уровни (`SewerLevel.java`, `PrisonLevel.java`, `CavesLevel.java`, `LastLevel.java`, `VaultLevel.java`).
   - Подпапки:
     - `builders/` — шаблоны построения (GridBuilder, RegularBuilder, BranchesBuilder и т.д.).
     - `painters/` — «рисовальщики» для размещения декора/структур (CavesPainter, PrisonPainter...).
     - `rooms/` — множество типов комнат: `standard/`, `quest/`, `secret/`, `special/` и т.д.; внутри — конкретные реализации (`LibraryRingRoom.java`, `SecretGardenRoom.java`, `VaultEntranceRoom.java`).
     - `features/` — дополнительные объекты уровня (Chasm, Door, HighGrass), `traps/` — разные ловушки (`PitfallTrap.java`, `PoisonDartTrap.java`, `ExplosiveTrap.java`).

   - Назначение: весь код, который строит карту уровня, размещает комнаты, ловушки, спавнит мобов и предметы; также хранит состояние тайлов и переход между уровнями.

5) `scenes/` — экраны игры
   - Файлы: `PixelScene.java` (базовый), `GameScene.java`, `TitleScene.java`, `WelcomeScene.java`, `HeroSelectScene.java`, `InterlevelScene.java`, `JournalScene.java`, `RankingsScene.java` и др.
   - Назначение: логика экранов (менеджмент сцен, input routing на уровне сцен, инициализация UI-компонентов для этих экранов).

6) `ui/` — UI-компоненты
   - Примеры файлов: `Toolbar.java`, `HealthBar.java`, `BuffIndicator.java`, `InventoryPane.java`, `ItemSlot.java`, `IconButton.java`, `Tooltip.java`, `Window.java`.
   - Назначение: атомарные визуальные элементы и обёртки (панели, кнопки, индикаторы), которые используются сценами и окнами.

7) `windows/` — готовые окна/диалоги
   - Большой набор окон: `WndInventory.java`, `WndJournal.java`, `WndOptions.java`, `WndHero.java`, `WndBlacksmith.java`, `WndWandmaker.java`, `WndTradeItem.java`, `WndVictoryCongrats.java` и т.д.
   - Назначение: составные UI-окна, собирающие компоненты из `ui/` и реализующие пользовательский поток (торговля, настройки, просмотр профессий, выбор подкласса и т.д.).

8) `messages/` и локализация
   - Файлы: `Messages.java`, `Languages.java` — централизованная система для строк локализации и ключей сообщений.

9) `plants/` — растения/семена
   - Файлы: `Plant.java` и реализации (`BlandfruitBush.java`, `Mageroyal.java`, `Firebloom.java`, `Sungrass.java` и др.).
   - Назначение: поведение растений на карте — сбор, эффекты, влияние на игрока/ящики.

10) `services/` и `updates/news`
   - `services/news/News.java` — новости; `services/updates/Updates.java` — обновления/апдейты. Эти классы инкапсулируют получение/форматирование удалённого контента и могут иметь платформенные адаптеры.

11) `sprites/` — спрайты и оболочки для отрисовки мобов/предметов
   - Множество `*Sprite.java` для каждого моба/героя/предмета (`HeroSprite.java`, `ItemSprite.java`, `RatSprite.java`, `WandmakerSprite.java` и т.д.).
   - Назначение: логика по загрузке текстур, анимациям и отображению спрайтов на сцене.

12) `tiles/` — тайловая отрисовка и карта видимости
   - Классы: `DungeonTilemap.java`, `DungeonTileSheet.java`, `FogOfWar.java`, `GridTileMap.java`, `RaisedTerrainTilemap.java` и др.
   - Назначение: слой рендеринга тайлов, маски освещения и кеши тайлов.

13) `mechanics/` — вспомогательные механики
   - Файлы: `Ballistica.java` (лучи/проверка попадания), `ConeAOE.java`, `ShadowCaster.java` и т.п.
   - Назначение: общие алгоритмы (линия огня, расчёт областного воздействия, трассировка), используемые в боях, заклинаниях и эффектах.

14) `utils/` — утилиты и логирование
   - Примеры: `DungeonSeed.java`, `GLog.java`, `Holiday.java`.
   - Назначение: вспомогательные классы для логирования, хранения сида, мелкие утилиты.


## 📐 Диаграмма модулей (упрощённая ASCII)

Dungeon (game state)
  ├─ levels/ (Level, builders, rooms, traps)
  ├─ actors/ (Hero, Mob, Buffs, Blobs)
  ├─ items/ (Item, Generator, artifacts, weapons)
  ├─ mechanics/ (Ballistica, ConeAOE)
  └─ utils/ (Bundle, FileUtils, Random)

UI layer
  scenes/ ---> ui/ ---> windows/
     ↑            ↑        ↑
     └────────────┴────────┘
               │
               └─ uses data from Dungeon (levels, actors, items)

Rendering layer
  sprites/ (Sprite classes)
  effects/ (VFX, particles)
  tiles/ (Tilemaps, FogOfWar)

Services
  services/ (news, updates)  — platform/remote integrations

Примечание: стрелки показывают зависимости по использованию: сцены/окна используют данные из `Dungeon` (уровни/акторы/предметы), при этом UI вызывает механики (например, активация заклинания вызывает `mechanics/` + `effects/` + `sprites/`).

---

Если хотите, могу:
- сгенерировать полную оглавление-перечень файлов (flat list) по каждой подпапке в виде таблицы;
- добавить UML-диаграмму классов для `Dungeon` ↔ `Level` ↔ `Actor` ↔ `Item` (в виде ASCII или PlantUML);
- детализировать один из подпакетов (например `items/` или `actors/`) — перечислить ключевые методы и взаимосвязи.

