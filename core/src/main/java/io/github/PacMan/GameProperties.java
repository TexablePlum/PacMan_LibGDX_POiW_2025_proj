package io.github.PacMan;

import com.badlogic.gdx.graphics.Color;
import io.github.PacMan.Game_Objects.Controllers.Moving_Directions;
import io.github.PacMan.Menu.AlignmentType;

import java.awt.*;

/**
 * Klasa konfiguracyjna zawierająca stałe i ustawienia dla gry Pac-Man.
 *
 * <p>Klasa {@code GameProperties} jest podzielona na kilka wewnętrznych klas, z których każda odpowiada
 * za określoną część konfiguracji gry, m.in. ustawienia gry, właściwości gridu, rozmiary sprite'ów, kolory,
 * ustawienia ekranu tytułowego, interfejsu użytkownika oraz atrybuty gry (takie jak prędkości i animacje).</p>
 */

public class GameProperties {
    public static class GameSettings {
        public static final String GAME_NAME = "Pac Man";
        public static final Point SCREEN_RESOLUTION = new Point(672, 864);
        public static boolean IS_VSYNC = true;
        public static int FRAME_RATE = 60;
    }

    public static class GameGridProps {
        public static final int POSITION_X = 0;
        public static final int POSITION_Y = 48;
        public static final int ROWS_COUNT = 31;
        public static final int COLS_COUNT = 28;
        public static final int CELL_SIZE = 24;
    }

    public static class GameSpriteSizes {
        public static final int BARRIER_SIZE = 24;
        public static final int DOT_SIZE = 4;
        public static final int POWER_UP_SIZE = 24;
        public static final int PACMAN_SIZE = 40;
        public static final int GHOST_SIZE = 40;
    }

    public static class GameColors {
        public static final Color BORDER_COLOR = Color.BLUE;
        public static final Color INTERIOR_COLOR = Color.BLUE;
        public static final Color STRUCTURE_COLOR = Color.BLUE;
        public static final Color DOOR_COLOR = new Color(1f, 0.72f, 1f, 1f);
    }

    // Nowa podklasa zawierająca stałe dla Title_Screen
    public static class TitleScreenProps {
        public static final String LOGO_TITLE = "pac-man";
        public static final int TITLE_SIZE = 72;
        public static final int MENU_ITEM_SIZE = 20;
        public static final int INFO_SIZE = 12;
        public static final int LOGO_POSITION_Y = 600;
        public static final int MENU_POSITION_Y = 275;
        public static final int MENU_ITEMS_OFFSET = 50;
        public static final Color MENU_TEXT_COLOR = Color.WHITE;
        public static final Color MENU_HOVER_COLOR = Color.CYAN;
        public static final AlignmentType MENU_ALIGNMENT_TYPE = AlignmentType.CENTER;
        public static final String APPLICATION_VERSION = "previous-alpha";
    }

    public static class UIProps {
        public static final Color TEXT_COLOR = Color.WHITE;
        public static final String OneUP_TEXT = "1UP";
        public static final String HIGH_SCORE_TEXT = "HIGH SCORE";
        public static final int FONT_SIZE = 20;
        public static final int PADDING = 75;
    }

    public static class GameAttributes {
        public static final float PAC_MAN_ANIMATION_SPEED = 0.06f;
        public static final float PAC_MAN_SPEED = 180f; // MAX 360!
        public static final float GHOST_ANIMATION_SPEED = 0.06f;
        public static final float GHOST_SPEED = 180f;
        public static String HIGH_SCORE_VALUE = "";
        public static final Moving_Directions START_MOVING_DIRECTIONS = Moving_Directions.NONE;
    }
}
