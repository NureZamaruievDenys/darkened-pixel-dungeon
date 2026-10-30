# Архитектура Shattered Pixel Dungeon (Silver)

## Обзор проекта
**Shattered Pixel Dungeon** — это открытый roguelike dungeon crawler на Java с поддержкой Android, iOS и Desktop платформ. Проект использует фреймворк **LibGDX (версия 1.14.0)** для кросс-платформной разработки.

**Версия:** 3.3.8 | **Java Compatibility:** 11+ | **Gradle:** 9.1.0

---

## 📁 Структура папок проекта

```
shattered-pixel-dungeon-silver/
├── core/                          # Основной исходный код игры (платформо-независимый)
│   ├── src/main/java/com/shatteredpixel/shatteredpixeldungeon/
│   │   ├── *.java                # Главные классы (см. раздел "Главные классы")
│   │   ├── actors/               # Система действующих персонажей
│   │   ├── items/                # Система предметов
│   │   ├── levels/               # Система уровней/подземелья
│   │   ├── scenes/               # UI сцены (экраны)
│   │   ├── ui/                   # UI компоненты
│   │   ├── sprites/              # Спрайты и визуализация
│   │   ├── effects/              # Визуальные эффекты
│   │   ├── mechanics/            # Игровая механика (баллистика, воздействие)
│   │   ├── journal/              # Навигация по интерфейсу
│   │   ├── messages/             # Локализация текста
│   │   ├── plants/               # Система растений
│   │   ├── tiles/                # Система плиток
│   │   └── utils/                # Утилиты (сид, случайность и т.д.)
│   └── build/
│
├── android/                       # Платформо-специфичный код Android
├── desktop/                       # Платформо-специфичный код Desktop
├── ios/                           # Платформо-специфичный код iOS
├── services/                      # Многоуровневые системы (новости, обновления)
└── SPD-classes/                   # Дополнительные классы проекта

```

---

## 🎮 Главные классы в корневом пакете

### **1. ShatteredPixelDungeon.java** - Точка входа приложения
- **Наследует:** `Game` (LibGDX)
- **Ответственность:** Инициализация приложения, управление сценами, аудио
- **Методы:**
  - `create()` - Инициализация при запуске (загрузка звуков, настроек)
  - `switchNoFade()` - Переход между сценами без затухания
  - `finish()` - Завершение приложения (особая логика для iOS)

### **2. Dungeon.java** - Главный контроллер игры
Это статический класс, который содержит всё состояние текущей игры (Singleton паттерн)

**Ключевые статические переменные:**
```java
public static Hero hero;                    // Главный персонаж
public static Level level;                  // Текущий уровень
public static int depth;                    // Глубина подземелья (1-26)
public static int branch;                   // Ветвь маршрута (0=основной, 1=квесты)
public static ArrayList<Integer> generatedLevels;  // Уже сгенерированные уровни
public static int gold;                     // Золото
public static int energy;                   // Энергия волшебства
public static long seed;                    // Сид для генерации
public static enum LimitedDrops { ... }     // Счётчик ограниченных предметов
```

**Ключевые методы:**
- `init()` - Инициализация новой игры
- `newLevel()` - Создание нового уровня
- `initSeed()` - Инициализация сида (ежедневный/пользовательский)
- `isChallenged()` - Проверка активных челленджей

### **3. SPDSettings.java** - Система настроек игрока
Управляет сохранением/загрузкой пользовательских настроек
- Громкость музыки/звуков
- Графические опции
- Элементы управления
- Пользовательский сод

### **4. Statistics.java** - Статистика игры
Отслеживает статистику игрока за текущий/исторический плей:
- Количество уничтоженных врагов
- Собранные предметы
- Преодолённые опасности

### **5. Rankings.java** - Рейтинги побед
Хранит список лучших побед, статистику, время игры

### **6. Badges.java** - Система достижений
Достижения и медали за достижения

### **7. Assets.java** - Управление ресурсами
Централизованный реестр всех графических/аудио ресурсов

### **8. Chrome.java** - UI tema и стили
Графические стили для UI компонентов

### **9. GamesInProgress.java** - Управление сохраниями
Хранит список текущих игр в прогрессе

---

## 🎯 ИГРОВОЙ ЦИКЛ

### Turn-Based Game Loop (Ход-ориентированная система)

