package komeiji.back.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Consultant  {

    @Id
    private Long consultantId;

    private String consultantName;

    private float avgScore;

    private int totalRecord = 0;
    private int scoreRecord = 0;
    public Consultant() {}
    public Consultant(Long consultantId, String consultantName, float avgScore, int totalRecord, int scoreRecord) {
        this.consultantId = consultantId;
        this.consultantName = consultantName;
        this.avgScore = avgScore;
        this.totalRecord = totalRecord;
        this.scoreRecord = scoreRecord;
    }


    //TODO 使用旁路缓存模式 在修改时将Redis缓存失效 在查询时将Redis缓存更新
    //DONE 在注册咨询师时创建一个新的记录 登陆时如果没有记录则进行创建 登陆时如果没有同样也为其创建添加
    //TODO 在chatRecord创建时将totalRecord+1 同时在Redis中更新   在ConsultantServiceImpl修改
    //DONE 评分后 将scoreRecord+1 并更新平均分 (在chatRecordServiceImpl中进行修改) 然后同时更新Consultant数据库和Redis中数据
}

