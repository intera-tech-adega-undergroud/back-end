package com.intera.adegaunderground.repository;

import com.intera.adegaunderground.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    @Query(value = "SELECT \n" +
            "\tSUM(\n" +
            "        c.preco * b.quantidade\n" +
            "\t) AS valor_total_vendido,\n" +
            "    COUNT(DISTINCT a.id_evento) as total_vendas\n" +
            "FROM\n" +
            "\ttbl_evento as a\n" +
            "INNER JOIN item_evento as b\n" +
            "\tON a.id_evento = b.id_evento\n" +
            "INNER JOIN tbl_produto as c\n" +
            "\tON b.id_produto = c.id_produto\n" +
            "WHERE\n" +
            "\ta.tipo = 'VENDA'\n" +
            "    AND MONTH(a.data_hora_evento) = MONTH(CURDATE())\n" +
            "    AND YEAR(a.data_hora_evento) = YEAR(CURDATE());", nativeQuery = true)
    List<Object[]> buscarVendasMes();

    @Query(value = "SELECT \n" +
            "\tSUM(\n" +
            "        c.preco * b.quantidade\n" +
            "\t) AS valor_total_vendido,\n" +
            "    COUNT(DISTINCT a.id_evento) as total_vendas\n" +
            "FROM\n" +
            "\ttbl_evento as a\n" +
            "INNER JOIN item_evento as b\n" +
            "\tON a.id_evento = b.id_evento\n" +
            "INNER JOIN tbl_produto as c\n" +
            "\tON b.id_produto = c.id_produto\n" +
            "WHERE\n" +
            "\ta.tipo = 'VENDA'\n" +
            "    AND DAY(a.data_hora_evento) = DAY(CURDATE())\n" +
            "    AND MONTH(a.data_hora_evento) = MONTH(CURDATE())\n" +
            "    AND YEAR(a.data_hora_evento) = YEAR(CURDATE());", nativeQuery = true)
    List<Object[]> buscarFaturamentoDia();

    @Query(value = "SELECT \n" +
            "    f.id_funcionario,\n" +
            "    f.nome_usuario AS vendedor,\n" +
            "    COUNT(DISTINCT e.id_evento) AS quantidade_vendas,\n" +
            "    SUM(\n" +
            "        p.preco * ie.quantidade\n" +
            "    ) AS valor_total_vendido\n" +
            "FROM tbl_funcionario f\n" +
            "INNER JOIN tbl_evento e\n" +
            "    ON f.id_funcionario = e.tbl_funcionario_id_funcionario\n" +
            "INNER JOIN item_evento ie\n" +
            "    ON e.id_evento = ie.id_evento\n" +
            "INNER JOIN tbl_produto p\n" +
            "    ON ie.id_produto = p.id_produto\n" +
            "WHERE e.tipo = 'VENDA'\n" +
            "    AND MONTH(e.data_hora_evento) = MONTH(CURDATE())\n" +
            "    AND YEAR(e.data_hora_evento) = YEAR(CURDATE())\n" +
            "GROUP BY \n" +
            "    f.id_funcionario,\n" +
            "    f.nome_usuario\n" +
            "ORDER BY valor_total_vendido DESC;", nativeQuery = true)
    List<Object[]> buscarRankFuncionarios();

    @Query(value = """
            SELECT
                DATE(e.data_hora_evento) AS data,
                SUM(
                    p.preco * ie.quantidade
                ) AS valor_total_vendas
            FROM tbl_evento e
            INNER JOIN item_evento ie
                ON e.id_evento = ie.id_evento
            INNER JOIN tbl_produto p
                ON ie.id_produto = p.id_produto
            WHERE e.tipo = 'VENDA'
                AND DATE(e.data_hora_evento)
                    BETWEEN :dataInicio AND :dataFim
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

    Evento findFirstByCliente_IdClienteOrderByDataHoraEventoDesc(Integer idCliente);
}