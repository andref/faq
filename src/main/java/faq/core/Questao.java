package faq.core;

import java.time.LocalDate;
import java.util.*;

public class Questao {

    private String pergunta;
    private String resposta;
    private List<Questao> questoesRelacionadas = new ArrayList<>();
    List<Categoria> categorias = new ArrayList<>();
    private String autor;
    private LocalDate dataDePublicacao;

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public Collection<Questao> getQuestoesRelacionadas() {
        return Collections.unmodifiableCollection(questoesRelacionadas);
    }

    public void adicionarQuestaoRelacionada(Questao pergunta) {
        if (pergunta == this || questoesRelacionadas.contains(pergunta)) {
            return;
        }
        this.questoesRelacionadas.add(pergunta);
    }

    public void removerQuestaoRelacionada(Questao pergunta) {
        questoesRelacionadas.remove(pergunta);
    }

    public Collection<Categoria> getCategorias() {
        return Collections.unmodifiableCollection(categorias);
    }

    public void adicionarCategoria(Categoria categoria) {
        if (categorias.contains(categoria)) {
            return;
        }
        categorias.add(categoria);
        categoria.questoes.add(this);
    }

    public void removerCategoria(Categoria categoria) {
        if (categorias.remove(categoria)) {
            categoria.questoes.remove(this);
        }
    }

    public Optional<String> getAutor() {
        return Optional.ofNullable(autor);
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public LocalDate getDataDePublicacao() {
        return dataDePublicacao;
    }

    public void setDataDePublicacao(LocalDate dataDePublicacao) {
        this.dataDePublicacao = dataDePublicacao;
    }
}
