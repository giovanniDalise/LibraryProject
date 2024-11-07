package org.giovanniDalise;

import org.giovanniDalise.dao.IDao;
import org.giovanniDalise.entities.Book;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/library")
public class BooksRS {

    @Inject
    IDao<Book, Long> dao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooks(){
        try{
            List<Book> books = dao.read();
            return Response.ok(books).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).entity("Error in getBooks").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBook(Book book){
        try{
            return Response.ok(dao.create(book)).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).entity("Error in addEvent").build();
        }
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBook(@PathParam("id") long id){
        try{
            return Response.ok(dao.delete(id)).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).entity("Error in deleteEvent").build();
        }
    }
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBook(@PathParam("id") long id,Book book){
        try{
            return Response.ok(dao.update(id,book)).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).entity("Error in updateBook").build();
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookById(@PathParam("id") long id) {
        try {
            Book book = dao.getById(id);
            if (book == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Book not found").build();
            }
            return Response.ok(book).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in getBookById").build();
        }
    }

    @GET
    @Path("/findByString")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findBooksByString(@QueryParam("param") String param){
        try{
            return Response.ok(dao.findByText(param)).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).entity("Error in findBooksByString").build();
        }
    }

    @POST
    @Path("/findByBook")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findBooksByBook(Book book){
        try{
            return Response.ok(dao.findByObject(book)).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).entity("Error in findBooksByBook").build();
        }
    }
}
