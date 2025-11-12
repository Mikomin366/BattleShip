import java.awt.*;

enum ColorTheme {
    STANDARD("Стандартная",
            new Color(240, 248, 255), // backgroundColor
            new Color(0, 0, 128),     // titleColor
            Color.BLACK,              // textColor
            new Color(70, 130, 180),  // primaryButtonColor
            new Color(65, 105, 225),  // secondaryButtonColor
            new Color(178, 34, 34),   // exitButtonColor
            new Color(173, 216, 230), // boardBackground
            new Color(0, 0, 128)      // gridColor
    ),

    DARK("Темная",
            new Color(40, 44, 52),     // backgroundColor
            new Color(220, 220, 220),  // titleColor
            new Color(220, 220, 220),  // textColor
            new Color(86, 98, 112),    // primaryButtonColor
            new Color(56, 66, 82),     // secondaryButtonColor
            new Color(152, 57, 57),    // exitButtonColor
            new Color(30, 34, 40),     // boardBackground
            new Color(150, 150, 150)   // gridColor
    );

    public final String displayName;
    public final Color backgroundColor;
    public final Color titleColor;
    public final Color textColor;
    public final Color primaryButtonColor;
    public final Color secondaryButtonColor;
    public final Color exitButtonColor;
    public final Color boardBackground;
    public final Color gridColor;

    ColorTheme(String displayName, Color backgroundColor, Color titleColor, Color textColor,
               Color primaryButtonColor, Color secondaryButtonColor, Color exitButtonColor,
               Color boardBackground, Color gridColor) {
        this.displayName = displayName;
        this.backgroundColor = backgroundColor;
        this.titleColor = titleColor;
        this.textColor = textColor;
        this.primaryButtonColor = primaryButtonColor;
        this.secondaryButtonColor = secondaryButtonColor;
        this.exitButtonColor = exitButtonColor;
        this.boardBackground = boardBackground;
        this.gridColor = gridColor;
    }

    public static ColorTheme fromDisplayName(String displayName) {
        for (ColorTheme theme : values()) {
            if (theme.displayName.equals(displayName)) {
                return theme;
            }
        }
        return STANDARD;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
