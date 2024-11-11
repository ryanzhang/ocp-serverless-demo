package com.example.demo;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;

import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "Image")
public class Image extends PanacheMongoEntity {

    public ObjectId userObjectId;  // Reference to User
    public String fileKey;  // S3 file key for the image
    public String thumbKey;  // S3 file key for the thumbnail
    public String url;
    public String thumbUrl;
    public List<String> labels;  // List of labels (tags)
    public List<ObjectId> albums;  // References to Album entities
    @JsonIgnore
    public Date uploadedAt;
    @JsonIgnore
    public Date updatedAt;

    // Find images by userId
    public static List<Image> findByUserId(ObjectId userId) {
        return list("userObjectId", userId);
    }

    // Find images by albumId
    public static List<Image> findByAlbumId(ObjectId albumId) {
        return list("albums", albumId);
    }

    public static Image findByFileKey(String fileKey) {
        return find("fileKey", fileKey).firstResult();
    }    
}
