package com.example.demo;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.bson.types.ObjectId;

import com.example.demo.User.Profile;
import com.example.demo.User.UserSettings;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class MongoDBDataInitializer {
    private static final Logger logger = Logger.getLogger(MongoDBDataInitializer.class);
 

    @ConfigProperty(name = "initial_data_onstart")
    boolean initializeData;

    void onStart(@Observes StartupEvent event) {
        if (initializeData){

            // Create a user with default settings
            User user = new User();
            user.userid = "rzhang";
            user.firstname = "Ryan";
            user.lastname = "Zhang";
            user.email = "rzhang@example.com";
            user.password = "hashed_password"; // Replace with secure password hashing

            Profile profile = new Profile();
            profile.icon = "https://example.com/icon.jpg";
            profile.bio = "Path Finder";
            user.profile = profile;

            UserSettings settings = new UserSettings();
            settings.language="en";
            settings.privacy=false;
            user.settings = settings;

            user.folder = "john-doe-folder";
            user.createdAt = new Date();
            user.updatedAt = new Date();

            User queryResult=User.findByUserId(user.userid);
            if (queryResult==null)
                user.persist(); // Persist the user directly
                queryResult=User.findByUserId(user.userid);

            // Sample image data
            List<Object[]> imageData = Arrays.asList(
                    new Object[]{
                            "upload/DSC_9176.JPG",
                            "https://ryan-workshop.s3.ap-southeast-1.amazonaws.com/upload/DSC_9176.JPG",
                            "https://ryan-workshop.s3.ap-southeast-1.amazonaws.com/thumb/DSC_9176_thumb.JPG",
                            Arrays.asList("Nature", "Outdoors", "Desert", "Scenery", "Sand"),
                            queryResult
                    },
                    new Object[]{
                            "upload/DSC_9177.JPG",
                            "https://ryan-workshop.s3.ap-southeast-1.amazonaws.com/upload/DSC_9177.JPG",
                            "https://ryan-workshop.s3.ap-southeast-1.amazonaws.com/thumb/DSC_9177_thumb.JPG",
                            Arrays.asList("Nature", "Wildlife", "Animals", "Desert", "Adventure"),
                            queryResult
                    },
                    new Object[]{
                            "upload/DSC_9178.JPG",
                            "https://ryan-workshop.s3.ap-southeast-1.amazonaws.com/upload/DSC_9178.JPG",
                            "https://ryan-workshop.s3.ap-southeast-1.amazonaws.com/thumb/DSC_9178_thumb.JPG",
                            Arrays.asList("Nature", "Cliff", "Desert", "Scenery", "Sand"),
                            queryResult
                    },
                    new Object[]{
                            "upload/DSC_9184.JPG",
                            "https://ryan-workshop.s3.ap-southeast-1.amazonaws.com/upload/DSC_9184.JPG",
                            "https://ryan-workshop.s3.ap-southeast-1.amazonaws.com/thumb/DSC_9184_thumb.JPG",
                            Arrays.asList("Nature", "Canyon", "Desert", "Scenery", "Sand"),
                            queryResult
                    },
                    new Object[]{
                            "upload/DSC_9185.JPG",
                            "https://ryan-workshop.s3.ap-southeast-1.amazonaws.com/upload/DSC_9185.JPG",
                            "https://ryan-workshop.s3.ap-southeast-1.amazonaws.com/thumb/DSC_9185_thumb.JPG",
                            Arrays.asList("Nature", "Canyon", "Scenery"),
                            queryResult
                    }
            );
            // Create and persist Images
            createImages(imageData);
        }else{
           logger.info("ignore initialization data since initial_data_onstart is not set to true"); 
        }

    }

    private List<Image> createImages(List<Object[]> imageData) {
        return imageData.stream()
                .map(this::createImage)
                .collect(Collectors.toList());
    }

    private Image createImage(Object[] data) {
        String fileName = (String) data[0];
        String url = (String) data[1];
        String thumbUrl = (String) data[2];
        List<String> labels = (List<String>) data[3];
        User owner = (User) data[4];

        Image existingImage = Image.findByFileKey(fileName);
        if (existingImage != null) {
            // Image already exists, return it
            return existingImage;
        }

        Image image = new Image();
        image.userObjectId = owner.id;
        image.fileKey= fileName;
        image.thumbKey = fileName;
        image.url = url;
        image.thumbUrl = thumbUrl;
        image.labels = labels;
        image.updatedAt = new Date();
        image.uploadedAt = new Date();

        // Map labels to existing albums or create new albums
        List<ObjectId> albumIds = labels.stream()
                .map(label -> findOrCreateAlbum(label, owner.id))
                .collect(Collectors.toList());

        image.albums = albumIds;
        image.persist();

        return image;
    }
    private ObjectId findOrCreateAlbum(String label, ObjectId userId) {
        // Check if an album with the label already exists for the user
        Album existingAlbum = null;
        try{
            existingAlbum = Album.findByName(label, userId).getFirst();
        } catch (Exception e){
            e.printStackTrace();
        }
    
        if (existingAlbum != null) {
            // Use the existing album ID
            return existingAlbum.id;
        } else {
            // Create a new album with the label
            Album newAlbum = new Album();
            newAlbum.userId = userId;
            newAlbum.name = label;
            newAlbum.createdAt = new Date();
            newAlbum.updatedAt = new Date();
            newAlbum.persist();
    
            return newAlbum.id;
        }
    }
}
