# Практический Гайд по Архитектуре SPD
## Примеры кода, паттерны и как всё работает вместе

---

## 🎮 Практический Игровой Цикл

### Сценарий: Герой атакует врага

```
USER CLICKS ON MOB POSITION
  ↓
GameScene.selectCell(mobPos)
  ↓
CellSelector.onCellSelected(mobPos)
  ↓
hero.attack(mobPos)
  │
  ├─→ Calculate damage = baseDamage ± variance ± bonuses
  ├─→ Apply armor reduction
  ├─→ Add buffs effects (Berserk, Vulnerable и т.д.)
  ├─→ mob.takeDamage(damage)
  │   └─→ mob.HP -= damage
  │   └─→ if (HP <= 0) mob.die()
  │       ├─→ Statistics.enemiesSlain++
  │       ├─→ Generate drops
  │       └─→ Remove from World
  │
  ├─→ hero.spend(attackTime)  // Герой потратил time
  │
  └─→ Actor Queue обрабатывает следующего актёра
      ├─→ Mob.act()
      │   ├─→ if (canSee(hero)) attack(hero)
      │   └─→ hero.takeDamage(mobDamage)
      │
      ├─→ Blob.act()  // Газы действуют
      │
      └─→ Buff.act()  // Баффы действуют
          ├─→ Poison наносит урон
          ├─→ Healing восстанавливает HP
          └─→ Burns нанося урон и распространяя огонь
```

---

## 📚 Иерархия классов с примерами кода

### **1. Actor иерархия**

