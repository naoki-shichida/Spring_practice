package com.example.demo.login.Controller;

import com.example.demo.login.domain.model.GroupOrder;
import com.example.demo.login.domain.model.SignupForm;
import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class SignupController {

    @Autowired
    private UserService userService;

    //ラジオボタン実装
    private Map<String, String> radioMarriage;

    private Map<String, String> initRadioMarrige() {
        Map<String, String>radio = new LinkedHashMap<>();

        //既婚、未婚をMapに格納
        radio.put("既婚", "true");
        radio.put("未婚", "false");

        return radio;
    }

    //ユーザー登録画面のGET用コントローラー
    @GetMapping("/signup")
    public String getSignUp(@ModelAttribute SignupForm form, Model model) {
        radioMarriage = initRadioMarrige();

        model.addAttribute("radioMarriage", radioMarriage);

        return "login/signup";
    }

    //ユーザー登録画面のPOST用コントローラー
    @PostMapping("/signup")
    public String postSignUp(@ModelAttribute @Validated(GroupOrder.class) SignupForm form,
                             BindingResult bindingResult,
                             Model model) {

        //ポイント3：データバインド失敗の場合
        //入力チェックに引っかかった場合、ユーザー登録画面に戻る
        if(bindingResult.hasErrors()) {

            return getSignUp(form, model);
        }

        //formの中身をコンソールに出して確認すします
        System.out.println(form);

        //insert用変数
        User user = new User();

        user.setUserId(form.getUserId());
        user.setPassword(form.getPassword());
        user.setUserName(form.getUserName());
        user.setBirthday(form.getBirthday());
        user.setAge(form.getAge());
        user.setMarriage(form.isMarriage());
        user.setRole("ROLE_GENERAL");

        //ユーザの登録処理
        boolean result = userService.insert(user);

        //ユーザーの登録結果の判定
        //三項演算子
        String str = result ? "insert成功" : "insert失敗";
        System.out.println(str);

        //ポイント：リダイレクト
        return "redirect:/login";
    }

    /**
     * DataAccessException発生時の処理メソッド.
     */
    @ExceptionHandler(DataAccessException.class)
    public String dataAccessExceptionHandler(DataAccessException e, Model model) {

        // 例外クラスのメッセージをModelに登録
        model.addAttribute("error", "内部サーバーエラー（DB）：ExceptionHandler");

        // 例外クラスのメッセージをModelに登録
        model.addAttribute("message", "SignupControllerでDataAccessExceptionが発生しました");

        // HTTPのエラーコード（500）をModelに登録
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR);

        return "error";
    }

    /**
     * Exception発生時の処理メソッド.
     */
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e, Model model) {

        // 例外クラスのメッセージをModelに登録
        model.addAttribute("error", "内部サーバーエラー：ExceptionHandler");

        // 例外クラスのメッセージをModelに登録
        model.addAttribute("message", "SignupControllerでExceptionが発生しました");

        // HTTPのエラーコード（500）をModelに登録
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR);

        return "error";
    }
}
