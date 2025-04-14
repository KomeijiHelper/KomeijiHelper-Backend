package komeiji.back.service;

import komeiji.back.utils.Result;
import komeiji.back.websocket.session.SessionToken;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface ConsultService {
    public void conenctRequest_Service(SessionToken patient,SessionToken consultant,String patient_name,String consultant_name) throws UnsupportedEncodingException, NoSuchAlgorithmException;
    public void rejectRequest_Service(SessionToken patient);

}
