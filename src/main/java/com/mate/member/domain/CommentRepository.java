package com.mate.member.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CommentRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Comment comment) {
        if(comment.getId() == null) {
            em.persist(comment);
            return;
        }
        em.merge(comment);
    }

    public List<Comment> findByMemberId(UUID memberId) {
        return em.createQuery("SELECT c FROM Comment c WHERE c.member.id = :memberId", Comment.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public Optional<Comment> findById(UUID id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    public void delete(Comment comment) {
        em.remove(comment);
    }
}
