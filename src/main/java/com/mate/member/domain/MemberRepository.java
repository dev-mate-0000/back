package com.mate.member.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Member save(Member member) {
        if(member.getId() == null) {
            em.persist(member);
        } else {
            em.merge(member);
        }

        return member;
    }

    public Member findById(Long id) {
        return em.find(Member.class, id);
    }
}