```java
// ============================================
// ACTOR БАЗОВЫЙ КЛАСС
// ============================================
public abstract class Actor implements Bundlable {
    
    // Система времени (turn-based)
    protected float time = 0f;
    protected int actPriority = DEFAULT;  // Приоритет в очереди
    
    // Динамический ID для идентификации
    private int id = 0;
    
    // КЛЮЧЕВОЙ МЕТОД - что делает этот актёр
    protected abstract boolean act();
    
    // Потратить time (может быть изменено баффами)
    protected void spend(float time) {
        spendConstant(time);  // Изменение баффами происходит в подклассах
    }
    
    // Кроме того, есть очередь всех актёров:
    private static SparseArray<Actor> all = new SparseArray<>();
    
    // Получить следующего актёра для действия
    public static Actor next() {
        Actor actor = all.valueAt(0);  // С минимальным time
        actor.time -= actor.time;      // Вычтем его time из очереди
        return actor;
    }
}

// ============================================
// HERO - ГЛАВНЫЙ ПЕРСОНАЖ
// ============================================
public class Hero extends Char {
    
    // Инвентарь ГЛАВНЫЙ АТРИБУТ ГЕРОЯ
    public Belongings belongings;
    
    // Магия и видения
    public int magicLevel = 0;
    public float spellbookLeft = 0;
    
    @Override
    protected boolean act() {
        // 1. Если герой парализован или в кошмаре
        if (isParalyzed() || isStunned()) {
            spendConstant(TICK);
            return true;
        }
        
        // 2. Герой ГОТОВ - ждём ввода игрока
        ready = true;
        return false;  // false = не потратили time, ждём ввода
    }
    
    // Обработка действия после клика
    public void move(int steps) {
        // Проверить можем ли ходить
        if (!canMove(steps)) return;
        
        // Движение на клетку
        pos = steps;
        
        // Проверить что на клетке
        Level level = Dungeon.level;
        
        if (level.hasHeap(steps)) {
            // На клетке предмет
            Heap heap = level.getHeap(steps);
            // Автоматически поднимаем, если есть место
            pickUp(heap);
        }
        
        if (level.hasMob(steps)) {
            // На клетке враг - атаковать
            attack(level.getMob(steps));
        }
        
        // Потратить time на движение (после всех действий)
        spend(TIME_TO_MOVE);
    }
    
    public void attack(Mob target) {
        int damage = weaponDamage();  // Базовый урон оружия
        
        // Модификаторы урона
        if (hasBuff(Berserk.class)) {
            damage *= 1.5f;  // Берсерк = +50% урон
        }
        
        if (target.hasBuff(Vulnerable.class)) {
            damage *= 1.25f;  // Уязвимость врага = +25% урон
        }
        
        target.takeDamage(damage);  // Враг получает урон
    }
    
    public void die() {
        // Герой умер
        state = DEAD;
        
        // Сохранить статистику
        Statistics.gameResult = LOST;
        Statistics.save();
        
        // Показать экран GameOver
        GameScene.show(new WndResurrect());
    }
}

// ============================================
// MOB - ВРАГ (управляло AI)
// ============================================
public class Mob extends Char {
    
    // AI параметры
    protected int target = -1;  // Кого атакуют?
    
    @Override
    protected boolean act() {
        // Проверить жив ли
        if (!isAlive()) return false;
        
        // Проверить если в каком-то CC (crowd control)
        if (isParalyzed()) {
            spendConstant(TICK);
            return true;
        }
        
        // ======= AI ЛОГИКА =======
        
        Hero hero = Dungeon.hero;
        
        // 1. Проверить видит ли герой
        if (canSee(hero.pos)) {
            // Атаковать
            attack(hero);
        }
        // 2. Если знает где герой (от MindVision или другого)
        else if (knowsAboutHero()) {
            // Идти туда
            moveTo(nextStep(hero.pos));
        }
        // 3. Иначе случайное блуждание
        else {
            wander();
        }
        
        // Потратить time на действие
        spend(TICK);
        return true;
    }
    
    public void takeDamage(int damage) {
        // Броня уменьшает урон
        damage = Math.max(1, damage - armor);
        
        // Применить урон
        HP -= damage;
        
        // Показать плавающий текст
        FloatingText.show(pos, damage, FloatingText.WARNING);
        
        // Если мертв
        if (HP <= 0) {
            die();
        }
    }
    
    public void die() {
        // Враги дропают лут
        dropLoot();
        
        // Добавить к статистике
        Statistics.enemiesSlain++;
        
        // Удалить из уровня
        Dungeon.level.mobs.remove(this);
    }
    
    private void dropLoot() {
        // Генератор решает что дропить
        Item item = Generator.random(Generator.Category.SCROLL);
        
        if (item != null) {
            Heap heap = new Heap(item, pos);
            Dungeon.level.heaps.add(heap);
        }
    }
}

// ============================================
// BLOB - ОБЛАСТЬ ЭФФЕКТОВ (газ, огонь, паутина)
// ============================================
public class Blob extends Actor {
    
    // Карта эффектов (какие клетки затронуты)
    public int[] cur;   // Текущее состояние
    public int[] cur;   // Предыдущее состояние
    
    @Override
    protected boolean act() {
        // Blob распространяется и взаимодействует
        
        for (int i = 0; i < map.length; i++) {
            if (cur[i] > 0) {
                // На этой клетке есть эффект
                
                // 1. Получить персонажа на клетке
                if (Dungeon.hero.pos == i) {
                    applyEffect(Dungeon.hero);  // Применить эффект на героя
                }
                
                // 2. Распространиться на соседние клетки
                spread();
            }
        }
        
        spend(TICK);
        return true;
    }
}
```

---

## 🎯 Система Buffs

