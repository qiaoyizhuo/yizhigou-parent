package com.yizhigou.seckill.service.impl;
import java.util.Date;
import java.util.List;

import com.yizhigou.com.yizhigou.seckill.service.SeckillOrderService;
import com.yizhigou.mapper.TbSeckillGoodsMapper;
import com.yizhigou.pojo.TbSeckillGoods;
import com.yizhigou.pojo.TbSeckillOrderExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yizhigou.mapper.TbSeckillOrderMapper;
import com.yizhigou.pojo.TbSeckillOrder;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import util.IdWorker;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;

	@Autowired
	private IdWorker idWorker;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		TbSeckillOrderExample.Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
		
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckillOrderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void submitOrder(Long seckillId, String userId) {
		//取出redis中商品
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);

		if(seckillGoods==null){
			throw  new RuntimeException("商品不存在");
		}
		if(seckillGoods.getStockCount()<=0){
			throw  new RuntimeException("商品已被抢光");
		}
		//扣减（redis）库存
		seckillGoods.setStockCount(seckillGoods.getStockCount()-1);

		//存入到redis中
		redisTemplate.boundHashOps("seckillGoods").put(seckillId,seckillGoods);
		//库存已经为0
		if(seckillGoods.getStockCount()==0){
			//同步到数据库
			seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
			//清空redis
			redisTemplate.boundHashOps("seckillGoods").delete(seckillId);
		}

		//订单信息存入到redis中
		long orderId=idWorker.nextId();//生成随机id
		TbSeckillOrder seckillOrder = new TbSeckillOrder();
		seckillOrder.setId(orderId);
		seckillOrder.setMoney(seckillGoods.getCostPrice());//付款金额
		seckillOrder.setSeckillId(seckillId);//商品id
		seckillOrder.setUserId(userId);//当前登陆用户的id
		seckillOrder.setSellerId(seckillGoods.getSellerId());//商户id
		seckillOrder.setStatus("0");//支付状态
		System.out.println("商品已经存入到缓存订单中。。。。");
		//存入数据库
		redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);
	}

	/**
	 * 根据用户名查询秒杀订单
	 * @param userId
	 */
	@Override
	public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {
		return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
	}


    /**
     * 支付成功保存订单
     * @param userId
     * @param orderId
     */
    @Override
    public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
        System.out.println("saveOrderFromRedisToDb:"+userId);
        //根据用户ID查询日志
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        if(seckillOrder==null){
            throw new RuntimeException("订单不存在");
        }
        //如果与传递过来的订单号不符
        if(seckillOrder.getId().longValue()!=orderId.longValue()){
            throw new RuntimeException("订单不相符");
        }
        seckillOrder.setTransactionId(transactionId);//交易流水号
        seckillOrder.setPayTime(new Date());//支付时间
        seckillOrder.setStatus("1");//状态
        seckillOrderMapper.insert(seckillOrder);//保存到数据库
        redisTemplate.boundHashOps("seckillOrder").delete(userId);//从redis中清除

    }

    /**
     * 从缓存中删除订单
     * @param userId
     * @param orderId
     */
    @Override
    public void deleteOrderFromRedis(String userId, Long orderId) {
        //1.根据用户id取出订单
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        //2。取出订单后，删除订单信息
        if(seckillOrder!=null  &&  seckillOrder.getId().longValue()==orderId.longValue()){
            redisTemplate.boundHashOps("seckillOrder").delete(userId);//删除缓存中的订单
            //恢复库存
            //1.从缓存中提取秒杀商品
            TbSeckillGoods seckillGoods=(TbSeckillGoods)redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());
            //3。跟新库存
            if(seckillGoods!=null){
                seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
                redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), seckillGoods);//存入缓存
            }


        }

    }

}
