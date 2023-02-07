package com.zhongzhi.data.redis;

import com.zhongzhi.data.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@Component
public class RedisClient {

    private final static Logger logger = LoggerFactory.getLogger(RedisClient.class);

    @Autowired
    private JedisPool jedisPool;

    // 存对象
    public String setObject(String key, Object obj, int expireOfSeconds) throws Exception {
    	String result = "";
        try (Jedis jedis = jedisPool.getResource()) {
            ObjectOutputStream oos = null;  //对象输出流
            ByteArrayOutputStream bos = null;  //内存缓冲流
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            byte[] byt = bos.toByteArray();
            result = jedis.set(key.getBytes(), byt);
            jedis.expire(key, expireOfSeconds);
            bos.close();
            oos.close();
        } catch (Exception e) {
            logger.error("jedis setObject 出错,key[" + key + "],obj[" + JacksonUtil.toJson(obj) + "]", e);
            throw new RuntimeException(e);
        }
        //返还到连接池
        return result;
    }

    // 取对象
    public Object getObject(String key) throws Exception {
        Object obj = null;
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] byt = jedis.get(key.getBytes());
            if (byt != null) {
                ObjectInputStream ois = null;  //对象输入流
                ByteArrayInputStream bis = null;   //内存缓冲流
                bis = new ByteArrayInputStream(byt);
                ois = new ObjectInputStream(bis);
                obj = ois.readObject();
                bis.close();
                ois.close();
            }
        } catch (Exception e) {
            logger.error("jedis getObject 出错,key[" + key + "]", e);
            throw new RuntimeException(e);
        }
        //返还到连接池
        return obj;
    }

    public String set(String key, String value) {
    	String result = "";
        try (Jedis jedis = jedisPool.getResource()) {
        	result = jedis.set(key, value);
//            jedis.expire(key, 30 * 60 * 1000);
        } catch (Exception e) {
            logger.error("jedis set 出错,key[" + key + "],value[" + value + "]", e);
            throw new RuntimeException(e);
        }
        //返还到连接池
        return result;
    }

    public String set(String key, String value, int expire) {
    	String result = "";
        try (Jedis jedis = jedisPool.getResource()) {
        	result = jedis.set(key, value);
            jedis.expire(key, expire);
        } catch (Exception e) {
            logger.error("jedis set 出错,key[" + key + "],value[" + value + "],expire[" + expire + "]", e);
            throw new RuntimeException(e);
        }
        //返还到连接池
        return result;
    }

    public String get(String key) {    	
        String value = "";
        try (Jedis jedis = jedisPool.getResource()) {
            value = jedis.get(key);
        } catch (Exception e) {
            logger.error("jedis set 出错,key[" + key + "],value[" + value + "]", e);
            throw new RuntimeException(e);
        }
        //返还到连接池

        return value;
    }

    public long remove(String key) {
    	long result = 0;
        try (Jedis jedis = jedisPool.getResource()) {
        	result = jedis.del(key);
        } catch (Exception e) {
            logger.error("jedis remove 出错,key[" + key + "]", e);
            throw new RuntimeException(e);
        }
        //返还到连接池
        return result;
    }
    
    public long incr(String key) {
    	long result = 0;
        try (Jedis jedis = jedisPool.getResource()) {
        	result = jedis.incr(key);
        } catch (Exception e) {
            logger.error("jedis incr 出错,key[" + key + "]", e);
            throw new RuntimeException(e);
        }
        //返还到连接池
        return result;
    }
    
    public long incrBy(String key,Long value) {    	
    	long result = 0;
        try (Jedis jedis = jedisPool.getResource()) {
        	result = jedis.incrBy(key, value);
        } catch (Exception e) {
            logger.error("jedis incrBy 出错,key[" + key + "]", e);
            throw new RuntimeException(e);
        }
        //返还到连接池
        return result;
    }
    
    public long decr(String key) {
    	long result = 0;
        try (Jedis jedis = jedisPool.getResource()) {
        	result = jedis.decr(key);
        } catch (Exception e) {
            logger.error("jedis decr 出错,key[" + key + "]", e);
            throw new RuntimeException(e);
        }
        //返还到连接池
        return result;
    }
    
    public long decrBy(String key,Long value) {
    	long result = 0;
        try (Jedis jedis = jedisPool.getResource()) {
            result = jedis.decrBy(key, value);
        } catch (Exception e) {
            logger.error("jedis decrBy 出错,key[" + key + "]", e);
            throw new RuntimeException(e);
        }
        //返还到连接池
        return result;
    }
    
    public long expire(String key, int value) {
    	long result = 0;
        try (Jedis jedis = jedisPool.getResource()) {
        	result = jedis.expire(key, value);
        } catch (Exception e) {
            logger.error("jedis set 出错,key[" + key + "],value[" + value + "]", e);
            throw new RuntimeException(e);
        }
        //返还到连接池
        return result;
    }
    
    public long lpush(String key, String... strings) {
    	long result = 0;
        try (Jedis jedis = jedisPool.getResource()) {
        	result = jedis.lpush(key, strings);
        } catch (Exception e) {
            logger.error("jedis lpush 出错,key[" + key + "],strings[" + strings + "]", e);
            throw new RuntimeException(e);
        }
        //返还到连接池
        return result;
    }
    
    public  long publish(String channel, String message) {
    	long result = 0;
        try (Jedis jedis = jedisPool.getResource()) {
        	result = jedis.publish(channel, message);
        } catch (Exception e) {
            logger.error("jedis publish 出错,channel[" + channel + "],message[" + message + "]", e);
            throw new RuntimeException(e);
        }
        //返还到连接池
        return result;
	}

    public Object eval(String sha1, List<String> keys, List<String> args) {
    	Object result = 0;
        try (Jedis jedis = jedisPool.getResource()) {
        	result = jedis.eval(sha1, keys, args);
        } catch (Exception e) {
            logger.error("jedis eval 出错,表达式[" + sha1 + "]", e);
            throw new RuntimeException(e);
        }
        //返还到连接池
        return result;
    }
}
