package io.github.PacMan.Menu;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.PacMan.PacMan_Helper;

/**
 * Pojedyncza opcja menu.
 *
 * <p>Klasa ta reprezentuje jedną opcję w menu gry. Każda opcja zawiera:
 * <ul>
 *   <li>Tekst, który jest wyświetlany przy użyciu określonego fontu,</li>
 *   <li>Pozycję na ekranie,</li>
 *   <li>Opcjonalną akcję (Runnable) do wykonania po kliknięciu,</li>
 *   <li>Stan aktywności (enabled), który decyduje o tym, czy opcja reaguje na interakcje.</li>
 * </ul>
 * </p>
 */
public class MenuOption {

    /** Font używany do renderowania tekstu opcji. */
    private final BitmapFont font;

    /** Tekst wyświetlany jako nazwa opcji. */
    private final String optionName;

    /** Pozycja opcji na ekranie. */
    private Vector2 position;

    /** Akcja, która zostanie wykonana po kliknięciu opcji (może być null). */
    private final Runnable action;

    /** Flaga określająca, czy opcja jest aktywna (enabled). */
    private boolean enabled;

    /**
     * Konstruktor opcji menu.
     *
     * @param font       Font używany do wyświetlania tekstu.
     * @param optionName Tekst opcji.
     * @param action     Akcja wywoływana przy kliknięciu (może być null).
     */
    public MenuOption(BitmapFont font, String optionName, Runnable action) {
        this.font = font;
        this.optionName = optionName;
        this.action = action;
        this.enabled = true;  // Domyślnie opcja jest aktywna.
    }

    /**
     * Ustawia stan aktywności opcji.
     *
     * @param enabled {@code true} – opcja jest aktywna; {@code false} – opcja jest wyłączona.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Zwraca informację, czy opcja jest aktywna.
     *
     * @return {@code true} jeśli opcja jest aktywna, w przeciwnym wypadku {@code false}.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Renderuje tekst opcji na ekranie.
     *
     * @param batch SpriteBatch używany do renderowania.
     */
    public void render(SpriteBatch batch) {
        font.draw(batch, optionName, position.x, position.y);
    }

    /**
     * Sprawdza, czy kursor myszy znajduje się nad obszarem tekstu opcji.
     *
     * <p>Metoda oblicza prostokątny obszar, w którym znajduje się tekst opcji:
     * <ul>
     *   <li>X: [position.x, position.x + textWidth]</li>
     *   <li>Y: [position.y - textHeight, position.y]</li>
     * </ul>
     * Zwraca {@code true}, jeśli pozycja myszy mieści się w tym obszarze, oraz opcja jest aktywna.
     * </p>
     *
     * @param mousePos Pozycja myszy jako Vector2.
     * @return {@code true} jeśli kursor znajduje się nad tekstem opcji, w przeciwnym wypadku {@code false}.
     */
    public boolean isHovered(Vector2 mousePos) {
        if (!enabled) return false;  // Jeśli opcja jest wyłączona, nie reagujemy na hover.

        float textWidth = PacMan_Helper.getTextWidth(font, optionName);
        float textHeight = PacMan_Helper.getTextHeight(font, optionName);

        return mousePos.x >= position.x && mousePos.x <= position.x + textWidth &&
            mousePos.y <= position.y && mousePos.y >= position.y - textHeight;
    }

    /**
     * Wywołuje akcję przypisaną do opcji, jeśli opcja jest aktywna.
     *
     * <p>Jeśli opcja jest wyłączona lub nie ma przypisanej akcji (null), metoda nic nie robi.</p>
     */
    public void onClick() {
        if (enabled && action != null) {
            action.run();
        }
    }

    // Gettery / Settery

    /**
     * Zwraca tekst opcji.
     *
     * @return Tekst opcji.
     */
    public String getOptionName() {
        return optionName;
    }

    /**
     * Zwraca font używany do wyświetlania opcji.
     *
     * @return BitmapFont używany przez opcję.
     */
    public BitmapFont getFont() {
        return font;
    }

    /**
     * Zwraca aktualną pozycję opcji.
     *
     * @return Pozycja opcji jako Vector2.
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Ustawia pozycję opcji.
     *
     * @param position Nowa pozycja opcji jako Vector2.
     */
    public void setPosition(Vector2 position) {
        this.position = position;
    }
}
