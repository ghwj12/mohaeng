package org.poolpool.mohaeng.event.list.dto;

import lombok.*;
import org.poolpool.mohaeng.event.host.dto.HostBoothDto;
import org.poolpool.mohaeng.event.host.dto.HostFacilityDto;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EventDetailDto {
    // 1. 행사 메인 정보 (행사명, 날짜, 장소, 조회수 등)
    private EventDto eventInfo;

    private String hostName;
    private String hostEmail;
    private String hostPhone;
    
    // 2. 해당 행사의 부스 목록
    private List<HostBoothDto> booths;

    // 3. 해당 행사의 부대시설 목록
    private List<HostFacilityDto> facilities;
}