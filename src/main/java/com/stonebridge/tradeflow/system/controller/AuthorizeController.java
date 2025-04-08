package com.stonebridge.tradeflow.system.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.dto.LoginRequest;
import com.stonebridge.tradeflow.system.service.AuthorizeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "权限管理", description = "用户登录、注册、授权处理类") // 定义 API 组名称
@RequestMapping("auth")
@RestController
@Slf4j
public class AuthorizeController {

    private AuthorizeService authorizeService;

    public AuthorizeController(AuthorizeService authorizeService) {
        this.authorizeService = authorizeService;
    }


    /**
     * Processes user login requests.
     *
     * @param request the login request containing the user's credentials
     * @return a result object containing a JSON object with the generated authentication token if successful
     */
    @Operation(summary = "用户登录", description = "处理用户登录请求")
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功返回订单列表"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "客户不存在")
    })
    public Result<JSONObject> login(@RequestBody LoginRequest request) {

        log.info("用户登录：{}", request.getUsername());
        String token = authorizeService.loginCheck(request);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", token);
        jsonObject.put("id", "007");
        jsonObject.put("name", "stonebridge");

        List<String> rights = new ArrayList<>();

        JSONObject roleObject = new JSONObject();
        rights.add("user-management");
        rights.add("role-management");
        rights.add("main");
        rights.add("menu-management");
        JSONArray jsonArray = new JSONArray(rights);

        roleObject.put("roleName", "admin");
        roleObject.put("roleType", "2");
        roleObject.put("rights", jsonArray);
        jsonObject.put("role", roleObject);
        log.info("{}用户登录登录成功", request.getUsername());
        return Result.ok(jsonObject);
    }
}