```
┌─────────────────────────────────────────────────────────┐
│                   ГЛАВНЫЙ ЦИКЛ ИГРЫ                     │
├─────────────────────────────────────────────────────────┤
│ 1. Ввод игрока (клик, действие)                        │
│    ↓                                                     │
│ 2. Выполнение действия героя                            │
│    • Движение (spend time)                             │
│    • Атака (spend time)                                │
│    • Использование предмета                            │
│    ↓                                                     │
│ 3. Обработка턴 системы Actor (очередь действий)         │
│    while (hasActions) {                               │
│      Actor с наименьшей time среди всех → act()       │
│      spendTime() → увеличивает его time                │
│    }                                                    │
│    ↓                                                     │
│ 4. Действия врагов (Mobs) - обработка AI               │
│    ↓                                                     │
│ 5. Действия эффектов (Blobs, Buffs)                    │
│    ↓                                                     │
│ 6. Проверка условий победы/поражения                   │
│    ↓                                                     │
│ 7. Обновление UI (визуализация)                        │
│    ↓                                                     │
│ [Ждёт следующего ввода игрока]                         │
└─────────────────────────────────────────────────────────┘
```

### Система действий (Actor Priority System)

Все действующие объекты наследуют **Actor** и имеют **time** (внутренние часы действия).

**Приоритет действия (actPriority):**
```java
VFX_PRIO  = 100   // Визуальные эффекты (первые)
HERO_PRIO = 0     // Герой (позитив = перед врагами)
BLOB_PRIO = -10   // Кровь/газы/эффекты поля
MOB_PRIO  = -20   // Враги
BUFF_PRIO = -30   // Баффы/дебаффы (последние)
```

**Система времени:**
```java
spendConstant(float time)   // Тратит точное время
spend(float time)           // Может быть изменено бафами (Haste, Slow)
spendToWhole()             // Округляет до целого числа
```

---

## 📦 Основные подсистемы

### **1. ACTORS (Действующие объекты)** 
**Файл:** `actors/Actor.java` - Базовый класс для ВСЕХ действующих объектов

**Иерархия:**
```
Actor (абстрактный)
├── Char (персонажи с HP, броней)
│   ├── Hero (главный персонаж, управляется игроком)
│   │   ├── Способности (Abilities)
│   │   ├── Заклинания (Spells)
│   │   ├── Таланты (Talents)
│   │   └── Класс персонажа (HeroClass)
│   │
│   └── Mob (враги, NPC) - управляются AI
│       ├── Враги разных типов (Rat, Bat, Brute и т.д.)
│       ├── NPC (Blacksmith, Imp, Wandmaker, Ghost)
│       └── Босс враги (DwarfKing, Tengu, YogDzewa и т.д.)
│
├── Blob (область эффектов, газы, огонь)
│   ├── Smoke, Web, Electricity
│   └── ToxicGas, WellWater
│
└── Другие эффекты
    ├── Emitter (эмиттер частиц)
    └── VisualEffect (визуальный эффект)
```

**Ключевые методы:**
- `act()` - Выполнить действие этого актёра (абстрактный)
- `spend(time)` - Потратить время
- `isAlive()` - Жив ли актер

### **2. BUFFS (Баффы/Дебаффы)** 
**Папка:** `actors/buffs/` - Система эффектов на персонажи

Баффы прикрепляются к Char через `buff.attachTo(char)`

**Типы:**
- **Положительные:** Haste, Speed, Invulnerability, Regeneration
- **Отрицательные:** Poison, Paralysis, Weaknes, Burning, Chill
- **Нейтральные:** Awareness, MindVision, Hunger

Каждый Buff имеет:
- `act()` - Действия баффа каждый ход
- `detach()` - Удаление с персонажа
- `prolonged = true/false` - Продлевается ли при отключении эффектов

### **3. ITEMS (Система предметов)**
**Файл:** `items/Item.java` - Базовый класс для всех предметов

**Логика:**
- Предметы могут быть в инвентаре, выброшены на полу (Heap) или экипированы
- Управление стекованием (stackable) и уровнем улучшения (level)
- Система curse (проклятие)

