/*
 * Copyright 2019 CYBERAX TECH PVT. LTD. . All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reddit.demo.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.reddit.demo.model.Post;

@Database(entities = {Post.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase sInstance;

    @VisibleForTesting
    private static final String DATABASE_NAME = "reddit_database";
    public abstract PostDao postDao();


    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static AppDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext(), executors);
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static AppDatabase buildDatabase(final Context appContext, final AppExecutors executors) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                // Add a delay to simulate a long-running operation
                                //addDelay();
                                // Generate the data for pre-population
                                AppDatabase database = AppDatabase.getInstance(appContext, executors);
//                            List<ProductEntity> products = DataGenerator.generateProducts();
//                            List<CommentEntity> comments =
//                                    DataGenerator.generateCommentsForProducts(products);
//
//                            insertData(database, products, comments);
//                             notify that the database was created and it's ready to be used
                                database.setDatabaseCreated();
                            }
                        });
                    }
                })
                //.addMigrations(MIGRATION_1_2)
                .build();
    }

    /**
     * Check whether the database already exists and expose it
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated(){
        mIsDatabaseCreated.postValue(true);
    }

}
