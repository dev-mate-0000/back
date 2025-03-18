package com.mate.member.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepository {

    private final int SUGGEST_START_MEMBER_SIZE = 3;

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

    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(em.find(Member.class, id));
    }

    public List<Member> findSuggestMembers() {
        return em.createQuery("SELECT m FROM Member m ORDER BY m.priority DESC", Member.class)
                .setMaxResults(SUGGEST_START_MEMBER_SIZE)
                .getResultList();
    }

    public Optional<Member> findSuggestNextMember(int page) {
        List<Member> result = em.createQuery("SELECT m FROM Member m ORDER BY m.priority DESC", Member.class)
                .setFirstResult(SUGGEST_START_MEMBER_SIZE + page)
                .setMaxResults(1)
                .getResultList();

        if(!result.isEmpty()) {
            return Optional.ofNullable(result.get(0));
        }
        return Optional.empty();
    }
}
