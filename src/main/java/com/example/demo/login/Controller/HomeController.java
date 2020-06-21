package com.example.demo.login.Controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.login.domain.model.SignupForm;
import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.service.UserService;



@Controller
public class HomeController {

    @Autowired
    UserService userService;

    private Map<String, String> radioMarriage;

    private Map<String, String> initRadioMarrige() {

        Map<String, String> radio = new LinkedHashMap<>();

        radio.put("既婚","true");
        radio.put("未婚","false");

        return radio;
    }

    /**
     * ホーム画面のGETメソッド
     *
     * @param model 画面から受け取る情報
     * @return 取得結果
     */
    @GetMapping("/home")
    public String getHome(Model model) {

        //コンテンツ部分にホーム画面を表示するための文字列を登録
        model.addAttribute("contents","login/home :: home_contents");

        return "login/homeLayout";
    }

    /**
     * ユーザー一覧画面のGETメソッド
     *
     * @param model 画面から受け取る情報,登録ユーザーの情報,登録件数情報
     * @return 取得結果
     */
    @GetMapping("/userList")
    public String getUserList(Model model) {

        //コンテンツ部分にユーザー一覧を表示するための文字列を登録
        model.addAttribute("contents", "login/userList :: userList_contents");

        //ユーザー一覧の生成
        List<User> userList = userService.selectMany();

        //Modelにユーザーリストを登録
        model.addAttribute("userList", userList);

        //データ件数を取得
        int count = userService.count();
        model.addAttribute("userListCount", count);

        return "login/homeLayout";
    }

    /**
     *ユーザー詳細画面のGETメソッド
     *
     * @param form 画面からの入力情報
     * @param model 画面から受け取る情報,ラジオボタンの初期化情報,画面からの入力情報
     * @param userId GETリクエストがきたユーザーID情報
     * @return 取得結果
     */
    @GetMapping("/userDetail/{id:.+}")
    public String getUserDetail(@ModelAttribute SignupForm form,
                                Model model,
                                @PathVariable("id") String userId) {
        //ユーザーID確認（デバッグ）
        System.out.println("userId = " + userId);

        //コンテンツ部分にユーザー詳細を表示するための文字列を登録
        model.addAttribute("contents", "login/userDetail :: userDetail_contents");

        //結婚ステータス用ラジオボタンの初期化
        radioMarriage = initRadioMarrige();

        //ラジオボタン用のMapをModelに登録
        model.addAttribute("radioMarriage", radioMarriage);

        //ユーザーIDのチェック
        if(!StringUtils.isEmpty(userId)) {
            //ユーザー情報を取得
            User user = userService.selectOne(userId);

            //Userクラスをフォームクラスに変換
            form.setUserId(user.getUserId());
            form.setUserName(user.getUserName());
            form.setBirthday(user.getBirthday());
            form.setAge(user.getAge());
            form.setMarriage(user.isMarriage());

            //Modelに登録
            model.addAttribute("signupForm", form);
        }

        return "login/homeLayout";
    }

    /**
     * ユーザー更新用メソッド
     * @param form 画面からの入力情報
     * @param model 更新結果の情報
     * @return ユーザー一覧画面のGETメソッド(model)
     */
    @PostMapping(value = "/userDetail", params = "update")
    public String postUserDetailUpdate(@ModelAttribute SignupForm form,
                                       Model model) {

        System.out.println("更新ボタンの処理");

        //Userインスタンスの生成
        User user = new User();

        //フォームクラスをUserクラスに変換
        user.setUserId(form.getUserId());
        user.setPassword(form.getPassword());
        user.setUserName(form.getUserName());
        user.setBirthday(form.getBirthday());
        user.setAge(form.getAge());
        user.setMarriage(form.isMarriage());

        try {

            //更新実行
            boolean result = userService.updateOne(user);

            if (result) {
                model.addAttribute("result", "更新成功");
            } else {
                model.addAttribute("result", "更新失敗");
            }

        } catch (DataAccessException e) {

            model.addAttribute("result","更新失敗 (トランザクションテスト)");
        }
        //ユーザー一覧画面を表示
        return getUserList(model);
    }

    /**
     * ユーザー削除用処理
     *
     * @param form 画面からの入力情報
     * @param model 削除結果の情報
     * @return ユーザー一覧画面のGETメソッド(model)
     */
    @PostMapping(value = "/userDetail", params = "delete")
    public String postUserDetailDelete(@ModelAttribute SignupForm form,
                                       Model model) {

        System.out.println("削除ボタンの処理");

        //削除実行
        boolean result = userService.deleteOne(form.getUserId());

        if(result) {
            model.addAttribute("result","削除成功");
        } else {
            model.addAttribute("result","削除失敗");
        }
        //ユーザー一覧画面表示
        return getUserList(model);
    }

    /**
     * ログアウト用メソッド
     *
     * @return 取得結果
     */
    @PostMapping("/logout")
    public String postLogout() {
        //ログイン画面にリダイレクト
        return "redirect:/login";
    }

    /**
     * ユーザー一覧のCSV出力メソッド
     *
     * @param model 画面から受け取る情報
     * @return 取得結果
     */
    @GetMapping("/userList/csv")
    public ResponseEntity<byte[]> getUserListCsv(Model model) {

        //ユーザーを全件取得して、CSVをサーバーに保存する
        userService.userCsvOut();

        byte[] bytes = null;

        try {

            //サーバーに保存されているuserList.csvファイルをbyteで取得する
            bytes = userService.getFile("userList.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }

        //HTTPヘッダーの設定
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", "text/csv; charset=UTF-8");
        header.setContentDispositionFormData("filename", "userList.csv");

        //userList.csvを戻す
        return new ResponseEntity<>(bytes, header, HttpStatus.OK);
    }


    /**
     * アドミン権限専用画面のGET用メソッド
     *
     * @param model 画面から受け取る情報
     * @return 取得結果
     */
    @GetMapping("/admin")
    public String getAdmin(Model model) {

        //コンテンツ部分にユーザー詳細を表示するための文字列を登録
        model.addAttribute("contents", "login/admin :: admin_contents");

        //レイアウト用テンプレート
        return "login/homeLayout";
    }
}