**Иерархия предметов:**
```
Item
├── EquipableItem
│   ├── Weapon (оружие)
│   │   ├── MeleeWeapon (мечи, булавы)
│   │   └── MissileWeapon (мечи, кинжалы, лучи)
│   │
│   ├── Armor (броня)
│   │   └── Glyphs (руны для брони)
│   │
│   ├── Artifact (артефакты)
│   │   ├── DriedRose
│   │   ├── MasterThievesArmband
│   │   └── TimekeepersHourglass
│   │
│   ├── Ring (кольца)
│   │   ├── RingOfWealth
│   │   └── RingofProtection
│   │
│   └── Trinket (кулоны)
│
├── Consumable (расходуемые)
│   ├── Scroll (свитки)
│   ├── Potion (зелья)
│   ├── Bomb (бомбы)
│   └── Spell (заклинания)
│
├── Storage
│   └── Bag (сумки для инвентаря)
│
└── Misc (разное)
    ├── Gold
    ├── Keys
    └── Quest Items
```

**Ключевые методы Item:**
- `doAction()` - Основное действие при использовании
- `use()` - Использование предмета
- `drop()` - Выброс предмета
- `actions()` - Список доступных действий
- `targetingParams()` - Параметры если нужно нацеливание

### **4. LEVELS (Система уровней)**
**Файл:** `levels/Level.java` - Базовый класс уровня

**Карта уровней:**
```
Уровень 1-2    : SewerLevel     (Сточные каналы)
Уровень 3-4    : PrisonLevel    (Тюрьма)
Уровень 5-6    : CavesLevel     (Пещеры)
Уровень 7-8    : CityLevel      (Город)
Уровень 9-10   : HallsLevel     (Залы)
Уровень 11-19  : VaultLevel     (Хранилище) - опционально
Уровень 20     : LastLevel      (Финальный уровень - босс)
Босс уровни    : BossLevel подклассы
```

**Ключевые члены Level:**
```java
public int width;           // Ширина карты
public int height;          // Высота карты
public int[] map;           // Массив клеток (0-31 для типов клеток)
public boolean[] visited;   // Посещённые ли клетки
public ArrayList<Heap> heaps;  // Предметы на полу
public ArrayList<Mob> mobs;    // Враги на уровне
public SparseArray<Trap> traps;  // Ловушки
public ArrayList<Plant> plants;  // Растения
```

**Процесс генерации:**
1. `Builder.build()` - Создание комнат и квадратов
2. `Painter.paint()` - Рисование стен и пола
3. `createMobs()` - Спаун врагов
4. `createItems()` - Спаун предметов

### **5. SCENES (UI Сцены)**
**Файл:** `scenes/PixelScene.java` - Базовый класс сцены

**Главные сцены:**
```
PixelScene (базовый класс для всех)
├── TitleScene          - Начальное меню
├── WelcomeScene        - Приветствие/интро
├── HeroSelectScene     - Выбор класса героя
├── GameScene           - ГЛАВНАЯ ИГРОВАЯ СЦЕНА
├── InterlevelScene     - Переход между уровнями
├── GameOverScene       - Экран смерти
├── AmuletScene         - Финальный экран (победа)
├── RankingsScene       - Таблица лучших результатов
├── JournalScene        - Справочник (Bestiary, Notes)
├── AlchemyScene        - Алхимия/крафт
├── CellSelector        - Выбор клетки на уровне
└── Другие...
```

**GameScene.java** - Самая важная сцена
- Отрисовка карты
- Обработка взаимодействия игрока
- Управление UI панелями (количество здоровья, инвентарь)
- Управление CellSelector (когда нужно выбрать цель)

---

## 🎲 Ключевые игровые системы

### **Боевая система**
**Файлы:** `actors/Char.java`, `mechanics/Ballistica.java`

**Расчёт урона:**
```
базовыйУрон + случайнаяВариация ± бонусы(орудие, способности) 
  → броня уменьшает урон
  → баффы влияют на финальный урон
```

**Система попадания:**
- Всё зависит от типа атаки
- Дальнобойные атаки используют Ballistica (траектория)
- Проверка видимости перед атакой

### **Система генерации лута**
**Файл:** `items/Generator.java`

- Враги при смерти дропят случайный лут
- Комнаты содержат сгенерированные сундуки
- Система LimitedDrops контролирует редкие предметы (не дропаются бесконечно)

### **Система сохранений**
- GameScene сохраняет при входе на новый уровень
- Используется Bundle/Bundlable для сериализации
- Сохранения хранятся в FileUtils

