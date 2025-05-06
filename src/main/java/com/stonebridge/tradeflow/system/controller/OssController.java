package com.stonebridge.tradeflow.system.controller;

import cn.hutool.json.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.stonebridge.tradeflow.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@Slf4j
@RequestMapping("system/aliyun")
public class OssController {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket}")
    private String bucket;


    @GetMapping(path = "policy")
    public Result<JSONObject> policy() {
        // 请填写您的 bucketname 。
        String bucket = "gulimall-ciel";
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        //https://gulimall-ciel.oss-cn-shanghai.aliyuncs.com/
        String host = "https://" + bucket + ".oss-cn-shanghai.aliyuncs.com"; // host的格式为 bucketname.endpoint
        // callbackUrl为上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
        String dir = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/";
        JSONObject jsonObject = new JSONObject();
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

            jsonObject.set("accessid", accessKeyId);
            jsonObject.set("policy", encodedPolicy);
            jsonObject.set("signature", postSignature);
            jsonObject.set("dir", dir);
            jsonObject.set("host", host);
            jsonObject.set("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));
        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
        log.info(jsonObject.toString());
        return Result.ok(jsonObject);
    }

}