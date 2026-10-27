package com.intera.adegaunderground.repository;

import com.intera.adegaunderground.entity.Produto;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CursorQueryRepository {

    private final EntityManager entityManager;

    public CursorQueryRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Produto> findStockAfter(
            int after,
            int limit,
            String search
    ) {
        return entityManager.createQuery("""
                SELECT p
                FROM Produto p
                LEFT JOIN FETCH p.categoria
                WHERE p.idProduto > :after
                  AND (
                    :search = ''
                    OR LOCATE(:search, LOWER(p.nome)) > 0
                  )
                ORDER BY p.idProduto ASC
                """, Produto.class)
                .setParameter("after", after)
                .setParameter("search", search)
                .setMaxResults(limit + 1)
                .getResultList();
    }

    public List<Object[]> findCreditAfter(
            int after,
            int limit,
            String status,
            LocalDateTime start,
            LocalDateTime end
    ) {
        return entityManager.createQuery("""
                SELECT
                    c.idCliente,
                    c.nome,
                    c.saldoDevedor,
                    MAX(e.dataHoraEvento)
                FROM Cliente c
                LEFT JOIN Evento e ON e.cliente = c
                WHERE c.idCliente > :after
                  AND (
                    :status = 'Todos'
                    OR (
                        :status = 'Em Aberto'
                        AND COALESCE(c.saldoDevedor, 0) > 0
                    )
                    OR (
                        :status = 'Pagos'
                        AND COALESCE(c.saldoDevedor, 0) <= 0
                    )
                  )
                  AND (
                    :start IS NULL
                    OR e.dataHoraEvento >= :start
                  )
                  AND (
                    :end IS NULL
                    OR e.dataHoraEvento < :end
                  )
                GROUP BY
                    c.idCliente,
                    c.nome,
                    c.saldoDevedor
                ORDER BY c.idCliente ASC
                """, Object[].class)
                .setParameter("after", after)
                .setParameter("status", status)
                .setParameter("start", start)
                .setParameter("end", end)
                .setMaxResults(limit + 1)
                .getResultList();
    }

    public BigDecimal sumOutstandingCredit() {
        BigDecimal total = entityManager.createQuery("""
                SELECT SUM(c.saldoDevedor)
                FROM Cliente c
                WHERE c.saldoDevedor > 0
                """, BigDecimal.class)
                .getSingleResult();

        return total == null ? BigDecimal.ZERO : total;
    }
}