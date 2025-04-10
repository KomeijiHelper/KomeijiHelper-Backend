package komeiji.back.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ChatRecord {
    @Id
    String id;  //由每一次绘画的UUID决定

    String patientName;
    String consultantName;

    int consultantClass;

    String timeStamp;
    String filePath;;

    public int getSocre() {
        return socre;
    }

    public void setSocre(int socre) {
        this.socre = socre;
    }

    int socre;


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ChatRecord(String id, String patientName, String consultantName, int consultantClass, String timeStamp,String filePath) {
        this.id = id;
        this.patientName = patientName;
        this.consultantName = consultantName;
        this.consultantClass = consultantClass;
        this.timeStamp = timeStamp;
        this.filePath = filePath;
    }

    public ChatRecord() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getConsultantName() {
        return consultantName;
    }

    public void setConsultantName(String consultantName) {
        this.consultantName = consultantName;
    }

    public int getConsultantClass() {
        return consultantClass;
    }

    public void setConsultantClass(int consultantClass) {
        this.consultantClass = consultantClass;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
