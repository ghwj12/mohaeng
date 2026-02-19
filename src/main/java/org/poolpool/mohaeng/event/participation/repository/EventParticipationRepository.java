package org.poolpool.mohaeng.event.participation.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.poolpool.mohaeng.event.participation.entity.EventParticipationEntity;
import org.poolpool.mohaeng.event.participation.entity.ParticipationBoothEntity;
import org.poolpool.mohaeng.event.participation.entity.ParticipationBoothFacilityEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EventParticipationRepository {

    @PersistenceContext
    private EntityManager em;

    // =========================
    // EVENT_PARTICIPATION
    // =========================

    public Optional<EventParticipationEntity> findParticipationById(Long pctId) {
        return Optional.ofNullable(em.find(EventParticipationEntity.class, pctId));
    }

    public List<EventParticipationEntity> findParticipationByUserId(Long userId) {
        return em.createQuery(
                        "select p from EventParticipationEntity p " +
                        "where p.userId = :userId " +
                        "order by p.pctDate desc",
                        EventParticipationEntity.class
                )
                .setParameter("userId", userId)
                .getResultList();
    }

    public EventParticipationEntity saveParticipation(EventParticipationEntity entity) {
        if (entity.getPctId() == null) {
            em.persist(entity);
            return entity;
        }
        return em.merge(entity);
    }

    // =========================
    // PARTICIPATION_BOOTH
    // =========================

    public Optional<ParticipationBoothEntity> findBoothById(Long pctBoothId) {
        return Optional.ofNullable(em.find(ParticipationBoothEntity.class, pctBoothId));
    }

    public List<ParticipationBoothEntity> findBoothByUserId(Long userId) {
        return em.createQuery(
                        "select b from ParticipationBoothEntity b " +
                        "where b.userId = :userId " +
                        "order by b.createdAt desc",
                        ParticipationBoothEntity.class
                )
                .setParameter("userId", userId)
                .getResultList();
    }

    public ParticipationBoothEntity saveBooth(ParticipationBoothEntity entity) {
        if (entity.getPctBoothId() == null) {
            em.persist(entity);
            return entity;
        }
        return em.merge(entity);
    }

    // =========================
    // PARTICIPATION_BOOTH_FACILITY
    // =========================

    public List<ParticipationBoothFacilityEntity> findFacilitiesByPctBoothId(Long pctBoothId) {
        return em.createQuery(
                        "select f from ParticipationBoothFacilityEntity f " +
                        "where f.pctBoothId = :pctBoothId",
                        ParticipationBoothFacilityEntity.class
                )
                .setParameter("pctBoothId", pctBoothId)
                .getResultList();
    }

    public void deleteFacilitiesByPctBoothId(Long pctBoothId) {
        em.createQuery(
                "delete from ParticipationBoothFacilityEntity f where f.pctBoothId = :pctBoothId"
        ).setParameter("pctBoothId", pctBoothId)
         .executeUpdate();
    }

    public void saveFacilities(List<ParticipationBoothFacilityEntity> facilities) {
        for (ParticipationBoothFacilityEntity f : facilities) {
            if (f.getPctBoothFaciId() == null) em.persist(f);
            else em.merge(f);
        }
    }
}

