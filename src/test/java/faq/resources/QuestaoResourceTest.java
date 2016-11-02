package faq.resources;

import faq.App;
import faq.AppConfig;
import faq.api.ItemComId;
import faq.api.QuestaoTO;
import faq.core.Categoria;
import faq.core.Questao;
import faq.db.Persistencia;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class QuestaoResourceTest {

    private static GenericType<List<Questao>> LISTA_DE_QUESTOES = new GenericType<List<Questao>>() {
    };

    private static GenericType<List<Categoria>> LISTA_DE_CATEGORIAS = new GenericType<List<Categoria>>() {
    };

    private PovoadorSimples povoador = new PovoadorSimples();

    private DropwizardAppRule<AppConfig> app = new DropwizardAppRule<>(App.class, resourceFilePath("test.yaml"));

    private Persistencia db = Persistencia.comPovoador(povoador);

    private QuestaoTO questao;

    private Client client;

    @Rule
    public RuleChain env = RuleChain.outerRule(db)
                                    .around(app);

    @Before
    public void setUp() throws Exception {
        client = new JerseyClientBuilder(app.getEnvironment()).build(getClass().getName());

        questao = new QuestaoTO();
        questao.pergunta = "Como excluir uma solicitação existente?";
        questao.resposta = "Para excluir uma solicitação basta...";
        questao.autor = "João da Silva Torres";
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void recuperaListaDeQuestões() throws Exception {

        Response response = target().request()
                                    .get();

        assertThat(response.getStatus()).isEqualTo(200);

        List<Questao> questoes = response.readEntity(LISTA_DE_QUESTOES);

        List<UUID> idsEsperados = povoador.todasQuestoes()
                                          .map(Questao::getId)
                                          .collect(Collectors.toList());

        assertThat(questoes).extracting(Questao::getId)
                            .containsExactlyElementsOf(idsEsperados);
    }

    @Test
    public void inserirRetornaStatusCREATED() throws Exception {

        Response response = target().request()
                                    .post(Entity.entity(questao, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_CREATED);
    }

    @Test
    public void inserirRetornaCabeçalhoLocation() throws Exception {

        Response response = target().request()
                                    .post(Entity.entity(questao, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getHeaderString("Location")).isNotEmpty();
    }

    @Test
    public void inserirRetornaQuestãoCriadaNoCorpoDaResposta() throws Exception {
        QuestaoTO response = target().request()
                                     .buildPost(Entity.entity(questao, MediaType.APPLICATION_JSON_TYPE))
                                     .invoke(QuestaoTO.class);

        assertThat(response).isEqualToComparingOnlyGivenFields(questao, "pergunta", "resposta", "autor")
                            .hasFieldOrProperty("id")
                            .hasFieldOrProperty("dataDePublicacao");
    }

    @Test
    public void inserirGravaQuestãoNoBancoDeDados() throws Exception {
        QuestaoTO response = target().request()
                                     .buildPost(Entity.entity(questao, MediaType.APPLICATION_JSON_TYPE))
                                     .invoke(QuestaoTO.class);

        Questao recuperadaDoBanco = db.get(Questao.class, response.id);

        assertThat(recuperadaDoBanco).isNotNull();
    }


    @Test
    public void inserirIgnoraDataDePublicaçãoSeForEnviadaNaRequisição() throws Exception {

        questao.dataDePublicacao = LocalDate.now().minusYears(10);

        QuestaoTO response = target().request()
                                     .buildPost(Entity.entity(questao, MediaType.APPLICATION_JSON_TYPE))
                                     .invoke(QuestaoTO.class);

        Questao recuperadaDoBanco = db.get(Questao.class, response.id);

        assertThat(recuperadaDoBanco.getDataDePublicacao()).isNotEqualTo(questao.dataDePublicacao);
    }

    @Test
    public void inserirIgnoraIDSeForEnviadoNaRequisição() throws Exception {

        questao.id = UUID.randomUUID();

        QuestaoTO response = target().request()
                                     .buildPost(Entity.entity(questao, MediaType.APPLICATION_JSON_TYPE))
                                     .invoke(QuestaoTO.class);

        Questao recuperadaDoBanco = db.get(Questao.class, response.id);

        assertThat(recuperadaDoBanco.getId()).isNotEqualByComparingTo(questao.id);
    }

    @Test
    public void alterarGravaAsAlteraçõesNoBancoDeDados() throws Exception {

        questao.pergunta = "Esta questão foi alterada?";
        questao.resposta = "Sim, ela foi alterada durante um teste";

        QuestaoTO response = target().path(povoador.questao1.getId().toString())
                                     .request()
                                     .buildPut(Entity.entity(questao, MediaType.APPLICATION_JSON_TYPE))
                                     .invoke(QuestaoTO.class);

        Questao recuperadaDoBanco = db.get(Questao.class, response.id);

        assertThat(recuperadaDoBanco).isEqualToComparingOnlyGivenFields(questao, "pergunta", "resposta");
    }

    @Test
    public void alterarIgnoraDataDePublicaçãoSeForEnviada() throws Exception {

        questao.dataDePublicacao = LocalDate.now().minusYears(20);

        QuestaoTO response = target().path(povoador.questao1.getId().toString())
                                     .request()
                                     .buildPut(Entity.entity(questao, MediaType.APPLICATION_JSON_TYPE))
                                     .invoke(QuestaoTO.class);

        Questao recuperadaDoBanco = db.get(Questao.class, response.id);

        assertThat(recuperadaDoBanco.getDataDePublicacao()).isNotEqualTo(questao.dataDePublicacao)
                                                           .isEqualTo(povoador.questao1.getDataDePublicacao());
    }

    @Test
    public void alterarRetornaOKApósAlteração() throws Exception {

        questao.pergunta = "Esta questão foi alterada?";
        questao.resposta = "Sim, ela foi alterada durante um teste";

        Response response = target().path(povoador.questao1.getId().toString())
                                    .request()
                                    .put(Entity.entity(questao, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    }

    @Test
    public void alterarRetornaUNPROCESSABLE_ENTITYSeAQuestãoFaltarAlgumPedaço() throws Exception {

        questao.pergunta = null;

        Response response = target().path(povoador.questao1.getId().toString())
                                    .request()
                                    .put(Entity.entity(questao, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    public void alterarRetornaNOT_FOUNDSeAQuestãoNãoExistir() throws Exception {

        Response response = target().path(UUID.randomUUID().toString())
                                    .request()
                                    .put(Entity.entity(questao, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void excluirRetornaNOT_FOUNDSeAQuestãoNãoExistir() throws Exception {

        Response response = target().path(UUID.randomUUID().toString())
                                    .request()
                                    .delete();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NOT_FOUND);
    }


    @Test
    public void excluirRetornaNO_CONTENTSQuandoApagaAQuestão() throws Exception {

        Response response = target().path(povoador.questao3.getId().toString())
                                    .request()
                                    .delete();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void recuperaCategoriasDeUmaQuestão() throws Exception {

        Response response = target().path(povoador.questao1.getId().toString())
                                    .path("categorias")
                                    .request()
                                    .get();

        assertThat(response.getStatus()).isEqualTo(200);

        List<Categoria> questoes = response.readEntity(LISTA_DE_CATEGORIAS);

        List<UUID> idsEsperados = povoador.questao1.getCategorias()
                                                   .stream()
                                                   .map(Categoria::getId)
                                                   .collect(Collectors.toList());

        assertThat(questoes).extracting(Categoria::getId)
                            .containsExactlyElementsOf(idsEsperados);
    }


    @Test
    public void vinculaCategoriaÀQuestão() throws Exception {

        ItemComId item = new ItemComId(povoador.categoriaImpressao.getId());

        Response response = target().path(povoador.questao1.getId().toString())
                                    .path("categorias")
                                    .request()
                                    .post(Entity.entity(item, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);

        Questao questao = db.get(Questao.class, povoador.questao1.getId());

        assertThat(questao.getCategorias()).hasSize(2)
                                           .extracting(Categoria::getId)
                                           .contains(povoador.categoriaImpressao.getId());
    }


    @Test
    public void desvinculaCategoriaDaQuestão() throws Exception {

        Categoria categoria = povoador.questao1.getCategorias().iterator().next();

        Response response = target().path(povoador.questao1.getId().toString())
                                    .path("categorias")
                                    .path(categoria.getId().toString())
                                    .request()
                                    .delete();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);

        Questao questao = db.get(Questao.class, povoador.questao1.getId());

        assertThat(questao.getCategorias()).extracting(Categoria::getId)
                                           .doesNotContain(categoria.getId());
    }

    @Test
    public void recuperaQuestõesRelacionadasDeUmaQuestão() throws Exception {

        Response response = target().path(povoador.questao1.getId().toString())
                                    .path("rel")
                                    .request()
                                    .get();

        assertThat(response.getStatus()).isEqualTo(200);

        List<Questao> questoes = response.readEntity(LISTA_DE_QUESTOES);

        List<UUID> idsEsperados = povoador.questao1.getQuestoesRelacionadas()
                                                   .stream()
                                                   .map(Questao::getId)
                                                   .collect(Collectors.toList());

        assertThat(questoes).extracting(Questao::getId)
                            .containsExactlyElementsOf(idsEsperados);
    }

    @Test
    public void vinculaQuestãoRelacionadaÀQuestão() throws Exception {

        ItemComId item = new ItemComId(povoador.questao2.getId());

        Response response = target().path(povoador.questao1.getId().toString())
                                    .path("rel")
                                    .request()
                                    .post(Entity.entity(item, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);

        db.refresh(povoador.questao1);

        assertThat(povoador.questao1.getQuestoesRelacionadas()).hasSize(2)
                                                               .extracting(Questao::getId)
                                                               .contains(povoador.questao2.getId());
    }

    @Test
    public void desvinculaQuestãoRelacionadaDaQuestão() throws Exception {

        Questao relacionada = povoador.questao1.getQuestoesRelacionadas().iterator().next();

        Response response = target().path(povoador.questao1.getId().toString())
                                    .path("rel")
                                    .path(relacionada.getId().toString())
                                    .request()
                                    .delete();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);

        db.refresh(povoador.questao1);

        assertThat(povoador.questao1.getQuestoesRelacionadas()).extracting(Questao::getId)
                                                               .doesNotContain(relacionada.getId());
    }


    private WebTarget target() {
        return client.target(format("http://localhost:%d/questoes", app.getLocalPort()));
    }
}
