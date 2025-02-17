package io.github.PacMan;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.PacMan.Game_Screens.Title_Screen;

/**
 * Główna klasa gry Pac-Man.
 *
 * <p>Klasa {@code PacMan} rozszerza klasę {@link Game} z biblioteki LibGDX i pełni rolę punktu wejścia
 * do gry. Odpowiada za:
 * <ul>
 *   <li>Inicjalizację głównego obiektu renderującego {@link SpriteBatch},</li>
 *   <li>Ustawienie początkowego ekranu (w tym przypadku ekranu tytułowego, {@link Title_Screen}),</li>
 *   <li>Wywoływanie metody renderującej i czyszczenie ekranu przy każdej klatce,</li>
 *   <li>Zwalnianie zasobów przy zamknięciu gry.</li>
 * </ul>
 * </p>
 */
public class PacMan extends Game {
    /** Główny obiekt renderujący wykorzystywany do rysowania sprite'ów i tekstur. */
    private SpriteBatch batch;

    /**
     * Metoda inicjalizująca grę.
     *
     * <p>Wywoływana na początku cyklu życia gry. Inicjalizuje {@link SpriteBatch} oraz ustawia
     * początkowy ekran, którym jest ekran tytułowy (Title_Screen).</p>
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new Title_Screen(this));
    }

    /**
     * Główna metoda renderująca.
     *
     * <p>Metoda ta jest wywoływana w każdej klatce gry. Najpierw czyści ekran do czarnego koloru,
     * a następnie przekazuje kontrolę do aktualnie ustawionego ekranu.</p>
     */
    @Override
    public void render() {
        // Czyszczenie ekranu na czarno.
        ScreenUtils.clear(Color.BLACK);
        // Wywołanie renderowania aktualnego ekranu (np. Title_Screen, Game_Screen).
        super.render();
    }

    /**
     * Metoda zwalniająca zasoby.
     *
     * <p>Wywoływana przy zamknięciu gry. Zwalnia zasoby wykorzystane przez {@link SpriteBatch}
     * oraz wykonuje dodatkowe sprzątanie w metodzie {@code dispose()} klasy nadrzędnej.</p>
     */
    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }
}
