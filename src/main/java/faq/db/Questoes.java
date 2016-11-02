package faq.db;

import faq.core.Questao;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Questoes extends AbstractDAO<Questao> {

    public Questoes(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Questao> listar() {
        return list(criteria());
    }

    public Optional<Questao> porId(UUID id) {
        return Optional.ofNullable(get(id));
    }

    public void persistir(Questao questao) {
        persist(questao);
    }

    public void excluir(Questao questao) {
        Criteria criteria = criteria();
        criteria.createCriteria("questoesRelacionadas")
                .add(Restrictions.idEq(questao.getId()));
        list(criteria).forEach(q -> q.removerQuestaoRelacionada(questao));
        currentSession().delete(questao);
    }
}
