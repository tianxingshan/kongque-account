package com.kongque.controller.account;

import com.kongque.dto.account.ReferrerEvaluateDto;
import com.kongque.service.account.IReferrerEvaluateService;
import com.kongque.util.PageBean;
import com.kongque.util.Result;
import com.kongque.util.SysUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 橙意处理推荐人评价接口
 * @author lilishan
 * @since 2018.7.16
 */
@RestController
public class ReferrerEvaluateChengYiController {

    private static Logger logger=LoggerFactory.getLogger(AccountAppController.class);

    @Resource
    private IReferrerEvaluateService referrerEvaluateService;

    /**
     * 请求分页查看评价推荐人列表内容
     * @param pageBean
     * @return
     */
    @RequestMapping(value = "/account/referrer/evaluate/list",method = RequestMethod.GET)
    public Result getReferrerEvaluateList(PageBean pageBean ){

        logger.info("橙意请求获取推荐人评论分页列表信息，请求参数:PageBean"+JSONObject.fromObject(pageBean)+"当前登录用户id:"+SysUtil.getAccountId());
        return referrerEvaluateService.getReferrerEvaluateList(pageBean);
    }

    /**
     * 新增推荐人评价
     * @param dto
     * @return
     */
    @RequestMapping(value = "/account/referrer/evaluate/save" ,method = RequestMethod.POST)
    public Result saveReferrerEvaluate(@RequestBody ReferrerEvaluateDto dto ){

        logger.info("橙意请求新增推荐人评论，请求参数:ReferrerEvaluateDto"+JSONObject.fromObject(dto)+"当前评论人id:"+SysUtil.getAccountId());
        return referrerEvaluateService.saveReferrerEvaluate(dto);
    }


}
