package io.github.PacMan.Game_Objects.Controllers;

/**
 * Enum reprezentujący kierunki ruchu w grze.
 *
 * <p>Definiuje dostępne kierunki ruchu, które mogą być używane do sterowania postaciami (np. Pac-Man,
 * duszkami) oraz określania ich orientacji na planszy.</p>
 */
public enum Moving_Directions {
    /** Kierunek ruchu w górę. */
    UP,

    /** Kierunek ruchu w dół. */
    DOWN,

    /** Kierunek ruchu w lewo. */
    LEFT,

    /** Kierunek ruchu w prawo. */
    RIGHT,

    /** Brak ruchu. */
    NONE;
}