```java
// ============================================
// BUFF - БАЗОВЫЙ ЭФФЕКТ
// ============================================
public class Buff implements Bundlable {
    
    public Char target;           // На кого действует баф
    protected float left = 0;     // Сколько ещё будет действовать
    
    protected boolean persistent = false;  // Остаётся после смерти?
    protected boolean postponed = false;   // Задержанный?
    
    // ГЛАВНЫЙ МЕТОД - действие каждый ход
    public void act() {
        if (left <= 0) {
            detach();  // Баф закончился - убираем
        } else {
            left -= TICK;  // Уменьшаем время
            spend(TICK);
        }
    }
    
    // Прикрепить к персонажу
    public void attachTo(Char target) {
        this.target = target;
        target.buffs.add(this);
        
        // Может быть переопределено для специального поведения
        onAttach();
    }
    
    // Отсоединить с персонажа
    public void detach() {
        target.buffs.remove(this);
    }
}

// ============================================
// ПРИМЕР: POISON - ЯД
// ============================================
public class Poison extends Buff {
    
    public int count = 0;  // Уровень яда
    
    @Override
    public void act() {
        // 1. Наносим урон
        target.takeDamage(1);
        
        // 2. Показываем эффект
        FloatingText.show(target.pos, "Poison", FloatingText.WARNING);
        
        // Каждый ход яд становится слабее
        count--;
        if (count <= 0) {
            detach();  // Яд закончился
        }
        
        spend(TICK);
    }
    
    // Возможно наложить яд снова (усилить)
    public void add(int count) {
        this.count = Math.max(this.count, count);
    }
}

// ============================================
// ПРИМЕР: HASTE - УСКОРЕНИЕ
// ============================================
public class Haste extends Buff {
    
    @Override
    public void spend(float time) {
        // Модификация spend() для ускорения
        target.spendConstant(time / 2f);  // Ускорение в 2 раза!
    }
}

// КАК ПРИМЕНИТЬ БАФФЫ:
// ========================
// В классе Char:

public void applyBuff(Buff buff) {
    // Если баф такого типа уже есть - нужно обновить его
    for (Buff b : buffs) {
        if (b.getClass() == buff.getClass()) {
            buff.attachTo(this);  // Переприменить
            return;
        }
    }
    
    // Иначе добавить новый баф
    buff.attachTo(this);
}
```

---

## 🎁 Система Items

```java
// ============================================
// ITEM - БАЗОВЫЙ КЛАСС ПРЕДМЕТОВ
// ============================================
public class Item implements Bundlable {
    
    // ГЛАВНЫЕ АТРИБУТЫ
    public int quantity = 1;           // Количество (для стека)
    public boolean stackable = false;  // Может ли складываться
    public int level = 0;              // Уровень улучшения (+0, +1, +2...)
    public boolean cursed = false;     // Проклят?
    
    // ГЛАВНЫЕ МЕТОДЫ
    public void doAction() {
        // Основное действие при использовании
    }
    
    public ItemSprite sprite() {
        // Получить спрайт для отрисовки
    }
    
    public String describe() {
        // Описание предмета
    }
    
    // Когда герой поднимает предмет
    public void pickUp(Hero hero) {
        if (stackable) {
            // Сложить со существующим
            Item existing = hero.belongings.find(getClass());
            if (existing != null) {
                existing.quantity += quantity;
                return;
            }
        }
        
        // Добавить в инвентарь
        hero.belongings.add(this);
    }
    
    // Когда герой выбрасывает предмет
    public void drop(int pos) {
        // Создать Heap на полу
        Heap heap = new Heap(this, pos);
        Dungeon.level.heaps.add(heap);
    }
    
    // Когда герой использует предмет
    public void use(Hero hero) {
        // Переопределено в подклассах
    }
}

// ============================================
// ПРИМЕР: POTION - ЗЕЛЬЕ
// ============================================
public class Potion extends Item {
    
    public Potion() {
        stackable = true;
    }
    
    @Override
    public void use(Hero hero) {
        // Применить эффект зелья
        applyEffect(hero);
        
        // Уменьшить количество
        quantity--;
        if (quantity <= 0) {
            hero.belongings.remove(this);
        }
    }
    
    protected void applyEffect(Hero hero) {
        // Переопределено в подклассах (HealPotion, PoisonPotion и т.д.)
    }
}

// ============================================
// ПРИМЕР: POTION OF HEALING - ЗЕЛЬЕ ИСЦЕЛЕНИЯ
// ============================================
public class PotionOfHealing extends Potion {
    
    public static final int VALUE = 30;  // Сколько лечит?
    
    @Override
    protected void applyEffect(Hero hero) {
        hero.HP = Math.min(hero.HT, hero.HP + VALUE);  // Лечит на 30
        
        // Показать эффект
        FloatingText.show(hero.pos, "Healing", FloatingText.POSITIVE);
        
        // Напиток издаёт звук
        Sample.INSTANCE.play(Assets.Sounds.DRINK);
    }
}

// ============================================
// ПРИМЕР: SCROLL - СВИТОК
// ============================================
public class Scroll extends Item {
    
    static {
        // Инициализация текстов свитков (для каждого игрока разные)
        initLabels();  
    }
    
    @Override
    public void use(Hero hero) {
        doRead(hero);
        quantity--;
        if (quantity <= 0) {
            hero.belongings.remove(this);
        }
    }
    
    protected void doRead(Hero hero) {
        // Переопределено в подклассах
    }
}

// ============================================
// ПРИМЕР: ARMOR - БРОНЯ
// ============================================
public class Armor extends EquipableItem {
    
    public int armor = 1;           // Значение защиты
    public Glyph glyph;             // Глиф (рунаы) на броне
    
    @Override
    public void equip(Hero hero) {
        // Надеть броню
        hero.belongings.armor = this;
        hero.armor += armor;  // Увеличить защиту
    }
    
    @Override
    public void unequip() {
        // Снять броню
        hero.armor -= armor;
    }
}

// ============================================
// HEAP - КУЧКА ПРЕДМЕТОВ НА ПОЛУ
// ============================================
public class Heap {
    
    public Item item;
    public int pos;
    public int count = 1;  // Количество полэтов?
    
    public Heap(Item item, int pos) {
        this.item = item;
        this.pos = pos;
    }
    
    public Item peek() {
        return item;  // Посмотреть что в куче
    }
    
    public Item pop() {
        // Взять из кучи
        return item;
    }
}
```

