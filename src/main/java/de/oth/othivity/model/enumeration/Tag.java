package de.oth.othivity.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Tag {

    OUTDOOR("tag.outdoor", "🌲"),
    INDOOR("tag.indoor", "🏠"),
    SPORTS("tag.sports", "🏃"),
    LEARNING("tag.learning", "📚"),
    PARTY("tag.party", "🎉"),
    MUSIC("tag.music", "🎶"),
    FOOD("tag.food", "🍽️"),
    TRAVEL("tag.travel", "✈️"),
    HIKING("tag.hiking", "🥾"),
    ART("tag.art", "🎨"),
    RELAX("tag.relax", "🛋️"),
    VOLUNTEERING("tag.volunteering", "🤝"),
    BOARDGAME("tag.boardgames", "🎲"),
    GAMING("tag.gaming", "🎮"),
    MOVIE("tag.movie", "🎬"),
    SOCIAL("tag.social", "👥");

    private final String key;
    private final String emoji;
}
