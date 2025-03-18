package com.mate.member.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class LanguageRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Language> findByMemberId(Long memberId) {
        return em.createQuery("SELECT l FROM Language l where l.member.id = :memberId", Language.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public void save(Language language) {
        if(language.getId() == null) {
            em.persist(language);
        } else {
            em.merge(language);
        }
    }
}