---

## 🗺️ Система Levels

```java
// ============================================
// LEVEL - БАЗОВЫЙ КЛАСС УРОВНЯ
// ============================================
public class Level implements Bundlable {
    
    public static final int LEVEL_WIDTH = 32;
    public static final int LEVEL_HEIGHT = 32;
    
    // ГЛАВНЫЕ ДАННЫЕ
    public int width;
    public int height;
    public int[] map;                    // Массив клеток (типы)
    public boolean[] visited;            // Посещённые ли клетки
    public ArrayList<Heap> heaps;        // Предметы на полу
    public ArrayList<Mob> mobs;          // Враги
    public SparseArray<Trap> traps;      // Ловушки
    public ArrayList<Plant> plants;      // Растения
    
    // Система видимости
    public Vision vision;                // Видимость из view
    public String tilesTex;              // Текстура плиток
    
    public static void create() {
        // ПРОЦЕСС СОЗДАНИЯ УРОВНЯ:
        
        // 1. Выбрать правильный класс уровня по depth
        Level level;
        switch (Dungeon.depth) {
            case 1: case 2:
                level = new SewerLevel();
                break;
            case 3: case 4:
                level = new PrisonLevel();
                break;
            // ... и т.д.
        }
        
        Dungeon.level = level;
        
        // 2. Генерировать комнаты
        if (RegularLevel.class.isInstance(level)) {
            ((RegularLevel)level).genRooms();  // Разбить на комнаты
        }
        
        // 3. Рисовать стены, полы
        level.create();  // create() вызывает Builder и Painter
        
        // 4. Спаун врагов
        level.createMobs();
        
        // 5. Спаун предметов
        level.createItems();
        
        // 6. Расположить героя
        Dungeon.hero.place(level.entrance());
    }
    
    // Цикл обновления уровня
    public void update() {
        // Система видимости
        updateVision();
    }
    
    // Получить что на клетке
    public char getTile(int pos) {
        return (char) map[pos];  // Тип клетки
    }
    
    public boolean hasHeap(int pos) {
        for (Heap heap : heaps) {
            if (heap.pos == pos) return true;
        }
        return false;
    }
    
    public Heap getHeap(int pos) {
        for (Heap heap : heaps) {
            if (heap.pos == pos) return heap;
        }
        return null;
    }
    
    public Mob getMob(int pos) {
        for (Mob mob : mobs) {
            if (mob.pos == pos) return mob;
        }
        return null;
    }
    
    // Диаграмма типов клеток (Terrain):
    // ====================================
    // STONE      = # (непроходимый камень)
    // WALL       = 1 (стена)
    // DOOR       = 2 (дверь)
    // FLOOR      = 3 (пол)
    // GRASS      = 4 (трава)
    // WATER      = 5 (вода)
    // CHASM      = 6 (пропасть - падение)
    // TRAP       = 7 (ловушка)
    // SECRET_WALL= 8 (секретная стена)
}

// ============================================
// TERRAIN - ТИПЫ КЛЕТОК
// ============================================
public interface Terrain {
    int CHASM            = 0;
    int EMPTY            = 1;
    int GRASS            = 2;
    int HIGH_GRASS       = 3;
    int WALL             = 4;
    int DOOR             = 5;
    int OPEN_DOOR        = 6;
    int WALL_DECO        = 7;
    int BARRICADE        = 8;
    int LOCKED_DOOR      = 9;
    int PEDESTAL         = 10;
    int WALL_SWITCH      = 11;
    int STATUE           = 12;
    int ALCHEMY          = 13;
    int WELL             = 14;
    int MINE_CART        = 15;
}

// ============================================
// REGULAR LEVEL - ОБЫЧНЫЙ УРОВЕНЬ
// ============================================
public class RegularLevel extends Level {
    
    // С комнатами и коридорами
    protected ArrayList<Room> rooms;
    
    @Override
    public void create() {
        // Builder разбивает уровень на комнаты
        rooms = new ArrayList<>();
        Builder.generate(this, rooms);
        
        // Painter рисует комнаты на map
        for (Room room : rooms) {
            Painter.paint(this, room);
        }
        
        // Создать врагов и предметы
        createMobs();
        createItems();
    }
    
    protected void createMobs() {
        // Генератор случайно выбирает врагов для этого уровня
        int mobCount = 1 + Dungeon.depth / 2;  // Больше врагов глубже
        
        for (int i = 0; i < mobCount; i++) {
            Mob mob = Generator.random(Generator.Category.MOB_WEAK);
            mob.place(randomFloor());  // Разместить на случайном полу
            
            mobs.add(mob);
            Actor.add(mob);  // Добавить в очередь действий
        }
    }
    
    protected void createItems() {
        // Генератор создаёт лут в комнатах
        for (Room room : rooms) {
            if (room.type == Room.Type.STORAGE) {
                // Room может содержать сундук с лутом
                Item item = Generator.random();
                Heap heap = new Heap(item, room.random());
                heaps.add(heap);
            }
        }
    }
}

// ============================================
// BOSS LEVEL ПРИМЕР - ФИНАЛЬНЫЙ УРОВЕНЬ
// ============================================
public class LastLevel extends Level {
    
    // Финальный уровень - встреча с боссом
    
    @Override
    public void create() {
        // На финальном уровне живёт босс
        
        // Создать первую арену
        // ....
        
        // Разместить босса на уровне
        YogDzewa boss = new YogDzewa();
        boss.place(center);
        mobs.add(boss);
        
        // Amuleт находится здесь
        Amulet amulet = new Amulet();
        Heap heapAmulet = new Heap(amulet, centerNextTo);
        heaps.add(heapAmulet);
    }
}
```

