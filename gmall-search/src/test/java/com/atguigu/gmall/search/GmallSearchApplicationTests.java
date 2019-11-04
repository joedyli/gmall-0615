package com.atguigu.gmall.search;

import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class GmallSearchApplicationTests {

    @Autowired
    private JestClient jestClient;

    @Test
    void contextLoads() throws IOException {

        Delete build = new Delete.Builder("1").index("user").type("info").build();
        jestClient.execute(build);

//        Index index = new Index.Builder(new User("liuyan", "123456", 18)).index("user").type("info").id("1").build();
//
//        jestClient.execute(index);

//        Map<String, Object> map = new HashMap<>();
//        map.put("doc", new User("bingbing", null, null));
//
//        Update update = new Update.Builder(map).index("user").type("info").id("1").build();
//        DocumentResult result = jestClient.execute(update);
//        System.out.println(result.toString());

//        Get get = new Get.Builder("user", "1").build();
//
//        System.out.println(jestClient.execute(get));

//        String query = "{\n" +
//                "  \"query\": {\n" +
//                "    \"match_all\": {}\n" +
//                "  }\n" +
//                "}";
//        Search search = new Search.Builder(query).addIndex("user").addType("info").build();
//        SearchResult searchResult = jestClient.execute(search);
//        System.out.println(searchResult.getSourceAsObject(User.class, false));
//        List<SearchResult.Hit<User, Void>> hits = searchResult.getHits(User.class);
//        hits.forEach(hit -> {
//            System.out.println(hit.source);
//        });

    }



}

@Data
@AllArgsConstructor
@NoArgsConstructor
class User {
    private String name;
    private String password;
    private Integer age;
}
