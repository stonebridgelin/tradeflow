package com.stonebridge.tradeflow.common.cache;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stonebridge.tradeflow.business.entity.category.Category;
import com.stonebridge.tradeflow.business.mapper.CategoryMapper;
import com.stonebridge.tradeflow.system.entity.DataDictionary;
import com.stonebridge.tradeflow.system.mapper.DataDictionaryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Redis 缓存工具类，负责加载数据库表数据到 Redis，并在数据更新时刷新缓存。
 */
@Component
@Slf4j
public class MyRedisCache implements CommandLineRunner {

    private final RedisTemplate<String, Object> redisTemplate;
    private final DataDictionaryMapper dataDictionaryMapper;
    private final CategoryMapper categoryMapper;

    public MyRedisCache(DataDictionaryMapper dataDictionaryMapper, CategoryMapper categoryMapper,
                        RedisTemplate<String, Object> redisTemplate) {
        this.dataDictionaryMapper = dataDictionaryMapper;
        this.categoryMapper = categoryMapper;
        this.redisTemplate = redisTemplate;

        // 初始化缓存加载器
        initializeCacheLoaders();
    }

    // 缓存加载器映射，key 为缓存类型，value 为对应的加载方法
    private final Map<String, CacheLoader> cacheLoaders = new HashMap<>();

    // 缓存常量定义
    public static class CacheConstants {
        // 缓存键前缀
        private static final String CACHE_PREFIX = "tradeflow:cache:";

        // 缓存类型
        public static final String TYPE_DATA_DICTIONARY = "dataDictionary";
        public static final String TYPE_CATEGORY = "category";


        /**
         * 生成 DataDictionary 的 type 键。
         *
         * @param type DataDictionary 类型
         * @return 缓存键，例如 "tradeflow:cache:dict:type:supplier_type"
         */
        static String getDictTypeKey(String type) {
            return CACHE_PREFIX + "dict:type:" + type;
        }

        /**
         * 生成 DataDictionary 的 type 和 code 键。
         *
         * @param type DataDictionary 类型
         * @param code DataDictionary 代码
         * @return 缓存键，例如 "tradeflow:cache:dict:supplier_type:code1"
         */
        static String getDictTypeCodeKey(String type, String code) {
            return CACHE_PREFIX + "dict:" + type + ":" + code;
        }

        /**
         * 生成 Category 的 id 键。
         *
         * @param id Category ID
         * @return 缓存键，例如 "tradeflow:cache:category:123"
         */
        static String getCategoryKey(String id) {
            return CACHE_PREFIX + "category:" + id;
        }
    }



    /**
     * 初始化缓存加载器，注册所有表的缓存加载逻辑。
     */
    private void initializeCacheLoaders() {
        // 注册 DataDictionary 缓存加载器
        cacheLoaders.put(CacheConstants.TYPE_DATA_DICTIONARY, new CacheLoader(() -> loadDataDictionary()));

        // 注册 Category 缓存加载器
        cacheLoaders.put(CacheConstants.TYPE_CATEGORY, new CacheLoader(() -> loadCategory()));
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting to load all data into Redis cache...");
        loadAllCaches();
    }

    /**
     * 项目启动时加载所有缓存。
     * <p>
     * 该方法会在项目启动时自动调用，加载所有注册的缓存数据到 Redis。
     * 如果某个缓存加载失败，会记录错误日志，但不会影响其他缓存的加载。
     * </p>
     * <p><b>调用示例：</b></p>
     * <pre>
     * // 通常无需手动调用，由 Spring Boot 在项目启动时自动执行
     * redisCache.loadAllCaches();
     * </pre>
     */
    public void loadAllCaches() {
        cacheLoaders.forEach((cacheType, loader) -> {
            try {
                log.info("Loading cache for type: {}", cacheType);
                loader.loadCache();
            } catch (Exception e) {
                log.error("Failed to load cache for type: {}. Error: {}", cacheType, e.getMessage(), e);
            }
        });
    }

