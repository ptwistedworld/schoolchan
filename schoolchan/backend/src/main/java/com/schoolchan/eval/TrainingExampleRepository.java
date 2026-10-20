package com.schoolchan.eval;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingExampleRepository extends JpaRepository<TrainingExample, UUID> {
    List<TrainingExample> findByLabel(String label);
}
