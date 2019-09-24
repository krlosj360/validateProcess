package com.conecel.tramite.repository.primary;

import com.conecel.tramite.entity.ValidationProcess;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IValidationProcessRespository extends MongoRepository<ValidationProcess, String> {
	 
}