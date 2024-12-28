package me.aloic.lazybot.osu.dao.entity.dto.player;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OauthRequestDTO {
    private final String client_id = "21171";

    private final String redirect_uri = "http://106.54.12.158:9001/oauth/callBack";

    private final String response_type = "code";

    private final String scope = "friends.read identify public";

    private String state;

    public OauthRequestDTO(String state) {
        this.state = state;
    }
}
