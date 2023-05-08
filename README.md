### 思路：

1、首先通过网络爬虫获取到网页的数据

2、通过hutool工具进行数据格式化 转换到成想要的格式 使用了hutool工具中的HtmlUtil来去除页面格式

3、循环遍历数据拿到数据

4、通过JSONObject和JSONArray来进行构造相应的格式

5、不会进行文字分析 所有用了现有的数据作为根节点分类  



### 主要代码：

```java
@Override
    public String findPost() {
        String jsonStr = null;
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
            jsonStr = root.toStringPretty();
        }
        return jsonStr;
    }
```

