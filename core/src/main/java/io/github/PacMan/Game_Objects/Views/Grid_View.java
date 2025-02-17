package io.github.PacMan.Game_Objects.Views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.PacMan.GameProperties;
import io.github.PacMan.Game_Objects.Models.*;
import io.github.PacMan.Game_Objects.Models.Barrier.Barrier_Model;
import io.github.PacMan.PacMan_Helper;

/**
 * Widok (View) planszy gry (Grid).
 *
 * <p>Klasa ta odpowiada za renderowanie wszystkich obiektów znajdujących się w gridzie:
 * <ul>
 *   <li>Statycznych obiektów, takich jak bariery i kropki</li>
 *   <li>Postaci Pac-Mana</li>
 *   <li>Duszków</li>
 * </ul>
 * Dodatkowo zawiera metodę do rysowania siatki (debug) przy użyciu ShapeRenderer.
 * </p>
 */
public class Grid_View {
    /** Renderer służący do rysowania kształtów (np. siatki) dla celów debugowania. */
    private final ShapeRenderer shapeRenderer;

    /** Model planszy (grid) zawierający wszystkie obiekty gry. */
    private final Grid_Model grid;

    /** Widok bariery – współdzielony dla wszystkich obiektów typu Barrier_Model. */
    private final Barrier_View barrierView;

    /** Widok kropek (Dot) w grze. */
    private final Dot_View dotView;

    /** Widok Pac-Mana. */
    private final PacMan_View pacManView;

    /** Widok duszków. */
    private final Ghost_View ghostView;

    /**
     * Konstruktor widoku gridu.
     *
     * <p>Inicjalizuje ShapeRenderer oraz tworzy instancje widoków dla poszczególnych typów obiektów,
     * wykorzystując przekazany model gridu.</p>
     *
     * @param grid Model planszy zawierający obiekty gry.
     */
    public Grid_View(Grid_Model grid) {
        this.shapeRenderer = new ShapeRenderer();
        this.grid = grid;
        this.barrierView = new Barrier_View(); // Współdzielony widok dla wszystkich barier
        this.dotView = new Dot_View();
        this.pacManView = new PacMan_View();
        this.ghostView = new Ghost_View();
    }

    /**
     * Renderuje wszystkie obiekty znajdujące się w gridzie.
     *
     * <p>Algorytm renderowania:
     * <ul>
     *   <li>Iteracja po wszystkich kolumnach i wierszach gridu.</li>
     *   <li>Dla każdego obiektu:
     *       <ul>
     *         <li>Jeśli obiekt jest instancją Barrier_Model, renderowany jest przy użyciu Barrier_View.</li>
     *         <li>Jeśli obiekt jest instancją Dot_Model, renderowany jest przy użyciu Dot_View.</li>
     *       </ul>
     *   </li>
     *   <li>Następnie wykonywana jest osobna iteracja dla Pac-Mana i duszków, aby zapewnić poprawną kolejność renderowania.</li>
     * </ul>
     * </p>
     *
     * @param batch SpriteBatch używany do renderowania obiektów.
     * @param v     Delta time (czas, który upłynął od ostatniej klatki).
     */
    public void render(SpriteBatch batch, float v) {
        // Renderowanie statycznych obiektów: bariery i kropki.
        for (int col = 0; col < grid.getCols(); col++) {
            for (int row = 0; row < grid.getRows(); row++) {
                GameObject_Model object = grid.getObjectAt(col, row);
                if (object instanceof Barrier_Model) {
                    barrierView.render(batch, (Barrier_Model) object);
                } else if (object instanceof Dot_Model) {
                    dotView.render(batch, (Dot_Model) object);
                    // Opcjonalnie: renderowanie debug info
                    // PacMan_Helper.Game_Object_DTrender(batch, object, shapeRenderer);
                }
            }
        }

        // Renderowanie Pac-Mana.
        for (int col = 0; col < grid.getCols(); col++) {
            for (int row = 0; row < grid.getRows(); row++) {
                GameObject_Model object = grid.getObjectAt(col, row);
                if (object instanceof PacMan_Model) {
                    pacManView.render(batch, (PacMan_Model) object, v);
                    // Opcjonalnie: renderowanie debug info
                    // PacMan_Helper.Game_Object_DTrender(batch, object, shapeRenderer);
                }
            }
        }

        // Renderowanie duszków.
        for (int col = 0; col < grid.getCols(); col++) {
            for (int row = 0; row < grid.getRows(); row++) {
                GameObject_Model object = grid.getObjectAt(col, row);
                if (object instanceof Ghost_Model) {
                    ghostView.render(batch, (Ghost_Model) object, v);
                    // Opcjonalnie: renderowanie debug info
                    // PacMan_Helper.Game_Object_DTrender(batch, object, shapeRenderer);
                }
            }
        }
    }

    /**
     * Rysuje siatkę (grid) przy użyciu ShapeRenderer.
     *
     * <p>Metoda służy głównie do debugowania. Rysuje prostokąty wokół każdego pola gridu,
     * dodając niewielki offset, aby wyrównać różnice w renderowaniu między ShapeRenderer a SpriteBatch.</p>
     *
     * @param batch SpriteBatch używany do rysowania (opcjonalnie, może być wykorzystywany do synchronizacji).
     */
    public void DTrender(SpriteBatch batch) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.LIME);

        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getCols(); col++) {
                float x = grid.getPosition().x + col * GameProperties.GameGridProps.CELL_SIZE;
                float y = grid.getPosition().y + row * GameProperties.GameGridProps.CELL_SIZE;

                // Rysowanie prostokąta z offsetem +/-1, aby wyrównać różnice w rysowaniu.
                shapeRenderer.rect(x + 1, y,
                    GameProperties.GameGridProps.CELL_SIZE - 1,
                    GameProperties.GameGridProps.CELL_SIZE - 1
                );
            }
        }
        shapeRenderer.end();
    }

    /**
     * Zwalnia zasoby używane przez widok gridu.
     *
     * <p>Wywołanie metody dispose() powinno nastąpić przy zamknięciu gry, aby uniknąć wycieków pamięci.
     * Metoda zwalnia zasoby ShapeRenderer oraz wywołuje dispose() na widokach, które zarządzają teksturami.</p>
     */
    public void dispose() {
        barrierView.dispose(); // Zwalnianie zasobów widoku bariery.
        dotView.dispose();
        shapeRenderer.dispose();
        pacManView.dispose();
    }
}
