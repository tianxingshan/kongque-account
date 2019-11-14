package com.kongque.controller.account;

import com.kongque.dto.account.InvitationDto;
import com.kongque.dto.account.ReferrerEvaluateDto;
import com.kongque.service.account.IReferrerEvaluateService;
import com.kongque.util.PageBean;
import com.kongque.util.Result;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * PC端处理推荐人评价业务
 * @author lilishan
 * @since 2018.7.16
 */
@RestController
public class ReferrerEvaluateController {

    private static Logger logger=LoggerFactory.getLogger(AccountAppController.class);

    @Resource
    private IReferrerEvaluateService referrerEvaluateService;

    /**
     * PC请求分页查看评价推荐人列表内容
     * @param pageBean
     * @param dto
     * @return
     */
    @RequestMapping(value = "/account-pc/get/referrer-evaluate/list")
    public Result getReferrerEvaluateList(PageBean pageBean,ReferrerEvaluateDto dto ){

        logger.info("前台请求获取推荐人评论分页列表信息，请求参数:PageBean"+JSONObject.fromObject(pageBean)+",ReferrerEvaluate"+JSONObject.fromObject(dto));
        return referrerEvaluateService.getReferrerEvaluateList(pageBean,dto);
    }

    /**
     * PC请求分页查看推荐人关系列表内容
     * @param pageBean
     * @param dto
     * @return
     */
    @RequestMapping(value = "/account-pc/get/invitation/list")
    public Result getInvitationList(PageBean pageBean,InvitationDto dto ){

        logger.info("前台请求获取推荐人关系列表信息，请求参数:PageBean"+JSONObject.fromObject(pageBean)+",ReferrerEvaluate"+JSONObject.fromObject(dto));
        return referrerEvaluateService.getInvitationList(pageBean,dto);
    }

    /**
     * 根据id查看当前用户评价信息
     * @param referrerId
     * @return
     */
    @GetMapping(value = "/account-pc/get/referrer-evaluate/{referrerId}")
    public Result getReferrerEvaluateById(@PathVariable String referrerId){

        logger.info("前台请求根据id获取推荐人评论信息，请求参数:referrerId"+referrerId);
        return referrerEvaluateService.getReferrerEvaluateById(referrerId);
    }

    /**
     * 获取会员列表信息
     * @param pageBean
     * @return
     */
    @GetMapping(value = "/account-pc/get/membership/list")
    public Result getMembershipList(PageBean pageBean){
        logger.info("前台修改推荐人时请求调用会员系统的详情分页列表接口PageBean："+JSONObject.fromObject(pageBean));
        return referrerEvaluateService.getMembershipList(pageBean);
    }

    /**
     * 修改推荐人
     * @param invitationDto
     * @author lilishan
     * @date: 2018年7月16日
     */
    @PostMapping(value="/account-pc/invitation/update",produces="application/json")
    public Result updateInvitation(@RequestBody InvitationDto invitationDto){
        return referrerEvaluateService.updateInvitation(invitationDto);
    }

    /**
     * 添加推荐人
     * @param invitationDto
     * @author lilishan
     * @date: 2018年7月16日
     */
    @PostMapping(value="/account-pc/invitation/save",produces="application/json")
    public Result saveInvitation(@RequestBody InvitationDto invitationDto){
        return referrerEvaluateService.saveInvitation(invitationDto);
    }


}