### **Система локализации**
**Файл:** `messages/Messages.java`
- Все текстовые строки хранятся в файлах свойств
- Система поддерживает множество языков

---

## 📂 Подробная структура основных папок

### **actors/**
```
actors/
├── Actor.java                 # Базовый класс для ВСЕХ действующих объектов
├── Char.java                  # Персонажи (Base class для Hero и Mob)
├── hero/
│   ├── Hero.java             # Главный персонаж
│   ├── Belongings.java       # Инвентарь героя
│   ├── HeroClass.java        # Классы (Warrior, Mage, Rogue, Huntress, Cleric, Duelist)
│   ├── HeroSubClass.java     # Подклассы (на первой развилке способностей)
│   ├── Talent.java           # Таланты (умения в боевых/прн навы)
│   ├── abilities/            # Армейские приёмы (Challenge, Endure и т.д.)
│   └── spells/               # Заклинания клирика и артефактные спеллы
│
├── mobs/
│   ├── Mob.java              # Базовый класс врага (AI логика)
│   ├── [конкретные враги].java  # Тип врага (Rat, Bat, Spinner и т.д.)
│   ├── npcs/                 # NPC персонажи (Blacksmith, Imp, Wandmaker и т.д.)
│   └── flying enemies и т.д.
│
├── blobs/
│   ├── Blob.java             # Базовый класс для газов/эффектов поля
│   ├── Smoke.java            # Дым
│   ├── Web.java              # Паутина
│   ├── Electricity.java      # Электричество
│   ├── ToxicGas.java         # Ядовитый газ
│   └── [другие эффекты].java
│
└── buffs/
    ├── Buff.java             # Базовый класс баффа
    ├── [конкретные баффы].java  # Haste, Speed, Poison, Burning и т.д.
```

### **items/**
```
items/
├── Item.java                 # Базовый класс
├── Heap.java                 # Кучка предметов на полу
├── Generator.java            # Генератор случайного лута
├── armor/                    # Броня и руны (глифы)
├── weapon/                   
│   ├── melee/               # Ближнее оружие
│   └── missiles/            # Дальнобойное оружие
├── artifacts/               # Артефакты
├── rings/                   # Кольца
├── potions/                 # Зелья
├── scrolls/                 # Свитки
├── spells/                  # Заклинания артефактов
├── wands/                   # Волшебные палочки
├── trinkets/                # Кулоны (новая система)
├── bags/                    # Сумки для хранения
├── bombs/                   # Бомбы
├── food/                    # Еда
└── quest/                   # Квестовые предметы
```

### **levels/**
```
levels/
├── Level.java               # Базовый класс уровня
├── [konkretnyeLevels].java  # SewerLevel, PrisonLevel, CavesLevel и т.д.
├── RegularLevel.java        # Стандартный уровень (используется для 99%)
├── Terrain.java             # Enum типов клеток (CHASM, FLOOR, WALL и т.д.)
├── builders/                # Генераторы уровней
│   ├── RoomBuilder.java     # Разбиение на комнаты
│   └── [другие строители]
├── painters/                # Рисовальщики (добавляют детали)
├── rooms/                   # Предопределённые комнаты
│   ├── standard/           # Обычные комнаты
│   ├── special/            # Особые комнаты (bossfight, armory и т.д.)
│   └── secret/             # Скрытые комнаты
├── features/                # Объекты уровня (двери, переходы, ловушки)
│   ├── Door.java
│   ├── HighGrass.java
│   └── LevelTransition.java # Переход на другой уровень
├── traps/                   # Ловушки
│   ├── Trap.java           # Базовый класс
│   └── [различные ловушки].java  # Poison, Fire, Pit и т.д.
```

### **scenes/**
```
scenes/
├── PixelScene.java          # Базовый класс для всех сцен
├── GameScene.java           # ГЛАВНАЯ ИГРОВАЯ СЦЕНА (отрисовка + логика игры)
├── CellSelector.java        # Облик клетки (используется для нацеливания)
├── TitleScene.java          # Главное меню
├── WelcomeScene.java        # Приветствие
├── HeroSelectScene.java     # Выбор класса
├── InterlevelScene.java     # Зацепление при переходе уровней
├── GameOverScene.java       # Экран смерти
├── AmuletScene.java         # Финальный экран
└── [другие сцены UI]
```

