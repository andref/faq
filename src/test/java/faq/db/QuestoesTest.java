package faq.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ThreadLocalSessionContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QuestoesTest {

    private SessionFactory factory;
    private Session session;
    private Questoes questoes;

    @Before
    public void setUp() throws Exception {
        factory = CriarSessionFactory.paraH2EmMemoria("teste")
                                     .comEntidadesNoPacote("faq.core")
                                     .criarBanco()
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
    public void consegueCriarBancoDeDados() throws Exception {
    }

}
