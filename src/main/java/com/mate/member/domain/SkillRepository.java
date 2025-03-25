package com.mate.member.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public class SkillRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Skill> findByMemberId(UUID memberId) {
        return em.createQuery("SELECT l FROM Skill l where l.member.id = :memberId ORDER BY l.codes DESC", Skill.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    @Transactional
    public void save(Skill skill) {
        if(skill.getId() == null) {
            em.persist(skill);
            return;
        }
        em.merge(skill);
    }

    @Transactional
    public void deleteByMemberId(UUID memberId) {
        em.createQuery("DELETE FROM Skill l WHERE l.member.id = :memberId")
                .setParameter("memberId", memberId)
                .executeUpdate();
    }
}