### **ui/**
```
ui/
├── Component.java           # Базовый класс для UI
├── Toolbar.java             # Внизу экрана (кнопки действий)
├── QuickSlot.java           # Быстрые слоты предметов
├── HealthBar.java           # Визуализация здоровья
├── BuffIndicator.java       # Визуализация баффов/дебаффов
├── StatsPane.java           # Панель статов
└── [другие компоненты]
```

### **sprites/**
```
sprites/
├── Sprite.java              # Базовый класс
├── CharSprite.java          # Спрайт персонажа (Hero, Mob)
├── ItemSprite.java          # Спрайт предмета
├── MissileSprite.java       # Спрайт снаряда (стрела, болт)
├── TalentSprite.java        # Спрайт таланта/способности
└── [другие визуальные элементы]
```

### **effects/**
```
effects/
├── Speck.java              # Маленькие частицы
├── FloatingText.java       # Плавающий текст (урон, хилл)
├── CellEmitter.java        # Эмиттер для клетки
├── Splash.java             # Всплеск
├── Flare.java              # Вспышка
└── [другие эффекты отрисовки]
```

### **mechanics/**
```
mechanics/
├── Ballistica.java         # Траектория для дальнобойных атак
├── ConeAOE.java           # Конусообразная область поражения
└── ShadowCaster.java      # Система видимости (FOV)
```

### **journal/**
```
journal/
├── Journal.java            # Главный справочник
├── Bestiary.java           # Справочник врагов
├── Catalog.java            # Каталог предметов
└── Notes.java              # Заметки игрока
```

---

## 🎬 Основной игровой цикл в коде

```java
// GameScene.java - главная игровая функция
class GameScene extends PixelScene {
    
    public void update() {
        // Обновляем каждый кадр
        
        // 1. Проверяем ввод игрока
        handleInput();
        
        // 2. Если герой сделал ход (потратил time)
        if (hero.ready) {
            hero.ready = false;
            
            // 3. Запускаем главный цикл актёров
            while (Actor.hasPending()) {
                Actor next = Actor.next();  // Получить актёра с наименьшей time
                if (next instanceof Mob) {
                    ((Mob)next).act();     // Враг действует
                } else if (next instanceof Hero) {
                    // ждём ввода игрока...
                } else {
                    next.act();            // Другой актёр действует
                }
            }
            
            // 4. Проверяем условия конца уровня
            if (hero.isDead()) {
                showGameOverScene();
            } else if (hero.hasReachedAmulet()) {
                showAmuletScene();
            }
        }
        
        // 5. Отрисовываем (камера, спрайты, UI)
        drawVisibleLevel();
    }
}
```

---

## 📊 Взаимосвязь основных компонентов

```
┌─────────────────────────────────────────────────────────────┐
│  ShatteredPixelDungeon (Game) - точка входа приложения     │
│  ↓ inherits from LibGDX Game                              │
│  ├─→ create() инициализирует приложение                   │
│  └─→ render() вызывает обновление текущей сцены           │
└──────────────┬────────────────────────────────────────────┘
               │
               ↓ переход через switchScene()
        ┌──────────────────┐
        │  PixelScene      │────────┐
        │  (базовый UI)    │        │
        └──────────────────┘        │
               ↑                     │
      ┌────────┼────────┐           │
      ├─────────────────┤           │
      │ GameScene       │◄──────────┤
      │ (главная игра)  │           │
      └─────────────────┘           │
               ↑                    │
      ┌────────┴─────────────────────┘
      │
      ├──→ Dungeon (static) - состояние игры
      │    ├── Hero - главный персонаж
      │    ├── Level - текущий уровень
      │    └── ...статические данные игры
      │
      ├──→ Level - карта уровня
      │    ├── map[] - массив клеток
      │    ├── heaps[] - предметы
      │    ├── mobs[] - враги
      │    └── traps[] - ловушки
      │
      ├──→ Hero - герой
      │    ├── Char (базовые параметры)
      │    │   ├── hp, ht, armor...
      │    │   └── buffs[] - активные эффекты
      │    ├── Belongings (инвентарь)
      │    │   ├── weapon, armor, ring...
      │    │   └── backpack (сумка)
      │    └── HeroClass (класс персонажа)
      │
      ├──→ Actor (очередь действий)
      │    ├── Hero, Mob, Blob, Buff...
      │    └── time-система приоритета
      │
      └──→ UI Components
           ├── Toolbar (действия внизу)
           ├── HealthBar (здоровье)
           └── BuffIndicator (баффы)
```

