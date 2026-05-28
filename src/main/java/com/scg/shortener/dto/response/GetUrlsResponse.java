package com.scg.shortener.dto.response;

import com.scg.shortener.dto.UrlSummary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetUrlsResponse {
    @Getter
    @AllArgsConstructor
    public static class Meta {
        private long total;
        private int page;
        private int limit;
        private int totalPages;
    }

    private Meta meta;
    private List<UrlSummary> data;

    public static <T> GetUrlsResponse of(Page<T> page, List<UrlSummary> data) {
        Meta meta = new Meta(
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages()
        );

        return new GetUrlsResponse(meta, data);
    }
}


