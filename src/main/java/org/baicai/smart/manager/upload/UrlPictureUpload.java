package org.baicai.smart.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import org.baicai.smart.exception.BusinessException;
import org.baicai.smart.exception.ErrorCode;
import org.baicai.smart.exception.ThrowUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

//url图片上传
@Service
public class UrlPictureUpload extends PictureUploadTemplate {
    @Override
    protected void validPicture(Object inputSource) {
        String fileUrl = (String) inputSource;
        //校验非空
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR,"文件地址为空");
        //校验url格式
        try {
            new URL(fileUrl);
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"文件地址格式不正确");
        }
        //校验rul协议
        ThrowUtils.throwIf(!fileUrl.startsWith("http://") && !fileUrl.startsWith("https://")
                ,ErrorCode.PARAMS_ERROR,"仅支持http和https协议");
        //发送head请求验证文件是否存在
        HttpResponse httpResponse = null;
        try {
            httpResponse = HttpUtil.createRequest(Method.HEAD, fileUrl).execute();
            //未正常返回,无需执行其他判断
            if(httpResponse.getStatus()!= HttpStatus.HTTP_OK){
                return;
            }
            //文件存在,文件类型校验
            String contentType = httpResponse.header("Content-Type");
            //不为空,才校验是否合法,这样校验规则相对宽松
            if(StrUtil.isBlank(contentType)){
                //允许的图片类型
                final List<String> ALLOW_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/webp");
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase())
                        ,ErrorCode.PARAMS_ERROR,"文件类型错误");
            }
            //文件存在,文件大小校验
            String contentLengthStr = httpResponse.header("Content-Length");
            if(StrUtil.isBlank(contentLengthStr)){
                try {
                    long contentLenth = Long.parseLong(contentLengthStr);
                    final long ONE_M = 1024 * 1024;
                    ThrowUtils.throwIf(contentLenth > 2 * ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过 2MB");
                }catch (NumberFormatException e){
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小格式异常");
                }
            }
        }finally {
            if(httpResponse != null){
                httpResponse.close();
            }
        }
    }

    @Override
    protected String getOriginFilename(Object inputSource) {
        String fileUrl = (String) inputSource;
        return FileUtil.mainName(fileUrl);
    }

    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        String fileUrl = (String) inputSource;
        //下载文件到临时目录
        HttpUtil.downloadFile(fileUrl,file);
    }
}
