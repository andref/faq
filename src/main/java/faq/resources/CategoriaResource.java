package faq.resources;

import com.codahale.metrics.annotation.Timed;
import faq.api.CategoriaTO;
import faq.api.QuestaoTO;
import faq.core.Categoria;
import faq.db.Categorias;
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
import java.util.stream.Collectors;

@Path("categorias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriaResource {

    private final Categorias categorias;

    public CategoriaResource(Categorias categorias) {
        this.categorias = categorias;
    }

    @GET
    @Timed
    @UnitOfWork(readOnly = true)
    public List<CategoriaTO> recuperarTodos() {
        return categorias.listar()
                         .stream()
                         .map(CategoriaTO::new)
                         .collect(Collectors.toList());
    }

    @GET
    @Timed
    @Path("{id}")
    @UnitOfWork(readOnly = true)
    public Optional<CategoriaTO> recuperarPorId(@PathParam("id") UUID id) {
        return categorias.porId(id)
                         .map(CategoriaTO::new);
    }

    @POST
    @UnitOfWork
    public Response inserir(@Valid CategoriaTO categoriaTO) {
        Categoria categoria = categoriaTO.atualizar(new Categoria());

        categorias.persistir(categoria);

        URI uri = UriBuilder.fromResource(CategoriaResource.class)
                            .path(CategoriaResource.class, "recuperarPorId")
                            .build(categoria.getId());

        return Response.created(uri)
                       .entity(new CategoriaTO(categoria))
                       .build();
    }

    @DELETE
    @UnitOfWork
    @Path("{id}")
    public void excluir(@PathParam("id") UUID id) {
        Categoria categoria = categorias.porId(id)
                                        .orElseThrow(NotFoundException::new);
        categorias.excluir(categoria);
    }

    @PUT
    @UnitOfWork
    @Path("{id}")
    public Response alterar(@PathParam("id") UUID id, @Valid CategoriaTO categoriaTO) {
        Categoria categoria = categorias.porId(id)
                                        .orElseThrow(NotFoundException::new);
        categoriaTO.atualizar(categoria);

        URI uri = UriBuilder.fromResource(CategoriaResource.class)
                            .path(CategoriaResource.class, "recuperarPorId")
                            .build(categoria.getId());

        return Response.seeOther(uri)
                       .entity(new CategoriaTO(categoria))
                       .build();
    }

    @GET
    @Timed
    @UnitOfWork(readOnly = true)
    @Path("{id}/questoes")
    public Collection<QuestaoTO> recuperarQuestoes(@PathParam("id") UUID id) {
        Categoria categoria = categorias.porId(id)
                                        .orElseThrow(NotFoundException::new);
        return categoria.getQuestoes()
                        .stream()
                        .map(QuestaoTO::new)
                        .collect(Collectors.toList());
    }
}
