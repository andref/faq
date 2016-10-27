package faq.db;

import faq.core.Questao;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.Optional;
import java.util.UUID;

public class Questoes extends AbstractDAO<Questao> {

    public Questoes(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<Questao> porId(UUID id) {
        return Optional.ofNullable(get(id));
    }

    public void persistir(Questao questao) {
        persist(questao);
    }

    public void excluir(Questao questao) {
        currentSession().delete(questao);
    }
}
