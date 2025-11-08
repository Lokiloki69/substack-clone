package com.substack.service;

import com.substack.model.*;
import com.substack.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelevanceCalculationService {

    private final EntityManager em;
    private final InterestRepository interestRepo;
    private final TagRepository tagRepo;
    private final InterestTagMappingRepository mappingRepo;
    private final PostRepository postRepo;


    @Transactional
    public void updateRelevanceForPostAuthorInterests(Post post) {
        User author = post.getAuthor();
        if (author == null || author.getInterests().isEmpty()) return;

        for (Interest interest : author.getInterests()) {
            recalculateRelevanceForInterest(interest);
        }
    }

    private void recalculateRelevanceForInterest(Interest interest) {
        String sql = """
            WITH interest_posts AS (
                SELECT p.id
                FROM posts p
                JOIN users u ON p.user_id = u.id
                JOIN user_interests ui ON u.id = ui.user_id
                WHERE ui.interest_id = :interestId
            ),
            total_posts AS (SELECT COUNT(*) AS cnt FROM interest_posts),
            tag_counts AS (
                SELECT pt.tag_id, COUNT(*) AS cnt
                FROM interest_posts ip
                JOIN post_tag pt ON ip.id = pt.post_id
                GROUP BY pt.tag_id
            )
            SELECT tc.tag_id, 
                   CASE WHEN tp.cnt = 0 THEN 0.0 
                        ELSE tc.cnt::DOUBLE PRECISION / tp.cnt 
                   END AS relevance
            FROM tag_counts tc
            CROSS JOIN total_posts tp
            """;

        List<Object[]> results = em.createNativeQuery(sql)
                .setParameter("interestId", interest.getId())
                .getResultList();

        mappingRepo.deleteByInterest(interest);

        results.forEach(row -> {
            Long tagId = ((Number) row[0]).longValue();
            Double relevance = ((Number) row[1]).doubleValue();

            Tag tag = tagRepo.findById(tagId)
                    .orElseThrow(() -> new RuntimeException("Tag not found: " + tagId));

            InterestTagMapping m = new InterestTagMapping();
            m.setInterest(interest);
            m.setTag(tag);
            m.setRelevanceScore(relevance);
            mappingRepo.save(m);
        });
    }
}