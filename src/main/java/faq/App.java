package faq;

import faq.db.Categorias;
import faq.db.Questoes;
import faq.resources.CategoriaResource;
import faq.resources.QuestaoResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.hibernate.ScanningHibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.hibernate.SessionFactory;

public class App extends Application<AppConfig> {

    public ScanningHibernateBundle<AppConfig> hibernate = new ScanningHibernateBundle<AppConfig>("faq.core") {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(AppConfig configuration) {
            return configuration.getDatabase();
        }
    };

    public FlywayBundle<AppConfig> flyway = new FlywayBundle<AppConfig>() {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(AppConfig configuration) {
            return configuration.getDatabase();
        }
    };

    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {
        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(flyway);
        bootstrap.addBundle(new AssetsBundle("/frontend", "/app", "index.html"));
    }

    @Override
    public void run(AppConfig configuration, Environment environment) throws Exception {
        SessionFactory sessionFactory = hibernate.getSessionFactory();
        Questoes questoes = new Questoes(sessionFactory);
        Categorias categorias = new Categorias(sessionFactory);

        environment.jersey().register(new QuestaoResource(questoes, categorias));
        environment.jersey().register(new CategoriaResource(categorias));
    }

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }
}
