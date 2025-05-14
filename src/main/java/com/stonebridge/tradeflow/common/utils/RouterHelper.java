package com.stonebridge.tradeflow.common.utils;

import cn.hutool.core.util.StrUtil;
import com.stonebridge.tradeflow.system.entity.SysMenu;
import com.stonebridge.tradeflow.system.entity.vo.MetaVo;
import com.stonebridge.tradeflow.system.entity.vo.RouterVo;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 根据菜单数据构建路由的工具类
 * <p>
 * 菜单类型（SysMenu.type）：
 * - 0：目录
 * - 1：菜单
 * - 2：按钮
 * <p>
 * 路由规则：
 * - 菜单（type=1）：将子菜单（按钮）作为隐藏路由添加。
 * - 目录（type=0）：递归构建子路由，设置 alwaysShow。
 */
public class RouterHelper {

    // 菜单类型常量
    private static final int TYPE_DIRECTORY = 0;
    private static final int TYPE_MENU = 1;
    private static final int PARENT_ID_ROOT = 0;

    /**
     * 根据菜单构建路由
     *
     * @param menus 菜单列表（SysMenu 对象）
     * @return 路由列表（RouterVo 对象）
     */
    public static List<RouterVo> buildRouters(List<SysMenu> menus) {
        if (CollectionUtils.isEmpty(menus)) {
            return new ArrayList<>();
        }

        List<RouterVo> routers = new ArrayList<>();
        for (SysMenu menu : menus) {
            // 缓存字段值，减少方法调用
            int menuType = menu.getType() != null ? menu.getType() : TYPE_DIRECTORY;
            List<SysMenu> children = menu.getChildren() != null ? menu.getChildren() : new ArrayList<>();

            // 创建路由对象
            RouterVo router = createRouter(menu, false);

            // 处理菜单（type=1），添加隐藏的按钮路由
            if (menuType == TYPE_MENU) {
                for (SysMenu child : children) {
                    if (!StrUtil.isEmpty(child.getComponent())) {
                        RouterVo hiddenRouter = createRouter(child, true);
                        routers.add(hiddenRouter);
                    }
                }
            } else if (!CollectionUtils.isEmpty(children)) {
                // 处理目录（type=0），递归构建子路由
                router.setAlwaysShow(true); // 始终显示（有子菜单）
                router.setChildren(buildRouters(children));
            }

            routers.add(router);
        }
        return routers;
    }

    /**
     * 创建单个路由对象
     *
     * @param menu   菜单对象
     * @param hidden 是否隐藏
     * @return 路由对象
     */
    private static RouterVo createRouter(SysMenu menu, boolean hidden) {
        RouterVo router = new RouterVo();
        router.setHidden(hidden);
        router.setAlwaysShow(false);
        router.setPath(getRouterPath(menu));
        router.setComponent(StrUtil.isEmpty(menu.getComponent()) ? "" : menu.getComponent());
        router.setMeta(new MetaVo(
                StrUtil.isEmpty(menu.getName()) ? "" : menu.getName(),
                StrUtil.isEmpty(menu.getIcon()) ? "" : menu.getIcon()
        ));
        return router;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址（以 / 开头）
     */
    public static String getRouterPath(SysMenu menu) {
        Objects.requireNonNull(menu, "Menu cannot be null");
        String path = StrUtil.isEmpty(menu.getPath()) ? "" : menu.getPath();
        long parentId = menu.getParentId() != null ? menu.getParentId() : PARENT_ID_ROOT;

        // 确保路径以 / 开头
        String routerPath = path.startsWith("/") ? path : "/" + path;
        // 如果不是根菜单（parentId != 0），使用原始路径（不强制加 /）
        if (parentId != PARENT_ID_ROOT) {
            routerPath = path;
        }
        return routerPath.isEmpty() ? "/" : routerPath;
    }
}