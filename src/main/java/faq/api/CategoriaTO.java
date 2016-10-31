package faq.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import faq.core.Categoria;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;
import java.util.UUID;

public class CategoriaTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public UUID id;

    @NotBlank
    @NotNull
    @Size(max = 100)
    public String titulo;

    @Size(max = 1000)
    @UnwrapValidatedValue
    public Optional<String> descricao = Optional.empty();

    CategoriaTO() {
    }

    public CategoriaTO(Categoria categoria) {
        id = categoria.getId();
        titulo = categoria.getTitulo();
        descricao = categoria.getDescricao();
    }

    public Categoria atualizar(Categoria categoria) {
        categoria.setTitulo(titulo);
        descricao.ifPresent(categoria::setDescricao);
        return categoria;
    }
}
