package pl.tiguarces.book.dto.request;

public record UserBookReactionRequest(long bookId, Integer numberOfStars, String comment) { }
