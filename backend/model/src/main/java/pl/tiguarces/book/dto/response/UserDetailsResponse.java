package pl.tiguarces.book.dto.response;

import pl.tiguarces.user.entity.AppUser;

public record UserDetailsResponse(String username, String roles, String avatar) {

    public static UserDetailsResponse map(final AppUser user) {
        return new UserDetailsResponse(user.getUsername(), user.getRoles(), user.getAvatar());
    }
}
