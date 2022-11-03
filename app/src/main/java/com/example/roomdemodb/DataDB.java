package com.example.roomdemodb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities={Data.class}, version = 5, exportSchema = false)
public abstract class DataDB  extends RoomDatabase {

        public static  final String DB_NAME = "data_db";

        private static DataDB  INSTANCE = null;

        public static DataDB getInstance(Context context)
        {
            if (INSTANCE==null)
                synchronized (DataDB.class)
                {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            DataDB.class,
                            DB_NAME
                    )
//                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            return  INSTANCE;
        }

        public abstract DataDAO dataDAO();

}
