package io.github.PacMan.Game_Objects.Models.Barrier;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.PacMan.GameProperties;
import io.github.PacMan.Game_Objects.Models.GameObject_Model;
import io.github.PacMan.PacMan_Helper;

import java.awt.*;

public class Barrier_Model implements GameObject_Model {
    private Point position;
    private Rectangle bounds;
    private Rectangle hitBox;
    private Vector2 spritePosition;

    private Barrier_Type type;
    private Color textureColor;
    private Texture_Type textureType;

    /**
     * Konstruktor klasy Barrier_Model.
     *
     * @param startX Współrzędna X początkowej pozycji bariery.
     * @param startY Współrzędna Y początkowej pozycji bariery.
     * @param type Typ bariery.
     * @param textureType Typ tekstury bariery.
     */

    public Barrier_Model(float startX, float startY, Barrier_Type type, Color textureColor, Texture_Type textureType) {
        this.position = new Point(0,0);
        initialization(startX, startY, type, textureColor, textureType);
    }

    public Barrier_Model(float startX, float startY, Barrier_Type type, Color textureColor, Texture_Type textureType, int gridX, int gridY) {
        this.position = new Point(gridX,gridY);
        initialization(startX, startY, type, textureColor, textureType);
    }

    private void initialization(float startX, float startY, Barrier_Type type, Color textureColor, Texture_Type textureType)
    {
        var WIDTH = GameProperties.GameSpriteSizes.BARRIER_SIZE;
        var HEIGHT = GameProperties.GameSpriteSizes.BARRIER_SIZE;
        bounds = new Rectangle(startX, startY, WIDTH, HEIGHT);
        hitBox = new Rectangle(startX, startY, WIDTH, HEIGHT); // Domyślnie hitbox równy granicom
        spritePosition = PacMan_Helper.positioner(GameProperties.GameSpriteSizes.BARRIER_SIZE, bounds);
        this.type = type;
        this.textureColor = textureColor;
        this.textureType = textureType;
    }

    @Override
    public Point getGridPosition() {
        return position;
    }

    @Override
    public void setGridPosition(int newX, int newY) {
        position = new Point(newX,newY);
    }

    public Vector2 getSpritePosition() {
        return spritePosition;
    }

    public void setDisplayPosition(float newX, float newY) {
        bounds.setPosition(newX, newY);
        hitBox.setPosition(newX, newY);
        spritePosition = PacMan_Helper.positioner(GameProperties.GameSpriteSizes.BARRIER_SIZE, bounds);
    }

    @Override
    public Rectangle getBounds() { return bounds; }

    @Override
    public Rectangle getHitBox() { return hitBox; }

    @Override
    public void update(float v) {
        throw new UnsupportedOperationException("Not implemented method.");
    }

    /**
     * Pobiera typ bariery, do którego należy dany obiekt.
     * Typ bariery określa funkcjonalne przeznaczenie obiektu
     * w grze, np. czy jest to granica, element wnętrza, drzwi
     * lub inna struktura.

     * Przykładowe wartości:
     * {@link Barrier_Type#BORDER}: granica planszy.
     * {@link Barrier_Type#INTERIOR}: wewnętrzna ściana labiryntu.
     * {@link Barrier_Type#DOOR}: element interaktywny, np. drzwi.
     * {@link Barrier_Type#STRUCTURE}: specjalne struktury dekoracyjne lub funkcjonalne.
     *
     * @return Typ bariery jako wartość z enum {@link Barrier_Type}.
     */

    public Barrier_Type getType() { return type; }

    /**
     * Pobiera typ tekstury przypisany do bariery.
     * Typ tekstury definiuje wizualne przedstawienie bariery
     * w grze i określa, jaki zasób graficzny powinien być użyty
     * podczas renderowania obiektu.
     *
     * @return Typ tekstury bariery jako wartość z enumeracji {@link Texture_Type}.
     */

    public Texture_Type getTextureType() { return textureType; }

    /**
     * Pobiera kolor tekstury przypisany do bariery.
     * Kolor tekstury definiuje odcień lub modyfikację
     * koloru podstawowego tekstury, co pozwala na łatwe
     * różnicowanie wizualne obiektów w grze.
     *
     * @return Kolor tekstury bariery jako obiekt {@link Color}.
     */

    public Color getTextureColor() { return textureColor; }

    /**
     * Ustawia nowy kolor tekstury dla bariery.
     * Metoda pozwala dynamicznie zmieniać kolor
     * bariery w trakcie gry, co może być przydatne
     * przy tworzeniu różnych efektów wizualnych
     * lub zmieniających się stanów gry.
     *
     * @param newColor Nowy kolor tekstury jako obiekt {@link Color}.
     */

    public void setTextureColor(Color newColor) { textureColor = newColor; }
}
