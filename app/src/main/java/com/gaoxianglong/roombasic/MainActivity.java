package com.gaoxianglong.roombasic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    WordDao mWordDao;
    WordDatabase mWordDatabase;
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 生成数据库
        mWordDatabase = Room.databaseBuilder(this,WordDatabase.class,"Word_database")
                .allowMainThreadQueries() // 加上这句可以强制在UI线程中更新数据
                .build();
        mWordDao = mWordDatabase.getWordDao();
        mTextView = findViewById(R.id.textView);
        updateView(); // 更新UI数据
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
    }

    /**
     * 更新数据
     */
    void updateView(){
        List<Word> list = mWordDao.queryAllWord(); // 查询数据
        String text = "";
        for (int i = 0; i < list.size(); i++) {
            Word word = list.get(i);
            text += String.format("%s:%s=%s\n", word.getId(), word.getWord(), word.getChineseMeaning());
        }
        mTextView.setText(text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                Word word1 = new Word("Hello!","你好！"); // 创建两个对象传入不同的数据
                Word word2 = new Word("Word.","世界。");
                mWordDao.insertWords(word1,word2); // 将两个对象传入Dao进行插入数据
                updateView(); // 更新数据
                break;
            case R.id.button2:
                Word word3 = new Word("Hi!","你好！"); // 创建一个对象，传入要更新成的数据
                word3.setId(1); // 设置id，根据id修改数据，要修改那一个数据就设置那一个id
                mWordDao.updateWords(word3);
                updateView(); // 更新数据
                break;
            case R.id.button3:
                mWordDao.deleteAllWord(); // 这里要注意，数据删除后，id并不是从1从新开始的
                updateView(); // 更新数据
                break;
            case R.id.button4:
                Word word4 = new Word(); // 这创建一个空对象就可以了
                word4.setId(2); // 根据id删除
                mWordDao.deleteWords(word4);
                updateView();
                break;
        }
    }
}
