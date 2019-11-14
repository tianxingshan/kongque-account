package com.kongque.dto.account;

import java.util.Date;

/**
 * 保存会员详情信息
 * @author lilishan
 * @since 2018.7.18
 */
public class MembershipDetailDto {

    private String id;
    /**
     * 会员详情
     * {"sex": "男", "tel": "13345678901", "name": "张无忌", "email": "zhangwuji@qq.com",
     * "height": "198", "mobile": "13345678901", "weight": "90", "address": "济南市",
     * "birthday": "2017-11-28", "occupation": "it", "postalcode": "123",
     * "deliveryAddress": "济南市", "receivingAddress": "济南市"}
     */
    private String detail;
    /**
     * 账号系统(kongque-account)账号表(t_account)c_id字段
     */
    private String kongqueAccountId;


    /**
     * 上级id
     */
    private String superiorId;

    /**
     * 合伙人id
     */
    private String partnerId;

    /**
     * 销售级别表id
     */
    private String tenantSaleRankId;

    /**
     * 是否是尊享会员（“Y”：是，“N”：不是）
     */
    private String enjoyMemberIdentification;

    /**
     * 会员类型（"PT":普通会员、"XS":销售、"HHR":合伙人）
     */
    private String memberType;



    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getEnjoyMemberIdentification() {
        return enjoyMemberIdentification;
    }

    public void setEnjoyMemberIdentification(String enjoyMemberIdentification) {
        this.enjoyMemberIdentification = enjoyMemberIdentification;
    }

    public String getTenantSaleRankId() {
        return tenantSaleRankId;
    }

    public void setTenantSaleRankId(String tenantSaleRankId) {
        this.tenantSaleRankId = tenantSaleRankId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getSuperiorId() {
        return superiorId;
    }

    public void setSuperiorId(String superiorId) {
        this.superiorId = superiorId;
    }

    public String getKongqueAccountId() {
        return kongqueAccountId;
    }

    public void setKongqueAccountId(String kongqueAccountId) {
        this.kongqueAccountId = kongqueAccountId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
