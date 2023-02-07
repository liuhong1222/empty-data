package com.zhongzhi.data.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.zhongzhi.data.constants.RedisConstant.*;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "timer", name = "enable", havingValue = "true")
public class PhoneRedisBitUtil {
    public static final long BASE = 100_0000_0000L;
    public static final long MAX = 199_9999_9999L;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${timer.active-timeout}")
    private int activeTimeout;

    @Value("${timer.risk-timeout}")
    private int riskTimeout;

    @Value("${timer.empty-timeout}")
    private int emptyTimeout;

    @Value("${timer.silent-timeout}")
    private int silentTimeout;

    /**
     * 添加活跃号码
     *
     * @param nos 活跃号码
     */
    public void addActiveNo(List<String> nos) {
        addNo(nos, ACTIVE_KEY, activeTimeout);
    }

    /**
     * 添加风险号码
     *
     * @param nos 风险号码
     */
    public void addRiskNo(List<String> nos) {
        addNo(nos, RISK_KEY, riskTimeout);
    }

    /**
     * 添加静默号码
     *
     * @param nos 静默号码
     */
    public void addSilentNo(List<String> nos) {
        addNo(nos, SILENT_KEY, silentTimeout);
    }

    /**
     * 添加空号码
     *
     * @param nos 空号码
     */
    public void addEmptyNo(List<String> nos) {
        addNo(nos, EMPTY_KEY, emptyTimeout);
    }

    /**
     * 添加匹配号码
     *
     * @param nos 匹配号码
     */
    public void addPhoneMatchNo(List<String> nos) {
        addNo(nos, PHONE_MATCHER_KEY, activeTimeout);
    }

    /**
     * 删除活跃号码
     *
     * @param nos 活跃号码
     */
    public void delActiveNo(String... nos) {
        delNo(nos, ACTIVE_KEY);
    }

    /**
     * 删除风险号码
     *
     * @param nos 风险号码
     */
    public void delRiskNo(String... nos) {
        delNo(nos, RISK_KEY);
    }

    /**
     * 删除静默号码
     *
     * @param nos 静默号码
     */
    public void delSilentNo(String... nos) {
        delNo(nos, SILENT_KEY);
    }

    /**
     * 删除空号码
     *
     * @param nos 空号码
     */
    public void delEmptyNo(String... nos) {
        delNo(nos, EMPTY_KEY);
    }

    /**
     * 判断缓存中是否存在活跃号码
     *
     * @param nos 号码
     * @return 返回null表示全不存在
     */
    public List<Boolean> existsActive(String[] nos) {
        return exists(nos, ACTIVE_KEY);
    }

    /**
     * 判断缓存中是否存在活跃号码
     *
     * @param nos 号码
     * @return 返回null表示全不存在
     */
    public List<Boolean> existsActive(List<String> nos) {
        return exists(nos, ACTIVE_KEY);
    }

    /**
     * 判断缓存中是否存在空号码
     *
     * @param nos 号码
     * @return 返回null表示全不存在
     */
    public List<Boolean> existsEmpty(String[] nos) {
        return exists(nos, EMPTY_KEY);
    }

    /**
     * 判断缓存中是否存在空号码
     *
     * @param nos 号码
     * @return 返回null表示全不存在
     */
    public List<Boolean> existsEmpty(List<String> nos) {
        return exists(nos, EMPTY_KEY);
    }

    /**
     * 判断缓存中是否存在风险号码
     *
     * @param nos 号码
     * @return 返回null表示全不存在
     */
    public List<Boolean> existsRisk(String[] nos) {
        return exists(nos, RISK_KEY);
    }

    /**
     * 判断缓存中是否存在风险号码
     *
     * @param nos 号码
     * @return 返回null表示全不存在
     */
    public List<Boolean> existsRisk(List<String> nos) {
        return exists(nos, RISK_KEY);
    }

