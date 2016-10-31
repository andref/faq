package faq.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import faq.core.Questao;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class QuestaoTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public UUID id;

    @NotBlank
    @NotNull
    @Size(max = 1000)
    @JsonProperty
    public String pergunta;

    @NotBlank
    @NotNull
    @Size(max = 10000)
    @JsonProperty
    public String resposta;

    @Size(max = 500)
    @UnwrapValidatedValue
    public Optional<String> autor = Optional.empty();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public LocalDate dataDePublicacao;

    QuestaoTO() {
    }

    public QuestaoTO(Questao questao) {
        id = questao.getId();
        pergunta = questao.getPergunta();
        resposta = questao.getResposta();
        autor = questao.getAutor();
        dataDePublicacao = questao.getDataDePublicacao();
    }

    public Questao atualizar(Questao questao) {
        questao.setPergunta(pergunta);
        questao.setResposta(resposta);
        autor.ifPresent(questao::setAutor);
        return questao;
    }
}
