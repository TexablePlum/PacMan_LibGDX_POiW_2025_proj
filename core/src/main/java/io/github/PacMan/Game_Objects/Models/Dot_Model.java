package io.github.PacMan.Game_Objects.Models;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.PacMan.GameProperties;
import io.github.PacMan.PacMan_Helper;

import java.awt.Point;

/**
 * Klasa reprezentująca model kropki (dot) w grze Pac-Man.
 * Kropka może być standardową kropką lub power-upem, w zależności od flagi {@code isPowerUp}.
 * Klasa implementuje interfejs {@link GameObject_Model}, definiując podstawowe właściwości i zachowania obiektów gry.
 */
public class Dot_Model implements GameObject_Model {

    /**
     * Flaga określająca, czy kropka jest power-upem.
     */
    private boolean isPowerUp;

    /**
     * Pozycja kropki w siatce gry.
     */
    private Point position;

    /**
     * Pozycja, która będzie używana do renderowania sprite'a kropki.
     */
    private Vector2 spritePosition;

    /**
     * Granice obiektu, używane m.in. do renderowania.
     */
    private Rectangle bounds;

    /**
     * Hitbox obiektu, używany do sprawdzania kolizji.
     */
    private Rectangle hitBox;

    //==========================================================================
    // Konstruktory
    //==========================================================================

    /**
     * Tworzy nową instancję Dot_Model z domyślną pozycją siatki (0,0).
     *
     * @param startX    Pozycja X startowa w pikselach.
     * @param startY    Pozycja Y startowa w pikselach.
     * @param isPowerUp Flaga określająca, czy kropka ma być power-upem.
     */
    public Dot_Model(float startX, float startY, boolean isPowerUp) {
        this.position = new Point(0, 0);
        initialization(startX, startY, isPowerUp);
    }

    /**
     * Tworzy nową instancję Dot_Model z podaną pozycją siatki.
     *
     * @param startX    Pozycja X startowa w pikselach.
     * @param startY    Pozycja Y startowa w pikselach.
     * @param isPowerUp Flaga określająca, czy kropka ma być power-upem.
     * @param gridX     Pozycja X w siatce gry.
     * @param gridY     Pozycja Y w siatce gry.
     */
    public Dot_Model(float startX, float startY, boolean isPowerUp, int gridX, int gridY) {
        this.position = new Point(gridX, gridY);
        initialization(startX, startY, isPowerUp);
    }

    //==========================================================================
    // Metody prywatne
    //==========================================================================

    /**
     * Prywatna metoda inicjalizująca wspólne właściwości kropki.
     * Ustawia granice, pozycję sprite'a oraz hitbox w zależności od typu kropki.
     *
     * @param startX    Pozycja X startowa w pikselach.
     * @param startY    Pozycja Y startowa w pikselach.
     * @param isPowerUp Flaga określająca, czy kropka ma być power-upem.
     */
    private void initialization(float startX, float startY, boolean isPowerUp) {
        // Ustawienie granic obiektu na podstawie właściwości siatki gry.
        bounds = new Rectangle(
            startX,
            startY,
            GameProperties.GameGridProps.CELL_SIZE,
            GameProperties.GameGridProps.CELL_SIZE
        );
        this.isPowerUp = isPowerUp;

        // Inicjalizacja pozycji sprite'a oraz hitboxa w zależności od typu kropki.
        if (!isPowerUp) {
            // Standardowa kropka
            spritePosition = PacMan_Helper.positioner(GameProperties.GameSpriteSizes.DOT_SIZE, bounds);
            Vector2 pos = new Vector2(spritePosition);
            hitBox = new Rectangle(
                pos.x,
                pos.y,
                GameProperties.GameSpriteSizes.DOT_SIZE,
                GameProperties.GameSpriteSizes.DOT_SIZE
            );
        } else {
            // Power-up
            spritePosition = PacMan_Helper.positioner(GameProperties.GameSpriteSizes.POWER_UP_SIZE, bounds);
            Vector2 pos = new Vector2(spritePosition);
            hitBox = new Rectangle(
                pos.x,
                pos.y,
                GameProperties.GameSpriteSizes.POWER_UP_SIZE,
                GameProperties.GameSpriteSizes.POWER_UP_SIZE
            );
        }
    }

    //==========================================================================
    // Implementacja metod interfejsu GameObject_Model
    //==========================================================================

    /**
     * Aktualizuje stan obiektu. W obecnej wersji metoda nie jest zaimplementowana.
     *
     * @param v Parametr czasu lub inny wskaźnik używany w logice gry.
     * @throws UnsupportedOperationException Zawsze rzucany, gdy metoda jest wywoływana.
     */
    @Override
    public void update(float v) {
        throw new UnsupportedOperationException("Not implemented method.");
    }

    /**
     * Pobiera aktualną pozycję obiektu w siatce gry.
     *
     * @return Obiekt {@link Point} reprezentujący pozycję w siatce.
     */
    @Override
    public Point getGridPosition() {
        return position;
    }

    /**
     * Ustawia nową pozycję obiektu w siatce gry.
     *
     * @param newX Nowa pozycja X.
     * @param newY Nowa pozycja Y.
     */
    @Override
    public void setGridPosition(int newX, int newY) {
        position = new Point(newX, newY);
    }

    /**
     * Pobiera granice obiektu używane m.in. do renderowania.
     *
     * @return Obiekt {@link Rectangle} definiujący granice.
     */
    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Pobiera hitbox obiektu, używany do sprawdzania kolizji.
     *
     * @return Obiekt {@link Rectangle} definiujący hitbox.
     */
    @Override
    public Rectangle getHitBox() {
        return hitBox;
    }

    //==========================================================================
    // Dodatkowe metody specyficzne dla Dot_Model
    //==========================================================================

    /**
     * Ustawia nową pozycję wyświetlania obiektu.
     * Metoda aktualizuje zarówno granice, jak i pozycję hitboxa oraz sprite'a,
     * w zależności od typu kropki (standardowa lub power-up).
     *
     * @param posX Nowa pozycja X w pikselach.
     * @param posY Nowa pozycja Y w pikselach.
     */
    public void setDisplayPosition(float posX, float posY) {
        bounds.setPosition(posX, posY);

        if (!isPowerUp) {
            // Dla standardowej kropki: przeliczanie pozycji sprite'a oraz hitboxa.
            spritePosition = PacMan_Helper.positioner(GameProperties.GameSpriteSizes.DOT_SIZE, bounds);
            hitBox.setPosition(PacMan_Helper.positioner(GameProperties.GameSpriteSizes.DOT_SIZE, bounds));
        } else {
            // Dla power-upu: ustawiamy hitbox na pozycję granic oraz przeliczamy sprite'a.
            spritePosition = PacMan_Helper.positioner(GameProperties.GameSpriteSizes.POWER_UP_SIZE, bounds);
            hitBox.setPosition(posX, posY);
        }
    }

    /**
     * Pobiera pozycję sprite'a, która jest używana podczas renderowania.
     *
     * @return Obiekt {@link Vector2} reprezentujący pozycję sprite'a.
     */
    public Vector2 getSpritePosition() {
        return spritePosition;
    }

    /**
     * Sprawdza, czy kropka jest power-upem.
     *
     * @return {@code true} jeśli kropka jest power-upem, {@code false} w przeciwnym razie.
     */
    public boolean isPowerUp() {
        return isPowerUp;
    }
}
