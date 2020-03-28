package com.gaoxianglong.roombasic;

import androidx.room.Database;
import androidx.room.RoomDatabase;

// entities这个指定实体，version这个指定版本，exportSchema指定导出模式
@Database(entities = {Word.class},version = 1,exportSchema = false)
public abstract class WordDatabase extends RoomDatabase {
    public abstract WordDao getWordDao();
}
