/**
 * 
 */
package com.kongque.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import com.kongque.util.StringUtils;

/**
 * @author yuehui 权限url表
 * @2017年12月12日
 */
@Table(name = "t_sys_resource")
@Entity
@DynamicInsert
@DynamicUpdate
public class SysResoure implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8557736826962468019L;

	@GenericGenerator(name = "idGenerator", strategy = "uuid")
	@GeneratedValue(generator = "idGenerator")
	@Id
	@Column(name = "c_id")
	private String id;

	/**
	 * url信息描述
	 */
	@Column(name = "c_description")
	private String description;

	/**
	 * url：格式 url+读
	 */
	@Column(name = "c_url_match")
	private String urlMatch;

	/**
	 * 排序
	 */
	@Column(name = "c_source_order")
	private int sourceOrder = 0;

	/**
	 * 类型：普通(Url)、菜单(前端页面)
	 */
	@Column(name = "c_type")
	private String type = "普通";

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "c_father_id")
	private SysResoure father;

	/**
	 * 各层父级id使用,隔开
	 */
	@Column(name="c_father_ids")
	private String fatherIds;
	
	@OneToMany(mappedBy = "father", fetch = FetchType.LAZY)
	@Where(clause = " c_del='否'")
	@OrderBy("sourceOrder")
	private Set<SysResoure> childSet;

	@Transient
	private List<SysResoure> sysList;

	/**
	 * 是否校验:是、否
	 */
	@Column(name = "c_check")
	private String check = "是";

	/**
	 * 是否删除
	 */
	@Column(name = "c_del")
	private String del = "否";

	/**
	 * 备注
	 */
	@Column(name = "c_remarks")
	private String remarks;
	
	/**
	 * 所属系统
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="c_sys_id")
	private SystemInfo system;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrlMatch() {
		return urlMatch;
	}

	public void setUrlMatch(String urlMatch) {
		this.urlMatch = urlMatch;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public SysResoure getFather() {
		return father;
	}

	public void setFather(SysResoure father) {
		this.father = father;
		if (father != null && StringUtils.isBlank(father.getId()))
			this.father = null;
	}

	public Set<SysResoure> getChildSet() {
		return childSet;
	}

	public void setChildSet(Set<SysResoure> childSet) {
		this.childSet = childSet;
	}

	public int getSourceOrder() {
		return sourceOrder;
	}

	public void setSourceOrder(int sourceOrder) {
		this.sourceOrder = sourceOrder;
	}

	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
	}

	public List<SysResoure> getSysList() {
		return sysList;
	}

	public void setSysList(List<SysResoure> sysList) {
		this.sysList = sysList;
	}

	public String getParentId() {
		return this.father == null ? null : this.father.getId();
	}

	public String getFatherIds() {
		return fatherIds;
	}

	public void setFatherIds(String fatherIds) {
		this.fatherIds = fatherIds;
	}

	public SystemInfo getSystem() {
		return system;
	}

	public void setSystem(SystemInfo system) {
		this.system = system;
		if (system != null && StringUtils.isBlank(system.getId()))
			this.father = null;
	}
	
}
