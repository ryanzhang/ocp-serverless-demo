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

@Path("/users")
@Consumes("application/json")
@Produces("application/json")
public class UserResource {
    @GET
    public List<User> list() {
        return User.listAll();
    }

    @GET
    @Path("/{id}")
    public User get(String id) {
        return User.findByObjectId(new ObjectId(id));
    }

    @POST
    public Response create(User User) {
        User.persist();
        return Response.status(201).build();
    }

    @PUT
    @Path("/{id}")
    public void update(String id, User User) {
        User.update();
    }

    // @DELETE
    // @Path("/{id}")
    // public void delete(String id) {
    //     User User = User.findById(new ObjectId(id));
    //     User.delete();
    // }

    @GET
    @Path("/search/{userid}")
    public User search(String userid) {
        return User.findByUserId(userid);
    }

    @DELETE
    public void deleteAll(){
        User.deleteAll();
    }



}
