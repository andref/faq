package faq.db;

import faq.core.Categoria;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Categorias extends AbstractDAO<Categoria> {

    public Categorias(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Categoria> listar() {
        return list(criteria());
    }

    public Optional<Categoria> porId(UUID id) {
        return Optional.ofNullable(get(id));
    }

    public void persistir(Categoria categoria) {
        persist(categoria);
    }

    public void excluir(Categoria categoria) {
        currentSession().delete(categoria);
    }
}
