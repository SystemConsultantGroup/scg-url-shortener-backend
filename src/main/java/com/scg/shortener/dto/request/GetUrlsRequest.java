package com.scg.shortener.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.scg.shortener.global.CustomException;
import com.scg.shortener.global.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@AllArgsConstructor
public class GetUrlsRequest {

    public enum SortBy {
        NEWEST,
        OLDEST,
        MOST_CLICKS,
        LEAST_CLICKS;

        @JsonCreator
        public static SortBy fromString(String value) {

            String normalizedValue = value.replace("-", "_").toUpperCase();

            try {
                return SortBy.valueOf(normalizedValue);
            } catch (IllegalArgumentException e) {
                throw new CustomException(ExceptionCode.INVALID_REQUEST);
            }
        }
    }

    private Long page;
    private Long limit;
    private SortBy sort;
}
