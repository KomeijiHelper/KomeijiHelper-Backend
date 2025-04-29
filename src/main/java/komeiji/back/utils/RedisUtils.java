package komeiji.back.utils;


import jakarta.annotation.Resource;
import org.jetbrains.annotations.TestOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Set;

/**
 * <p>
 *  Redis 工具类
 * </p>
 *
 * @author Ya Shi
 * @since 2024/3/12 14:02
 */
@Component
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    private static final Long RELEASE_SUCCESS = 1L;
    private static final String RELEASE_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "return redis.call('del', KEYS[1]) " +
            "else " +
            "return 0 " +
            "end";;


    // 设置键值对


    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 设置键值对并指定过期时间
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // 设置键值对并指定过期时间
    public void set(String key, Object value, long seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    // 获取值
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 获取值
    public String getString(String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        return obj == null ? null : obj.toString();
    }

    // 删除键
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    // 判断键是否存在
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    // 如果不存在，则设置
    public Boolean setNx(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    // 如果不存在，则设置，附带过期时间
    public Boolean tryLock(String lockKey, String requestId, long seconds) {
        return redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, seconds, TimeUnit.SECONDS);
    }

    // 如果不存在，则设置，附带过期时间
    public Boolean tryLock(String lockKey, String requestId, long timeout, TimeUnit unit) {
        return redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, timeout, unit);
    }

    // 不存在返回true，存在则删除
    public Boolean releaseLock(String lockKey, String requestId){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(RELEASE_SCRIPT);
        redisScript.setResultType(Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        return RELEASE_SUCCESS.equals(result);
    }

    public Long rpush(String key,Object value)
    {
        return redisTemplate.opsForList().rightPush(key,value);
    }
    public Long lpush(String key,Object value)
    {
        return redisTemplate.opsForList().leftPush(key,value);
    }
    public Object lpop(String key)
    {
        return redisTemplate.opsForList().leftPop(key);
    }
    public Object rpop(String key)
    {
        return redisTemplate.opsForList().rightPop(key);
    }
    public Long getListSize(String key){
        System.out.println("I'm in");
        return redisTemplate.opsForList().size(key);
    }
    public List<Object> getList(String key){
        return redisTemplate.opsForList().range(key,0,-1);
    }

    public Long addSet(String Key,Object value){
        return redisTemplate.opsForSet().add(Key,value);
    }
    public Long removeSet(String Key,Object value)
    {
        return redisTemplate.opsForSet().remove(Key,value);
    }
    public Long getSetSize(String Key)
    {
        return redisTemplate.opsForSet().size(Key);
    }
    public Set getSetMembers(String Key)
    {
        return redisTemplate.opsForSet().members(Key);
    }
    public Boolean isMember(String Key,Object value) {
        return redisTemplate.opsForSet().isMember(Key, value);
    }

    public void addHash(String table,String key,Object value){
         redisTemplate.opsForHash().put(table,key,value);
    }
    public Long deleteHash(String table,String key){
        return redisTemplate.opsForHash().delete(table,key);
    }
    public Boolean hasHash(String table,String key){
        return redisTemplate.opsForHash().hasKey(table,key);
    }
    public Object getHash(String table,String key){
        return redisTemplate.opsForHash().get(table,key);
    }
    public Long getHashSize(String table){
        return redisTemplate.opsForHash().size(table);
    }
    public Boolean hasHashKey(String table,String key){
        return redisTemplate.opsForHash().hasKey(table,key);
    }
    public Set<Object> getHashKeys(String table){
        return redisTemplate.opsForHash().keys(table);
    }
    public void setHashKey(String table,String key,Object value)
    {
        redisTemplate.opsForHash().put(table,key,value);
    }
}
