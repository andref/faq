package faq.core;

import java.util.*;

public class Categoria {

    private String titulo;
    private String descricao;
    List<Questao> questoes = new ArrayList<>();

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Optional<String> getDescricao() {
        return Optional.ofNullable(descricao);
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Collection<Questao> getQuestoes() {
        return Collections.unmodifiableCollection(questoes);
    }

    public void adicionarQuestao(Questao pergunta) {
        if (questoes.contains(pergunta)) {
            return;
        }
        questoes.add(pergunta);
        pergunta.categorias.add(this);
    }
}
