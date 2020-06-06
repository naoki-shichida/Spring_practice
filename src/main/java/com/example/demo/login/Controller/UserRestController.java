package com.example.demo.login.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.service.RestService;


@RestController
public class UserRestController {

    @Autowired
    @Qualifier("RestServiceMybatisImpl")
    RestService service;

    //ユーザー全件取得
    @GetMapping("/rest/get")
    public List<User> getUserMany() {

        //ユーザー全件取得
        return service.selectMany();
    }

    //ユーザー1件取得
    @GetMapping("/rest/get/{id:.+}")
    public User getUserOne(@PathVariable("id")String userId) {

        //ユーザー1件取得
        return service.selectOne(userId);
    }

    //ポイント：@RequestBody
    @PostMapping("/rest/insert")
    public String postUserOne(@RequestBody User user) {

        //ユーザーを1件登録
        boolean result = service.insert(user);

        String str = "";

        if(result) {
            str = "{\"result\":\"ok\"}";
        } else {
            str = "{\"result\":\"error\"}";
        }

        //結果用の文字列をリターン
        return str;
    }

    @PutMapping("/rest/update")
    public String putUserOne(@RequestBody User user) {

        //ユーザーを1件更新
        boolean result = service.update(user);

        String str = "";

        if(result) {
            str = "{\"result\":\"ok\"}";
        } else {
            str = "{\"result\":\"error\"}";
        }

        //結果用の文字列をリターン
        return str;
    }

    @DeleteMapping("/rest/delete/{id:.+}")
    public String deleteUserOne(@PathVariable("id") String userId) {

        // ユーザーを１件削除
        boolean result = service.delete(userId);

        String str = "";

        if(result) {
            str = "{\"result\":\"ok\"}";
        } else {
            str = "{\"result\":\"error\"}";
        }

        // 結果用の文字列をリターン
        return str;
    }
}
