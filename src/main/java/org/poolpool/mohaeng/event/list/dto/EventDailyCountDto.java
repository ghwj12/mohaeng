package org.poolpool.mohaeng.event.list.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EventDailyCountDto {
    private LocalDate date;
    private Long count;

    public EventDailyCountDto(LocalDate date, Long count) {
        this.date = date;
        this.count = count;
    }
}