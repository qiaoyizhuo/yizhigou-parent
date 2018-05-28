package com.yizhigou.order.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yizhigou.mapper.TbOrderItemMapper;
import com.yizhigou.mapper.TbPayLogMapper;
import com.yizhigou.pojo.TbOrderExample;
import com.yizhigou.pojo.TbOrderItem;
import com.yizhigou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yizhigou.mapper.TbOrderMapper;
import com.yizhigou.pojo.TbOrder;
import com.yizhigou.order.service.OrderService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import util.IdWorker;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private  IdWorker idWorker;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	//支付状态
	@Autowired
	private TbPayLogMapper payLogMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {

		//1.从redis购物车中，获取到购物车内商品信息
		//从购物车中去数据
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

		List<String> orderIdList=new ArrayList();//订单ID列表
		double total_money=0;//总金额 （元）

		//循环购物车
		 for (Cart cart:cartList){
		 	//生成订单号
			 long orderId = idWorker.nextId();
			 System.out.println("生成了订单号："+orderId);
			 //创建订单
			 //2.保存订单表tb_order
			 TbOrder tborder=new TbOrder();//新创建订单对象
			 tborder.setOrderId(orderId);//生成订单号
			 tborder.setUserId(order.getUserId());//用户名
			 tborder.setPaymentType(order.getPaymentType());//支付类型
			 tborder.setStatus("1");//1.未付款
			 tborder.setCreateTime(new Date());//订单创建日期
			 tborder.setUpdateTime(new Date());//订单更新日期
			 tborder.setReceiverAreaName(order.getReceiverAreaName());//地址
			 tborder.setReceiverMobile(order.getReceiverMobile());//手机号
			 tborder.setReceiver(order.getReceiver());//收货人
			 tborder.setSourceType(order.getSourceType());//订单来源
			 tborder.setSellerId(cart.getSellerId());//商家ID

			 //3.保存订单项表tb_orderItem
			 double money=0;
			 for(TbOrderItem orderItem:cart.getOrderItemList()){
				 orderItem.setId(idWorker.nextId());
				 orderItem.setOrderId(orderId);//订单ID
				 orderItem.setSellerId(cart.getSellerId());
				 money+=orderItem.getTotalFee().doubleValue();//金额累加,doubleValue()转化
				 //报讯订单项目表
				 orderItemMapper.insert(orderItem);
			 }
			 tborder.setPayment(new BigDecimal(money));
			 //保存订单
			orderMapper.insert(tborder);
		 }
		 //清除redis缓存
		redisTemplate.boundHashOps("cartList").delete(order.getUserId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		TbOrderExample.Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