    /**
     * 刷新指定类型的缓存。
     * <p>
     * 当数据库表数据更新后，可以调用此方法刷新对应的 Redis 缓存，确保缓存与数据库一致。
     * 如果指定的缓存类型未注册，会记录警告日志。
     * </p>
     *
     * @param cacheType 缓存类型，例如 "dataDictionary" 或 "category"
     *                  <p><b>调用示例：</b></p>
     *                  <pre>
     *                  // 更新 DataDictionary 表后刷新缓存
     *                  dataDictionaryMapper.updateById(updatedDict);
     *                  redisCache.refreshCache("dataDictionary");
     *                  </pre>
     */
    public void refreshCache(String cacheType) {
        CacheLoader loader = cacheLoaders.get(cacheType);
        if (loader == null) {
            log.warn("No cache loader found for type: {}", cacheType);
            return;
        }
        try {
            log.info("Refreshing cache for type: {}", cacheType);
            loader.loadCache();
        } catch (Exception e) {
            log.error("Failed to refresh cache for type: {}. Error: {}", cacheType, e.getMessage(), e);
        }
    }

    /**
     * 加载 DataDictionary 数据到 Redis 缓存。
     */
    private void loadDataDictionary() {
        List<DataDictionary> list = dataDictionaryMapper.selectList(null);
        if (list == null || list.isEmpty()) {
            log.warn("No DataDictionary data found in database.");
            return;
        }

        // 按 type 分组存储（用于前端下拉选）
        Map<String, List<DataDictionary>> typeMap = list.stream()
                .collect(Collectors.groupingBy(DataDictionary::getType));
        for (Map.Entry<String, List<DataDictionary>> entry : typeMap.entrySet()) {
            String typeKey = CacheConstants.getDictTypeKey(entry.getKey());
            redisTemplate.delete(typeKey); // 先删除旧数据
            for (DataDictionary dict : entry.getValue()) {
                redisTemplate.opsForHash().put(typeKey, dict.getCode(), dict);
            }
        }

        // 按 type+code 存储（用于后端翻译）
        for (DataDictionary dict : list) {
            String typeCodeKey = CacheConstants.getDictTypeCodeKey(dict.getType(), dict.getCode());
            redisTemplate.opsForValue().set(typeCodeKey, dict);
        }

        log.info("Loaded {} DataDictionary records into Redis cache.", list.size());
    }

    /**
     * 加载 Category 数据到 Redis 缓存。
     */
    private void loadCategory() {
        List<Category> list = categoryMapper.selectList(null);
        if (list == null || list.isEmpty()) {
            log.warn("No Category data found in database.");
            return;
        }

        for (Category category : list) {
            String key = CacheConstants.getCategoryKey(category.getId());
            redisTemplate.opsForValue().set(key, category);
        }

        log.info("Loaded {} Category records into Redis cache.", list.size());
    }

    /**
     * 根据 ID 从 Redis 获取 Category 数据，未命中时从数据库加载。
     * <p>
     * 如果 Redis 中存在指定 ID 的 Category 数据，则直接返回；否则从数据库加载并存入 Redis。
     * </p>
     *
     * @param id Category ID
     * @return Category 对象，如果未找到则返回 null
     * <p><b>调用示例：</b></p>
     * <pre>
     * // 获取 ID 为 "123" 的 Category 数据
     * Category category = redisCache.getCategoryById("123");
     * if (category != null) {
     *     System.out.println("Category name: " + category.getName());
     * }
     * </pre>
     */
    public Category getCategoryById(String id) {
        String key = CacheConstants.getCategoryKey(id);
        Category category = (Category) redisTemplate.opsForValue().get(key);
        if (category == null) {
            log.debug("Cache miss for Category ID: {}. Loading from database...", id);
            category = categoryMapper.selectById(id);
            if (category != null) {
                redisTemplate.opsForValue().set(key, category);
                log.debug("Loaded Category ID: {} from database and cached in Redis.", id);
            } else {
                log.warn("Category ID: {} not found in database.", id);
            }
        }
        return category;
    }

