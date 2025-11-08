package com.substack.repository;

import com.substack.model.Interest;
import com.substack.model.InterestTagMapping;
import com.substack.model.InterestTagMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterestTagMappingRepository extends JpaRepository<InterestTagMapping, InterestTagMappingId> {
    @Modifying
    @Query("DELETE FROM InterestTagMapping m WHERE m.interest = :interest")
    void deleteByInterest(@Param("interest") Interest interest);
}