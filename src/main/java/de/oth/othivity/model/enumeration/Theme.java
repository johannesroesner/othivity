package de.oth.othivity.model.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Theme {
    LIGHT("light", false, "theme.light", "☀️"),
    DARK("dark", true, "theme.dark", "🌑"),
    CUPCAKE("cupcake", false, "theme.cupcake", "🧁"),
    BUMBLEBEE("bumblebee", false, "theme.bumblebee", "🐝"),
    EMERALD("emerald", false, "theme.emerald", "✳️"),
    CORPORATE("corporate", false, "theme.corporate", "🏢"),
    SYNTHWAVE("synthwave", true, "theme.synthwave", "🌃"),
    RETRO("retro", false, "theme.retro", "📼"),
    CYBERPUNK("cyberpunk", false, "theme.cyberpunk", "🤖"),
    VALENTINE("valentine", false, "theme.valentine", "🌸"),
    HALLOWEEN("halloween", true, "theme.halloween", "🎃"),
    GARDEN("garden", false, "theme.garden", "🌷"),
    FOREST("forest", true, "theme.forest", "🌲"),
    AQUA("aqua", false, "theme.aqua", "💧"),
    LOFI("lofi", false, "theme.lofi", "📻"),
    PASTEL("pastel", false, "theme.pastel", "🎨"),
    FANTASY("fantasy", false, "theme.fantasy", "🧚"),
    WIREFRAME("wireframe", false, "theme.wireframe", "📝"),
    BLACK("black", true, "theme.black", "🖤"),
    LUXURY("luxury", true, "theme.luxury", "💎"),
    DRACULA("dracula", true, "theme.dracula", "🧛"),
    CMYK("cmyk", false, "theme.cmyk", "🖨️"),
    AUTUMN("autumn", false, "theme.autumn", "🍂"),
    BUSINESS("business", true, "theme.business", "💼"),
    ACID("acid", false, "theme.acid", "🧪"),
    LEMONADE("lemonade", false, "theme.lemonade", "🍋"),
    NIGHT("night", true, "theme.night", "🌙"),
    COFFEE("coffee", true, "theme.coffee", "☕"),
    WINTER("winter", false, "theme.winter", "❄️"),
    DIM("dim", true, "theme.dim", "🔅"),
    NORD("nord", false, "theme.nord", "❄️"),
    SUNSET("sunset", true, "theme.sunset", "🌅");

    private final String daisyUiName;
    private final boolean dark;
    private final String messageKey;
    private final String icon;
}