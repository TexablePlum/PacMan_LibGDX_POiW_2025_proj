package io.github.PacMan.Game_Objects.Views;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.PacMan.GameProperties;
import io.github.PacMan.Game_Objects.Models.Dot_Model;

/**
 * Widok (View) kropek w grze Pac-Man.
 *
 * <p>Klasa odpowiada za:
 * <ul>
 *   <li>Ładowanie tekstur dla zwykłych kropek oraz power-upów.</li>
 *   <li>Renderowanie kropek na ekranie przy użyciu SpriteBatch.</li>
 *   <li>Obsługę migania tekstury power-upu w określonych interwałach czasowych.</li>
 *   <li>Wyczyść zasoby (dispose) przy zamknięciu gry.</li>
 * </ul>
 * </p>
 */
public class Dot_View {

    /** Tekstura zwykłej kropki. */
    private final Texture dotTexture;

    /** Tekstura power-upa (duża kropka). */
    private final Texture powerUPTexture;

    /**
     * Konstruktor widoku kropek.
     * Ładuje tekstury dla zwykłej kropki oraz power-upa.
     */
    public Dot_View() {
        dotTexture = new Texture("dots/dot.png");
        powerUPTexture = new Texture("dots/powerup.png");
    }

    /**
     * Renderuje kropkę na ekranie.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Rozpoczyna batch rendering.</li>
     *   <li>Sprawdza, czy dana kropka jest zwykłą kropką czy power-upem.</li>
     *   <li>Dla zwykłej kropki rysuje teksturę przy użyciu stałego rozmiaru określonego w GameProperties.</li>
     *   <li>Dla power-upa stosuje efekt migania – tekstura jest renderowana lub pomijana w interwałach 250 ms.</li>
     *   <li>Zamyka batch rendering.</li>
     * </ul>
     * </p>
     *
     * @param batch SpriteBatch używany do renderowania.
     * @param model Model kropki zawierający m.in. pozycję sprite'a i informację, czy jest power-upem.
     */
    public void render(SpriteBatch batch, Dot_Model model) {
        batch.begin();
        if (!model.isPowerUp()) {
            // Rysowanie zwykłej kropki
            batch.draw(dotTexture,
                model.getSpritePosition().x,
                model.getSpritePosition().y,
                GameProperties.GameSpriteSizes.DOT_SIZE,
                GameProperties.GameSpriteSizes.DOT_SIZE
            );
        } else {
            // Efekt migania power-upa: co 250 ms tekstura jest rysowana lub pomijana.
            long time = System.currentTimeMillis();
            if ((time / 250) % 2 == 0) {
                batch.draw(powerUPTexture,
                    model.getSpritePosition().x,
                    model.getSpritePosition().y,
                    GameProperties.GameSpriteSizes.POWER_UP_SIZE,
                    GameProperties.GameSpriteSizes.POWER_UP_SIZE
                );
            }
        }
        batch.end();
    }

    /**
     * Zwalnia zasoby tekstur używanych przez widok kropek.
     * Należy wywołać tę metodę przy zamknięciu gry, aby uniknąć wycieków pamięci.
     */
    public void dispose() {
        dotTexture.dispose();
        powerUPTexture.dispose();
    }
}
