package komeiji.back.service;

import jakarta.annotation.Resource;
import komeiji.back.repository.ChatRecordDao;
import komeiji.back.repository.UserDao;
import komeiji.back.utils.RedisUtils;
import org.springframework.stereotype.Service;

import komeiji.back.utils.RedisTable;

@Service
public class ChatRecordService {
    @Resource
    private ChatRecordDao chatRecordDao;
    @Resource
    private UserDao userDao;
    @Resource
    private RedisUtils redisUtils;



}
