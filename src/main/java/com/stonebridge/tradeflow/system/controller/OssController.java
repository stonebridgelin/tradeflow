package com.stonebridge.tradeflow.system.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stonebridge.tradeflow.common.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "获取aliyunOss签名", description = "处理图片上传时获取aliyunOss签名，将对象保存到oss存储")
@RestController
@Slf4j
@RequestMapping("system/aliyun")
public class OssController {
    private final static Map<String, String> dirMap = new HashMap<>();
    static {
        dirMap.put("brand_logo", "logo");
        dirMap.put("user_avatar", "avatar");
        dirMap.put("product", "product");
    }

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket}")
    private String bucket;


    @RequestMapping(path = "policy", method = RequestMethod.GET)
    public Result<ObjectNode> policy(String dirType) {
        // 请填写您的 bucketname 。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        //https://gulimall-ciel.oss-cn-shanghai.aliyuncs.com/
        String host = "https://" + bucket + ".oss-cn-shanghai.aliyuncs.com"; // host的格式为 bucketname.endpoint
        // callbackUrl为上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
        String dir = dirMap.get(dirType) + "/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/";
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = objectMapper.createObjectNode();
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);

            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            jsonObject.put("accessid", accessKeyId);
            jsonObject.put("policy", encodedPolicy);
            jsonObject.put("signature", postSignature);
            jsonObject.put("dir", dir);
            jsonObject.put("host", host);
            jsonObject.put("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));
        } catch (Exception e) {
            log.error("获取签名失败", e);
        } finally {
            ossClient.shutdown();
        }
        log.info(jsonObject.toString());
        return Result.ok(jsonObject);
    }
}