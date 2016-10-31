package faq.db;

import faq.core.ObjetosDeTeste;
import faq.core.Questao;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestoesTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public Persistencia db = Persistencia.padrao();

    private ObjetosDeTeste criar = new ObjetosDeTeste();
    private Questoes questoes;

    @Before
    public void setUp() throws Exception {
        questoes = new Questoes(db.getSessionFactory());
    }

    @Test
    public void listarRecuperaTodasAsQuestões() throws Exception {

        Stream.of("Pergunta 1?", "Pergunta 2?", "Pergunta 3?")
              .map(criar::questaoComPergunta)
              .forEach(questoes::persistir);

        db.flushAndClear();

        List<Questao> todas = questoes.listar();

        assertThat(todas).hasSize(3);
    }

    @Test
    public void persistirGravaQuestãoNoBanco() throws Exception {
        Questao questao = criar.questao();
        questoes.persistir(questao);

        db.flushAndClear();

        assertThat(questao.getId()).isNotNull();

        Questao questaoRecuperada = db.get(Questao.class, questao.getId());

        assertThat(questaoRecuperada.getId()).isEqualByComparingTo(questao.getId());
    }

    @Test
    public void persistirNãoPermiteQuestãoSemPergunta() throws Exception {
        Questao questao = criar.questao();
        questao.setPergunta(null);
        questoes.persistir(questao);

        thrown.expect(ConstraintViolationException.class);
    }

    @Test
    public void persistirNãoPermiteQuestãoSemResposta() throws Exception {
        Questao questao = criar.questao();
        questao.setResposta(null);
        questoes.persistir(questao);

        thrown.expect(ConstraintViolationException.class);
    }

    @Test
    public void persistirNãoPermiteQuestãoComPerguntaEmBranco() throws Exception {
        Questao questao = criar.questao();
        questao.setPergunta("    ");
        questoes.persistir(questao);

        thrown.expect(ConstraintViolationException.class);
    }

    @Test
    public void persistirNãoPermiteQuestãoComRespostaEmBranco() throws Exception {
        Questao questao = criar.questao();
        questao.setResposta("    ");
        questoes.persistir(questao);

        thrown.expect(ConstraintViolationException.class);
    }

    @Test
    public void persistirNãoPermiteQuestãoComPerguntaDuplicada() throws Exception {
        Questao primeira = criar.questao();
        questoes.persistir(primeira);

        db.flushAndClear();

        Questao segunda = criar.questao();
        questoes.persistir(segunda);

        thrown.expect(org.hibernate.exception.ConstraintViolationException.class);
    }

    @Test
    public void persistirPermiteQuestãoSemAutor() throws Exception {
        Questao questao = criar.questao();
        questao.setAutor(null);
        questoes.persistir(questao);
    }

    @Test
    public void porIdRecuperaQuestaoCorretamente() throws Exception {
        Questao questao = criar.questao();
        questoes.persistir(questao);

        db.flushAndClear();

        Optional<Questao> optQuestao = questoes.porId(questao.getId());

        assertThat(optQuestao).hasValueSatisfying(questaoRecuperada -> {
            assertThat(questaoRecuperada.getId()).isEqualByComparingTo(questao.getId());
        });
    }

    @Test
    public void excluirRemoveQuestãoDoBancoDeDados() throws Exception {
        Questao questao = criar.questao();
        questoes.persistir(questao);

        db.flushAndClear();

        Questao recuperada = questoes.porId(questao.getId()).orElseThrow(AssertionError::new);

        questoes.excluir(recuperada);

        db.flushAndClear();

        Optional<Questao> optQuestao = questoes.porId(questao.getId());

        assertThat(optQuestao).isEmpty();
    }
}
