package com.zhongzhi.data.service;

import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.entity.Goods;
import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.mapper.GoodsMapper;
import com.zhongzhi.data.service.agent.AgentSettingsService;
import com.zhongzhi.data.util.ThreadLocalContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 套餐实现类
 * @author xybb
 * @date 2021-11-07
 */
@Service
public class GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Value("file.upload.path")
    private String uploadPath;

    @Autowired
    private AgentSettingsService agentSettingsService;

    /**
     * 获取套餐列表
     * @date 2021/11/7
     * @param
     * @return ApiResult<Goods>
     */
    public ApiResult<Goods> getGoodsList() {
        AgentSettings agentSettings = ThreadLocalContainer.getAgentSettings();

        Goods goods = new Goods();
        goods.setAgentId(agentSettings.getAgentId());

        // 查询普通套餐空号检测产品和实时检测产品列表
        goods.setType(0);
        goods.setCategory(0);
        List<Goods> list = this.listByCondition(goods, 6);
        goods.setCategory(1);
        List<Goods> list2 = this.listByCondition(goods, 6);
        goods.setCategory(2);
        List<Goods> list3 = this.listByCondition(goods, 6);
        goods.setCategory(4);
        List<Goods> list4 = this.listByCondition(goods, 6);
        goods.setCategory(5);
        List<Goods> list5 = this.listByCondition(goods, 6);

        // 查询自定义套餐的空号检测产品和实时检测产品
        goods.setType(1);
        goods.setCategory(0);
        Goods customerGoods = this.findByCondition(goods);
        goods.setCategory(1);
        Goods customerGoods2 = this.findByCondition(goods);
        goods.setCategory(2);
        Goods customerGoods3 = this.findByCondition(goods);
        goods.setCategory(4);
        Goods customerGoods4 = this.findByCondition(goods);
        goods.setCategory(5);
        Goods customerGoods5 = this.findByCondition(goods);

        if (list2 != null && list2.size() > 0) {
            list.addAll(list2);
        }
        if (list3 != null && list3.size() > 0) {
            list.addAll(list3);
        }
        if (list4 != null && list4.size() > 0) {
            list.addAll(list4);
        }
        if (list5 != null && list5.size() > 0) {
            list.addAll(list5);
        }
        if (customerGoods != null) {
            list.add(customerGoods);
        }
        if (customerGoods2 != null) {
            list.add(customerGoods2);
        }
        if (customerGoods3 != null) {
            list.add(customerGoods3);
        }
        if (customerGoods4 != null) {
            list.add(customerGoods4);
        }
        if (customerGoods5 != null) {
            list.add(customerGoods5);
        }

        return ApiResult.ok(list);
    }


    /**
     * 通过条件查询套餐列表
     * @date 2021/11/7
     * @param goods
     * @return List<Goods>
     */
    public List<Goods> listByCondition(Goods goods, int limit) {
        return goodsMapper.listByCondition(goods, limit);
    }

    /**
     * 通过条件查询套餐
     * @date 2021/11/8
     * @param goods
     * @return List<Goods>
     */
    public Goods findByCondition(Goods goods) {
        return goodsMapper.findByCondition(goods);
    }



}
