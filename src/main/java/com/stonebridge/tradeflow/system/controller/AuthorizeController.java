package com.stonebridge.tradeflow.system.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.common.utils.PasswordUtils;
import com.stonebridge.tradeflow.system.entity.User;
import com.stonebridge.tradeflow.system.entity.dto.LoginDto;
import com.stonebridge.tradeflow.system.entity.dto.RegisterDto;
import com.stonebridge.tradeflow.system.service.AuthorizeService;
import com.stonebridge.tradeflow.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Tag(name = "权限管理", description = "用户登录、注册、授权处理类") // 定义 API 组名称
@RequestMapping("auth")
@RestController
@Slf4j
public class AuthorizeController {

    private AuthorizeService authorizeService;
    private UserService userService;

    public AuthorizeController(AuthorizeService authorizeService, UserService userService) {
        this.authorizeService = authorizeService;
        this.userService = userService;
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
    public Result<JSONObject> login(@RequestBody LoginDto request) {

        log.info("用户登录：{}", request.getUsername());
        String token = authorizeService.loginCheck(request);
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("token", token);
        jsonObject.set("id", "007");
        jsonObject.set("username", "stonebridge");

        List<String> rights = new ArrayList<>();

        JSONObject roleObject = new JSONObject();
        rights.add("user-management");
        rights.add("role-management");
        rights.add("main");
        rights.add("menu-management");
        rights.add("assignMenu");
        JSONArray jsonArray = new JSONArray(rights);

        roleObject.set("roleName", "admin");
        roleObject.set("roleType", "2");
        roleObject.set("rights", jsonArray);
        jsonObject.set("role", roleObject);
        log.info("{}用户登录登录成功", request.getUsername());
        return Result.ok(jsonObject);
    }

    @Operation(summary = "用户注册", description = "处理用户注册请求")
    @PostMapping("register")
    public Result register(@RequestBody RegisterDto registerDto) {
        log.info("开始处理用户注册请求: username={}", registerDto.getUsername());
        try {
            User newUser = new User();
            newUser.setUsername(registerDto.getUsername().trim());
            newUser.setPassword(PasswordUtils.encode(registerDto.getPassword().trim()));
            newUser.setFirstName(registerDto.getFirstName().trim());
            newUser.setLastName(registerDto.getLastName().trim());
            newUser.setEmail(registerDto.getEmail().trim());
            newUser.setPhone(registerDto.getPhone().trim());
            newUser.setCreateTime(new Date());
            newUser.setUpdateTime(new Date());
            newUser.setIsDeleted(0);
            newUser.setStatus("0");
            userService.save(newUser);
            log.info("用户 {} 注册成功", registerDto.getUsername());
            return Result.ok();
        } catch (Exception e) {
            log.error("用户 {}", registerDto.getUsername(), e);
            throw e;
        }
    }
}
