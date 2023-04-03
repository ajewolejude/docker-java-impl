package ai.openfabric.api.util;

import ai.openfabric.api.error.WorkerErrorDto;
import ai.openfabric.api.error.WorkerServiceException;
import ai.openfabric.api.model.Worker;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public class WorkerListValidator {

    public static void validatePageAndSize(int page, int size, List<Worker> workerList) {
        if (workerList == null) {
            throw new WorkerServiceException(new WorkerErrorDto(LocalDateTime.now().toString(), Instant.now(), 500, "Error", "Failed to fetch worker list"));
        }

        if (page < 0) {
            throw new WorkerServiceException(new WorkerErrorDto(LocalDateTime.now().toString(), Instant.now(), 400, "Error", "Page number should be non-negative"));
        }

        if (size <= 0) {
            throw new WorkerServiceException(new WorkerErrorDto(LocalDateTime.now().toString(), Instant.now(), 400, "Error", "Page size should be positive"));
        }

        if (size > workerList.size()) {
            throw new WorkerServiceException(new WorkerErrorDto(LocalDateTime.now().toString(), Instant.now(), 400, "Error", "Page size cannot be greater than count of workers"));
        }

        int pagesPossible = (int) Math.ceil((double) workerList.size() / size);

        if (page >= pagesPossible) {
            throw new WorkerServiceException(new WorkerErrorDto(LocalDateTime.now().toString(), Instant.now(), 400, "Error", "Invalid page number"));
        }
    }
}