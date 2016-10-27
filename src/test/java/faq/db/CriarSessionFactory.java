package faq.db;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.reflect.ClassPath;
import org.h2.tools.Server;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;

import javax.persistence.Entity;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import static java.lang.String.format;

/**
 * Este utilitário permite criar rapidamente um session factory do Hibernate para uso em testes.
 */
public class CriarSessionFactory {
    private final Configuration c;

    @VisibleForTesting
    CriarSessionFactory(Configuration configuration) {
        c = configuration;
        c.setProperty(AvailableSettings.USE_GET_GENERATED_KEYS, "true");
        c.setProperty(AvailableSettings.USE_REFLECTION_OPTIMIZER, "true");
        c.setProperty(AvailableSettings.ORDER_UPDATES, "true");
        c.setProperty(AvailableSettings.ORDER_INSERTS, "true");
        c.setProperty(AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, "true");
        c.setProperty(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS,
                      "org.hibernate.context.internal.ThreadLocalSessionContext");
    }

    public static CriarSessionFactory paraH2EmMemoria(String banco) {
        Configuration c = new Configuration();
        c.setProperty(AvailableSettings.DRIVER, "org.h2.Driver");
        c.setProperty(AvailableSettings.URL, urlParaH2EmMemoria(banco));
        return new CriarSessionFactory(c);
    }

    public static CriarSessionFactory paraH2EmMemoria(Server server, String banco) {
        Configuration c = new Configuration();
        c.setProperty(AvailableSettings.DRIVER, "org.h2.Driver");
        c.setProperty(AvailableSettings.URL, urlParaH2EmMemoria(server, banco));
        return new CriarSessionFactory(c);
    }

    public static CriarSessionFactory paraH2EmArquivo(Server server, File databaseFile) {
        Configuration c = new Configuration();
        c.setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.H2Dialect");
        c.setProperty(AvailableSettings.DRIVER, "org.h2.Driver");
        c.setProperty(AvailableSettings.URL, urlParaH2EmArquivo(server, databaseFile));
        return new CriarSessionFactory(c);
    }

    public CriarSessionFactory construirBanco() {
        c.setProperty(AvailableSettings.HBM2DDL_AUTO, "create");
        return this;
    }

    public CriarSessionFactory atualizarBanco() {
        c.setProperty(AvailableSettings.HBM2DDL_AUTO, "update");
        return this;
    }

    public CriarSessionFactory validarBanco() {
        c.setProperty(AvailableSettings.HBM2DDL_AUTO, "validate");
        return this;
    }

    public CriarSessionFactory mostrarSQL() {
        c.setProperty(AvailableSettings.USE_SQL_COMMENTS, "true");
        c.setProperty(AvailableSettings.FORMAT_SQL, "true");
        c.setProperty(AvailableSettings.SHOW_SQL, "true");
        return this;
    }

    public CriarSessionFactory comConfiguracoesAdicionais(Consumer<Configuration> consumer) {
        consumer.accept(c);
        return this;
    }

    public CriarSessionFactory comEntidadesNoPacote(String packageName) {
        try {
            ClassPath
                    .from(getClass().getClassLoader())
                    .getTopLevelClassesRecursive(packageName)
                    .stream()
                    .map(classInfo -> classInfo.load())
                    .filter(classe -> classe.getAnnotation(Entity.class) != null)
                    .forEach(classe -> c.addAnnotatedClass(classe));
        } catch (IOException e) {
            throw new IllegalArgumentException("Erro ao adicionar pacote", e);
        }
        return this;
    }

    public SessionFactory getSessionFactory() {
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(c.getProperties())
                .build();
        SessionFactory sessionFactory = c.buildSessionFactory(serviceRegistry);
        return sessionFactory;
    }

    public static String urlParaH2EmMemoria(Server server, String banco) {
        return format("jdbc:h2:%s/mem:%s", server.getURL(), banco);
    }

    public static String urlParaH2EmArquivo(Server server, File databaseFile) {
        return format("jdbc:h2:%s/%s", server.getURL(), databaseFile.getAbsolutePath());
    }

    public static String urlParaH2EmMemoria(String banco) {
        return format("jdbc:h2:mem:%s", banco);
    }
}
