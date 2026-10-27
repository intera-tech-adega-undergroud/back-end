package com.intera.adegaunderground.dto;

import com.intera.adegaunderground.pagination.CursorCodec;

import java.util.List;
import java.util.function.ToIntFunction;

public record CursorPage<T>(
        List<T> content,
        String nextCursor,
        boolean hasNext,
        int limit
) {

    public CursorPage {
        content = List.copyOf(content);
    }

    public static <T> CursorPage<T> from(
            List<T> rows,
            int limit,
            String scope,
            ToIntFunction<T> identifier
    ) {
        boolean hasNext = rows.size() > limit;

        List<T> content = rows.stream()
                .limit(limit)
                .toList();

        String nextCursor = hasNext
                ? CursorCodec.encode(
                scope,
                identifier.applyAsInt(content.getLast())
        )
                : null;

        return new CursorPage<>(
                content,
                nextCursor,
                hasNext,
                limit
        );
    }
}