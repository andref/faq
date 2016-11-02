package faq.api;

import faq.core.Categoria;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

public class CategoriaTO {

    public UUID id;

    @NotBlank
    @NotNull
    @Size(max = 100)
    public String titulo;

    @Size(max = 1000)
    public String descricao;

    public CategoriaTO() {
    }

    public CategoriaTO(Categoria categoria) {
        id = categoria.getId();
        titulo = categoria.getTitulo();
        categoria.getDescricao().ifPresent(descricao -> this.descricao = descricao);
    }

    public Categoria atualizar(Categoria categoria) {
        categoria.setTitulo(titulo);
        categoria.setDescricao(descricao);
        return categoria;
    }
}
