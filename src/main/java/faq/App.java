package faq;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import faq.resources.QuestaoResource;

public class App extends Application<AppConfig> {

    @Override
    public void run(AppConfig configuration, Environment environment) throws Exception {
        environment.jersey().register(new QuestaoResource());
    }

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }
}
