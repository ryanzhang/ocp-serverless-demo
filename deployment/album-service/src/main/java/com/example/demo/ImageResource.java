package com.example.demo;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.util.List;

import org.bson.types.ObjectId;

@Path("/images")
@Consumes("application/json")
@Produces("application/json")
public class ImageResource {
        @GET
    public List<Image> list() {
        return Image.listAll();
    }

    @GET
    @Path("/{id}")
    public List<Image> get(String id) {
        return Image.findByUserId(new ObjectId(id));
    }

    @POST
    public Response create(Image Image) {
        Image.persist();
        return Response.status(201).build();
    }

    @PUT
    @Path("/{id}")
    public void update(String id, Image Image) {
        Image.update();
    }

    // @DELETE
    // @Path("/{id}")
    // public void delete(String id) {
    //     Image Image = Image.findById(new ObjectId(id));
    //     Image.delete();
    // }

    // @GET
    // @Path("/search/{name}")
    // public Image search(String name) {
    //     return Image.findByName(name);
    // }

    @DELETE
    public void deleteAll(){
        Image.deleteAll();
    }



}
