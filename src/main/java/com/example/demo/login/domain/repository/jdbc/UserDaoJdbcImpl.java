package com.example.demo.login.domain.repository.jdbc;

import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository("UserDaoJdbcImpl")
public class UserDaoJdbcImpl implements UserDao {

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    PasswordEncoder passwordEncoder;

    //Userテーブルの件数を取得
    @Override
    public int count() throws DataAccessException {

        //オブジェクトの取得,全件取得してカウント
        return jdbc.queryForObject("SELECT COUNT(*) FROM m_user", Integer.class);
    }

    //Userテーブルにデータを1件追加
    @Override
    public int insertOne(User user) throws DataAccessException {

        //パスワードの暗号化
        String password = passwordEncoder.encode(user.getPassword());

        //１件登録
        return jdbc.update("INSERT INTO m_user(user_id,"
                        + " password,"
                        + " user_name,"
                        + " birthday,"
                        + " age,"
                        + " marriage,"
                        + " role)"
                        + " VALUES(?, ?, ?, ?, ?, ?, ?)",
                        user.getUserId(),
                        password,
                        user.getUserName(),
                        user.getBirthday(),
                        user.getAge(),
                        user.isMarriage(),
                        user.getRole());
    }

    //Userテーブルにデータを1件取得
    @Override
    public User selectOne(String userId) throws DataAccessException {

        //1件取得
        Map<String, Object> map = jdbc.queryForMap("SELECT * FROM m_user"
        + " WHERE user_id = ?"
        , userId);

        //結果返却用変数
        User user = new User();

        //取得したデータを結果返却用の変数にセットしていく
        user.setUserId((String) map.get("user_id"));
        user.setPassword((String) map.get("password"));
        user.setUserName((String) map.get("user_name"));
        user.setBirthday((Date) map.get("birthday"));
        user.setAge((Integer) map.get("age"));
        user.setMarriage((Boolean) map.get("marriage"));
        user.setRole((String) map.get("role"));

        return user;
    }

    //Userテーブルに全データを取得
    @Override
    public List<User> selectMany() throws DataAccessException {

        //複数件のセレクト、M_USERテーブルのデータを全件取得
        List<Map<String, Object>> getList = jdbc.queryForList("SELECT * FROM m_user");

        //結果返却用の変数
        List<User> userList = new ArrayList<>();

        //取得したデータを結果返却用のListに格納していく
        for(Map<String, Object> map: getList) {

            //Userインスタンスの生成
            User user = new User();

            //Userインスタンスに取得したデータをセットする
            user.setUserId((String)map.get("user_id"));
            user.setPassword((String)map.get("password"));
            user.setUserName((String)map.get("user_name"));
            user.setBirthday((Date)map.get("birthday"));
            user.setAge((Integer)map.get("age"));
            user.setMarriage((Boolean)map.get("marriage"));
            user.setRole((String)map.get("role"));

            //結果返却用のListに追加
            userList.add(user);
        }

        return userList;
    }

    //Userテーブルを1件更新
    @Override
    public int updateOne(User user) throws DataAccessException {

        //パスワードの暗号化
        String password = passwordEncoder.encode(user.getPassword());

        //1件更新
        return jdbc.update("UPDATE M_USER"
                        + " SET"
                        + " password = ?,"
                        + " user_name = ?,"
                        + " birthday = ?,"
                        + " age = ?,"
                        + " marriage = ?"
                        + " WHERE user_id = ?",
                        password,
                        user.getUserName(),
                        user.getBirthday(),
                        user.getAge(),
                        user.isMarriage(),
                        user.getUserId());
    }

    //Userテーブルを1件削除
    @Override
    public int deleteOne(String userId) throws  DataAccessException {

        //1件削除
        return jdbc.update("DELETE FROM m_user WHERE user_id = ?", userId);
    }

    //SQL取得結果をサーバーにCSVで保存する
    @Override
    public void userCsvOut() throws DataAccessException {

        //M_USERテーブルのデータを全件取得するSQL
        String sql = "SELECT * FROM m_user";

        //ResultSetExtractorの生成
        UserRowCallbackHandler handler = new UserRowCallbackHandler();

        //SQL実行＆CSV出力
        jdbc.query(sql, handler);
    }
}
