package io.github.PacMan.Menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import io.github.PacMan.PacMan_Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Reprezentuje "oświadczenie menu" – pionowo wyświetlaną listę opcji.
 *
 * <p>Klasa przechowuje listę opcji typu {@link MenuOption}, które można konfigurować poprzez przypisanie
 * tekstu, akcji (Runnable) oraz stanu (włączone/wyłączone). Dodatkowo obsługuje wyrównanie opcji,
 * obliczanie ich pozycji w menu, a także rysowanie dodatkowych elementów, takich jak strzałka przy
 * opcji, nad którą znajduje się kursor myszy (hover).
 * </p>
 */
public class MenuStatement {
    /** Pozycja bazowa menu (punkt startowy). */
    private final Vector2 position;

    /** Font używany do renderowania tekstu opcji w menu. */
    private final BitmapFont font;

    /** Lista opcji menu (MenuOption). */
    private final List<MenuOption> items;

    /** Kolor tekstu w stanie normalnym. */
    private final Color textColor;

    /** Kolor tekstu, gdy kursor znajduje się nad opcją (hover). */
    private final Color hoverTextColor;

    /** Kolor tekstu dla opcji wyłączonych. */
    private final Color disableColor;

    /** Typ wyrównania menu (LEFT, CENTER, RIGHT). */
    private final AlignmentType alignmentType;

    /** Flaga określająca, czy przy opcji ma być rysowana strzałka przy hover. */
    private final boolean isArrow;

    /** Tekstura strzałki wyświetlanej przy hover. */
    private final Texture arrow;

    /** Odstęp pionowy między kolejnymi opcjami w menu. */
    private final int itemsOffset;

    /** Całkowita szerokość menu (wyliczana w metodzie recalcPositions). */
    private int width;

    /** Całkowita wysokość menu (wyliczana w metodzie recalcPositions). */
    private int height;

    // Stałe rozmiary strzałki.
    private static final float ARROW_WIDTH = 14f;
    private static final float ARROW_HEIGHT = 24f;

    /**
     * Konstruktor menu.
     *
     * <p>Inicjalizuje menu z określonymi parametrami:
     * <ul>
     *   <li>Pozycja bazowa – punkt startowy, od którego liczone są pozycje opcji.</li>
     *   <li>Font – używany do renderowania tekstu.</li>
     *   <li>textColor – kolor tekstu w stanie normalnym.</li>
     *   <li>hoverTextColor – kolor tekstu przy najechaniu kursorem myszy.</li>
     *   <li>alignmentType – określa wyrównanie opcji menu (LEFT, CENTER, RIGHT).</li>
     *   <li>itemsOffset – odstęp pionowy między opcjami.</li>
     *   <li>enableArrow – flaga określająca, czy ma być rysowana strzałka przy opcji hover.</li>
     * </ul>
     * </p>
     *
     * @param position        Pozycja początkowa menu (Vector2).
     * @param font            Font używany do rysowania tekstu.
     * @param textColor       Kolor tekstu w stanie normalnym.
     * @param hoverTextColor  Kolor tekstu przy hover.
     * @param alignmentType   Wyrównanie opcji (LEFT, CENTER, RIGHT).
     * @param itemsOffset     Odstęp pionowy między opcjami.
     * @param enableArrow     Czy rysować strzałkę przy opcji hover.
     */
    public MenuStatement(Vector2 position,
                         BitmapFont font,
                         Color textColor,
                         Color hoverTextColor,
                         AlignmentType alignmentType,
                         int itemsOffset,
                         boolean enableArrow) {

        this.items = new ArrayList<>();
        this.position = position;
        this.font = font;
        this.textColor = textColor;
        this.hoverTextColor = hoverTextColor;
        // Wyliczamy kolor dla opcji wyłączonych, zmniejszając jasność koloru tekstu.
        disableColor = new Color(textColor.r - 0.3f, textColor.g - 0.3f, textColor.b - 0.3f, 1);
        this.alignmentType = alignmentType;
        this.itemsOffset = itemsOffset;
        this.isArrow = enableArrow;

        // Wczytujemy teksturę strzałki, jeśli opcja jest włączona.
        if (enableArrow) {
            this.arrow = new Texture("title_screen/menu-arrow.png");
        } else {
            this.arrow = null;
        }
    }

    /**
     * Ustawia pozycję bazową menu i przelicza pozycje wszystkich opcji.
     *
     * @param x Pozycja X.
     * @param y Pozycja Y.
     */
    public void setPosition(float x, float y) {
        this.position.set(x, y);
        recalcPositions();
    }

    /**
     * Dodaje nową opcję do menu.
     *
     * <p>Tworzy nowy obiekt {@link MenuOption} z podanym tekstem i akcją (Runnable) i dodaje go do listy opcji.
     * Po dodaniu, ponownie przelicza pozycje opcji w menu.</p>
     *
     * @param text   Tekst wyświetlany jako opcja menu.
     * @param action Akcja (Runnable) wywoływana przy kliknięciu opcji; może być null.
     */
    public void addOption(String text, Runnable action) {
        MenuOption option = new MenuOption(font, text, action);
        items.add(option);
        recalcPositions();
    }

