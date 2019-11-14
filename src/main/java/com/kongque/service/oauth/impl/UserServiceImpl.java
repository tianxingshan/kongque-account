package com.kongque.service.oauth.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kongque.component.oauth.WdcyUserDetails;
import com.kongque.dao.impl.DaoServiceImpl;
import com.kongque.entity.Account;
import com.kongque.service.oauth.UserService;

/**
 * 处理用户, 账号, 安全相关业务
 *
 * @author Shengzhao Li
 */
@Service("myUserService")
public class UserServiceImpl implements UserService {

    @Autowired
    private DaoServiceImpl userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	Criteria cri=userRepository.createCriteria(Account.class);
    	cri.add(Restrictions.eq("username", username.split(":")[0]));
    	cri.add(Restrictions.eq("sysId", username.split(":")[1]));
    	Account user=(Account)cri.uniqueResult();
        if (user == null || !user.getStatus().equals("1")) {
            throw new UsernameNotFoundException("[" + username + "]状态异常");
        }
        return new WdcyUserDetails(user);
    }


    @Override
    public boolean isExistedUsername(String username) {
        final Account user = userRepository.findUniqueByProperty(Account.class, "username", username);
        return user != null;
    }

	/* (non-Javadoc)
	 * @see com.kongque.service.oauth.UserService#saveUser(com.kongque.entity.system.User)
	 */
	@Override
	public String saveUser(Account formDto) {
		userRepository.save(formDto);
		return formDto.getId();
	}
}