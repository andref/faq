package faq.db;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ThreadLocalSessionContext;
import org.junit.rules.ExternalResource;

import java.io.Serializable;
import java.util.function.Supplier;

public class Persistencia extends ExternalResource {

    private final Supplier<SessionFactory> supplier;
    private SessionFactory sessionFactory;

    public Persistencia(Supplier<SessionFactory> supplier) {
        this.supplier = supplier;
    }

    public static Persistencia padrao() {
        return new Persistencia(() -> CriarSessionFactory.paraH2EmMemoria("teste")
                                                         .comEntidadesNoPacote("faq.core")
                                                         .construirBanco()
                                                         .mostrarSQL()
                                                         .getSessionFactory());
    }

    @Override
    protected void before() throws Throwable {
        this.sessionFactory = this.supplier.get();
        ThreadLocalSessionContext.bind(this.sessionFactory.openSession());
        getTransaction().begin();
    }

    @Override
    protected void after() {
        getTransaction().commit();
        getSession().close();
        getSessionFactory().close();
        ThreadLocalSessionContext.unbind(sessionFactory);
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public Session getSession() {
        return getSessionFactory().getCurrentSession();
    }

    public Transaction getTransaction() {
        return getSession().getTransaction();
    }

    public void deleteFlushAndClear(Object... entities) {
        delete(entities);
        flushAndClear();
    }

    public void delete(Object... entities) {
        Session session = getSession();
        for (Object entity : entities) {
            session.delete(entity);
        }
    }

    public SQLQuery sql(String sql) {
        return getSession().createSQLQuery(sql);
    }

    public void persistFlushAndClear(Object... entities) {
        persist(entities);
        flushAndClear();
    }

    public void persist(Object... entities) {
        Session session = getSession();
        for (Object entity : entities) {
            session.persist(entity);
        }
    }

    public void flushAndClear() {
        Session session = getSession();
        session.flush();
        session.clear();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> classe, Serializable id) {
        return (T) getSession().get(classe, id);
    }
}
