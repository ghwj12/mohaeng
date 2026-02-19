package org.poolpool.mohaeng.event.list.dto;

import org.poolpool.mohaeng.event.list.entity.EventRegionEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class EventRegionDto {
    private Long regionId;
    private String regionName;
    private EventRegionDto parent;

    // Entity -> DTO 변환
    public static EventRegionDto fromEntity(EventRegionEntity entity) {
        if (entity == null) return null;
        return EventRegionDto.builder()
                .regionId(entity.getRegionId())
                .regionName(entity.getRegionName())
                .parent(fromEntity(entity.getParent())) // 재귀적 변환
                .build();
    }

    // DTO -> Entity 변환 (추가)
    public EventRegionEntity toEntity() {
        return EventRegionEntity.builder()
                .regionId(this.regionId)
                .regionName(this.regionName)
                .parent(this.parent != null ? this.parent.toEntity() : null) // 재귀적 변환
                .build();
    }
}
