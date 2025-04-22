package komeiji.back.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ConToSup {
    @Id
    private Long consultantId;

    private Long supervisorId;

    public ConToSup() {}


}
