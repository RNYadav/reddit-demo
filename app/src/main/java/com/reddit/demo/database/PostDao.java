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

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.reddit.demo.model.Post;

import java.util.List;

@Dao
public interface PostDao {
    @Query("SELECT * FROM fav_Post")
    List<Post> getAll();

    @Query("SELECT * FROM fav_Post WHERE postId IN (:postId)")
    List<Post> isFav(int postId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPost(Post post);

    @Delete
    void deletePost(Post post);

}
