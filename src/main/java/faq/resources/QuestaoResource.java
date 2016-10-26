package faq.resources;

import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("questoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QuestaoResource {

    @GET
    @Timed
    public Response recuperarTodas() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Timed
    @Path("{id}")
    public Response recuperarPorId(@PathParam("id") UUID id) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @POST
    public Response inserir() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("{id}")
    public Response excluir(@PathParam("id") UUID id) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @PUT
    @Path("{id}")
    public Response alterar(@PathParam("id") UUID id) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Timed
    @Path("{id}/categorias")
    public Response recuperarCategorias(@PathParam("id") UUID id) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @POST
    @Path("{id}/categorias")
    public Response vincularCategoria(@PathParam("id") UUID id) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("{id}/categorias/{idCategoria}")
    public Response desvincularCategoria(@PathParam("id") UUID id, @PathParam("idCategoria") UUID idCategoria) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Timed
    @Path("{id}/rel")
    public Response recuperarRelacionadas(@PathParam("id") UUID id) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @POST
    @Path("{id}/rel")
    public Response vincularRelacionada(@PathParam("id") UUID id) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("{id}/rel/{idRelacionada}")
    public Response desvincularRelacionada(@PathParam("id") UUID id, @PathParam("idRelacionada") UUID idCategoria) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
