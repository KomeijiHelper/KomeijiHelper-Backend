package komeiji.back.entity;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class YCWConsultant  {

    private String consultantNickname;
    private Long consultantId;

    private String consultantName;

    private float avgScore;

    private int totalRecord = 0;
    private int scoreRecord = 0;
    public YCWConsultant() {}
    public YCWConsultant(Consultant consultant, String nickname) {
        this.consultantId = consultant.getConsultantId();
        this.consultantName = consultant.getConsultantName();
        this.avgScore = consultant.getAvgScore();
        this.totalRecord = consultant.getTotalRecord();
        this.scoreRecord = consultant.getScoreRecord();
        this.consultantNickname = nickname;
    }
    public String toString(){
        return this.consultantNickname;
    }
}