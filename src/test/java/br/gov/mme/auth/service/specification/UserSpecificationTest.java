package br.gov.mme.auth.service.specification;

import br.gov.mme.auth.dto.filtro.FiltroLoginDTO;
import br.gov.mme.auth.model.User;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.List;

class UserSpecificationTest {

    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<User> criteriaQuery;
    private Root<User> root;

    @BeforeEach
    void setUp() {
        criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        criteriaQuery = Mockito.mock(CriteriaQuery.class);
        root = Mockito.mock(Root.class);
        Mockito.when(criteriaBuilder.createQuery(Mockito.any(Class.class))).thenReturn(criteriaQuery);
        Mockito.when(criteriaBuilder.equal(root.get("isDeleted"), Boolean.FALSE)).thenReturn(new Predicate() {
            @Override
            public Class<? extends Boolean> getJavaType() {
                return null;
            }

            @Override
            public String getAlias() {
                return "";
            }

            @Override
            public Selection<Boolean> alias(String s) {
                return null;
            }

            @Override
            public boolean isCompoundSelection() {
                return false;
            }

            @Override
            public List<Selection<?>> getCompoundSelectionItems() {
                return List.of();
            }

            @Override
            public Predicate isNull() {
                return null;
            }

            @Override
            public Predicate isNotNull() {
                return null;
            }

            @Override
            public Predicate in(Object... objects) {
                return null;
            }

            @Override
            public Predicate in(Expression<?>... expressions) {
                return null;
            }

            @Override
            public Predicate in(Collection<?> collection) {
                return null;
            }

            @Override
            public Predicate in(Expression<Collection<?>> expression) {
                return null;
            }

            @Override
            public <X> Expression<X> as(Class<X> aClass) {
                return null;
            }

            @Override
            public BooleanOperator getOperator() {
                return null;
            }

            @Override
            public boolean isNegated() {
                return false;
            }

            @Override
            public List<Expression<Boolean>> getExpressions() {
                return List.of();
            }

            @Override
            public Predicate not() {
                return null;
            }
        });
    }

    @Test
    void testNomeSpecificationComNomeNulo() {
        // Arrange
        FiltroLoginDTO filtro = new FiltroLoginDTO();
        filtro.setNome("teste");
        filtro.setIdSistema(1L);
        filtro.setSitema("CONSULTA-PUBLICA");
        filtro.setPerfil("ADMIN");
        UserSpecification.searchPorPerfilAdmin(filtro);

        filtro.setNome(null);
        filtro.setIdSistema(null);
        filtro.setSitema(null);
        filtro.setPerfil(null);
        UserSpecification.searchPorPerfilAdmin(filtro);
    }


}
