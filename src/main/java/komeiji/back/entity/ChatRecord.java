package komeiji.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class ChatRecord {
    @Id
    String id;  //由每一次绘画的UUID决定

    String patientName;
    String consultantName;

    int consultantClass;

    String timeStamp;
    String filePath;;

    @Column(nullable = true)
    int score;

    public ChatRecord(String id, String patientName, String consultantName, int consultantClass, String timeStamp, String filePath) {
        this.id = id;
        this.patientName = patientName;
        this.consultantName = consultantName;
        this.consultantClass = consultantClass;
        this.timeStamp = timeStamp;
        this.filePath = filePath;
    }

    public ChatRecord() {

    }


}
