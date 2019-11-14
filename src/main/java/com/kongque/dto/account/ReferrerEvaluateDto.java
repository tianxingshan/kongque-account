package com.kongque.dto.account;

/**
 * 保存模糊查询推荐人评论信息的查询条件字段
 * @author lilishan
 * @since 2018.7.13
 */
public class ReferrerEvaluateDto {

    private String id;

    /**
     * 推荐人姓名
     */
    private String referrerName;

    /**
     * 用户姓名
     */
    private String userName;


    /**
     * 推荐人id
     */
    private String referrerId;

    /**
     * 评价人id
     */
    private String appraiserId;

    /**
     * 评价内容
     */
    private String comment;

    /**
     * 评价内容
     */
    private Double score;

    /**
     * 评价类型（用户投诉：“TS”，用户评价：“PJ”）
     */
    private String commentType;

    public String getReferrerName() {
        return referrerName;
    }

    public void setReferrerName(String referrerName) {
        this.referrerName = referrerName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAppraiserId() {
        return appraiserId;
    }

    public void setAppraiserId(String appraiserId) {
        this.appraiserId = appraiserId;
    }

    public String getReferrerId() {
        return referrerId;
    }

    public void setReferrerId(String referrerId) {
        this.referrerId = referrerId;
    }

    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
