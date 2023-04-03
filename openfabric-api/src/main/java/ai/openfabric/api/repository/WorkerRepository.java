package ai.openfabric.api.repository;

import ai.openfabric.api.model.Worker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerRepository extends JpaRepository<Worker, String> {

    Page<Worker> findAll(Pageable pageable);

}
