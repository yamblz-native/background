package ru.yandex.yamblz.ui.other;

public enum ImageType {
    FOREST("Forest"),
    WATER("Water"),
    ANIMAL("Animal"),
    SUNSET("Sunset"),
    DESERT("Desert"),
    MOUNTAIN("Mountain"),
    LANDSCAPE("Landscape"),
    CAR("Car"),
    ONE("One"),
    FOUR("Four"),
    NINE("Nine"),
    SIXTEEN("Sixteen"),
    TWENTY_FIVE("Twenty Five"),
    THIRTY_SIX("Thirty Six");

    private final String label;

    ImageType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
