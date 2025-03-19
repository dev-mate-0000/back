package com.mate.member.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public class LanguageRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Language> findByMemberId(UUID memberId) {
        return em.createQuery("SELECT l FROM Language l where l.member.id = :memberId", Language.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    @Transactional
    public void save(Language language) {
        if(language.getId() == null) {
            em.persist(language);
            return;
        }
        em.merge(language);
    }

    @Transactional
    public void deleteByMemberId(UUID memberId) {
        em.createQuery("DELETE FROM Language l WHERE l.member.id = :memberId")
                .setParameter("memberId", memberId)
                .executeUpdate();
    }
}
