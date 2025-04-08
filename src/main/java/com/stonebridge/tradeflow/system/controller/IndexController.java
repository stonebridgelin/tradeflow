package com.stonebridge.tradeflow.system.controller;

import cn.hutool.json.JSONArray;
import com.stonebridge.tradeflow.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "首页功能菜单", description = "首页功能菜单树生成，基础数据加载") // 定义 API 组名称
@RequestMapping("index")
@RestController
@Slf4j
public class IndexController {

    @Operation(summary = "获取首页树形菜单列表", description = "根据用户信息，获取用户权限，最后生成用户的首页树形菜单列表")
    @RequestMapping(value = "menuTreeList", method = RequestMethod.GET)
    public Result<JSONArray> getMenuTreeList(String userId) {

        String jsonStr = "[\n" +
                "  {\n" +
                "    \"title\": \"首页\",\n" +
                "    \"path\": \"/index\",\n" +
                "    \"icon\": \"HomeFilled\",\n" +
                "    \"children\": []\n" +
                "  },\n" +
                "  {\n" +
                "    \"title\": \"用户管理\",\n" +
                "    \"path\": \"/index/main\",\n" +
                "    \"icon\": \"User\",\n" +
                "    \"children\": [\n" +
                "      {\n" +
                "        \"title\": \"用户列表\",\n" +
                "        \"path\": \"/index/user-management\",\n" +
                "        \"icon\": \"List\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"title\": \"权限管理\",\n" +
                "    \"path\": \"/index/role-management\",\n" +
                "    \"icon\": \"Lock\",\n" +
                "    \"children\": [\n" +
                "      {\n" +
                "        \"title\": \"角色列表\",\n" +
                "        \"path\":\"/index/menu-management\",\n" +
                "        \"icon\": \"UserFilled\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"title\": \"权限列表\",\n" +
                "        \"path\": \"/index/register\",\n" +
                "        \"icon\": \"Grid\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"title\": \"新闻管理\",\n" +
                "    \"path\": \"/index/register\",\n" +
                "    \"icon\": \"Document\",\n" +
                "    \"children\": [\n" +
                "      {\n" +
                "        \"title\": \"撰写新闻\",\n" +
                "        \"path\": \"/index/news-manage\",\n" +
                "        \"icon\": \"Edit\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"title\": \"草稿箱\",\n" +
                "        \"path\": \"/index/news-manage\",\n" +
                "        \"icon\": \"DocumentDelete\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"title\": \"新闻分类\",\n" +
                "        \"path\": \"/index/news-manage\",\n" +
                "        \"icon\": \"Collection\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"title\": \"审核管理\",\n" +
                "    \"path\": \"/index/audit-manage\",\n" +
                "    \"icon\": \"Finished\",\n" +
                "    \"children\": [\n" +
                "      {\n" +
                "        \"title\": \"审核新闻\",\n" +
                "        \"path\": \"/index/audit-manage\",\n" +
                "        \"icon\": \"DocumentChecked\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"title\": \"审核列表\",\n" +
                "        \"path\": \"/index/audit-manage\",\n" +
                "        \"icon\": \"DocumentCopy\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"title\": \"发布管理\",\n" +
                "    \"path\": \"/index/publish-manage\",\n" +
                "    \"icon\": \"UploadFilled\",\n" +
                "    \"children\": [\n" +
                "      {\n" +
                "        \"title\": \"待发布\",\n" +
                "        \"path\": \"\",\n" +
                "        \"icon\": \"Upload\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"title\": \"已发布\",\n" +
                "        \"path\": \"\",\n" +
                "        \"icon\": \"Promotion\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"title\": \"已下线\",\n" +
                "        \"path\": \"\",\n" +
                "        \"icon\": \"Download\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "];";

        JSONArray jsonArray = new JSONArray(jsonStr);
        return Result.ok(jsonArray);
    }
}
