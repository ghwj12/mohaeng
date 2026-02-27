package org.poolpool.mohaeng.event.list.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.poolpool.mohaeng.user.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private UserEntity host; 

    @Column(nullable = false, length = 200)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private EventCategoryEntity category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String simpleExplain;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate startRecruit;
    private LocalDate endRecruit;
    private LocalDate boothStartRecruit;
    private LocalDate boothEndRecruit;

    @Column(nullable = false)
    private Boolean hasBooth;

    @Column(nullable = false)
    private Boolean hasFacility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private EventRegionEntity region;

    private Integer price;
    private Integer capacity;

    @Column(length = 500)
    private String thumbnail;

    @Column(nullable = false)
    private Integer views;

    @Column(length = 20)
    private String eventStatus;

    private String lotNumberAdr;
    private String detailAdr;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
    private String zipCode;
    private String topicIds;
    private String hashtagIds;
    
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    @Builder.Default // 빌더 사용 시 리스트 초기화 유지
    private List<FileEntity> eventFiles = new ArrayList<>();
    
    
    public void changeStatusToHostDeleted() {
        this.eventStatus = "행사삭제";
    }

public void changeStatusToDeleted() {
        this.eventStatus = "DELETED";
    }
    
    public void updateCategoryAndRegion(EventCategoryEntity category, EventRegionEntity region) {
        this.category = category;
        this.region = region;
    }
}
