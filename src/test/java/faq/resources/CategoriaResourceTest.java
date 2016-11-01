package faq.resources;

import faq.App;
import faq.AppConfig;
import faq.core.Categoria;
import faq.core.Questao;
import faq.db.Persistencia;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class CategoriaResourceTest {

    private static GenericType<List<Questao>> LISTA_DE_QUESTOES = new GenericType<List<Questao>>() {
    };

    private static GenericType<List<Categoria>> LISTA_DE_CATEGORIAS = new GenericType<List<Categoria>>() {
    };

    private PovoadorSimples povoador = new PovoadorSimples();

    private DropwizardAppRule<AppConfig> app = new DropwizardAppRule<>(App.class, resourceFilePath("test.yaml"));

    private Persistencia db = Persistencia.comPovoador(povoador);

    @Rule
    public RuleChain env = RuleChain.outerRule(db)
                                    .around(app);


    @Test
    public void recuperaListaDePerguntas() throws Exception {
        Client client = new JerseyClientBuilder(app.getEnvironment()).build(getClass().getName());

        Response response = client.target(format("http://localhost:%d/questoes", app.getLocalPort()))
                                  .request()
                                  .get();

        assertThat(response.getStatus()).isEqualTo(200);

        List<Questao> questoes = response.readEntity(LISTA_DE_QUESTOES);

        assertThat(questoes).extracting(Questao::getPergunta)
                            .containsExactlyInAnyOrder(povoador.questao1.getPergunta(), povoador.questao2.getPergunta());
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

        assertThat(questoes).extracting(Categoria::getTitulo)
                            .containsExactlyElementsOf(povoador.questao1.getCategorias()
                                                                        .stream()
                                                                        .map(Categoria::getTitulo)
                                                                        .collect(Collectors.toList()));
    }
}
