package faq.api;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public class ItemComId {

    @NotNull
    public UUID id;

    public String descricao;

    ItemComId() {
    }

    public ItemComId(UUID id) {
        this(id, null);
    }

    public ItemComId(UUID id, String descricao) {
        this.id = Objects.requireNonNull(id);
        this.descricao = descricao;
    }
}
