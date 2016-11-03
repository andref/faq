package faq.core;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
public class Categoria {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @NotBlank
    @NotNull
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String titulo;

    @Size(max = 1000)
    @Column(length = 1000)
    private String descricao;

    @ManyToMany(mappedBy = "categorias")
    List<Questao> questoes = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    void setId(UUID id) {
        this.id = id;
    }

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
