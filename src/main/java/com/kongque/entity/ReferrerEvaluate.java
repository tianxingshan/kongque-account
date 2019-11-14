package com.kongque.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 推荐人评价表
 * @author lilisahn
 * @since 2018.7.13
 */
@Entity
@Table(name="t_referrer_evaluate")
@DynamicInsert(true)
@DynamicUpdate(true)
public class ReferrerEvaluate implements Serializable {

    private static final long serialVersionUID = -8602550311843585971L;

    /**
     * ID主键
     */
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Id
    @Column(name = "c_id")
    private String id;

    /**
     * 推荐人id
     */
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="c_referrer_id")
    private Account referrer;

    /**
     * 评价人id
     */
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn (name="c_appraiser_id")
    private Account appraiser;

    /**
     * 评价内容
     */
    @Column(name="c_comment")
    private String comment;

    /**
     * 评价内容
     */
    @Column(name="c_score")
    private Double score;

    /**
     * 评价类型（用户投诉：“TS”，用户评价：“PJ”）
     */
    @Column(name="c_comment_type")
    private String commentType;



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

    public Account getAppraiser() {
        return appraiser;
    }

    public void setAppraiser(Account appraiser) {
        this.appraiser = appraiser;
    }

    public Account getReferrer() {
        return referrer;
    }

    public void setReferrer(Account referrer) {
        this.referrer = referrer;
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
