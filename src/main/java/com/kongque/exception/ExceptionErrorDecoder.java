package com.kongque.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kongque.util.MyLogger;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import net.sf.json.JSONObject;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: yuehui
 * @Date: 6/17 0017 13:47
 * @Description:
 */
@Configuration
public class ExceptionErrorDecoder implements ErrorDecoder {

    private static MyLogger logger=MyLogger.getinstance();

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String s, Response response) {

        try {
            if (response.body() != null) {
                String targetMsg = "";
                String body = Util.toString(response.body().asReader());
                FeignExceptionInfo ei = this.objectMapper.readValue(body.getBytes("UTF-8"), FeignExceptionInfo.class);
                String message = ei.getMessage();
                if (ei.getException()!=null&&ei.getException().endsWith("CommonException")) {
                    targetMsg = message.substring(message.indexOf("{"), message.indexOf("}") + 1);
                    JSONObject exJosn=JSONObject.fromObject(targetMsg);
                    logger.error("系统调用异常："+ei.toString());
                    return new CommonException(exJosn.getString("code"),exJosn.getString("message"));
                }else{
                    return new RuntimeException(ei.toString());
                }
            }
        } catch (Exception var4) {
            return var4;
        }
        return new CommonException("400","系统异常,请联系管理员");
    }

}
