package com.yizhigou.user.service.impl;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.yizhigou.pojo.TbUserExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yizhigou.mapper.TbUserMapper;
import com.yizhigou.pojo.TbUser;
import com.yizhigou.user.service.UserService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.DigestUtils;

import javax.jms.*;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;

	//注入redis
	@Autowired
	private RedisTemplate redisTemplate;

	//注入activeMq
	@Autowired
    private JmsTemplate jmsTemplate;

	@Autowired
    private Destination queueSmsCodeDestination;


	/**
	 * 查询全部
	 */
	@Override
	public List<TbUser> findAll() {
		return userMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbUser> page=   (Page<TbUser>) userMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbUser user) {

		user.setCreated(new Date());
		user.setUpdated(new Date());
		//md5加密   spring提供的
		user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
		userMapper.insert(user);
	}


	/**
	 * 修改
	 */
	@Override
	public void update(TbUser user){
		userMapper.updateByPrimaryKey(user);
	}

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbUser findOne(Long id){
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			userMapper.deleteByPrimaryKey(id);
		}
	}


		@Override
	public PageResult findPage(TbUser user, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbUserExample example=new TbUserExample();
		TbUserExample.Criteria criteria = example.createCriteria();

		if(user!=null){
						if(user.getUsername()!=null && user.getUsername().length()>0){
				criteria.andUsernameLike("%"+user.getUsername()+"%");
			}
			if(user.getPassword()!=null && user.getPassword().length()>0){
				criteria.andPasswordLike("%"+user.getPassword()+"%");
			}
			if(user.getPhone()!=null && user.getPhone().length()>0){
				criteria.andPhoneLike("%"+user.getPhone()+"%");
			}
			if(user.getEmail()!=null && user.getEmail().length()>0){
				criteria.andEmailLike("%"+user.getEmail()+"%");
			}
			if(user.getSourceType()!=null && user.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
			}
			if(user.getNickName()!=null && user.getNickName().length()>0){
				criteria.andNickNameLike("%"+user.getNickName()+"%");
			}
			if(user.getName()!=null && user.getName().length()>0){
				criteria.andNameLike("%"+user.getName()+"%");
			}
			if(user.getStatus()!=null && user.getStatus().length()>0){
				criteria.andStatusLike("%"+user.getStatus()+"%");
			}
			if(user.getHeadPic()!=null && user.getHeadPic().length()>0){
				criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
			}
			if(user.getQq()!=null && user.getQq().length()>0){
				criteria.andQqLike("%"+user.getQq()+"%");
			}
			if(user.getIsMobileCheck()!=null && user.getIsMobileCheck().length()>0){
				criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
			}
			if(user.getIsEmailCheck()!=null && user.getIsEmailCheck().length()>0){
				criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
			}
			if(user.getSex()!=null && user.getSex().length()>0){
				criteria.andSexLike("%"+user.getSex()+"%");
			}

		}

		Page<TbUser> page= (Page<TbUser>)userMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}



	/**
	 * 生成短信验证码
	 * @return
	 */
	@Override
	public void createSmsCods(String phone) {
		//生成6位随机数
		String str = String.valueOf(new Random().nextInt(899999) + 100000);
		System.out.println("验证码是======"+str);

		//放入到redis中------手机号作为建值，随机数作为value值
		redisTemplate.boundHashOps("smscode").put(phone,str);

		//发送mq消息队列，短信验证码
        jmsTemplate.send(queueSmsCodeDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {

                //发送Map
                MapMessage mapMessage = session.createMapMessage();

                mapMessage.setString("mobile",phone);
                mapMessage.setString("template_code","SMS_135042031");
                mapMessage.setString("sign_name","明天的你会感谢现在的你");
                Map map = new HashMap();
                map.put("name",str);//name是模版里的标签
                mapMessage.setString("param",JSON.toJSONString(map));//从redis取出
                return mapMessage;
            }
        });
	}

    @Override
    public boolean checkCode(String phone, String code) {

	    //去redis中取东西
       String smscode = (String) redisTemplate.boundHashOps("smscode").get(phone);
        //判断如果为空返回false
        if(code==null){
            return  false;
        }
        //不一致返回false
        if(!smscode.equals(code)){
            return  false;
        }

        return true;
    }

}
