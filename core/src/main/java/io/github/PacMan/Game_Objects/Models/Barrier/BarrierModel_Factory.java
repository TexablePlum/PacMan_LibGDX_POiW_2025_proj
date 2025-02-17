package io.github.PacMan.Game_Objects.Models.Barrier;

import com.badlogic.gdx.graphics.Color;
import io.github.PacMan.GameProperties;

/**
 * Fabryka modeli barier gry Pac-Man.
 */

public class BarrierModel_Factory {

    /**
     * Tworzy nowy model bariery na podstawie podanych parametrów.
     *
     * @param startX       Początkowa współrzędna X bariery.
     * @param startY       Początkowa współrzędna Y bariery.
     * @param type         Typ bariery {@link Barrier_Type}.
     * @param textureType  Typ tekstury {@link Texture_Type} przypisanej do bariery.
     * @return Nowy obiekt {@link Barrier_Model} reprezentujący barierę.
     * @throws IllegalArgumentException Jeśli typ tekstury nie jest zgodny z typem bariery.
     */

    public static Barrier_Model createBarrier(float startX, float startY, Barrier_Type type, Texture_Type textureType) {
        var color = textureColorForType(type);
        if (!isTextureValidForType(type, textureType)) {
            throw new IllegalArgumentException("Niedozwolony typ tekstury " + textureType + " dla typu bariery " + type);
        }
        return new Barrier_Model(startX, startY, type, color, textureType);
    }

    private static Color textureColorForType(Barrier_Type type) {
        return switch (type) {
            case BORDER -> GameProperties.GameColors.BORDER_COLOR;
            case STRUCTURE -> GameProperties.GameColors.STRUCTURE_COLOR;
            case INTERIOR -> GameProperties.GameColors.INTERIOR_COLOR;
            case DOOR -> GameProperties.GameColors.DOOR_COLOR;
            default -> Color.BLUE;
        };
    }

    private static boolean isTextureValidForType(Barrier_Type type, Texture_Type textureType) {
        return switch (type) {
            case BORDER -> true;
            case STRUCTURE, INTERIOR, DOOR -> !textureType.name().contains("BORDER");
            default -> false;
        };
    }
}
