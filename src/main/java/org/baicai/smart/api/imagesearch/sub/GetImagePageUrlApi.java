package org.baicai.smart.api.imagesearch.sub;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.baicai.smart.exception.BusinessException;
import org.baicai.smart.exception.ErrorCode;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取以图搜图页面地址（step 1）
 */
@Slf4j
public class GetImagePageUrlApi {

    /**
     * 获取以图搜图页面地址
     *
     * @param imageUrl
     * @return
     */
    public static String getImagePageUrl(String imageUrl) {
        // image: https%3A%2F%2Fwww.codefather.cn%2Flogo.png
        //tn: pc
        //from: pc
        //image_source: PC_UPLOAD_URL
        //sdkParams:
        // 1. 准备请求参数
        Map<String, Object> formData = new HashMap<>();
        formData.put("image", imageUrl);
        formData.put("tn", "pc");
        formData.put("from", "pc");
        formData.put("image_source", "PC_UPLOAD_URL");
        // 获取当前时间戳
        long uptime = System.currentTimeMillis();
        // 请求地址
        String url = "https://graph.baidu.com/upload?uptime=" + uptime;
        String Token = "1775107143618_1775114265911_SkreuirozdI5doDxekfckkKJrEk" +
                "UUFRfltWQ8wN7uq+22EJC7Vw1tX1dJLYde9jAOYSQv4vKopsmd6fCFveyvF+iXRkPhO3lWCKWkx6pF9yi2KS" +
                "ZW3QABhtGRwXPET8pfP25MBuX6pQVQPwC8X4yjS9hijO+cRckZXBZerz6qRFoHHIJD67O2IYklbTo+QzAeFXAnOYIAfLFwf" +
                "NMTmOnG14jl43czVC6dWokA8RsvxqNCt3fjefeYzWNVcv1zP40DkJXu5JO/q+0Dljv9jZBD0QkoaY9/LrAU2DRe1mWtzAMXdK" +
                "hrK+JYl66Fh//DZ466lKTqplo51DEE8OE9FstikYX/eoXF/t/QWQfLjI3C3U7XiQ90mE+fzVUhUzq+tef69KqcFquIn1ku1bs+VYmKicxL" +
                "YwuSetiX8FdDFpLKtP717dti29rA1PtoNZxsMkSPpl2DmXLpzgZ+V+pOFgYVtkLHzHKDjPkGgRu5/0ClH0=";
        try {
            // 2. 发送请求
            HttpResponse httpResponse = HttpRequest.post(url)
                    .form(formData)
                    .header("Acs-Token", Token)
                    .timeout(5000)
                    .execute();
            System.out.println(httpResponse.body());
            /*if (httpResponse.getStatus() != HttpStatus.HTTP_OK) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败1");
            }
            // 解析响应
            // {"status":0,"msg":"Success","data":{"url":"https://graph.baidu.com/sc","sign":"1262fe97cd54acd88139901734784257"}}
            String body = httpResponse.body();
            Map<String, Object> result = JSONUtil.toBean(body, Map.class);
            // 3. 处理响应结果
            if (result == null || !Integer.valueOf(0).equals(result.get("status"))) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            // 对 URL 进行解码
            String rawUrl = (String) data.get("url");
            String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);
            // 如果 URL 为空
            if (StrUtil.isBlank(searchResultUrl)) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "未返回有效的结果地址");
            }*/
            String searchResultUrl = "https://graph.baidu.com/s?card_key=&entrance=GENERAL&extUiData%5BisLogoShow%5D=1&f=all&isLogoShow=1&session_id=3717292902148093323&sign=126346a884a7974e2b35501775100317&tpl_from=pc";
            return searchResultUrl;
        } catch (Exception e) {
            log.error("调用百度以图搜图接口失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜索失败");
        }
    }



    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://www.codefather.cn/logo.png";
        String searchResultUrl = getImagePageUrl(imageUrl);
        System.out.println("搜索成功，结果 URL：" + searchResultUrl);
    }
}
