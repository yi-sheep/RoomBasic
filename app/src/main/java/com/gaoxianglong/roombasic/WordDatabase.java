package com.gaoxianglong.roombasic;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// entities这个指定实体，version这个指定版本，exportSchema指定导出模式
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

    /**
     * 抽象方法，获取Dao对象
     * @return
     */
    public abstract WordDao getWordDao();
}
