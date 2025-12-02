package de.oth.othivity.model.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Theme {
    LIGHT("light", "theme.light", "☀️"),
    DARK("dark", "theme.dark", "🌑"),
    CUPCAKE("cupcake", "theme.cupcake", "🧁"),
    BUMBLEBEE("bumblebee", "theme.bumblebee", "🐝"),
    EMERALD("emerald", "theme.emerald", "✳️"),
    CORPORATE("corporate", "theme.corporate", "🏢"),
    SYNTHWAVE("synthwave", "theme.synthwave", "🌃"),
    RETRO("retro", "theme.retro", "📼"),
    CYBERPUNK("cyberpunk", "theme.cyberpunk", "🤖"),
    VALENTINE("valentine", "theme.valentine", "🌸"),
    HALLOWEEN("halloween", "theme.halloween", "🎃"),
    GARDEN("garden", "theme.garden", "🌷"),
    FOREST("forest", "theme.forest", "🌲"),
    AQUA("aqua", "theme.aqua", "💧"),
    LOFI("lofi", "theme.lofi", "📻"),
    PASTEL("pastel", "theme.pastel", "🎨"),
    FANTASY("fantasy", "theme.fantasy", "🧚"),
    WIREFRAME("wireframe", "theme.wireframe", "📝"),
    BLACK("black", "theme.black", "🖤"),
    LUXURY("luxury", "theme.luxury", "💎"),
    DRACULA("dracula", "theme.dracula", "🧛"),
    CMYK("cmyk", "theme.cmyk", "🖨️"),
    AUTUMN("autumn", "theme.autumn", "🍂"),
    BUSINESS("business", "theme.business", "💼"),
    ACID("acid", "theme.acid", "🧪"),
    LEMONADE("lemonade", "theme.lemonade", "🍋"),
    NIGHT("night", "theme.night", "🌙"),
    COFFEE("coffee", "theme.coffee", "☕"),
    WINTER("winter", "theme.winter", "❄️"),
    DIM("dim", "theme.dim", "🔅"),
    NORD("nord", "theme.nord", "❄️"),
    SUNSET("sunset", "theme.sunset", "🌅");

    private final String daisyUiName;
    private final String messageKey;
    private final String icon;
}