    /**
     * 根据 type 从 Redis 获取 DataDictionary 数据，未命中时从数据库加载。
     * <p>
     * 如果 Redis 中存在指定 type 的 DataDictionary 数据，则直接返回；否则从数据库加载并存入 Redis。
     * </p>
     *
     * @param type DataDictionary 类型
     * @return DataDictionary 列表，如果未找到则返回空列表
     * <p><b>调用示例：</b></p>
     * <pre>
     * // 获取 type 为 "supplier_type" 的 DataDictionary 数据
     * List<DataDictionary> dictList = redisCache.getDataDictionaryByType("supplier_type");
     * dictList.forEach(dict -> System.out.println("Code: " + dict.getCode() + ", Name: " + dict.getName()));
     * </pre>
     */
    public List<DataDictionary> getDataDictionaryByType(String type) {
        String typeKey = CacheConstants.getDictTypeKey(type);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(typeKey);
        if (entries.isEmpty()) {
            log.debug("Cache miss for DataDictionary type: {}. Loading from database...", type);
            List<DataDictionary> list = dataDictionaryMapper.selectList(
                    new QueryWrapper<DataDictionary>().eq("type", type));
            if (list != null && !list.isEmpty()) {
                for (DataDictionary dict : list) {
                    redisTemplate.opsForHash().put(typeKey, dict.getCode(), dict);
                }
                log.debug("Loaded {} DataDictionary records for type: {} from database and cached in Redis.", list.size(), type);
                return list;
            } else {
                log.warn("No DataDictionary records found for type: {} in database.", type);
                return new ArrayList<>();
            }
        }
        return entries.values().stream()
                .map(obj -> (DataDictionary) obj)
                .collect(Collectors.toList());
    }

    /**
     * 根据 type 和 code 从 Redis 获取 DataDictionary 数据，未命中时从数据库加载。
     * <p>
     * 如果 Redis 中存在指定 type 和 code 的 DataDictionary 数据，则直接返回；否则从数据库加载并存入 Redis。
     * </p>
     *
     * @param type DataDictionary 类型
     * @param code DataDictionary 代码
     * @return DataDictionary 对象，如果未找到则返回 null
     * <p><b>调用示例：</b></p>
     * <pre>
     * // 获取 type 为 "supplier_type" 且 code 为 "code1" 的 DataDictionary 数据
     * DataDictionary dict = redisCache.getDataDictionaryByTypeAndCode("supplier_type", "code1");
     * if (dict != null) {
     *     System.out.println("Name: " + dict.getName());
     * }
     * </pre>
     */
    public DataDictionary getDataDictionaryByTypeAndCode(String type, String code) {
        String typeCodeKey = CacheConstants.getDictTypeCodeKey(type, code);
        DataDictionary dict = (DataDictionary) redisTemplate.opsForValue().get(typeCodeKey);
        if (dict == null) {
            log.debug("Cache miss for DataDictionary type: {}, code: {}. Loading from database...", type, code);
            dict = dataDictionaryMapper.selectOne(
                    new QueryWrapper<DataDictionary>()
                            .eq("type", type)
                            .eq("code", code));
            if (dict != null) {
                redisTemplate.opsForValue().set(typeCodeKey, dict);
                log.debug("Loaded DataDictionary type: {}, code: {} from database and cached in Redis.", type, code);
            } else {
                log.warn("DataDictionary type: {}, code: {} not found in database.", type, code);
            }
        }
        return dict;
    }

    /**
     * 缓存加载器类，封装缓存加载逻辑。
     */
    private static class CacheLoader {
        private final Runnable loadCacheMethod;

        CacheLoader(Runnable loadCacheMethod) {
            this.loadCacheMethod = loadCacheMethod;
        }

        void loadCache() {
            loadCacheMethod.run();
        }
    }
}