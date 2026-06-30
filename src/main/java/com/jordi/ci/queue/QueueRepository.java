package com.jordi.ci.queue;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

@Repository
@Profile("jdbc")
//TODO: probably use JPA instead, industry standard
public interface QueueRepository extends JpaRepository<Job, Long>{
    
    /**
     * Gets the first job with the corresponding status
     * This operation is meant to be used to swap a job's status, 
     * therefore it LOCKS the returned row, it must be used inside a @Transactional method
     * @param status
     * @return
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    //-2 is the Hibernate SKIP LOCK magic number. Hacky but the alternative is worse
    @QueryHints({@QueryHint(name="jakarta.persistence.lock.timeout", value = "-2")})
    public Optional<Job> findFirstByStatus(@Param("status") String status);
    
}
