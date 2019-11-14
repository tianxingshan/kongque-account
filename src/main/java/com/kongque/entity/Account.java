/**
 * @author pengcheng
 * @since 2017年10月16日
 */
package com.kongque.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 账户实体类
 *
 * @author pengcheng
 * @since 2017年10月16日
 */
@Entity
@Table(name = "t_account")
@DynamicInsert(true)
@DynamicUpdate(true)
public class Account implements Serializable {

    private static final long serialVersionUID = -3609734196204063073L;

    /**
     * ID主键
     */
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Id
    @Column(name = "c_id")
    private String id;

    /**
     * 当前账号登录名
     */
    @Column(name = "c_username")
    private String username;

    /**
     * 当前账号登录密码：保存MD5加密文本
     */
    @Column(name = "c_password")
    private String password;

    /**
     * 当前账户手机号
     */
    @Column(name = "c_phone")
    private String phone;

    /**
     * 当前账户手机号
     */
    @Column(name = "c_openid")
    private String openid;

    /**
     * 当前账户手机号
     */
    @Column(name = "c_unionid")
    private String unionid;

    /**
     * 注册来源：[0]非应用系统后端创建[1]PC端[2]App端[3]小程序[31]小程序注册的普通用户
     */
    @Column(name = "c_source")
    private String source;

    /**
     * 注册来源业务系统id
     */
    @Column(name = "c_sys_id")
    private String sysId;

    /**
     * 当前账号状态:1:正常（默认）、2:冻结、3:删除
     */
    @Column(name = "c_status")
    private String status;

    /**
     * 最后一次修改当前账号信息的日期时间
     */
    @Column(name = "c_update_time")
    private Date updateTime;

    /**
     * 当前账号创建日期
     */
    @Column(name = "c_create_time")
    private Date createTime;

    /**
     * 当前账户最后一次登录的日期和时间
     */
    @Column(name = "c_last_login_time")
    private Date lastLoginTime;

    /**
     * 当前账户最后一次退出的日期和时间
     */
    @Column(name = "c_last_logout_time")
    private Date lastLogoutTime;

    /**
     * 与当前账户（通常是普通小程序的自动注册账户）有映射关系的普通类型账户的id
     */
    @Column(name = "c_mapped_id")
    private String mappedId;

    /**
     * 当前账户对应的邀请码
     */
    @Column(name = "c_invitation_code")
    private String invitationCode;

    /**
     * 保存在Redis中的账号登录数据所对应的有效token
     */
    @Column(name = "c_token")
    private String token;

    /**
     * 新手指引标识 0是 1否
     */
    @Column(name = "c_new_flag")
    private String newFlag;


    /**
     * 企业账号完善信息标识 0未完善 1已完善 2非企业账号不必校验
     */
    @Column(name = "c_message_flag")
    private String messageFlag;


    /**
     * 当前账户所关联的系统对应关系列表
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "accountId", cascade = CascadeType.ALL)
    private List<AccountSys> sysList;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @Where(clause = " c_del='否'")
    @OrderBy("roleOrder")
    @JoinTable(name = "t_account_role", joinColumns = {@JoinColumn(name = "c_account_id")},
               inverseJoinColumns = {@JoinColumn(name = "c_role_id")})
    private Set<SysRole> roleSet = new HashSet<SysRole>();

    /**
     * 当前账户登录微信平台后所获session_key参数
     */
    @Transient
    private String sessionKey;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Date getLastLogoutTime() {
        return lastLogoutTime;
    }

    public void setLastLogoutTime(Date lastLogoutTime) {
        this.lastLogoutTime = lastLogoutTime;
    }

    public List<AccountSys> getSysList() {
        return sysList;
    }

    public void setSysList(List<AccountSys> sysList) {
        this.sysList = sysList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public Set<SysRole> getRoleSet() {
        return roleSet;
    }

    public void setRoleSet(Set<SysRole> roleSet) {
        this.roleSet = roleSet;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMappedId() {
        return mappedId;
    }

    public void setMappedId(String mappedId) {
        this.mappedId = mappedId;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public String getNewFlag() {
        return newFlag;
    }

    public void setNewFlag(String newFlag) {
        this.newFlag = newFlag;
    }

    public String getMessageFlag() {
        return messageFlag;
    }

    public void setMessageFlag(String messageFlag) {
        this.messageFlag = messageFlag;
    }

    @Override
    public String toString() {
        return "Account{" +
               "id='" + id + '\'' +
               ", username='" + username + '\'' +
               ", password='" + password + '\'' +
               ", phone='" + phone + '\'' +
               ", openid='" + openid + '\'' +
               ", unionid='" + unionid + '\'' +
               ", source='" + source + '\'' +
               ", sysId='" + sysId + '\'' +
               ", status='" + status + '\'' +
               ", updateTime=" + updateTime +
               ", createTime=" + createTime +
               ", lastLoginTime=" + lastLoginTime +
               ", lastLogoutTime=" + lastLogoutTime +
               ", mappedId='" + mappedId + '\'' +
               ", invitationCode='" + invitationCode + '\'' +
               ", token='" + token + '\'' +
               ", newFlag='" + newFlag + '\'' +
               ", messageFlag='" + messageFlag + '\'' +
               ", sysList=" + sysList +
               ", roleSet=" + roleSet +
               ", sessionKey='" + sessionKey + '\'' +
               '}';
    }
}