    /**
     * 判断缓存中是否存在静默号码
     *
     * @param nos 号码
     * @return 返回null表示全不存在
     */
    public List<Boolean> existsSilent(String[] nos) {
        return exists(nos, SILENT_KEY);
    }

    /**
     * 判断缓存中是否存在静默号码
     *
     * @param nos 号码
     * @return 返回null表示全不存在
     */
    public List<Boolean> existsSilent(List<String> nos) {
        return exists(nos, SILENT_KEY);
    }

    /**
     * 判断缓存中是否存在匹配号码
     *
     * @param nos 号码
     * @return 返回null表示全不存在
     */
    public List<Boolean> existsPhoneMatch(List<String> nos) {
        return exists(nos, PHONE_MATCHER_KEY);
    }

    private List<Boolean> exists(List<String> nos, String key) {
        if (nos == null || nos.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        return redisTemplate.executePipelined(new SessionCallback<Boolean>() {
            @Override
            public <K, V> Boolean execute(RedisOperations<K, V> operations) throws DataAccessException {
                Long number;
                int seg;
                for (String no : nos) {
                    number = PhoneUtil.toPhone(no);
                    if (number != null) {
                        seg = toSegment(number);
                        redisTemplate.opsForValue().getBit(key + seg, Long.parseLong(number.toString().substring(3)));
                    } else {
                        log.error("非法号码: {}", no);
                    }
                }
                return null;
            }
        });
    }

    private List<Boolean> exists(String[] nos, String key) {
        if (nos == null || nos.length == 0) {
            return Collections.EMPTY_LIST;
        }

        return redisTemplate.executePipelined(new SessionCallback<Boolean>() {
            @Override
            public <K, V> Boolean execute(RedisOperations<K, V> operations) throws DataAccessException {
                Long number;
                int seg;
                for (String no : nos) {
                    number = PhoneUtil.toPhone(no);
                    if (number != null) {
                        seg = toSegment(number);
                        redisTemplate.opsForValue().getBit(key + seg, Long.parseLong(number.toString().substring(3)));
                    } else {
                        log.error("非法号码{}", number);
                    }
                }
                return null;
            }
        });
    }

    private void delNo(String[] nos, String key) {
        if (nos != null && nos.length > 0) {
            redisTemplate.executePipelined(new SessionCallback<Boolean>() {
                @Override
                public <K, V> Boolean execute(RedisOperations<K, V> operations) throws DataAccessException {
                    Long number;
                    int seg;
                    for (String no : nos) {
                        number = PhoneUtil.toPhone(no);
                        if (number != null) {
                            seg = toSegment(number);
                            boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(key + seg));
                            if (exists) {
                                redisTemplate.opsForValue().setBit(key + seg, Long.parseLong(number.toString().substring(3)), false);
                            }
                        }
                    }
                    return null;
                }
            });
        }
    }

    private void addNo(List<String> nos, String key, int timeout) {
        if (nos != null && !nos.isEmpty()) {
            redisTemplate.executePipelined(new SessionCallback<Boolean>() {
                @Override
                public <K, V> Boolean execute(RedisOperations<K, V> operations) throws DataAccessException {
                    Set<Integer> newSegs = new HashSet<>(128);
                    Long number;
                    int seg;
                    for (String no : nos) {
                        number = PhoneUtil.toPhone(no);
                        if (number != null) {
                            seg = toSegment(number);
                            if (timeout > 0 && !newSegs.contains(seg) && !Boolean.TRUE.equals(redisTemplate.hasKey(key + seg))) {
                                newSegs.add(seg);
                            }
                            redisTemplate.opsForValue().setBit(key + seg, Long.parseLong(number.toString().substring(3)), true);
                        }
                    }

                    if (timeout > 0) {
                        for (Integer s : newSegs) {
                            // 第一次设置值时，设置超时时间
                            redisTemplate.expire(key + s, timeout, TimeUnit.HOURS);
                        }
                    }
                    return null;
                }
            });
        }
    }

    private int toSegment(Long no) {
        return (int) Long.parseLong(no.toString().substring(0, 3));
    }
}
