# Список .java файлов по подпапкам (основные файлы)

Файл содержит сводную таблицу по ключевым подпапкам `com.shatteredpixel.shatteredpixeldungeon` в `core/src/main/java` — основные `.java` файлы и краткое описание.

> Примечание: перечислены ключевые файлы и подпапки; репозиторий содержит гораздо больше мелких файлов (особенно в `items/`, `mobs/`, `buffs/` и т.д.).

---

## actors/

| Файл / Папка | Описание |
|---|---|
| Actor.java | Базовый класс для всех действующих сущностей (act(), spend()). |
| Char.java | Общие поля для персонажей (hp, defense, buffs). |
| blobs/ (папка) | Эффекты на клетках: Blob.java, Fire.java, ToxicGas.java, Regrowth.java, Web.java и др. |
| buffs/ (папка) | Баффы/дебаффы: Buff.java и множество реализаций (Poison, Haste, Regen, Invisibility и др.). |
| hero/ (папка) | Hero.java, Belongings.java, HeroClass.java, abilities/ и spells/ — способности героя. |
| mobs/ (папка) | Mob.java и конкретные реализации монстров (Rat.java, Bat.java, DwarfKing.java, Necromancer.java, ...), а также npcs/. |

## effects/

| Файл / Папка | Описание |
|---|---|
| Effects.java | Фабрика/центр запуска визуальных эффектов.
| BlobEmitter.java, CellEmitter.java | Эмиттеры частиц.
| particles/ | Частицы: BlastParticle, SmokeParticle, SparkParticle и др.

## items/

| Файл / Папка | Описание |
|---|---|
| Item.java | Базовый класс предмета (use(), doPickUp(), сериализация). |
| Generator.java | Логика генерации лута/спавна предметов. |
| Heap.java | Куча предметов на карте. |
| armor/, weapon/, wands/, scrolls/, potions/, trinkets/, artifacts/, bags/ | Множество реализаций предметов и их подкатегорий. |
| examples: Amulet.java, Gold.java, Torch.java, Recipe.java | Часто используемые предметы в корне.

## levels/

| Файл / Папка | Описание |
|---|---|
| Level.java | Базовая структура уровня (тайлы, объекты, создание). |
| Terrain.java, RegularLevel.java | Описание террейна и общий уровень. |
| конкретные уровни: SewerLevel.java, PrisonLevel.java, CavesLevel.java, VaultLevel.java, LastLevel.java | Специфичная логика уровней. |
| builders/ | Алгоритмы построения (GridBuilder, RegularBuilder, BranchesBuilder). |
| painters/ | Декораторы/рисовальщики (CavesPainter, PrisonPainter, ...). |
| rooms/ | Типы комнат (standard/, secret/, quest/ и т.д.). |
| traps/ | Trap.java и реализации ловушек (PitfallTrap, PoisonDartTrap и др.). |

## scenes/

| Файл | Описание |
|---|---|
| PixelScene.java | Базовый класс для сцен. |
| GameScene.java | Экран игры — рендер уровня, ввод, таймеры. |
| TitleScene.java, WelcomeScene.java, HeroSelectScene.java, InterlevelScene.java, JournalScene.java | Прочие сцены приложения. |

## ui/

| Файл / Папка | Описание |
|---|---|
| Toolbar.java, HealthBar.java, BuffIndicator.java | Основные UI-компоненты. |
| InventoryPane.java, ItemSlot.java, ItemButton.java | Компоненты инвентаря. |
| Window.java | Базовый класс для окон. |

## windows/

| Файл | Описание |
|---|---|
| WndInventory.java, WndJournal.java, WndOptions.java, WndHero.java, WndBlacksmith.java, WndWandmaker.java, WndTradeItem.java | Большой набор окон/диалогов для взаимодействия с игроком. |

## effects/particles/

| Файл | Описание |
|---|---|
| BlastParticle.java, SmokeParticle.java, SparkParticle.java, ... | Частицы для VFX. |

## sprites/

| Файл / Папка | Описание |
|---|---|
| HeroSprite.java, MobSprite.java, ItemSprite.java, ... | Классы спрайтов для отображения сущностей и предметов. |
| множество `*Sprite.java` для каждого моба | Отдельный спрайт для каждой сущности. |

## tiles/

| Файл | Описание |
|---|---|
| DungeonTilemap.java, DungeonTileSheet.java, FogOfWar.java, GridTileMap.java, RaisedTerrainTilemap.java | Тайловая отрисовка, система Fog of War и кеши. |

## mechanics/

| Файл | Описание |
|---|---|
| Ballistica.java, ConeAOE.java, ShadowCaster.java | Алгоритмы трассировки, расчёт AOE и видимости. |

## plants/

| Файл | Описание |
|---|---|
| Plant.java, BlandfruitBush.java, Mageroyal.java, Firebloom.java, Sungrass.java, ... | Реализации растений и их поведение. |

## services/

| Файл | Описание |
|---|---|
| services/news/News.java, services/updates/Updates.java | Сервисы для новостей и обновлений. |

## messages/

| Файл | Описание |
|---|---|
| Messages.java, Languages.java | Локализация и строки сообщений. |

## utils/

| Файл | Описание |
|---|---|
| DungeonSeed.java, GLog.java, Holiday.java | Вспомогательные утилиты и логирование. |

---

Если нужно — могу сгенерировать расширенную таблицу (полный flat-list всех `.java` файлов в репозитории с путями) в виде CSV или Markdown. Напишите «полный список (CSV)» или «полный список (md)» — и я сгенерирую.
