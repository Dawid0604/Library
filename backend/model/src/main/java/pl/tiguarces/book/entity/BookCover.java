package pl.tiguarces.book.entity;

import static org.apache.commons.lang3.StringUtils.capitalize;

public enum BookCover {
    HARD, SOFT;

    public String getCoverName() {
        return capitalize(this.toString().toLowerCase());
    }
}
