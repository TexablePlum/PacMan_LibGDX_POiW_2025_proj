package io.github.PacMan.Game_Objects.Views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.PacMan.GameProperties;
import io.github.PacMan.Game_Objects.Models.UserInterface_Model;
import io.github.PacMan.PacMan_Helper;

/**
 * Widok interfejsu użytkownika (UI) w grze Pac-Man.
 *
 * <p>Klasa odpowiada za:
 * <ul>
 *   <li>Wyświetlanie wyniku, highscore oraz liczby żyć.</li>
 *   <li>Renderowanie ikon żyć (Pac-Mana) i napisu "One UP" migającego co pewien czas.</li>
 *   <li>Wyświetlanie napisu "GAME OVER" w przypadku zakończenia gry.</li>
 * </ul>
 * </p>
 */
public class UserInterface_View {
    /** Czcionka używana do renderowania tekstu UI. */
    private final BitmapFont font;

    /** Tekstura reprezentująca ikonę Pac-Mana (używana do wyświetlania żyć). */
    private final Texture pacManTexture;

    /** Timer używany do migania napisu "One UP". */
    private float blinkTimer;

    /** Flaga wskazująca, czy aktualnie ma być wyświetlony napis "One UP". */
    private boolean showOneUp = true;

    /**
     * Konstruktor widoku interfejsu użytkownika.
     *
     * <p>W konstruktorze:
     * <ul>
     *   <li>Ładowana jest czcionka przy użyciu metody {@link PacMan_Helper#loadFont(String, int, Color, Color, int)}.</li>
     *   <li>Ładowana jest tekstura ikony Pac-Mana.</li>
     * </ul>
     * </p>
     */
    public UserInterface_View(){
        // Ładujemy czcionkę z określonymi parametrami (ścieżka, rozmiar, kolor tekstu, kolor obrysu)
        font = PacMan_Helper.loadFont(
            "fonts/Emulogic.ttf",
            GameProperties.UIProps.FONT_SIZE,
            GameProperties.UIProps.TEXT_COLOR,
            Color.BLACK,
            0
        );
        // Ładujemy teksturę reprezentującą ikonę Pac-Mana używaną przy wyświetlaniu żyć.
        pacManTexture = new Texture("pacman-left/3.png");
    }

    /**
     * Renderuje interfejs użytkownika.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Aktualizuje timer migania napisu "One UP" oraz przełącza flagę wyświetlania co 0.5 sekundy.</li>
     *   <li>Renderuje ikony żyć – wszystkie poza ostatnią, która może być przeznaczona dla dodatkowych informacji.</li>
     *   <li>Renderuje napisy: "One UP", "HIGH SCORE", aktualny wynik oraz highscore (jeśli jest dostępny).</li>
     *   <li>W przypadku zakończenia gry (game over), ustawia kolor czcionki na czerwony i wyświetla napis "GAME OVER".</li>
     * </ul>
     * </p>
     *
     * @param batch     SpriteBatch używany do renderowania.
     * @param model     Model interfejsu użytkownika, zawierający m.in. wynik, highscore, liczbę żyć oraz flagę game over.
     * @param deltaTime Czas, który upłynął od ostatniej klatki (w sekundach).
     */
    public void render(SpriteBatch batch, UserInterface_Model model, float deltaTime){
        // Aktualizacja timera migania.
        blinkTimer += deltaTime;
        if (blinkTimer >= 0.5f) {
            showOneUp = !showOneUp;
            blinkTimer = 0;
        }

        batch.begin();
        // Renderowanie ikon żyć – pomijamy ostatnią, która może być zarezerwowana (np. na wyświetlenie "One UP").
        for (int i = 0; i < model.getLivesValue() - 1; i++) {
            int x = GameProperties.UIProps.PADDING + i * (GameProperties.GameSpriteSizes.PACMAN_SIZE + 15);
            batch.draw(pacManTexture, x, 4,
                GameProperties.GameSpriteSizes.PACMAN_SIZE,
                GameProperties.GameSpriteSizes.PACMAN_SIZE);
        }

        // Migający napis "One UP" wyświetlany na górze ekranu.
        if (showOneUp) {
            font.draw(batch, GameProperties.UIProps.OneUP_TEXT, GameProperties.UIProps.PADDING, 860);
        }
        // Wyświetlenie napisu "HIGH SCORE" oraz aktualnego highscore.
        font.draw(batch, GameProperties.UIProps.HIGH_SCORE_TEXT, 225, 860);
        font.draw(batch,
            model.getScoreValue(),
            GameProperties.UIProps.PADDING + 75 - PacMan_Helper.getTextWidth(font, model.getScoreValue()),
            835);
        if (!model.getHighScoreValue().isEmpty()) {
            font.draw(batch,
                model.getHighScoreValue(),
                GameProperties.UIProps.PADDING + 310 - PacMan_Helper.getTextWidth(font, model.getHighScoreValue()),
                835);
        }

        // W przypadku stanu game over, zmiana koloru czcionki na czerwony i wyświetlenie napisu "GAME OVER!".
        if (model.isGameOver()) {
            font.setColor(Color.RED);
            float gameOverX = 240;
            float gameOverY = 380;
            font.draw(batch, model.getGameOverText(), gameOverX, gameOverY);
            // Przywracanie domyślnego koloru czcionki.
            font.setColor(GameProperties.UIProps.TEXT_COLOR);
        }
        batch.end();
    }

    /**
     * Zwalnia zasoby graficzne używane przez widok interfejsu użytkownika.
     *
     * <p>Metoda wywoływana przy zamknięciu gry w celu uniknięcia wycieków pamięci.</p>
     */
    public void dispose(){
        pacManTexture.dispose();
        font.dispose();
    }
}
