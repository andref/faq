package faq;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestaoTest {

    private ObjetosDeTeste criar = new ObjetosDeTeste();

    @Test
    public void adicionarQuestãoRelacionadaAdicionaQuestãoÀLista() throws Exception {
        Questao questao1 = criar.questao();
        Questao questao2 = criar.questao();

        questao1.adicionarQuestaoRelacionada(questao2);

        assertThat(questao1.getQuestoesRelacionadas()).contains(questao2);
    }

    @Test
    public void adicionarQuestãoRelacionadaNãoCriaDuplicidade() throws Exception {
        Questao questao1 = criar.questao();
        Questao questao2 = criar.questao();

        questao1.adicionarQuestaoRelacionada(questao2);
        questao1.adicionarQuestaoRelacionada(questao2);

        assertThat(questao1.getQuestoesRelacionadas()).containsExactly(questao2);
    }

    @Test
    public void adicionarQuestãoRelacionadaNãoCriaRelacionamentoSimétrico() throws Exception {
        Questao questao1 = criar.questao();
        Questao questao2 = criar.questao();

        questao1.adicionarQuestaoRelacionada(questao2);

        assertThat(questao2.getQuestoesRelacionadas()).doesNotContain(questao1);
    }

    @Test
    public void adicionarQuestãoRelacionadaNãoCriaRelacionamentoReflexivo() throws Exception {
        Questao questao1 = criar.questao();

        questao1.adicionarQuestaoRelacionada(questao1);

        assertThat(questao1.getQuestoesRelacionadas()).isEmpty();
    }

    @Test
    public void removerQuestãoRelacionadaNãoDesfazRelacionamentoSimétrico() throws Exception {
        Questao questao1 = criar.questao();
        Questao questao2 = criar.questao();

        questao1.adicionarQuestaoRelacionada(questao2);
        questao2.adicionarQuestaoRelacionada(questao1);

        questao1.removerQuestaoRelacionada(questao2);

        assertThat(questao2.getQuestoesRelacionadas()).contains(questao1);
    }

    @Test
    public void adicionarCategoriaAdicionaCategoriaÀListaDeCategorias() throws Exception {
        Questao questao = criar.questao();
        Categoria categoria = criar.categoria();

        questao.adicionarCategoria(categoria);

        assertThat(questao.getCategorias()).contains(categoria);
    }

    @Test
    public void adicionarCategoriaAdicionaCategoriaAoFinalDaListaDeCategorias() throws Exception {
        Questao questao = criar.questao();
        Categoria categoria1 = criar.categoria();
        Categoria categoria2 = criar.categoria();

        questao.adicionarCategoria(categoria1);
        questao.adicionarCategoria(categoria2);

        assertThat(questao.getCategorias()).containsExactly(categoria1, categoria2);
    }

    @Test
    public void adicionarCategoriaCriaRelacionamentoSimétrico() throws Exception {
        Questao questao = criar.questao();
        Categoria categoria = criar.categoria();

        questao.adicionarCategoria(categoria);

        assertThat(categoria.getQuestoes()).contains(questao);
    }

    @Test
    public void removerCategoriaDesfazRelacionamentoSimétrico() throws Exception {
        Questao questao1 = criar.questao();
        Questao questao2 = criar.questao();
        Categoria categoria = criar.categoria();

        questao1.adicionarCategoria(categoria);
        questao2.adicionarCategoria(categoria);

        questao1.removerCategoria(categoria);

        assertThat(categoria.getQuestoes()).containsExactly(questao2);
    }

    @Test
    public void adicionarCategoriaNãoCriaDuplicidade() throws Exception {
        Questao questao = criar.questao();
        Categoria categoria = criar.categoria();

        questao.adicionarCategoria(categoria);
        questao.adicionarCategoria(categoria);

        assertThat(questao.getCategorias()).containsExactly(categoria);
        assertThat(categoria.getQuestoes()).containsExactly(questao);
    }
}
