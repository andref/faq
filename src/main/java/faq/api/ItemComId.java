package faq.api;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

public class ItemComId {

    @NotNull
    public UUID id;

    public Optional<String> descricao;

    ItemComId() {
    }

    public ItemComId(UUID id) {
        this.id = id;
        this.descricao = Optional.empty();
    }

    public ItemComId(UUID id, String descricao) {
        this.id = id;
        this.descricao = Optional.ofNullable(descricao);
    }
}
