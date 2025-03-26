package komeiji.back.service;

import komeiji.back.websocket.session.SessionToken;

public interface ConsultService {
    public void conenctRequest_Service(SessionToken patient,SessionToken consultant,String patient_name,String consultant_name);

}
