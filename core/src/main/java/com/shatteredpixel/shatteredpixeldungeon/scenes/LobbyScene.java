package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.network.GameClient;
import com.shatteredpixel.shatteredpixeldungeon.network.NetworkManager;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.session.LobbyPlayerDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.session.PlayerReadyDto;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class LobbyScene extends PixelScene {

    private RenderedTextBlock titleText;
    private ArrayList<LobbyPlayerSlot> playerSlots = new ArrayList<>();
    private RedButton btnReady;
    private RedButton btnStart;

    // UI для настройки хостом
    private OptionSlider syncModeSlider;
    private RenderedTextBlock syncModeDesc;

    // Названия и описания наших режимов
    private static final String[] SYNC_MODES = {"Пошагово (Строго)", "Асинхронно (Свободно)", "Tick Time (Реал-тайм)"};
    private static final String[] SYNC_DESCS = {
            "Ждем хода каждого игрока. Мобы ходят после всех.",
            "Каждый ходит в своем темпе. Опасно при встрече!",
            "Ход длится 0.5с. Не успел нажать - пропустил!"
    };

    @Override
    public void create() {
        super.create();

        int w = Camera.main.width;
        int h = Camera.main.height;
        RectF insets = getCommonInsets();

        TitleBackground BG = new TitleBackground(w, h);
        add( BG );

        w -= insets.left + insets.right;

        ExitButton btnExit = new ExitButton();
        btnExit.setPos( insets.left + w - btnExit.width(), insets.top );
        add( btnExit );

        titleText = PixelScene.renderTextBlock(Messages.get(this, "title") != null ? Messages.get(this, "title") : "ЛОББИ", 9);
        titleText.setPos((Camera.main.width - titleText.width()) / 2f, 20);
        add(titleText);

        if (NetworkManager.instance().isHost()) {
            String ip = "127.0.0.1";
            try {
                try (DatagramSocket socket = new DatagramSocket()) {
                    socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                    ip = socket.getLocalAddress().getHostAddress();
                }
            } catch (Exception e) {
                try { ip = InetAddress.getLocalHost().getHostAddress(); } catch (Exception ignored) {}
            }

            RenderedTextBlock ipText = PixelScene.renderTextBlock("Ваш IP для друзей: " + ip, 6);
            ipText.hardlight(0xAAAAAA);
            ipText.setPos((Camera.main.width - ipText.width()) / 2f, titleText.bottom() + 2);
            add(ipText);

            // --- НАСТРОЙКА РЕЖИМОВ ТОЛЬКО ДЛЯ ХОСТА ---
            syncModeSlider = new OptionSlider("Режим Синхронизации", "Пошагово", "Реал-тайм", 0, 2) {
                @Override
                protected void onChange() {
                    int val = getSelectedValue();
                    if (syncModeDesc != null) {
                        syncModeDesc.text(SYNC_MODES[val] + ": " + SYNC_DESCS[val]);
                        syncModeDesc.setPos((Camera.main.width - syncModeDesc.width()) / 2f, bottom() + 2);
                    }
                }
            };
            syncModeSlider.setRect((Camera.main.width - 160) / 2f, ipText.bottom() + 10, 160, 24);
            add(syncModeSlider);

            syncModeDesc = PixelScene.renderTextBlock(SYNC_MODES[0] + ": " + SYNC_DESCS[0], 6);
            syncModeDesc.hardlight(0xAAAAAA);
            syncModeDesc.setPos((Camera.main.width - syncModeDesc.width()) / 2f, syncModeSlider.bottom() + 2);
            add(syncModeDesc);
        } else {
            // Для клиента просто пишем какой режим выбран
            syncModeDesc = PixelScene.renderTextBlock("Ожидание настроек хоста...", 6);
            syncModeDesc.hardlight(0xAAAAAA);
            syncModeDesc.setPos((Camera.main.width - syncModeDesc.width()) / 2f, titleText.bottom() + 5);
            add(syncModeDesc);
        }

        btnReady = new RedButton("Готов") {
            @Override
            protected void onClick() {
                super.onClick();
                PlayerReadyDto readyDto = new PlayerReadyDto();
                readyDto.playerId = NetworkManager.instance().localClient().playerId();
                readyDto.ready = true;

                if (NetworkManager.instance().isHost()) {
                    NetworkManager.instance().server().handlePlayerReady(readyDto);
                } else {
                    NetworkManager.instance().localClient().sendNetworkPacket(readyDto);
                }

                text("Ожидание...");
                enable(false);
            }
        };
        add(btnReady);

        btnStart = new RedButton("Спуск!") {
            @Override
            protected void onClick() {
                super.onClick();
                if (NetworkManager.instance().isHost()) {
                    NetworkManager.instance().server().launchGame();
                }
            }
        };
        add(btnStart);

        // --- ЛОГИКА СОВМЕЩЕНИЯ КНОПОК ---
        float btnY = Camera.main.height - 45;
        float btnWidth = 110;
        float btnHeight = 26;

        if (NetworkManager.instance().isHost()) {
            btnReady.setRect(Camera.main.width / 2f - btnWidth, btnY, btnWidth, btnHeight);
            btnStart.setRect(Camera.main.width / 2f, btnY, btnWidth, btnHeight);
            btnStart.visible = true;
            btnStart.enable(false);
        } else {
            btnReady.setRect((Camera.main.width - btnWidth) / 2f, btnY, btnWidth, btnHeight);
            btnStart.visible = false;
        }

        updatePlayerList(new ArrayList<>());

        if (NetworkManager.instance().isHost()) {
            NetworkManager.instance().server().broadcastLobbyUpdate();
        }
    }

    public void updatePlayerList(ArrayList<LobbyPlayerDto> players) {
        for (LobbyPlayerSlot slot : playerSlots) {
            remove(slot);
        }
        playerSlots.clear();

        float startY = (syncModeDesc != null ? syncModeDesc.bottom() : titleText.bottom()) + 15;
        for (LobbyPlayerDto p : players) {
            LobbyPlayerSlot slot = new LobbyPlayerSlot();
            slot.set(p);
            slot.setRect((Camera.main.width - 140) / 2f, startY, 140, 24);
            add(slot);
            playerSlots.add(slot);
            startY += 28;
        }
    }

    @Override
    public void update() {
        super.update();

        GameClient client = NetworkManager.instance().localClient();
        if (client != null && client.lastLobbyUpdate != null) {

            if (client.lastLobbyUpdate.launchGame) {
                if (!NetworkManager.instance().isHost()) {
                    // КЛИЕНТ: Синхронизируем сид и класс перед генерацией
                    com.shatteredpixel.shatteredpixeldungeon.Dungeon.seed = client.lastLobbyUpdate.dungeonSeed;

                    com.shatteredpixel.shatteredpixeldungeon.network.dto.state.GameStateDto state = client.snapshot().state;
                    com.shatteredpixel.shatteredpixeldungeon.network.dto.state.PlayerStateDto myPlayer = null;
                    for (com.shatteredpixel.shatteredpixeldungeon.network.dto.state.PlayerStateDto p : state.players) {
                        if (p.playerId == client.playerId()) {
                            myPlayer = p;
                            break;
                        }
                    }

                    if (myPlayer != null) {
                        com.shatteredpixel.shatteredpixeldungeon.network.dto.state.HeroStateDto myHero = null;
                        for (com.shatteredpixel.shatteredpixeldungeon.network.dto.state.HeroStateDto h : state.heroes) {
                            if (h.entityId == myPlayer.controlledHeroId) {
                                myHero = h;
                                break;
                            }
                        }
                        if (myHero != null) {
                            try { com.shatteredpixel.shatteredpixeldungeon.GamesInProgress.selectedClass = HeroClass.valueOf(myHero.heroClass); } catch(Exception e){}
                        }
                    }

                    com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero = null;
                    com.shatteredpixel.shatteredpixeldungeon.Dungeon.daily = false;
                    com.shatteredpixel.shatteredpixeldungeon.Dungeon.init(); // Подготовка базовых систем
                }

                // Идем в InterlevelScene для правильной генерации уровня и заставки спуска!
                // Это должно сработать И для Хоста, И для Клиента.
                com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene.curTransition = null;
                com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene.mode = com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene.Mode.DESCEND;
                ShatteredPixelDungeon.switchScene(com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene.class);

                client.lastLobbyUpdate = null;
                return;
            }

            // Обновляем описание режима у клиента
            if (!NetworkManager.instance().isHost() && client.lastLobbyUpdate.settings != null) {
                int mode = client.lastLobbyUpdate.settings.syncMode;
                syncModeDesc.text("Режим игры: " + SYNC_MODES[mode]);
                syncModeDesc.setPos((Camera.main.width - syncModeDesc.width()) / 2f, titleText.bottom() + 5);
            }

            updatePlayerList(client.lastLobbyUpdate.players);

            if (NetworkManager.instance().isHost()) {
                btnStart.enable(client.lastLobbyUpdate.allReadyToStart);
            }

            client.lastLobbyUpdate = null;
        }
    }

    @Override
    protected void onBackPressed() {
        NetworkManager.instance().stopHost();
        com.shatteredpixel.shatteredpixeldungeon.windows.WndMultiplayer.isMultiplayerPending = false;
        ShatteredPixelDungeon.switchScene(TitleScene.class);
    }

    private static class LobbyPlayerSlot extends Component {
        private NinePatch bg;
        private Image heroSprite;
        private RenderedTextBlock nameText;
        private RenderedTextBlock statusText;
        private Image classIcon;

        @Override
        protected void createChildren() {
            super.createChildren();
            bg = Chrome.get(Chrome.Type.TOAST_TR);
            add(bg);
            nameText = PixelScene.renderTextBlock(9);
            add(nameText);
            statusText = PixelScene.renderTextBlock(6);
            add(statusText);
        }

        public void set(LobbyPlayerDto p) {
            nameText.text(p.name + (p.isHost ? " (Хост)" : ""));
            if (p.ready) {
                statusText.text("Готов к спуску!");
                statusText.hardlight(0x00FF00);
            } else {
                statusText.text("Ожидание...");
                statusText.resetColor();
            }

            try {
                HeroClass hClass = HeroClass.valueOf(p.heroClass);
                if (heroSprite == null) {
                    heroSprite = new Image(hClass.spritesheet(), 0, 0, 12, 15);
                    add(heroSprite);
                    classIcon = new Image(Icons.get(hClass));
                    add(classIcon);
                } else {
                    heroSprite.copy(new Image(hClass.spritesheet(), 0, 0, 12, 15));
                    classIcon.copy(Icons.get(hClass));
                }
            } catch (Exception e) {}

            layout();
        }

        @Override
        protected void layout() {
            super.layout();
            if (bg != null) { bg.x = x; bg.y = y; bg.size(width, height); }
            if (heroSprite != null) {
                heroSprite.x = x + 8;
                heroSprite.y = y + (height - heroSprite.height()) / 2f;
                nameText.setPos(heroSprite.x + heroSprite.width() + 6, y + (height - nameText.height() - statusText.height() - 2) / 2f);
                statusText.setPos(heroSprite.x + heroSprite.width() + 6, nameText.bottom() + 2);
                if (classIcon != null) {
                    classIcon.x = x + width - classIcon.width() - 8;
                    classIcon.y = y + (height - classIcon.height()) / 2f;
                }
            } else {
                nameText.setPos(x + (width - nameText.width()) / 2f, y + (height - nameText.height() - statusText.height() - 2) / 2f);
                statusText.setPos(x + (width - statusText.width()) / 2f, nameText.bottom() + 2);
            }
        }
    }
}