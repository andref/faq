package faq.resources;

import com.codahale.metrics.annotation.Timed;
import faq.core.Categoria;
import faq.core.Questao;
import faq.db.Questoes;
import io.dropwizard.hibernate.UnitOfWork;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("questoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QuestaoResource {

    private final Questoes questoes;

    public QuestaoResource(Questoes questoes) {
        this.questoes = questoes;
    }

    @GET
    @Timed
    @UnitOfWork(readOnly = true)
    public List<Questao> recuperarTodas() {
        return questoes.todas();
    }

    @GET
    @Timed
    @Path("{id}")
    @UnitOfWork(readOnly = true)
    public Optional<Questao> recuperarPorId(@PathParam("id") UUID id) {
        return questoes.porId(id);
    }

    @POST
    @UnitOfWork
    public Response inserir(@Valid Questao questao) {
        questoes.persistir(questao);

        URI uri = UriBuilder.fromResource(QuestaoResource.class)
                            .path(QuestaoResource.class, "recuperarPorId")
                            .build(questao.getId());

        return Response.created(uri)
                       .entity(questao)
                       .build();
    }

    @DELETE
    @UnitOfWork
    @Path("{id}")
    public void excluir(@PathParam("id") UUID id) {
        Questao questao = questoes.porId(id)
                                  .orElseThrow(NotFoundException::new);
        questoes.excluir(questao);
    }

    @PUT
    @UnitOfWork
    @Path("{id}")
    public Response alterar(@PathParam("id") UUID id, @Valid Questao questao) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Timed
    @UnitOfWork(readOnly = true)
    @Path("{id}/categorias")
    public Collection<Categoria> recuperarCategorias(@PathParam("id") UUID id) {
        Questao questao = questoes.porId(id)
                                  .orElseThrow(NotFoundException::new);
        return questao.getCategorias();
    }

    @POST
    @UnitOfWork
    @Path("{id}/categorias")
    public Response vincularCategoria(@PathParam("id") UUID id) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @UnitOfWork
    @Path("{id}/categorias/{idCategoria}")
    public void desvincularCategoria(@PathParam("id") UUID id, @PathParam("idCategoria") UUID idCategoria) {
        Questao questao = questoes.porId(id)
                                  .orElseThrow(NotFoundException::new);

        Categoria categoria = questao.getCategorias()
                                     .stream()
                                     .filter(t -> t.getId() == idCategoria)
                                     .findFirst()
                                     .orElseThrow(NotFoundException::new);

        questao.removerCategoria(categoria);
    }

    @GET
    @Timed
    @UnitOfWork(readOnly = true)
    @Path("{id}/rel")
    public Collection<Questao> recuperarRelacionadas(@PathParam("id") UUID id) {
        Questao questao = questoes.porId(id)
                                  .orElseThrow(NotFoundException::new);
        return questao.getQuestoesRelacionadas();
    }

    @POST
    @UnitOfWork
    @Path("{id}/rel")
    public Response vincularRelacionada(@PathParam("id") UUID id) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @UnitOfWork
    @Path("{id}/rel/{idRelacionada}")
    public void desvincularRelacionada(@PathParam("id") UUID id, @PathParam("idRelacionada") UUID idRelacionada) {
        Questao questao = questoes.porId(id)
                                  .orElseThrow(NotFoundException::new);

        Questao relacionada = questao.getQuestoesRelacionadas()
                                     .stream()
                                     .filter(t -> t.getId() == idRelacionada)
                                     .findFirst()
                                     .orElseThrow(NotFoundException::new);

        questao.removerQuestaoRelacionada(relacionada);
    }
}
