package faq.resources;

import faq.core.Categoria;
import faq.core.ObjetosDeTeste;
import faq.core.Questao;
import org.hibernate.Session;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class PovoadorSimples implements Consumer<Session> {

    public ObjetosDeTeste criar = new ObjetosDeTeste();

    public Questao questao1;
    public Questao questao2;
    public Categoria categoriaImpressao;
    public Categoria categoriaSolicitacao;

    @Override
    public void accept(Session session) {

        questao1 = criar.questaoComPergunta("Como criar uma nova solicitação?");
        questao2 = criar.questaoComPergunta("Qual imprimir a ficha de solicitação?");
        categoriaImpressao = criar.categoriaComTitulo("Impressão");
        categoriaSolicitacao = criar.categoriaComTitulo("Solicitação");

        questao1.adicionarCategoria(categoriaSolicitacao);
        questao2.adicionarCategoria(categoriaSolicitacao);
        questao2.adicionarCategoria(categoriaImpressao);

        Stream.of(questao1, questao2, categoriaImpressao, categoriaSolicitacao)
              .forEach(session::persist);
    }
}
