package faq.core;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoriaTest {

    private ObjetosDeTeste criar = new ObjetosDeTeste();

    @Test
    public void adicionarQuestãoAdicionaQuestãoÀListaDeQuestões() throws Exception {
        Categoria categoria = criar.categoria();
        Questao questao = criar.questao();

        categoria.adicionarQuestao(questao);

        assertThat(categoria.getQuestoes()).contains(questao);
    }

    @Test
    public void adicionarQuestãoCriaRelacionamentoSimétrico() throws Exception {
        Categoria categoria = criar.categoria();
        Questao questao = criar.questao();

        categoria.adicionarQuestao(questao);

        assertThat(questao.getCategorias()).contains(categoria);
    }

    @Test
    public void adicionarQuestãoNãoCriaDuplicidade() throws Exception {
        Categoria categoria = criar.categoria();
        Questao questao = criar.questao();

        categoria.adicionarQuestao(questao);
        categoria.adicionarQuestao(questao);

        assertThat(categoria.getQuestoes()).containsExactly(questao);
        assertThat(questao.getCategorias()).containsExactly(categoria);
    }

    @Test
    public void removerQuestãoDesfazRelacionamentoSimétrico() throws Exception {
        Categoria categoria = criar.categoria();
        Questao questao = criar.questao();

        categoria.adicionarQuestao(questao);
        categoria.adicionarQuestao(questao);

        assertThat(categoria.getQuestoes()).containsExactly(questao);
        assertThat(questao.getCategorias()).containsExactly(categoria);
    }
}
