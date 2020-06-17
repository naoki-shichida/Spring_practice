package com.example.demo.login.domain.service;

import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.repository.UserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Transactional
@Service
public class UserService {

    @Autowired
    @Qualifier("UserDaoJdbcImpl")
    UserDao dao;

    //insert用メソッド
    public boolean insert(User user) {

        //insert実行
        int rowNumber = dao.insertOne(user);

        //判定用変数
        boolean result = false;

        //insert成功
        return (rowNumber > 0);
    }

    //カウント用メソッド
    public int count() {
        return dao.count();
    }

    //全件取得メソッド
    public List<User> selectMany() {
        //全件取得
        return dao.selectMany();
    }

    //1件取得メソッド
    public User selectOne(String userId) {
        //selectOne実行
        return dao.selectOne(userId);
    }

    //1件更新メソッド
    public boolean updateOne(User user) {

        //判定用変数
        boolean result= false;

        //1件更新
        int rowNumber = dao.updateOne(user);

        //update成功
        return (rowNumber > 0);
    }

    //1件削除メソッド
    public boolean deleteOne(String userId) {

        //1件削除
        int rowNumber = dao.deleteOne(userId);

        //判定用変数
        boolean result = false;

        //delete成功
        return (rowNumber > 0);
    }

    // ユーザー一覧をCSV出力する.
    /**
     * @throws DataAccessException
     */
    public void userCsvOut() throws DataAccessException {
        // CSV出力
        dao.userCsvOut();
    }

    //サーバーに保存されているファイルを取得して、byte配列に変換する
    public byte[] getFile(String fileName) throws IOException {

        //ファイルシステム　（デフォルト）の取得
        FileSystem fs = FileSystems.getDefault();

        //ファイル取得
        Path p = fs.getPath(fileName);

        //ファイルをbyte配列に変換
        byte[] bytes = Files.readAllBytes(p);

        return bytes;
    }
}
