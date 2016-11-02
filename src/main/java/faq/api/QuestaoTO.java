package faq.api;

import faq.core.Questao;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public class QuestaoTO {

    public UUID id;

    @NotBlank
    @NotNull
    @Size(max = 1000)
    public String pergunta;

    @NotBlank
    @NotNull
    @Size(max = 10000)
    public String resposta;

    @Size(max = 500)
    public String autor;

    public LocalDate dataDePublicacao;

    public QuestaoTO() {
    }

    public QuestaoTO(Questao questao) {
        id = questao.getId();
        pergunta = questao.getPergunta();
        resposta = questao.getResposta();
        questao.getAutor().ifPresent(autor -> this.autor = autor);
        dataDePublicacao = questao.getDataDePublicacao();
    }

    public Questao atualizar(Questao questao) {
        questao.setPergunta(pergunta);
        questao.setResposta(resposta);
        questao.setAutor(autor);
        return questao;
    }
}
