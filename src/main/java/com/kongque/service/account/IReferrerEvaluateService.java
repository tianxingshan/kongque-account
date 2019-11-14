package com.kongque.service.account;

import com.kongque.dto.account.InvitationDto;
import com.kongque.dto.account.ReferrerEvaluateDto;
import com.kongque.util.PageBean;
import com.kongque.util.Result;

/**
 * 推荐人评价接口
 * @author lilishan
 * @since 2018.7.16
 */
public interface IReferrerEvaluateService {

    /**
     * PC端
     * 分页查询推荐人评价列表
     * @param pageBean
     * @param dto
     * @return
     */
    public Result getReferrerEvaluateList(PageBean pageBean, ReferrerEvaluateDto dto);

    /**
     * 橙意端分页查询推荐人评价列表
     * @param pageBean
     * @return
     */
    public Result getReferrerEvaluateList(PageBean pageBean);

    /**
     * 橙意新增推荐用户评价信息
     * @param referrerEvaluate
     * @return
     */
    public Result saveReferrerEvaluate(ReferrerEvaluateDto referrerEvaluate);

    /**
     * 根据id查看当前评价详情
     * @param referrerId
     * @return
     */
    public Result getReferrerEvaluateById(String referrerId);


    /**
     * 查询会员详情列表
     * @param pageBean
     * @return
     */
    public  Result getMembershipList(PageBean pageBean);


    /**
     * PC端修改推荐人接口
     * @param invitationDto
     * @return
     */
    public Result updateInvitation(InvitationDto invitationDto);

    /**
     * PC请求查看邀请关系列表
     * @param pageBean
     * @param dto
     * @return
     */
    public Result getInvitationList(PageBean pageBean, InvitationDto dto);

    /**
     * 添加推荐人
     * @param invitationDto
     * @return
     */
    public Result saveInvitation(InvitationDto invitationDto);
}
