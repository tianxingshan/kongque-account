
package com.kongque.dao.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Repository;

import com.kongque.dao.IDaoService;
import com.kongque.util.PageBean;
import com.kongque.util.Pagination;

@Repository
public class DaoServiceImpl implements IDaoService {

	@Resource
	private SessionFactory sessionFactory;

	private Session getSession() {

		Session session = sessionFactory.getCurrentSession();
		if (session == null) {
			session = sessionFactory.openSession();
		}
		return session;
	}

	
	@Override
	public <T> T findById(Class<T> entityClass, String id) {

		return (T) getSession().get(entityClass, id);
	}

	@Override
	public <T> void save(T entity) {

		getSession().save(entity);
		getSession().flush();
	}

	@Override
	public <T> void saveAllEntity(List<T> list) {

		for (T t : list) {
			save(t);
		}

	}

	@Override
	public <T> void update(T entity) {

		getSession().update(entity);
		getSession().flush();
	}

	@Override
	public <T> void updateAllEntity(List<T> list) {

		for (T t : list) {
			update(t);
		}
	}

	@Override
	public <T> void delete(T entity) {

		getSession().delete(entity);
	}

	@Override
	public <T> void deleteAllEntity(List<T> list) {

		for (T t : list) {
			delete(t);
		}
		getSession().flush();
	}

