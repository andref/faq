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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
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

    @Rule
    public RuleChain env = RuleChain.outerRule(db)
                                    .around(app);

    @Before
    public void setUp() throws Exception {
        questao = new QuestaoTO();
        questao.pergunta = "Como excluir uma solicitação existente?";
        questao.resposta = "Para excluir uma solicitação basta...";
        questao.autor = "João da Silva Torres";
    }

    @Test
    public void recuperaListaDeQuestões() throws Exception {
        Client client = new JerseyClientBuilder(app.getEnvironment()).build(getClass().getName());

        Response response = client.target(format("http://localhost:%d/questoes", app.getLocalPort()))
                                  .request()
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
        Client client = new JerseyClientBuilder(app.getEnvironment()).build(getClass().getName());

        Response response = client.target(format("http://localhost:%d/questoes", app.getLocalPort()))
                                  .request()
                                  .post(Entity.entity(questao, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_CREATED);
    }

    @Test
    public void inserirRetornaCabeçalhoLocation() throws Exception {
        Client client = new JerseyClientBuilder(app.getEnvironment()).build(getClass().getName());

        Response response = client.target(format("http://localhost:%d/questoes", app.getLocalPort()))
                                  .request()
                                  .post(Entity.entity(questao, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getHeaderString("Location")).isNotEmpty();
    }

    @Test
    public void inserirRetornaQuestãoCriadaNoCorpoDaResposta() throws Exception {
        Client client = new JerseyClientBuilder(app.getEnvironment()).build(getClass().getName());

        QuestaoTO response = client.target(format("http://localhost:%d/questoes", app.getLocalPort()))
                                   .request()
                                   .buildPost(Entity.entity(questao, MediaType.APPLICATION_JSON_TYPE))
                                   .invoke(QuestaoTO.class);

        assertThat(response).isEqualToComparingOnlyGivenFields(questao, "pergunta", "resposta", "autor")
                            .hasFieldOrProperty("id")
                            .hasFieldOrProperty("dataDePublicacao");
    }

    @Test
    public void inserirGravaQuestãoNoBancoDeDados() throws Exception {
        Client client = new JerseyClientBuilder(app.getEnvironment()).build(getClass().getName());

        QuestaoTO response = client.target(format("http://localhost:%d/questoes", app.getLocalPort()))
                                   .request()
                                   .buildPost(Entity.entity(questao, MediaType.APPLICATION_JSON_TYPE))
                                   .invoke(QuestaoTO.class);

        Questao recuperadaDoBanco = db.get(Questao.class, response.id);

        assertThat(recuperadaDoBanco).isNotNull();
    }


    @Test
    public void inserirIgnoraDataDePublicaçãoSeForEnviadaNaRequisição() throws Exception {

        questao.dataDePublicacao = LocalDate.now().minusYears(10);

        Client client = new JerseyClientBuilder(app.getEnvironment()).build(getClass().getName());

        QuestaoTO response = client.target(format("http://localhost:%d/questoes", app.getLocalPort()))
                                   .request()
                                   .buildPost(Entity.entity(questao, MediaType.APPLICATION_JSON_TYPE))
                                   .invoke(QuestaoTO.class);

        Questao recuperadaDoBanco = db.get(Questao.class, response.id);

        assertThat(recuperadaDoBanco.getDataDePublicacao()).isNotEqualTo(questao.dataDePublicacao);
    }

    @Test
    public void inserirIgnoraIDSeForEnviadoNaRequisição() throws Exception {

        questao.id = UUID.randomUUID();

        Client client = new JerseyClientBuilder(app.getEnvironment()).build(getClass().getName());

        QuestaoTO response = client.target(format("http://localhost:%d/questoes", app.getLocalPort()))
                                   .request()
                                   .buildPost(Entity.entity(questao, MediaType.APPLICATION_JSON_TYPE))
                                   .invoke(QuestaoTO.class);

        Questao recuperadaDoBanco = db.get(Questao.class, response.id);

        assertThat(recuperadaDoBanco.getId()).isNotEqualByComparingTo(questao.id);
    }


    @Test
    public void recuperaCategoriasDeUmaQuestão() throws Exception {
        Client client = new JerseyClientBuilder(app.getEnvironment()).build(getClass().getName());

        Response response = client.target(format("http://localhost:%d/questoes", app.getLocalPort()))
                                  .path(povoador.questao1.getId().toString())
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
        Client client = new JerseyClientBuilder(app.getEnvironment()).build(getClass().getName());

        ItemComId item = new ItemComId(povoador.categoriaImpressao.getId());

        Response response = client.target(format("http://localhost:%d/questoes", app.getLocalPort()))
                                  .path(povoador.questao1.getId().toString())
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
        Client client = new JerseyClientBuilder(app.getEnvironment()).build(getClass().getName());

        Categoria categoria = povoador.questao1.getCategorias().iterator().next();

        Response response = client.target(format("http://localhost:%d/questoes", app.getLocalPort()))
                                  .path(povoador.questao1.getId().toString())
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
        Client client = new JerseyClientBuilder(app.getEnvironment()).build(getClass().getName());

        Response response = client.target(format("http://localhost:%d/questoes", app.getLocalPort()))
                                  .path(povoador.questao1.getId().toString())
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
}