---

## 🎨 Система Scenes (UI)

```java
// ============================================
// PIXEL SCENE - БАЗОВАЯ СЦЕНА
// ============================================
public class PixelScene extends Scene {
    
    public static final int INTERFACE_COLOR = 0xFF8800;  // Оранжевый
    
    @Override
    public void create() {
        // Вызывается когда сцена становится активной
        super.create();
    }
    
    @Override
    public void update() {
        super.update();
    }
    
    @Override
    public void draw() {
        // Отрисовка сцены каждый кадр
        super.draw();
    }
}

// ============================================
// GAME SCENE - ГЛАВНАЯ ИГРОВАЯ СЦЕНА
// ============================================
public class GameScene extends PixelScene {
    
    // UI компоненты
    private Toolbar toolbar;
    private HealthBar healthBar;
    private BuffIndicator buffs;
    
    // Система камеры
    private Camera camera;
    
    // Выбор клетки
    public static CellSelector cellSelector;
    
    @Override
    public void create() {
        super.create();
        
        // 1. Инициализировать UI
        toolbar = new Toolbar();
        healthBar = new HealthBar();
        buffs = new BuffIndicator();
        
        addToFront(toolbar);
        addToFront(healthBar);
        addToFront(buffs);
        
        // 2. Инициализировать камеру
        camera = new Camera(0, 0, WIDTH, HEIGHT);
        camera.follow(Dungeon.hero);  // Камера следует за героем
        
        // 3. Инициализировать выбор клетки
        cellSelector = new CellSelector(this);
        addToFront(cellSelector);
    }
    
    @Override
    public void update() {
        super.update();
        
        // Основной цикл игры:
        
        // 1. Обновить систему действий (главный цикл Actor)
        updateActors();
        
        // 2. Проверить условия конца уровня
        checkLevelEnd();
        
        // 3. Обновить камеру
        camera.update();
    }
    
    private void updateActors() {
        // Главный цикл Actor обработки
        
        while (Actor.hasPending()) {
            Actor actor = Actor.next();
            
            if (actor.act()) {
                // Актер потратил time, перейти к следующему
            } else {
                // Актер не потратил time (например, ждёт ввода)
                if (actor instanceof Hero) {
                    // Герой готов - ждём ввода игрока
                    inputMode = INPUT_MODE_MOVE;
                }
                break;  // Выход из цикла
            }
        }
    }
    
    private void checkLevelEnd() {
        if (!Dungeon.hero.isAlive()) {
            // Герой мёртв
            gameOver();
        } else if (Dungeon.hero.pos == Dungeon.level.exit()) {
            // Герой достиг выхода
            nextLevel();
        }
    }
    
    @Override
    public void draw() {
        // Отрисовка каждый кадр
        
        // 1. Отрисовать подземелье
        drawLevel();
        
        // 2. Отрисовать герметов
        drawCharacters();
        
        // 3. Отрисовать эффекты
        drawEffects();
        
        // 4. Отрисовать UI
        super.draw();
    }
    
    private void drawLevel() {
        // Нарисовать плитки уровня
        Level level = Dungeon.level;
        
        for (int y = 0; y < level.height; y++) {
            for (int x = 0; x < level.width; x++) {
                int pos = x + y * level.width;
                
                // Проверить видна ли плитка
                if (level.visited[pos]) {
                    // Получить тип плитки
                    int tile = level.map[pos];
                    
                    // Нарисовать плитку
                    drawTile(pos, tile);
                    
                    // Если есть Heap (предметы) - нарисовать
                    if (level.hasHeap(pos)) {
                        drawHeap(level.getHeap(pos));
                    }
                }
            }
        }
    }
    
    private void drawCharacters() {
        // Нарисовать героя
        drawCharSprite(Dungeon.hero);
        
        // Нарисовать врагов
        for (Mob mob : Dungeon.level.mobs) {
            drawCharSprite(mob);
        }
    }
    
    public void onInput(int keyCode) {
        // Обработка ввода игрока
        
        if (inputMode == INPUT_MODE_MOVE) {
            // Режим движения
            handleMovementInput(keyCode);
        } else if (inputMode == INPUT_MODE_AIM) {
            // Режим нацеливания (для спеллов/дальнобойного оружия)
            handleAimingInput(keyCode);
        }
    }
    
    public void selectCell(int pos) {
        // Клик на клетку в режиме движения
        
        Hero hero = Dungeon.hero;
        
        if (pos == hero.pos) {
            // Герой кликнул на себя - ничего не делать
            return;
        }
        
        // Проверить что на клетке
        if (Dungeon.level.hasMob(pos)) {
            // На клетке враг - атаковать
            hero.attack(Dungeon.level.getMob(pos));
        } else {
            // Пустая клетка - идти туда
            hero.move(pos);
        }
        
        // Герой совершил действие, теперь другие могут действовать
        inputMode = INPUT_MODE_WAITING;
    }
    
    private void gameOver() {
        // Герой мёртв - показать экран
        ShatteredPixelDungeon.switchScene(GameOverScene.class);
    }
    
    private void nextLevel() {
        // Герой достиг выхода - перейти на следующий уровень
        
        // Сохранить текущий уровень
        Dungeon.saveCurrentLevel();
        
        // Наступить depth
        Dungeon.depth++;
        
        // Перейти на InterlevelScene для загрузки нового уровня
        ShatteredPixelDungeon.switchScene(InterlevelScene.class);
    }
}

// ============================================
// CELL SELECTOR - ВЫБОР КЛЕТКИ
// ============================================
public class CellSelector extends Component {
    
    public CellSelector(GameScene scene) {
        // Создать элемент для выбора клетки
    }
    
    public void onMouseDown(int pos) {
        // Клик мыши (или сенсор)
        scene.selectCell(pos);
    }
}
```

