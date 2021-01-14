package com.fastcampus.operation.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationRepository extends PagingAndSortingRepository<OperationEntity, String> {

}
