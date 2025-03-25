package com.mate.member.domain;

import com.mate.member.presentation.enums.MemberStatusEnum;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MemberRepository {

    private final int SUGGEST_START_MEMBER_SIZE = 10;

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

    public Optional<Member> findById(UUID id) {
        return Optional.ofNullable(em.find(Member.class, id));
    }

    public Optional<Member> findByGithubId(Integer providerId) {
        List<Member> result = em.createQuery("SELECT m FROM Member m WHERE m.providerId = :providerId", Member.class)
                .setParameter("providerId", providerId)
                .getResultList();
        if(!result.isEmpty()) {
            return Optional.ofNullable(result.get(0));
        }
        return Optional.empty();
    }

    public List<Member> findSuggestMembers(int page) {
        int pageSize = SUGGEST_START_MEMBER_SIZE;
        int firstResult = (page - 1) * pageSize;

        return em.createQuery("SELECT m FROM Member m WHERE m.status = :status ORDER BY m.priority DESC", Member.class)
                .setParameter("status", MemberStatusEnum.PUBLIC)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }
}