---

## 💾 Система Сохранений

```java
// ============================================
// КАК СОХРАНИТЬ/ЗАГРУЗИТЬ ИГРУ
// ============================================

public class Dungeon {
    
    // Сохранение всей игры
    public static void saveAll() {
        // Упаковать в Bundle
        Bundle bundle = new Bundle();
        
        // Главные данные
        bundle.put("hero", hero);           // Hero.storeInBundle()
        bundle.put("level", level);         // Level.storeInBundle()
        bundle.put("depth", depth);
        bundle.put("branch", branch);
        bundle.put("gold", gold);
        bundle.put("energy", energy);
        bundle.put("seed", seed);
        bundle.put("seed_text", customSeedText);
        
        // LimitedDrops
        LimitedDrops.store(bundle);
        
        // Сохранить в файл
        FileUtils.saveBundle(SAVE_FILENAME, bundle);
    }
    
    // Загрузка всей игры
    public static void loadAll() {
        Bundle bundle = FileUtils.loadBundle(SAVE_FILENAME);
        
        if (bundle == null) {
            // Файл не найден
            return;
        }
        
        // Распаковать
        hero = (Hero) bundle.get("hero");
        level = (Level) bundle.get("level");
        depth = bundle.getInt("depth");
        // ... и т.д.
        
        LimitedDrops.restore(bundle);
    }
}

// ============================================
// BUNDLABLE ИНТЕРФЕЙС
// ============================================
public interface Bundlable {
    
    // Сохранить этот объект
    void storeInBundle(Bundle bundle);
    
    // Загрузить этот объект
    void restoreFromBundle(Bundle bundle);
}

// ============================================
// ПРИМЕР: Hero реализует Bundlable
// ============================================
public class Hero extends Char implements Bundlable {
    
    @Override
    public void storeInBundle(Bundle bundle) {
        // Сохранить все данные героя
        bundle.put("class", heroClass);
        bundle.put("subclass", subclass);
        bundle.put("level", lvl);
        bundle.put("exp", exp);
        bundle.put("hp", HP);
        bundle.put("ht", HT);
        bundle.put("str", STR);
        
        // Инвентарь
        belongings.storeInBundle(bundle);
        
        // Баффы
        Bundle buffsBundle = new Bundle();
        for (Buff buff : buffs) {
            buff.storeInBundle(buffsBundle);
        }
        bundle.put("buffs", buffsBundle);
    }
    
    @Override
    public void restoreFromBundle(Bundle bundle) {
        // Восстановить все данные героя
        heroClass = bundle.getEnum("class", HeroClass.class);
        subclass = bundle.getEnum("subclass", HeroSubClass.class);
        lvl = bundle.getInt("level");
        exp = bundle.getInt("exp");
        HP = bundle.getInt("hp");
        HT = bundle.getInt("ht");
        STR = bundle.getInt("str");
        
        // Инвентарь
        belongings.restoreFromBundle(bundle);
        
        // Баффы
        Bundle buffsBundle = bundle.getBundle("buffs");
        for (String key : buffsBundle.keys()) {
            Buff buff = (Buff) buffsBundle.get(key);
            buff.attachTo(this);
        }
    }
}
```

