# RoomBaslc
演示视频

<video src="https://yi-sheep.github.io/RoomBasic/Res/MP4/1.mp4"  autoplay loop muted>浏览器不支持播放该视频</video>

[无法播放点击](https://yi-sheep.github.io/RoomBasic/Res/MP4/1.mp4)

Room 是在 SQLite 的基础上提供了一个抽象层，让用户能够在充分利用 SQLite 的强大功能的同时，获享更强健的数据库访问机制。
[官方文档](https://developer.android.google.cn/jetpack/androidx/releases/room?hl=zh_cn)

使用前需要添加如下依赖(这里是我添加的，如果不行就去看看官方文档)
```gradle
def room_version = "2.2.3"

implementation "androidx.room:room-runtime:$room_version"
annotationProcessor "androidx.room:room-compiler:$room_version" // For Kotlinuse kapt instead of annotationProcessor

// Test helpers
testImplementation "androidx.room:room-testing:$room_version"
```
然后创建一个Entity数据库的实体类，在类前面使用@Entity注解就可以声明这个类就是数据库的实体类了。
我们在这个类里定义变量(列)，构造方法，get/set方法
```java
@Entity // 使用注解指定这是一个数据表的实体
public class Word {
    @PrimaryKey(autoGenerate = true) // 根据下面的成员变量自动生成数据库列
    @ColumnInfo(name = "_id") // 指定在生成列时的列名称
    private int id;
    @ColumnInfo(name = "english_word") // 指定在生成列时的列名称
    private String word;
    @ColumnInfo(name = "chinese_meaning") // 指定在生成列时的列名称
    private String chineseMeaning;

    /**
     * 构造方法，方便创建对象
     */
    public Word() {}

    /**
     * 构造方法，方便创建对象时再添加数据
     * @param word
     * @param chineseMeaning
     */
    public Word(String word, String chineseMeaning) {
        this.word = word;
        this.chineseMeaning = chineseMeaning;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getChineseMeaning() {
        return chineseMeaning;
    }

    public void setChineseMeaning(String chineseMeaning) {
        this.chineseMeaning = chineseMeaning;
    }
}
```
再来创建一个Dao接口(Database access object)数据库访问对象,使用@Dao声明这个接口为数据库访问对象。
在这个接口中可以使用很多注解来进行数据库的增删查改操作，如@Insert、@Update、@Delete...
```java
@Dao // Database access object,数据库访问对象
public interface WordDao {
    @Insert // 使用注解的方式声明这是一个插入语句，传入对象数据
    void insertWords(Word... words); // 这里的...表示可以接收多个对象,返回值也可以是int返回插入多少行

    @Update // 声明这是一个更新语句
    void updateWords(Word... words); // 返回值也可以是int返回更新多少行

    @Delete // 声明这是一个删除语句
    void deleteWords(Word... words); // 返回值也可以是int返回删除多少行

    @Query("SELECT * FROM WORD ORDER BY _ID DESC") // 使用@Query注解可以执行后面跟的SQL语句,使用降序排序
    List<Word> queryAllWord(); // 查询所有的数据

    @Query("DELETE FROM WORD")
    void deleteAllWord(); // 删除整个表
}
```
还有创建一个Database类，使用注解@Database()这里需要传入参数，具体传入什么看看下面代码,这个类继承于RoomDatabase.
```java
// entities这个指定实体，version这个指定版本，exportSchema指定导出模式
@Database(entities = {Word.class},version = 1,exportSchema = false)
public abstract class WordDatabase extends RoomDatabase {
    /**
     * 抽象方法，获取Dao对象
     * @return
     */
    public abstract WordDao getWordDao();
}
```

然后就是具体的使用了，xml布局就自己去上面找代码把，自己随便写一个也不难。
```java
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
```

---

感谢B站大佬longway777的[视频教程](https://www.bilibili.com/video/BV1ct411K7tp/?spm_id_from=333.788.videocard.0)

如果侵权，请联系qq:1766816333
立即删除

---