package com.intera.adegaunderground.service;

import com.intera.adegaunderground.dto.CreditPageDTO;
import com.intera.adegaunderground.dto.CursorPage;
import com.intera.adegaunderground.dto.FiadoDTO;
import com.intera.adegaunderground.dto.StockProductDTO;
import com.intera.adegaunderground.pagination.CursorCodec;
import com.intera.adegaunderground.repository.CursorQueryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class CursorPaginationService {

    private final CursorQueryRepository queries;

    public CursorPaginationService(CursorQueryRepository queries) {
        this.queries = queries;
    }

    private void validateLimit(int limit) {
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException(
                    "O limite deve estar entre 1 e 100."
            );
        }
    }

    public CursorPage<StockProductDTO> findStock(
            String cursor,
            int limit,
            String search
    ) {
        validateLimit(limit);

        String normalized = search == null
                ? ""
                : search.trim().toLowerCase(Locale.ROOT);

        String scope = CursorCodec.scope("stock|" + normalized);
        int after = CursorCodec.decode(cursor, scope);

        List<StockProductDTO> rows = queries
                .findStockAfter(after, limit, normalized)
                .stream()
                .map(StockProductDTO::from)
                .toList();

        return CursorPage.from(
                rows,
                limit,
                scope,
                StockProductDTO::idProduto
        );
    }

    public CreditPageDTO findCredit(
            String cursor,
            int limit,
            String status,
            LocalDate start,
            LocalDate end
    ) {
        validateLimit(limit);

        if (!List.of("Todos", "Em Aberto", "Pagos").contains(status)) {
            throw new IllegalArgumentException(
                    "Status deve ser Todos, Em Aberto ou Pagos."
            );
        }

        if ((start == null) != (end == null)
                || (start != null && start.isAfter(end))) {
            throw new IllegalArgumentException(
                    "Informe as duas datas, em ordem válida."
            );
        }

        if (end != null && end.equals(LocalDate.MAX)) {
            throw new IllegalArgumentException(
                    "Data final fora do intervalo permitido."
            );
        }

        String scope = CursorCodec.scope(
                "credit|" + status + "|" + start + "|" + end
        );

        int after = CursorCodec.decode(cursor, scope);

        List<FiadoDTO> rows = queries.findCreditAfter(
                        after,
                        limit,
                        status,
                        start == null ? null : start.atStartOfDay(),
                        end == null ? null : end.plusDays(1).atStartOfDay()
                )
                .stream()
                .map(this::toCredit)
                .toList();

        CursorPage<FiadoDTO> page = CursorPage.from(
                rows,
                limit,
                scope,
                FiadoDTO::getIdCliente
        );

        return new CreditPageDTO(
                page.content(),
                page.nextCursor(),
                page.hasNext(),
                page.limit(),
                queries.sumOutstandingCredit()
        );
    }

    private FiadoDTO toCredit(Object[] row) {
        BigDecimal balance = row[2] == null
                ? BigDecimal.ZERO
                : (BigDecimal) row[2];

        LocalDateTime date = (LocalDateTime) row[3];

        String formattedDate = date == null
                ? "-"
                : date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return new FiadoDTO(
                (Integer) row[0],
                (String) row[1],
                balance.doubleValue(),
                formattedDate,
                balance.signum() > 0 ? "Em Aberto" : "Pago",
                List.of()
        );
    }
}