package com.chen.test;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class CrawlerTest {
    @Test
    void testFetchPassage() {
        // 1、获取数据
        String json = "{\"current\":1,\"pageSize\":10,\"sortField\":\"createTime\",\"sortOrder\":\"descend\",\"userId\":\"1608455295015583745\"}";
        String url = "https://www.code-nav.cn/api/post/list/page/vo";
        String result = HttpRequest
                .post(url)
                .body(json)
                .execute()
                .body();
        // 2、json 转对象
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) map.get("data");
        JSONArray records = (JSONArray) data.get("records");
        for (Object record : records) {
            JSONObject tempRecord = (JSONObject) record;
            //3、获取对象中的数据
            String title = tempRecord.getStr("title");
            String content = tempRecord.getStr("content");
            String category = tempRecord.getStr("category");
            JSONArray tags = (JSONArray) tempRecord.get("tags");
            //4、转换tags格式
            List<String> tagList = tags.toList(String.class);
            String contentText = extractText(content);
            //5、创建根节点
            JSONObject root = new JSONObject();
            //6、创建一级标签 将tag标签放入到一级标签中
            JSONArray oneLevel = new JSONArray();
            oneLevel.add(tagList.toString());
            //7、创建二级标签，并将内容放入到二级标签中
            JSONArray twoLevel = new JSONArray();
            twoLevel.add(contentText);
            //8、创建Node节点  加入数据  key为title  value为twoLevel(二级节点)
            JSONObject Node = new JSONObject();
            // todo 没有title的话会拿不到 二级节点，所以title为空的时候给他一个 ""(待优化)
            Node.set(title == null ? " " : title, twoLevel);
            //9、一级标签放入Node节点
            oneLevel.add(Node);
            //10、根节点放入数据 key为category(类型)，value为oneLevel(一级标签)
            //todo 通过文字分析得到文章类型或者标签(待优化)
            root.set(category, oneLevel);
            //11、root根节点转换成String并且输出
            String jsonStr = root.toStringPretty();
            System.out.println(jsonStr);
        }
    }

    public static String extractText(String html) {
        // 剔除HTML标签和样式
        String plainText = HtmlUtil.cleanHtmlTag(html);
        // 去除多余空白字符
        plainText = StrUtil.trim(plainText);
//
        return plainText;
    }


    @Test
    void test1() {
        JSONObject root = new JSONObject();

        // 创建 "Java" 标签
        JSONArray javaTags = new JSONArray();
        javaTags.add("Java 文章");

        // 创建 "Dubbo" 标签
        JSONArray dubboTags = new JSONArray();
        dubboTags.add("Dubbo 文章");

        // 将 "Dubbo" 标签作为子级添加到 "Java" 标签
        JSONObject dubboNode = new JSONObject();
        dubboNode.set("Dubbo", dubboTags);

        // 将子级标签添加到 "Java" 标签
        javaTags.add(dubboNode);

        // 将 "Java" 标签添加到根节点
        root.set("Java", javaTags);

        // 将 JSON 对象转换为字符串并打印输出
        String json = root.toStringPretty();
        System.out.println(json);

    }
}