---

## 🔄 Процесс создания и сохран-ния игры

### **Создание новой игры:**
```
1. TitleScene → HeroSelectScene (выбор класса)
2. HeroSelectScene → InterlevelScene
3. InterlevelScene:
   - Dungeon.init() инициализирует все статические переменные
   - Создаёт нового Hero
   - Dungeon.newLevel() создаёт первый уровень
   - Level.create() генерирует карту, врагов, предметы
4. InterlevelScene → GameScene (начало игры)
```

### **Переход между уровнями:**
```
1. Hero использует переход уровня или достигает LevelTransition
2. GameScene показывает InterlevelScene
3. InterlevelScene генерирует новый Level если его ещё нет
4. Сохраняется игра через Bundle
5. InterlevelScene → GameScene с новым уровнем
```

### **Сохранение игры:**
- При входе на новый уровень (автосохранение)
- Все Save-способные объекты реализуют **Bundlable**
- Bundle упаковывает State в HashMap

---

## ⚙️ Ключевые паттерны проектирования

### **1. Singleton Pattern** 
- `Dungeon` - хранит состояние всей игры
- `Level` - статическая ссылка текущего уровня

### **2. Action/Command Pattern**
- `SPDAction` - команды управления игроком

### **3. Strategy Pattern**
- `HeroClass` - разные стратегии для класса героя
- `Mob.AIType` - разные AI стратегии врагов

### **4. Observer Pattern**
- Buff система - баффы наблюдают за изменениями персонажа

### **5. Factory Pattern**
- `Generator` - фабрика для создания предметов/врагов

### **6. Component Pattern**
- `Buff` - компоненты для персонажей
- UI компоненты (наследуют Component)

---

## 📶 Примеры кода

### **Как работает Action Hero:**
```java
class Hero extends Char {
    @Override
    protected boolean act() {
        // Герой может действовать только если есть энергия
        if (states.paralyzed > 0 || states.stun > 0) {
            spendConstant(TICK);
            return true;
        }
        
        ready = true;  // Сигнал GameScene ждать ввода
        return false;  // Не потратили time, ждём ввода
    }
}

// Когда игрок кликает на клетку
class GameScene {
    void onCellSelected(int pos) {
        if (hero.canMove(pos)) {
            hero.move(pos);
            hero.spend(1);  // Потратил time
            ready = false;  // Может действовать другой актёр
        }
    }
}
```

### **Как работает система Buff:**
```java
class Poison extends Buff {
    @Override
    public void act() {
        if (target.isAlive()) {
            // Урон от яда каждый ход
            target.takeDamage(1);
            spend(TICK);  // Следующий ход баффа
        } else {
            detach();     // Умер - уберём баффу
        }
    }
}

// Применение баффа на персонажа
Poison poison = new Poison();
poison.attachTo(target);  // Добавляется в target.buffs[]
```

### **Как враг делает ход:**
```java
class Mob extends Char {
    @Override
    protected boolean act() {
        if (!isAlive()) return false;
        
        // AI логика
        int heroPos = Dungeon.hero.pos;
        
        if (canSee(heroPos)) {
            // Может видеть героя - атаковать
            attack(heroPos);
        } else if (knows(heroPos)) {
            // Знает где герой - идти туда
            moveTo(nextStep(heroPos));
        } else {
            // Случайное блуждание
            moveTo(getRandomNearby());
        }
        
        spend(1);  // Враг потратил свой turn
        return true;
    }
}
```

---

## 📝 Заключение

Архитектура SPD строится на нескольких ключевых принципах:
1. **Модульность** - каждая система (items, actors, levels) независима
2. **Composition Over Inheritance** - баффы применяются через компоненты
3. **Game State Singleton** - Dungeon хранит всё состояние
4. **Turn-based system** - все действия основаны на очереди Actor
5. **Serializable state** - всё может быть сохранено/получено

Это делает проект хорошо масштабируемым и позволяет легко добавлять новые враги, предметы, баффы и уровни!

