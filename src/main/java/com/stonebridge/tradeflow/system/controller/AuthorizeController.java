package com.stonebridge.tradeflow.system.controller;

import cn.hutool.json.JSONObject;
import com.stonebridge.tradeflow.common.exception.CustomizeException;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.common.utils.JwtUtil;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static com.stonebridge.tradeflow.common.result.ResultCodeEnum.*;

@Tag(name = "处理用户注册，登录检查并获取token，返回用户信息（权限信息、个人信息）", description = "该方法主要对接User表，涉及权限的表有sys_role,sys_user_role,sys_role_menu,sys_menu表")
// 定义 API 组名称
@RequestMapping("auth")
@RestController
@Slf4j
public class AuthorizeController {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

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
//        jsonObject.set("id", "007");
//        jsonObject.set("username", "stonebridge");
//
//        List<String> rights = new ArrayList<>();
//
//        JSONObject roleObject = new JSONObject();
//        rights.add("user-management");
//        rights.add("role-management");
//        rights.add("main");
//        rights.add("menu-management");
//        rights.add("assignMenu");
//        JSONArray jsonArray = new JSONArray(rights);
//
//        roleObject.set("roleName", "admin");
//        roleObject.set("roleType", "2");
//        roleObject.set("rights", jsonArray);
//        jsonObject.set("role", roleObject);
        log.info("{}用户登录登录成功", request.getUsername());
        return Result.ok(jsonObject);
    }


    /**
     * 获取用户信息接口
     * 从请求头的 Authorization 中提取 Bearer Token，解析 userId，查询用户信息并返回。
     *
     * @param request HTTP 请求，包含 Authorization 头
     * @return Result 封装的用户信息（UserInfoVO）
     */
    @Operation(summary = "用户信息", description = "获取当前用户信息")
    @GetMapping("info")
    public Result<JSONObject> info(HttpServletRequest request) {
        // 1. 从请求头获取 Token
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Invalid or missing Authorization header: {}", authHeader);
            throw new CustomizeException(LOGIN_AUTH.getCode(), LOGIN_AUTH.getMessage());
        }
        String token = authHeader.substring(BEARER_PREFIX.length());
        if (token.isEmpty()) {
            log.warn("Token is empty");
            throw new CustomizeException(LOGIN_AUTH.getCode(), LOGIN_AUTH.getMessage());
        }

        // 2. 解析 Token 获取 userId
        String userId;
        try {
            userId = JwtUtil.getUserId(token);
            if (userId == null) {
                log.warn("UserId not found in token");
                throw new CustomizeException(ACCOUNT_ERROR.getCode(), ACCOUNT_ERROR.getMessage());

            }
            log.info("Successfully parsed userId from token: {}", userId);
        } catch (Exception e) {
            log.error("Failed to parse token: {}", e.getMessage());
            throw new CustomizeException(PERMISSION.getCode(), PERMISSION.getMessage());
        }

        // 3. 查询用户信息
        //根据用户id获取用户信息（基本信息 菜单权限 按钮权限信息）
        JSONObject userInfo = authorizeService.getUserInfo(userId);
        if (userInfo == null) {
            log.warn("User info not found for userId: {}", userId);
            throw new CustomizeException(ACCOUNT_ERROR.getCode(), ACCOUNT_ERROR.getMessage());
        }
        // 4. 返回用户信息
        return Result.ok(userInfo);
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
            newUser.setAvatar(registerDto.getAvatarUrl().trim());
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
