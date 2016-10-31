package faq.db;

import faq.core.Categoria;
import faq.core.ObjetosDeTeste;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoriasTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public Persistencia db = Persistencia.padrao();

    private ObjetosDeTeste criar = new ObjetosDeTeste();

    private Categorias categorias;

    @Before
    public void setUp() throws Exception {
        categorias = new Categorias(db.getSessionFactory());
    }

    @Test
    public void listarRecuperaTodasAsCategorias() throws Exception {
        Stream.of("Categoria 1", "Categoria 2", "Categoria 3")
              .map(criar::categoriaComTitulo)
              .forEach(categorias::persistir);

        db.flushAndClear();

        List<Categoria> todas = categorias.listar();

        assertThat(todas).hasSize(3);
    }

    @Test
    public void persistirGravaCategoriaNoBanco() throws Exception {
        Categoria categoria = criar.categoria();
        categorias.persistir(categoria);

        db.flushAndClear();

        assertThat(categoria.getId()).isNotNull();

        Categoria categoriaRecuperada = db.get(Categoria.class, categoria.getId());

        assertThat(categoriaRecuperada.getId()).isEqualByComparingTo(categoria.getId());
    }

    @Test
    public void persistirNãoPermiteCategoriaSemTitulo() throws Exception {
        Categoria categoria = criar.categoria();
        categoria.setTitulo(null);
        categorias.persistir(categoria);

        thrown.expect(ConstraintViolationException.class);
    }

    @Test
    public void persistirNãoPermiteCategoriaComTituloEmBranco() throws Exception {
        Categoria categoria = criar.categoria();
        categoria.setTitulo("    ");
        categorias.persistir(categoria);

        thrown.expect(ConstraintViolationException.class);
    }

    @Test
    public void persistirNãoPermiteCategoriaComTituloDuplicado() throws Exception {
        Categoria primeira = criar.categoriaComTitulo("Título");
        categorias.persistir(primeira);

        db.flushAndClear();

        Categoria segunda = criar.categoriaComTitulo("Título");
        categorias.persistir(segunda);

        thrown.expect(org.hibernate.exception.ConstraintViolationException.class);
    }
    @Test
    public void porIdRecuperaCategoriaCorretamente() throws Exception {
        Categoria categoria = criar.categoria();
        categorias.persistir(categoria);

        db.flushAndClear();

        Optional<Categoria> optCategoria = categorias.porId(categoria.getId());

        assertThat(optCategoria).hasValueSatisfying(categoriaRecuperada -> {
            assertThat(categoriaRecuperada.getId()).isEqualByComparingTo(categoria.getId());
        });
    }

    @Test
    public void excluirRemoveQuestãoDoBancoDeDados() throws Exception {
        Categoria categoria = criar.categoria();
        categorias.persistir(categoria);

        db.flushAndClear();

        Categoria recuperada = categorias.porId(categoria.getId()).orElseThrow(AssertionError::new);

        categorias.excluir(recuperada);

        db.flushAndClear();

        Optional<Categoria> optCategoria = categorias.porId(categoria.getId());

        assertThat(optCategoria).isEmpty();
    }
}
