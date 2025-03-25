package com.mate.member.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class StarRepository {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(Star star) {
        if(star.getId() == null) {
            em.persist(star);
            return;
        }
        em.merge(star);
    }

    @Transactional
    public void delete(Star star) {
        em.createQuery("DELETE FROM Skill l WHERE l.member.id = :memberId")
                .setParameter("memberId", star)
                .executeUpdate();
    }
}