	@SuppressWarnings("deprecation")
	@Override
	public <T> Criteria createCriteria(Class<T> t) {

		return getSession().createCriteria(t).setCacheable(true).setCacheRegion(t.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T findUniqueByProperty(Class<T> entityClass, String propertyName, Object value) {

		return (T) createCriteria(entityClass).add(Restrictions.eq(propertyName, value)).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> findListWithPagebeanCriteria(Criteria criteria, PageBean pageBean) {

		if (pageBean.getPage() == null || pageBean.getRows() == null) {
			pageBean.setPage(1);
			pageBean.setRows(10);
		}
		if (pageBean.getSort() != null) {
			for (int i = 0; i < pageBean.getSort().split(",").length; i++) {
				if (pageBean.getOrder().split(",")[i].equals("asc"))
					criteria.addOrder(Order.asc(pageBean.getSort().split(",")[i]));
				else
					criteria.addOrder(Order.desc(pageBean.getSort().split(",")[i]));
			}
		}
		criteria.setFirstResult((pageBean.getPage() - 1) * pageBean.getRows());
		criteria.setMaxResults(pageBean.getRows());
		return criteria.list();
	}

	@Override
	public Long findTotalWithCriteria(Criteria criteria) {
		criteria.setFirstResult(0);
		criteria.setMaxResults(1);
		criteria.setProjection(Projections.rowCount());
		Long total = (Long) criteria.uniqueResult();
		if (total == null || total == 0) {
			return 0L;
		}
		return total;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> findListByProperty(Class<T> entityClass, String propertyName, Object value) {

		return createCriteria(entityClass).add(Restrictions.eq(propertyName, value)).list();
	}

	@Override
	public <T, D> Pagination<T> findPaginationWithPagebeanAndDtoReflect(Class<T> tClass, D dto, PageBean pageBean) {

		Pagination<T> pagination = new Pagination<T>();
		pagination.setRows(findListWithPagebeanCriteria(createRefelctCriteria(tClass, dto, null), pageBean));
		pagination.setTotal(findTotalWithCriteria(createRefelctCriteria(tClass, dto, null)));
		return pagination;
	}

	private <T, D> Criteria createRefelctCriteria(Class<T> tClass, D dto, Order order) {

		Criteria criteria = createCriteria(tClass);
		if (order != null) {
			criteria.addOrder(order);
		}
		Field[] fields = dto.getClass().getDeclaredFields();
		for (Field field : fields) {
			String propertyName = field.getName();
			String getMethodName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
			try {
				Method method = dto.getClass().getMethod(getMethodName);
				Object object = method.invoke(dto);
				if (object instanceof String && StringUtils.isBlank(String.valueOf(object))) {
					continue;
				}
				if (object != null) {
					criteria.add(Restrictions.eq(propertyName, object));
				}
			} catch (Exception e) {
				System.out.println("反射异常");
			}
		}
		return criteria;
	}

	@Override
	public <T, D> Pagination<T> findPaginationWithPagebeanAndDtoReflectLike(Class<T> tClass, D dto, PageBean pageBean) {

		Pagination<T> pagination = new Pagination<T>();
		pagination.setRows(findListWithPagebeanCriteria(createReflectLikeAndCriteria(tClass, dto, null), pageBean));
		pagination.setTotal(findTotalWithCriteria(createReflectLikeAndCriteria(tClass, dto, null)));
		return pagination;
	}

	private <T, D> Criteria createReflectLikeAndOrCriteria(Class<T> tClass, D dto, Order order) {

		Criteria criteria = createCriteria(tClass);
		if (order != null) {
			criteria.addOrder(order);
		}
		Field[] fields = dto.getClass().getDeclaredFields();
		Disjunction disjunction = Restrictions.disjunction();
		for (Field field : fields) {
			String propertyName = field.getName();
			String getMethodName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
			try {
				Method method = dto.getClass().getMethod(getMethodName);
				String str = (String) method.invoke(dto);
				if (!StringUtils.isBlank(str)) {
					disjunction.add(Restrictions.like(propertyName, str, MatchMode.ANYWHERE));
				}
			} catch (Exception e) {
				System.out.println("反射异常");
			}
		}
		criteria.add(disjunction);
		return criteria;
	}

	@Override
	public <T> void evict(T t) {

		getSession().evict(t);
	}

	@Override
	public <T> void saveOrUpdate(T t) {

		getSession().saveOrUpdate(t);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> findAll(Class<T> tclass) {

		return createCriteria(tclass).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> findListByPropertyWithOrder(Class<T> tClass, String name, String value, Order asc) {

		return (List<T>) createCriteria(tClass).add(Restrictions.eq(name, value)).addOrder(asc).list();
	}

	@Override
	public <T, D> Pagination<T> findPaginationWithPagebeanAndDtoReflectAndLike(Class<T> tClass, D dto,
			PageBean pageBean) {

		Pagination<T> pagination = new Pagination<T>();
		pagination.setRows(findListWithPagebeanCriteria(createReflectLikeAndCriteria(tClass, dto, null), pageBean));
		pagination.setTotal(findTotalWithCriteria(createReflectLikeAndCriteria(tClass, dto, null)));
		return pagination;
	}

	private <T, D> Criteria createReflectLikeAndCriteria(Class<T> tClass, D dto, Order order) {

		Criteria criteria = createCriteria(tClass);
		if (order != null) {
			criteria.addOrder(order);
		}
		Field[] fields = dto.getClass().getDeclaredFields();
		for (Field field : fields) {
			String propertyName = field.getName();
			String getMethodName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
			try {
				Method method = dto.getClass().getMethod(getMethodName);
				String str = (String) method.invoke(dto);
				if (!StringUtils.isBlank(str)) {
					criteria.add(Restrictions.like(propertyName, str, MatchMode.ANYWHERE));
				}
			} catch (Exception e) {
				System.out.println("反射异常");
			}
		}
		return criteria;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> queryBySql(String sql) {

		return getSession().createNativeQuery(sql).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, D> List<T> findListByDto(Class<T> entityClass, D dto, Order order) {

		return createReflectLikeAndOrCriteria(entityClass, dto, order).list();
	}

	@Override
	public <T, D> Pagination<T> findPaginationWithPagebeanAndDtoReflectOrLike(Class<T> tClass, D dto,
			PageBean pageBean) {
		Pagination<T> pagination = new Pagination<T>();
		pagination.setRows(findListWithPagebeanCriteria(createReflectLikeAndOrCriteria(tClass, dto, null), pageBean));
		pagination.setTotal(findTotalWithCriteria(createReflectLikeAndOrCriteria(tClass, dto, null)));
		return pagination;
	}

	@Override
	public <T> void refresh(T t) {
		getSession().refresh(t);

	}

	@Override
	public void flush() {
		getSession().flush();
	}
	
	public <T> void rmCache( T t){
		getSession().getSessionFactory().getCache().evictQueryRegion( t.getClass().getName() );
	}
	
	public Map<String,String> getStatistics(String className){
		Map<String,String> map=new HashMap<>();
		Statistics statistics = getSession().getSessionFactory().getStatistics();
		if(StringUtils.isNotBlank(className)){
			SecondLevelCacheStatistics secondLevelCacheStatistics =
					statistics.getSecondLevelCacheStatistics( className );
			long hitCount = secondLevelCacheStatistics.getHitCount();
			long missCount = secondLevelCacheStatistics.getMissCount();
			double hitRatio = (double) hitCount / ( hitCount + missCount );
			map.put(className,hitRatio+"");
			return map;
		}else{
			for(String s:statistics.getSecondLevelCacheRegionNames()){
				SecondLevelCacheStatistics secondLevelCacheStatistics =statistics.getSecondLevelCacheStatistics( s );
				long hitCount = secondLevelCacheStatistics.getHitCount();
				long missCount = secondLevelCacheStatistics.getMissCount();
				if(0==(hitCount+missCount))
					continue;
				double hitRatio = (double) hitCount / ( hitCount + missCount );
					map.put(s,hitRatio+"");
			}
			return map;
		}
	}

	public void cacheRm(String className){
		if(StringUtils.isBlank(className)){
				getSession().getSessionFactory().getCache().evictAllRegions();
		}else{
			getSession().getSessionFactory().getCache().evictQueryRegion(className);
		}
	}

}
