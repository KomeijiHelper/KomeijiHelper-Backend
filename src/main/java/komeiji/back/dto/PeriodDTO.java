package komeiji.back.dto;

import lombok.Data;

@Data
public class PeriodDTO {
    private String start;
    private String end;

    public PeriodDTO(){}
    public PeriodDTO(String start,String end){
        this.start=start;
        this.end=end;
    }
    public String toString(){
        return start+" "+end;
    }
}
