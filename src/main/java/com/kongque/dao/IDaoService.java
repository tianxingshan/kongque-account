package com.kongque.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.codingapi.tx.aop.bean.TxTransactionLocal;
import com.codingapi.tx.control.LCNTransactionAspectSupport;
import com.kongque.util.PageBean;
import com.kongque.util.Pagination;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

public interface IDaoService {

	public <T> T findById(Class<T> entityClass, String id);

	public <T> void save(T entity);

	public <T> void saveAllEntity(List<T> entity);

	public <T> void delete(T entity);

	public <T> void deleteAllEntity(List<T> entity);

	public <T> void update(T entity);

	public <T> void updateAllEntity(List<T> entity);

	public <T> void saveOrUpdate(T t);

	public <T> Criteria createCriteria(Class<T> t);

	public <T> List<T> queryBySql(String sql);

	/**
	 * 根据实体属性查询唯一记录
	 * 
	 * @param entityClass
	 *            待查询实体Class
	 * @param propertyName
	 *            待查询实体属性
	 * @param value
	 *            待查询实体属性值
	 * @return T 查询到的实体
	 */
	public <T> T findUniqueByProperty(Class<T> entityClass, String propertyName, Object value);

	/**
	 * 分页查询实体列表
	 * 
	 * @return
	 */
	public <T> List<T> findListWithPagebeanCriteria(Criteria criteria, PageBean pageBean);

	/**
	 * 根据属性值查询实体列表
	 * 
	 * @param entityClass
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public <T> List<T> findListByProperty(Class<T> entityClass, String propertyName, Object value);

	/**
	 * 适应combobox remote方式查询结果集
	 * 
	 * @param entityClass
	 * @param dto
	 * @param order
	 * @return
	 */
	public <T, D> List<T> findListByDto(Class<T> entityClass, D dto, Order order);

	/**
	 * 查询总数
	 * 
	 * @param criteria
	 * @return
	 */
	public Long findTotalWithCriteria(Criteria criteria);

	/**
	 * 遍历dto属性为查询条件，分页查询实体列表
	 * 
	 * @param tClass
	 * @param dto
	 * @param pageBean
	 * @return
	 */
	public <T, D> Pagination<T> findPaginationWithPagebeanAndDtoReflect(Class<T> tClass, D dto, PageBean pageBean);

	/**
	 * 遍历dto属性为“或”的模糊查询条件，分页查询实体列表
	 * 
	 * @param tClass
	 * @param dto
	 * @param pageBean
	 * @return
	 */
	public <T, D> Pagination<T> findPaginationWithPagebeanAndDtoReflectLike(Class<T> tClass, D dto, PageBean pageBean);

	public <T, D> Pagination<T> findPaginationWithPagebeanAndDtoReflectOrLike(Class<T> tClass, D dto,
			PageBean pageBean);

	public <T> void evict(T t);

	public <T> void refresh(T t);

	public <T> List<T> findAll(Class<T> tclass);

	public <T> List<T> findListByPropertyWithOrder(Class<T> tClass, String string, String tpId, Order asc);

	public <T, D> Pagination<T> findPaginationWithPagebeanAndDtoReflectAndLike(Class<T> tClass, D dto,
			PageBean pageBean);

	public void flush();
	
 	/**
	 * yuehui
	 * 2018-10-16
	 * 移除缓存
	 * @param t
	 * @param <T>
	 */
	public <T> void rmCache( T t);

	/**
	 * yuehui
	 * 2018-10-16
	 * 缓存命中率
	 * @param className
	 * @return
	 */
	public Map<String,String> getStatistics(String className);

	/**
	 * 移除缓存
	 * @param className
	 */
	public void cacheRm(String className);
	
	/**
	 * 事务只读，用于校验失败，数据不修改
	 */
	public static void transactionRollback(){
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		//分布式事务只读
		if(TxTransactionLocal.current()!=null){
			TxTransactionLocal.current().setReadOnly(true);
			LCNTransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
	}
}
