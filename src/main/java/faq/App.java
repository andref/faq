package faq;

import faq.resources.QuestaoResource;
import io.dropwizard.Application;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.ScanningHibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class App extends Application<AppConfig> {

    public ScanningHibernateBundle<AppConfig> hibernate = new ScanningHibernateBundle<AppConfig>("faq.core") {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(AppConfig configuration) {
            return configuration.getDatabase();
        }
    };

    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(AppConfig configuration, Environment environment) throws Exception {
        environment.jersey().register(new QuestaoResource());
    }

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }
}
