package faq.resources;

import com.codahale.metrics.annotation.Timed;
import faq.api.CategoriaTO;
import faq.api.ItemComId;
import faq.api.QuestaoTO;
import faq.core.Categoria;
import faq.core.Questao;
import faq.db.Categorias;
import faq.db.Questoes;
import io.dropwizard.hibernate.UnitOfWork;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("questoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QuestaoResource {

    private final Questoes questoes;
    private final Categorias categorias;

    public QuestaoResource(Questoes questoes, Categorias categorias) {
        this.questoes = questoes;
        this.categorias = categorias;
    }

    @GET
    @Timed
    @UnitOfWork(readOnly = true)
    public List<QuestaoTO> recuperarTodas() {
        return questoes.listar()
                       .stream()
                       .map(QuestaoTO::new)
                       .collect(Collectors.toList());
    }

    @GET
    @Timed
    @Path("{id}")
    @UnitOfWork(readOnly = true)
    public Optional<QuestaoTO> recuperarPorId(@PathParam("id") UUID id) {
        return questoes.porId(id)
                       .map(QuestaoTO::new);
    }

    @POST
    @UnitOfWork
    public Response inserir(@Valid @NotNull QuestaoTO questaoTO) {
        Questao questao = questaoTO.atualizar(new Questao());
        questao.setDataDePublicacao(LocalDate.now());

        questoes.persistir(questao);

        return Response.created(uriPara(questao))
                       .entity(new QuestaoTO(questao))
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
    public Response alterar(@PathParam("id") UUID id, @Valid @NotNull QuestaoTO questaoTO) {
        Questao questao = questoes.porId(id)
                                  .orElseThrow(NotFoundException::new);
        questaoTO.atualizar(questao);

        return Response.ok(new QuestaoTO(questao))
                       .build();
    }

    @GET
    @Timed
    @UnitOfWork(readOnly = true)
    @Path("{id}/categorias")
    public Collection<CategoriaTO> recuperarCategorias(@PathParam("id") UUID id) {
        Questao questao = questoes.porId(id)
                                  .orElseThrow(NotFoundException::new);
        return questao.getCategorias()
                      .stream()
                      .map(CategoriaTO::new)
                      .collect(Collectors.toList());
    }

    @POST
    @UnitOfWork
    @Path("{id}/categorias")
    public void vincularCategoria(@PathParam("id") UUID id, @Valid @NotNull ItemComId item) {
        Questao questao = questoes.porId(id)
                                  .orElseThrow(NotFoundException::new);
        Categoria categoria = categorias.porId(item.id)
                                        .orElseThrow(NotFoundException::new);

        questao.adicionarCategoria(categoria);
    }

    @DELETE
    @UnitOfWork
    @Path("{id}/categorias/{idCategoria}")
    public void desvincularCategoria(@PathParam("id") UUID id, @PathParam("idCategoria") UUID idCategoria) {
        Questao questao = questoes.porId(id)
                                  .orElseThrow(NotFoundException::new);

        Categoria categoria = questao.getCategorias()
                                     .stream()
                                     .filter(t -> t.getId().equals(idCategoria))
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
    public void vincularRelacionada(@PathParam("id") UUID id, @Valid @NotNull ItemComId item) {
        Questao questao = questoes.porId(id)
                                  .orElseThrow(NotFoundException::new);
        Questao relacionada = questoes.porId(item.id)
                                      .orElseThrow(NotFoundException::new);
        questao.adicionarQuestaoRelacionada(relacionada);
    }

    @DELETE
    @UnitOfWork
    @Path("{id}/rel/{idRelacionada}")
    public void desvincularRelacionada(@PathParam("id") UUID id, @PathParam("idRelacionada") UUID idRelacionada) {
        Questao questao = questoes.porId(id)
                                  .orElseThrow(NotFoundException::new);

        Questao relacionada = questao.getQuestoesRelacionadas()
                                     .stream()
                                     .filter(t -> t.getId().equals(idRelacionada))
                                     .findFirst()
                                     .orElseThrow(NotFoundException::new);

        questao.removerQuestaoRelacionada(relacionada);
    }

    private URI uriPara(Questao questao) {
        return UriBuilder.fromResource(QuestaoResource.class)
                         .path(QuestaoResource.class, "recuperarPorId")
                         .build(questao.getId());
    }
}
