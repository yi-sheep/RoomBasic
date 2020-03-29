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

上面的已经实现了整个功能，但是有很多地方不足，比如我们强制了在主线程中更新UI数据、还有多处调用了updateView()方法这样会很累赘、多处进退应用会重复创建Database的实例，这样很消耗资源、也没有做到对应的类只完成自己该做的事.
现在来优化一下，先解决多处调用updateView()吧，我们利用LiveData就可以做到监听数据发生变化时。
修改Dao中的查询语句代码，将返回值改成liveData
```java
@Query("SELECT * FROM WORD ORDER BY _ID DESC")
LiveData<List<Word>> queryAllWordLive(); // 本身Room就是支持LiveData的
```
然后在MainActivity里添加如下代码，(要合理观看代码，搞清楚每一个变量表示的是什么)
```java
LiveData<List<Word>> queryAllWordLive; // 成员变量,保存数据库中的数据
queryAllWordLive = mWordDao.queryAllWordLive(); // 获取数据库中的数据，mWordDao是已经定义过的，你要是照着上面一步一步的做肯定就有，它是Dao的实例
queryAllWordLive.observe(this,words -> {
// 这里是当LiveData的数据发生改变的时候就会回调的函数onChanged(List<Word> words)
// 在这里进行更新UI的操作就不用在多处调用updateView()进行更新UI了
StringBuilder text = new StringBuilder();
for (int i = 0; i < words.size(); i++) {
    Word word = words.get(i);
    text.append(String.format("%s:%s=%s\n", word.getId(), word.getWord(), word.getChineseMeaning()));
}
mTextView.setText(text.toString());
});
```
解决重复创建Database实例
```java
@Database(entities = {Word.class},version = 1,exportSchema = false)
public abstract class WordDatabase extends RoomDatabase {
    // singleton 单例模式,让无论是什么情况下,创建的实例都是同一个,这样就能解决创建多个实例消耗资源的情况
    private static WordDatabase INSTANCE;
    // synchronized让不同的线程中访问需要排队,消除碰撞
    static synchronized WordDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),WordDatabase.class,"Word_database")
                    .build();
        }
        return INSTANCE;
    }
    ...
}
```
这样就可以代替updateView()方法了，还不用到处调用，LiveData监听到数据发生变化就会更新UI数据。
现在来一起出来剩下的不足，首先需要创建一个仓库，用于获取数据，Activity是用于操作控件的，ViewModel是用于操作数据的，获取数据的重要任务就交个仓库了。
在仓库里面就可以解决主线程的问题，使用AsyncTask.
```java
/**
 * 这个是仓库类，用于获取数据
 */
public class WordRepository {
    private WordDao mWordDao;
    private LiveData<List<Word>> queryAllWordLive;
    public WordRepository(Context context) {
        WordDatabase wordDatabase = WordDatabase.getDatabase(context.getApplicationContext());
        mWordDao = wordDatabase.getWordDao();
        queryAllWordLive = mWordDao.queryAllWordLive();
    }

    public LiveData<List<Word>> getQueryAllWordLive() {
        return queryAllWordLive;
    }

    public void insertWord(Word... words) {
        new InsertAsyncTask(mWordDao).execute(words);
    }
    public void updateWord(Word... words) {
        new UpdateAsyncTask(mWordDao).execute(words);
    }
    public void deleteWord(Word... words) {
        new DeleteAsyncTask(mWordDao).execute(words);
    }
    public void deleteAllWord() {
        new DeleteAllAsyncTask(mWordDao).execute();
    }

    /**
     * 使用AsyncTask完成更新UI
     * 前面的操作都是强制在主线程(UI线程)中更新的数据
     * 在Android中这个操作是很危险的
     * 插入
     */
    static class InsertAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao mWordDao;

        public InsertAsyncTask(WordDao wordDao) {
            mWordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            // 这个回调方法中写更新数据的逻辑
            mWordDao.insertWords(words);
            return null;
        }
    }

    /**
     * 更新
     */
    static class UpdateAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao mWordDao;

        public UpdateAsyncTask(WordDao wordDao) {
            mWordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            mWordDao.updateWords(words);
            return null;
        }
    }

    /**
     * 删除
     */
    static class DeleteAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao mWordDao;

        public DeleteAsyncTask(WordDao wordDao) {
            mWordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            mWordDao.deleteWords(words);
            return null;
        }
    }

    /**
     * 清空
     * 因为这个操作不需要数据库实体对象，所以三个参数都可以是Void
     */
    static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private WordDao mWordDao;

        public DeleteAllAsyncTask(WordDao wordDao) {
            mWordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mWordDao.deleteAllWord();
            return null;
        }
    }
}
```
有了数据就是操作数据了，创建一个ViewModel
```java
public class WordViewModel extends AndroidViewModel {
    private WordDao mWordDao;
    private WordRepository mWordRepository;

    public WordViewModel(@NonNull Application application) {
        super(application);
        mWordRepository = new WordRepository(application);
    }

    public LiveData<List<Word>> getQueryAllWordLive() {
        return mWordRepository.getQueryAllWordLive();
    }

    public void insertWord(Word... words) {
        mWordRepository.insertWord(words);
    }
    public void updateWord(Word... words) {
        mWordRepository.updateWord(words);
    }
    public void deleteWord(Word... words) {
        mWordRepository.deleteWord(words);
    }
    public void deleteAllWord() {
        mWordRepository.deleteAllWord();
    }
}
```
最后是Activity的使用控件触发ViewModel中的数据操作逻辑了
```java
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ...
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ...
        mViewModel = ViewModelProviders.of(this).get(WordViewModel.class);
        mTextView = findViewById(R.id.textView);
        mViewModel.getQueryAllWordLive().observe(this,words -> {
            // 这里是当LiveData的数据发生改变的时候就会回调的函数onChanged(List<Word> words)
            // 在这里进行更新UI的操作就不用在多处调用updateView()进行更新UI了
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < words.size(); i++) {
                Word word = words.get(i);
                text.append(String.format("%s:%s=%s\n", word.getId(), word.getWord(), word.getChineseMeaning()));
            }
            mTextView.setText(text.toString());
        });
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                Word word1 = new Word("Hello!","你好！");
                Word word2 = new Word("Word.","世界。");
                mViewModel.insertWord(word1,word2);
                break;
            case R.id.button2:
                Word word3 = new Word("Hi!","你好！");
                word3.setId(1);
                mViewModel.updateWord(word3);
                break;
            case R.id.button3:
                mViewModel.deleteAllWord();
                break;
            case R.id.button4:
                Word word4 = new Word();
                word4.setId(2);
                mViewModel.deleteWord(word4);
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