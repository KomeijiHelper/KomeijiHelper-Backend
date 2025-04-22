package komeiji.back.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
//@Data
public class RankDTO {
    private int approve;
    private int rank;
    private String cid ;
    public RankDTO(){}
    public String toString() {
        return "RankDTO [approve=" + approve + ", rank=" + rank + ", CID=" + cid + "]";
    }
}
