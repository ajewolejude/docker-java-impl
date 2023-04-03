package ai.openfabric.api.error;

/**
 * Exception for errors thrown by @CpiClient.
 */
public class WorkerServiceException extends RuntimeException {

    private WorkerErrorDto workerErrorDto;

    public WorkerErrorDto getWorkerErrorDto() {
        return workerErrorDto;
    }

    public WorkerServiceException(WorkerErrorDto workerErrorDto) {
        super(workerErrorDto.getMessage());
        this.workerErrorDto = workerErrorDto;
    }
}