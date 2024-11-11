package com.example.demo;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;


import io.quarkus.mongodb.panache.common.MongoEntity;


@MongoEntity(collection = "Album")
public class Album extends PanacheMongoEntity {

    public ObjectId userId;  // Reference to User
    public String name;
    public String description;
    public Date createdAt;
    public Date updatedAt;

    // Find albums by userId
    public static List<Album> findByUserId(ObjectId userId) {
        return list("userId", userId);
    }

    // Find albums by userId
    public static List<Album> findByName(String name, ObjectId userId) {
        return list("name", name, "userId", userId);
    }
}
