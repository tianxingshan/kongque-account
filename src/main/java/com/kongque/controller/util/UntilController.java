package com.kongque.controller.util;

import com.kongque.dao.IDaoService;
import com.kongque.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * 工具类
 */
@RestController
public class UntilController {
    @Resource
    private IDaoService daoService;

    /**
     * 缓存命中率
     * @param className
     * @return calssName null返回 所有
     */
    @GetMapping("/util/cache/ratio")
    public Result getCacheRatio(String className){
        return new Result(daoService.getStatistics(className));
    }

    /**
     * 清理缓存
     * @param className
     */
    @GetMapping("/util/cache/rm")
    public void cacheRm(String className){
        daoService.cacheRm(className);
    }

}