    /**
     * Przelicza pozycje wszystkich opcji w menu.
     *
     * <p>Metoda oblicza całkowitą szerokość i wysokość menu, a następnie rozkłada opcje
     * w pionie, zaczynając od górnej części (pozycja bazowa + wysokość menu) i schodząc w dół.
     * Pozycja X każdej opcji zależy od wybranego wyrównania (alignmentType).</p>
     */
    private void recalcPositions() {
        width = 0;
        height = 0;

        // 1) Obliczanie wymiarów całego menu (bounding box).
        for (int i = 0; i < items.size(); i++) {
            MenuOption option = items.get(i);
            float textWidth  = PacMan_Helper.getTextWidth(font, option.getOptionName());
            float textHeight = PacMan_Helper.getTextHeight(font, option.getOptionName());

            // Uaktualnij maksymalną szerokość.
            if (textWidth > width) {
                width = (int) textWidth;
            }

            // Dodaj wysokość opcji.
            height += (int) textHeight;

            // Dodaj odstęp między opcjami (poza ostatnią opcją).
            if (i < items.size() - 1) {
                height += itemsOffset;
            }
        }

        // 2) Ustal początkową pozycję Y (górny róg menu).
        float currentY = position.y + height;

        // 3) Ustal pozycje poszczególnych opcji, rozkładając je od góry do dołu.
        for (int i = 0; i < items.size(); i++) {
            MenuOption option = items.get(i);

            float textWidth  = PacMan_Helper.getTextWidth(font, option.getOptionName());
            float textHeight = PacMan_Helper.getTextHeight(font, option.getOptionName());

            // Wylicz pozycję X w zależności od wyrównania.
            float x = switch (alignmentType) {
                case LEFT -> position.x;
                case CENTER -> position.x + (width - textWidth) / 2f;
                case RIGHT -> position.x + (width - textWidth);
            };

            // Zmniejsz currentY o wysokość tekstu.
            currentY -= textHeight;

            // Ustaw pozycję bieżącej opcji.
            option.setPosition(new Vector2(x, currentY));

            // Dodaj odstęp między opcjami, jeśli nie jest to ostatnia opcja.
            if (i < items.size() - 1) {
                currentY -= itemsOffset;
            }
        }
    }

    /**
     * Rysuje wszystkie opcje menu na ekranie.
     *
     * <p>Metoda iteruje przez listę opcji i:
     * <ul>
     *   <li>Sprawdza, czy myszka znajduje się nad daną opcją (hover),</li>
     *   <li>Ustawia kolor tekstu w zależności od stanu (normalny, hover, wyłączony),</li>
     *   <li>Renderuje opcję, a jeśli włączone jest rysowanie strzałki i kursor jest nad opcją, rysuje strzałkę.</li>
     * </ul>
     * </p>
     *
     * @param batch    SpriteBatch używany do rysowania.
     * @param mousePos Aktualna pozycja myszy.
     */
    public void render(SpriteBatch batch, Vector2 mousePos) {
        for (MenuOption option : items) {
            // Sprawdzenie, czy kursor znajduje się nad opcją.
            boolean hovered = option.isHovered(mousePos);

            // Ustawienie koloru w zależności od stanu opcji.
            if (!option.isEnabled()) {
                option.getFont().setColor(disableColor);
            } else if (hovered) {
                option.getFont().setColor(hoverTextColor);
            } else {
                option.getFont().setColor(textColor);
            }

            // Renderowanie opcji.
            option.render(batch);

            // Rysowanie strzałki przy opcji, jeśli włączone i kursor jest nad opcją.
            if (isArrow && hovered) {
                float textHeight = PacMan_Helper.getTextHeight(option.getFont(), option.getOptionName());

                // Pozycja strzałki: po lewej stronie opcji z niewielkim przesunięciem.
                float arrowX = option.getPosition().x - ARROW_WIDTH - 20;
                float arrowY = option.getPosition().y - textHeight / 2f - ARROW_HEIGHT / 2f;

                batch.draw(arrow, arrowX, arrowY, ARROW_WIDTH, ARROW_HEIGHT);
            }
        }
    }

    /**
     * Obsługuje kliknięcie myszy.
     *
     * <p>Metoda iteruje przez opcje menu i, jeśli kursor znajduje się nad daną opcją,
     * wywołuje przypisaną akcję (Runnable) dla tej opcji.</p>
     *
     * @param mousePos Pozycja myszy (zazwyczaj pobierana z Gdx.input).
     */
    public void handleClick(Vector2 mousePos) {
        for (MenuOption option : items) {
            if (option.isHovered(mousePos)) {
                option.onClick();
                break;
            }
        }
    }

    /**
     * Zwalnia zasoby graficzne używane przez menu.
     *
     * <p>Metoda powinna być wywołana, gdy menu nie jest już potrzebne.</p>
     */
    public void dispose() {
        if (arrow != null) {
            arrow.dispose();
        }
    }

    // Gettery

    /**
     * Zwraca szerokość całego menu.
     *
     * @return Szerokość menu w pikselach.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Zwraca wysokość całego menu.
     *
     * @return Wysokość menu w pikselach.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Zwraca opcję menu o podanym indeksie.
     *
     * @param index Indeks opcji (0 - pierwszy, górny element menu).
     * @return Obiekt MenuOption.
     */
    public MenuOption getMenuOption(int index) {
        return items.get(index);
    }
}
