package faq.core;

import java.time.LocalDate;

public final class ObjetosDeTeste {

    public Questao questao() {
        Questao q = new Questao();
        q.setPergunta("Qual a cor do patinho amarelinho?");
        q.setResposta("Amarelinho.");
        q.setDataDePublicacao(LocalDate.now());
        q.setAutor("João Sales");
        return q;
    }

    public Categoria categoria() {
        Categoria c = new Categoria();
        c.setTitulo("Patinhos");
        c.setDescricao("Patinhos são pequenas aves que nadam e voam.");
        return c;
    }
}
