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

@Path("/albums")
@Consumes("application/json")
@Produces("application/json")
public class AlbumResource {
        @GET
    public List<Album> list() {
        return Album.listAll();
    }

    @GET
    @Path("/{id}")
    public Album get(String id) {
        return Album.findById(new ObjectId(id));
    }

    @POST
    public Response create(Album Album) {
        Album.persist();
        return Response.status(201).build();
    }

    @PUT
    @Path("/{id}")
    public void update(String id, Album Album) {
        Album.update();
    }

    // @DELETE
    // @Path("/{id}")
    // public void delete(String id) {
    //     Album Album = Album.findByUserId(new ObjectId(id));
    //     Album.delete();
    // }

    // @GET
    // @Path("/search/{name}")
    // public Album search(String name) {
    //     return Album.findByName(name);
    // }

    @DELETE
    public void deleteAll(){
        Album.deleteAll();
    }


}