---

## 🔗 Взаимодействие между компонентами

### Flow: GameScene → Actor → Dungeon → Level → Mob → Item

```
GameScene.update()
    ↓
while (Actor.hasPending()) {
    Actor next = Actor.next()
        ├─→ if next = Hero {
        │       hero.act()  // ждёт ввода
        │       return false
        │   } else if next = Mob {
        │       mob.act()
        │       ├─→ mob.canSee(hero) ?
        │       ├─→ mob.attack(hero)
        │       │   └─→ hero.takeDamage()
        │       └─→ mob.spend(TICK)
        │   } else if next = Blob {
        │       blob.act()
        │       ├─→ blob.spread()
        │       ├─→ blob.applyEffect(hero)
        │       └─→ blob.spend(TICK)
        │   } else if next = Buff {
        │       buff.act()
        │       ├─→ buff.onAction()
        │       └─→ buff.spend(TICK)
        │   }
    └─→ if (!acted) break  // ждём это же нужно
    
    CheckGameEnd()
        ├─→ hero.isDead() → GameOverScene
        ├─→ hero.finishGame() → AmuletScene
        └─→ hero.levelChanged → InterlevelScene
}
```

---

## ⚡ Примеры расширения архитектуры

### Как добавить нового врага?

```java
// 1. Создать новый класс, наследующий Mob
public class NewEnemy extends Mob {
    
    @Override
    protected boolean act() {
        // AI логика
        if (canSee(Dungeon.hero.pos)) {
            attack(Dungeon.hero);
        } else {
            wander();
        }
        spend(TICK);
        return true;
    }
}

// 2. Добавить в генератор
public class Generator {
    public static Mob newMob() {
        switch (Random.Int(10)) {
            case 0: return new NewEnemy();
            // ... другие враги
        }
    }
}
```

### Как добавить новый баф?

```java
// 1. Создать новый баффь
public class MyBuff extends Buff {
    @Override
    public void act() {
        // что делает баф каждый ход
        spend(TICK);
    }
}

// 2. Применить на персонажа
character.applyBuff(new MyBuff());
```

### Как добавить новый предмет?

```java
// 1. Создать класс предмета
public class MyItem extends Item {
    @Override
    public void use(Hero hero) {
        // что делает предмет
    }
}

// 2. Добавить в генератор
public class Generator {
    public static Item randomScroll() {
        switch (Random.Int(15)) {
            case 0: return new MyItem();
            // ... другие предметы
        }
    }
}
```

---

## 🎓 Ключевые паттерны в коде

1. **Singleton Pattern** - Dungeon класс
2. **Observer Pattern** - Buff система
3. **Factory Pattern** - Generator класс
4. **Strategy Pattern** - HeroClass, Mob.AIType
5. **Composite Pattern** - Component иерархия
6. **Command Pattern** - SPDAction

---

Это полный картина того как работает SPD под капотом! Система turn-based, управление состоянием, система действий - всё интегрировано воедино.

