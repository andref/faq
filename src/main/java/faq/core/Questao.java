package faq.core;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Entity
public class Questao {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @NotBlank
    @NotNull
    @Size(max = 1000)
    @Column(nullable = false, unique = true, length = 1000)
    private String pergunta;

    @NotBlank
    @NotNull
    @Size(max = 10000)
    @Column(nullable = false, length = 10000)
    private String resposta;

    @ManyToMany
    private List<Questao> questoesRelacionadas = new ArrayList<>();

    @ManyToMany
    List<Categoria> categorias = new ArrayList<>();

    private String autor;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataDePublicacao;

    @Version
    @Column(nullable = false)
    private int versao;

    public UUID getId() {
        return id;
    }

    void setId(UUID id) {
        this.id = id;
    }

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
