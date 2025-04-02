package komeiji.back.websocket.persistence.meta;

import komeiji.back.websocket.session.SessionToken;
import lombok.Data;

@Data
public class Character{
    SessionToken token;

    public Character(SessionToken token){
        this.token = token;
    }
}
