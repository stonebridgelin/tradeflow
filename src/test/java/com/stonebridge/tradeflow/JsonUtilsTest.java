package com.stonebridge.tradeflow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.stonebridge.tradeflow.common.utils.JsonUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonUtils工具类测试
 */
@SpringBootTest
public class JsonUtilsTest {

    private User testUser;
    private List<User> testUserList;
    private Map<String, Object> testMap;
    private String userJson;
    private String userListJson;
    private String complexJson;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testUser = new User("张三", 25, "zhangsan@example.com", LocalDateTime.now());

        User user2 = new User("李四", 30, "lisi@example.com", LocalDateTime.now());
        testUserList = Arrays.asList(testUser, user2);

        testMap = new HashMap<>();
        testMap.put("name", "王五");
        testMap.put("age", 28);
        testMap.put("active", true);

        userJson = "{\"name\":\"张三\",\"age\":25,\"email\":\"zhangsan@example.com\",\"createTime\":\"2025-06-19 10:30:00\"}";
        userListJson = "[{\"name\":\"张三\",\"age\":25},{\"name\":\"李四\",\"age\":30}]";
        complexJson = "{\"user\":{\"name\":\"张三\",\"profile\":{\"age\":25,\"city\":\"北京\"}},\"tags\":[\"java\",\"spring\"],\"count\":100}";
    }

    @Test
    @DisplayName("测试对象转JSON字符串")
    void testToJsonString() {
        String json = JsonUtils.toJsonString(testUser);
        assertNotNull(json);
        assertTrue(json.contains("张三"));
        assertTrue(json.contains("25"));
        System.out.println("对象转JSON: " + json);
    }

    @Test
    @DisplayName("测试对象转格式化JSON字符串")
    void testToJsonPrettyString() {
        String prettyJson = JsonUtils.toJsonPrettyString(testUser);
        assertNotNull(prettyJson);
        assertTrue(prettyJson.contains("\n")); // 格式化后应该包含换行符
        System.out.println("格式化JSON: \n" + prettyJson);
    }

    @Test
    @DisplayName("测试JSON字符串转对象")
    void testParseObject() {
        User user = JsonUtils.parseObject(userJson, User.class);
        assertNotNull(user);
        assertEquals("张三", user.getName());
        assertEquals(25, user.getAge());
        System.out.println("JSON转对象: " + user);
    }

    @Test
    @DisplayName("测试JSON字符串转对象(TypeReference)")
    void testParseObjectWithTypeReference() {
        String mapJson = JsonUtils.toJsonString(testMap);
        Map<String, Object> map = JsonUtils.parseObject(mapJson, new TypeReference<Map<String, Object>>() {});
        assertNotNull(map);
        assertEquals("王五", map.get("name"));
        assertEquals(28, map.get("age"));
        System.out.println("TypeReference转换: " + map);
    }

    @Test
    @DisplayName("测试JSON字符串转List")
    void testParseList() {
        List<User> users = JsonUtils.parseList(userListJson, User.class);
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("张三", users.get(0).getName());
        assertEquals("李四", users.get(1).getName());
        System.out.println("JSON转List: " + users);
    }

    @Test
    @DisplayName("测试JSON字符串转Map")
    void testParseMap() {
        Map<String, Object> map = JsonUtils.parseMap(userJson);
        assertNotNull(map);
        assertEquals("张三", map.get("name"));
        assertEquals(25, map.get("age"));
        System.out.println("JSON转Map: " + map);
    }

    @Test
    @DisplayName("测试JSON字符串转指定类型Map")
    void testParseMapWithTypes() {
        String simpleJson = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
        Map<String, String> map = JsonUtils.parseMap(simpleJson, String.class, String.class);
        assertNotNull(map);
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));
        System.out.println("指定类型Map: " + map);
    }

    @Test
    @DisplayName("测试对象转Map")
    void testObjectToMap() {
        Map<String, Object> map = JsonUtils.objectToMap(testUser);
        assertNotNull(map);
        assertEquals("张三", map.get("name"));
        assertEquals(25, map.get("age"));
        System.out.println("对象转Map: " + map);
    }

    @Test
    @DisplayName("测试Map转对象")
    void testMapToObject() {
        User user = JsonUtils.mapToObject(testMap, User.class);
        assertNotNull(user);
        assertEquals("王五", user.getName());
        assertEquals(28, user.getAge());
        System.out.println("Map转对象: " + user);
    }

    @Test
    @DisplayName("测试解析JSON树")
    void testParseTree() {
        JsonNode node = JsonUtils.parseTree(complexJson);
        assertNotNull(node);
        assertTrue(node.has("user"));
        assertEquals("张三", node.get("user").get("name").asText());
        System.out.println("JSON树解析: " + node.toPrettyString());
    }

    @Test
    @DisplayName("测试JSON格式校验")
    void testIsValidJson() {
        assertTrue(JsonUtils.isValidJson(userJson));
        assertTrue(JsonUtils.isValidJson("{}"));
        assertTrue(JsonUtils.isValidJson("[]"));
        assertFalse(JsonUtils.isValidJson("{invalid json}"));
        assertFalse(JsonUtils.isValidJson(null));
        assertFalse(JsonUtils.isValidJson(""));
        System.out.println("JSON格式校验通过");
    }

    @Test
    @DisplayName("测试路径取值")
    void testGetValueByPath() {
        String name = JsonUtils.getValueByPath(complexJson, "user.name");
        assertEquals("张三", name);

        String age = JsonUtils.getValueByPath(complexJson, "user.profile.age");
        assertEquals("25", age);

        String firstTag = JsonUtils.getValueByPath(complexJson, "tags.0");
        assertEquals("java", firstTag);

        String count = JsonUtils.getValueByPath(complexJson, "count");
        assertEquals("100", count);

        System.out.println("路径取值测试:");
        System.out.println("user.name: " + name);
        System.out.println("user.profile.age: " + age);
        System.out.println("tags.0: " + firstTag);
        System.out.println("count: " + count);
    }

    @Test
    @DisplayName("测试深拷贝")
    void testDeepCopy() {
        User copiedUser = JsonUtils.deepCopy(testUser, User.class);
        assertNotNull(copiedUser);
        assertEquals(testUser.getName(), copiedUser.getName());
        assertEquals(testUser.getAge(), copiedUser.getAge());
        assertNotSame(testUser, copiedUser); // 确保是不同的对象实例
        System.out.println("原对象: " + testUser);
        System.out.println("拷贝对象: " + copiedUser);
    }

    @Test
    @DisplayName("测试安全JSON转换")
    void testParseObjectSafely() {
        // 正常情况
        User user = JsonUtils.parseObjectSafely(userJson, User.class, new User("默认", 0));
        assertEquals("张三", user.getName());

        // 异常情况
        User defaultUser = JsonUtils.parseObjectSafely("{invalid json}", User.class, new User("默认", 0));
        assertEquals("默认", defaultUser.getName());
        assertEquals(0, defaultUser.getAge());

        System.out.println("安全转换正常: " + user);
        System.out.println("安全转换异常: " + defaultUser);
    }

    @Test
    @DisplayName("测试安全对象转JSON")
    void testToJsonStringSafely() {
        String json = JsonUtils.toJsonStringSafely(testUser, "{}");
        assertTrue(json.contains("张三"));

        // 模拟异常情况（这里用null对象测试）
        String defaultJson = JsonUtils.toJsonStringSafely(null, "{}");
        assertEquals("{}", defaultJson);

        System.out.println("安全转JSON正常: " + json);
        System.out.println("安全转JSON异常: " + defaultJson);
    }

    @Test
    @DisplayName("测试空值和边界情况")
    void testNullAndEdgeCases() {
        // 测试null值
        assertNull(JsonUtils.toJsonString(null));
        assertNull(JsonUtils.parseObject(null, User.class));
        assertNull(JsonUtils.parseList(null, User.class));
        assertNull(JsonUtils.parseMap(null));

        // 测试空字符串
        assertNull(JsonUtils.parseObject("", User.class));
        assertNull(JsonUtils.parseObject("   ", User.class));

        // 测试空集合
        List<String> emptyList = new ArrayList<>();
        String emptyListJson = JsonUtils.toJsonString(emptyList);
        assertEquals("[]", emptyListJson);

        Map<String, Object> emptyMap = new HashMap<>();
        String emptyMapJson = JsonUtils.toJsonString(emptyMap);
        assertEquals("{}", emptyMapJson);

        System.out.println("空值测试通过");
    }

    @Test
    @DisplayName("测试复杂嵌套对象")
    void testComplexNestedObject() {
        ComplexObject complexObj = new ComplexObject();
        complexObj.setId(1L);
        complexObj.setName("复杂对象");
        complexObj.setUsers(testUserList);
        complexObj.setMetadata(testMap);

        String json = JsonUtils.toJsonString(complexObj);
        assertNotNull(json);
        System.out.println("复杂对象JSON: " + json);

        ComplexObject parsed = JsonUtils.parseObject(json, ComplexObject.class);
        assertNotNull(parsed);
        assertEquals("复杂对象", parsed.getName());
        assertEquals(2, parsed.getUsers().size());
        assertNotNull(parsed.getMetadata());

        System.out.println("复杂对象解析: " + parsed);
    }

    // 测试用的内部类
    public static class User {
        private String name;
        private Integer age;
        private String email;
        private LocalDateTime createTime;

        public User() {}

        public User(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public User(String name, Integer age, String email, LocalDateTime createTime) {
            this.name = name;
            this.age = age;
            this.email = email;
            this.createTime = createTime;
        }

        // getter和setter方法
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

        @Override
        public String toString() {
            return "User{name='" + name + "', age=" + age + ", email='" + email + "'}";
        }
    }

    public static class ComplexObject {
        private Long id;
        private String name;
        private List<User> users;
        private Map<String, Object> metadata;

        // getter和setter方法
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<User> getUsers() { return users; }
        public void setUsers(List<User> users) { this.users = users; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

        @Override
        public String toString() {
            return "ComplexObject{id=" + id + ", name='" + name + "', users=" + users + ", metadata=" + metadata + "}";
        }
    }
}
