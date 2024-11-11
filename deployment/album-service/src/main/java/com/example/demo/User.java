package com.example.demo;


import java.util.Date;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import org.bson.types.ObjectId;

@MongoEntity(collection = "User")
public class User extends PanacheMongoEntity {

    public String userid;
    public String firstname;
    public String lastname;
    public String email;
    public String password;
    public Profile profile;
    public String folder;
    public UserSettings settings;
    public Date createdAt;
    public Date updatedAt;

    // Find a user by ObjectId
    public static User findByObjectId(ObjectId objectId) {
        return find("_id", objectId).firstResult();
    }

    // Find a user by userid
    public static User findByUserId(String userid) {
        return find("userid", userid).firstResult();
    }
    // Inner class for Profile
    public static class Profile {
        public String icon;
        public String bio;
    }
    
    // Inner class for UserSettings
    public static class UserSettings {
        public String language;
        public boolean privacy;
    }
}
