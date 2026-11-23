package com.intera.adegaunderground.repository;

import com.intera.adegaunderground.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    // Receita = total gravado na venda (já com desconto). Não depende do preço atual do produto
    // e ignora vendas canceladas.
    @Query(value = """
            SELECT SUM(a.total) AS valor_total_vendido,
                   COUNT(a.id_evento) AS total_vendas
            FROM tbl_evento a
            WHERE a.tipo = 'VENDA'
              AND a.status = 'CONCLUIDA'
              AND MONTH(a.data_hora_evento) = MONTH(CURDATE())
              AND YEAR(a.data_hora_evento) = YEAR(CURDATE())
            """, nativeQuery = true)
    List<Object[]> buscarVendasMes();

    @Query(value = """
            SELECT SUM(a.total) AS valor_total_vendido,
                   COUNT(a.id_evento) AS total_vendas
            FROM tbl_evento a
            WHERE a.tipo = 'VENDA'
              AND a.status = 'CONCLUIDA'
              AND DATE(a.data_hora_evento) = CURDATE()
            """, nativeQuery = true)
    List<Object[]> buscarFaturamentoDia();

    @Query(value = """
            SELECT f.id_funcionario,
                   f.nome_usuario AS vendedor,
                   COUNT(e.id_evento) AS quantidade_vendas,
                   SUM(e.total) AS valor_total_vendido
            FROM tbl_funcionario f
            INNER JOIN tbl_evento e
                ON f.id_funcionario = e.tbl_funcionario_id_funcionario
            WHERE e.tipo = 'VENDA'
              AND e.status = 'CONCLUIDA'
              AND MONTH(e.data_hora_evento) = MONTH(CURDATE())
              AND YEAR(e.data_hora_evento) = YEAR(CURDATE())
            GROUP BY f.id_funcionario, f.nome_usuario
            ORDER BY valor_total_vendido DESC
            """, nativeQuery = true)
    List<Object[]> buscarRankFuncionarios();

    @Query(value = """
            SELECT DATE(e.data_hora_evento) AS data,
                   SUM(e.total) AS valor_total_vendas
            FROM tbl_evento e
            WHERE e.tipo = 'VENDA'
              AND e.status = 'CONCLUIDA'
              AND DATE(e.data_hora_evento) BETWEEN :dataInicio AND :dataFim
            GROUP BY DATE(e.data_hora_evento)
            ORDER BY DATE(e.data_hora_evento)
            """, nativeQuery = true)
    List<Object[]> buscarGraficoDash(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    @Query("""
        SELECT e
        FROM Evento e
        WHERE e.cliente IS NOT NULL
        ORDER BY e.dataHoraEvento DESC
    """)
    List<Evento> buscarFiados();

    @Query("""
        SELECT e
        FROM Evento e
        WHERE e.cliente IS NOT NULL
          AND FUNCTION('DATE', e.dataHoraEvento)
                BETWEEN :dataInicio AND :dataFim
        ORDER BY e.dataHoraEvento DESC
    """)
    List<Evento> buscarFiadosPorPeriodo(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    @Query(value = """
        SELECT 
            c.id_cliente AS id_cliente,
            c.nome AS cliente,
            COALESCE(c.saldo_devedor, 0) AS valor,
            MAX(e.data_hora_evento) AS data,
            CASE 
                WHEN COALESCE(c.saldo_devedor, 0) > 0 THEN 'Em Aberto'
                ELSE 'Pago'
            END AS status
        FROM tbl_cliente c
        LEFT JOIN tbl_evento e
            ON e.tbl_cliente_id_cliente = c.id_cliente
        GROUP BY 
            c.id_cliente,
            c.nome,
            c.saldo_devedor
        ORDER BY c.nome
        """, nativeQuery = true)
    List<Object[]> buscarTodosClientesFiado();

    @Query(value = """
        SELECT 
            c.id_cliente AS id_cliente,
            c.nome AS cliente,
            COALESCE(c.saldo_devedor, 0) AS valor,
            MAX(e.data_hora_evento) AS data,
            CASE 
                WHEN COALESCE(c.saldo_devedor, 0) > 0 THEN 'Em Aberto'
                ELSE 'Pago'
            END AS status
        FROM tbl_cliente c
        INNER JOIN tbl_evento e
            ON e.tbl_cliente_id_cliente = c.id_cliente
            AND e.tipo = 'VENDA'
            AND DATE(e.data_hora_evento) BETWEEN :dataInicio AND :dataFim
        GROUP BY 
            c.id_cliente,
            c.nome,
            c.saldo_devedor
        ORDER BY c.nome
        """, nativeQuery = true)
    List<Object[]> buscarTodosClientesFiadoPorPeriodo(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    Evento findFirstByCliente_IdClienteOrderByDataHoraEventoDesc(Integer idCliente);
}