package faq.db;

import faq.core.ObjetosDeTeste;
import faq.core.Questao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ThreadLocalSessionContext;
import org.junit.After;
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

    private ObjetosDeTeste criar = new ObjetosDeTeste();
    private SessionFactory factory;
    private Session session;
    private Questoes questoes;

    @Before
    public void setUp() throws Exception {
        factory = CriarSessionFactory.paraH2EmMemoria("teste")
                                     .comEntidadesNoPacote("faq.core")
                                     .construirBanco()
                                     .mostrarSQL()
                                     .getSessionFactory();

        questoes = new Questoes(factory);

        session = factory.openSession();
        ThreadLocalSessionContext.bind(session);
        session.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
        session.getTransaction().commit();
        session.close();
        factory.close();
        ThreadLocalSessionContext.unbind(factory);
    }

    @Test
    public void todasRecuperaDeFatoTodasAsQuestões() throws Exception {

        Stream.of("Pergunta 1?", "Pergunta 2?", "Pergunta 3?")
              .map(criar::questaoComPergunta)
              .forEach(questoes::persistir);

        session.flush();
        session.clear();

        List<Questao> todas = questoes.listar();

        assertThat(todas).hasSize(3);
    }

    @Test
    public void persistirGravaQuestãoNoBanco() throws Exception {
        Questao questao = criar.questao();
        questoes.persistir(questao);

        session.flush();
        session.clear();

        assertThat(questao.getId()).isNotNull();

        Questao questaoRecuperada = session.get(Questao.class, questao.getId());

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
    public void persistirPermiteQuestãoSemAutor() throws Exception {
        Questao questao = criar.questao();
        questao.setAutor(null);
        questoes.persistir(questao);

        session.flush();
        session.clear();
    }

    @Test
    public void porIdRecuperaQuestaoCorretamente() throws Exception {
        Questao questao = criar.questao();
        questoes.persistir(questao);

        session.flush();
        session.clear();

        Optional<Questao> optQuestao = questoes.porId(questao.getId());

        assertThat(optQuestao).hasValueSatisfying(questaoRecuperada -> {
            assertThat(questaoRecuperada.getId()).isEqualByComparingTo(questao.getId());
        });
    }

    @Test
    public void excluirRemoveQuestãoDoBancoDeDados() throws Exception {
        Questao questao = criar.questao();
        questoes.persistir(questao);

        session.flush();
        session.clear();

        Questao recuperada = questoes.porId(questao.getId()).orElseThrow(AssertionError::new);

        questoes.excluir(recuperada);

        session.flush();
        session.clear();

        Optional<Questao> optQuestao = questoes.porId(questao.getId());

        assertThat(optQuestao).isEmpty();
    }
}
