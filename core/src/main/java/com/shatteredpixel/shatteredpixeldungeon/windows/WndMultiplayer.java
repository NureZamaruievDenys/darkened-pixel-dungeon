package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

public class WndMultiplayer extends Window {

    private static final int WIDTH = 140;

    public static boolean isMultiplayerPending = false;
    public static boolean pendingHost = false;
    public static String pendingIp = "127.0.0.1";
    public static String pendingName = "Player";

    private RedButton btnName;

    public WndMultiplayer() {
        super();

        // nickname
        btnName = new RedButton("Ник: " + pendingName) {
            @Override
            protected void onClick() {
                super.onClick();
                ShatteredPixelDungeon.scene().addToFront(new WndTextInput(
                        "Ваш Никнейм",
                        "Введите имя, которое увидят другие игроки.",
                        pendingName,
                        20,
                        false,
                        "ОК",
                        "Отмена") {
                    @Override
                    public void onSelect(boolean positive, String text) {
                        if (positive && !text.isEmpty()) {
                            pendingName = text;
                            btnName.text("Ник: " + pendingName);
                        }
                    }
                });
            }
        };
        btnName.setRect(0, 0, WIDTH, 24);
        add(btnName);

        // host
        RedButton btnHost = new RedButton("Создать игру (Хост)") {
            @Override
            protected void onClick() {
                super.onClick();
                isMultiplayerPending = true;
                pendingHost = true;
                hide();
                ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
            }
        };
        btnHost.setRect(0, btnName.bottom() + 15, WIDTH, 30);
        add(btnHost);

        // client
        RedButton btnJoin = new RedButton("Подключиться") {
            @Override
            protected void onClick() {
                super.onClick();
                ShatteredPixelDungeon.scene().addToFront(new WndTextInput(
                        "IP-адрес Хоста",
                        "Введите IP друга для подключения:",
                        pendingIp,
                        15,
                        false,
                        "Подключиться",
                        "Отмена") {
                    @Override
                    public void onSelect(boolean positive, String text) {
                        if (positive && !text.isEmpty()) {
                            pendingIp = text;
                            isMultiplayerPending = true;
                            pendingHost = false;
                            hide();
                            ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
                        }
                    }
                });
            }
        };
        btnJoin.setRect(0, btnHost.bottom() + 5, WIDTH, 30);
        add(btnJoin);

        resize(WIDTH, (int)btnJoin.bottom());
    }
}