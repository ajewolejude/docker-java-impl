package ai.openfabric.api.controller;

import ai.openfabric.api.error.WorkerErrorDto;
import ai.openfabric.api.error.WorkerServiceException;
import ai.openfabric.api.model.Worker;
import ai.openfabric.api.model.ContainerInfo;
import ai.openfabric.api.services.WorkerService;
import com.github.dockerjava.api.model.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("${node.api.path}/worker")
public class WorkerController {


    @Autowired
    WorkerService workerService;


    @GetMapping(path = "/workers")
    public @ResponseBody
    ResponseEntity<List<Worker>> getWorkers() {
        List<Worker> workerList = workerService.getContainerList();
        return new ResponseEntity<>(workerList, HttpStatus.OK);
    }

    @PostMapping(path = "/workers/save")
    public @ResponseBody
    ResponseEntity<?> saveWorkersToDatabase() {
        try {
            workerService.saveContainerList();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            WorkerErrorDto errorDto = new WorkerErrorDto(LocalDateTime.now().toString(), Instant.now(), 500, "Error", ex.getMessage());
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/workers/paged")
    public @ResponseBody
    ResponseEntity<?> getWorkerListPaginatedWithoutDatabase(
            @RequestParam("page") Integer page,
            @RequestParam("items_per_page") Integer itemsPerPage) {
        try {
            Page<Worker> workerList = workerService.getWorkerListPaginatedWithoutDatabase(page, itemsPerPage);
            return new ResponseEntity<>(workerList, HttpStatus.OK);
        } catch (WorkerServiceException ex) {
            WorkerErrorDto errorDto = ex.getWorkerErrorDto();
            HttpStatus httpStatus = HttpStatus.valueOf(errorDto.getStatus());
            return new ResponseEntity<>(errorDto, httpStatus);
        } catch (Exception ex) {
            WorkerErrorDto errorDto = new WorkerErrorDto(LocalDateTime.now().toString(), Instant.now(), 500, "Internal Server Error", ex.getMessage());
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/workers/paged/postgres")
    public ResponseEntity<?> getWorkerListPaginatedFromDatabase(
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("items_per_page") Optional<Integer> itemsPerPage
    ) {

        int currentPage = page.orElse(1);
        int perPage = itemsPerPage.orElse(10);
        Map<String, Object> workerMap = workerService.getWorkerListPaginatedFromDatabase(
                PageRequest.of(currentPage - 1, perPage));

        if (workerMap == null) {
            return ResponseEntity.ok()
                    .body("No worker available");
        }

        return ResponseEntity.ok(workerMap);
    }

    @GetMapping(path = "{containerId}/get")
    public ResponseEntity<?> getWorkerInformation(@PathVariable("containerId") String containerId) {
        try {
            ContainerInfo containerInfo = workerService.getContainerInfo(containerId);
            return new ResponseEntity<>(containerInfo, HttpStatus.OK);
        } catch (WorkerServiceException workerServiceException) {
            return new ResponseEntity<>(workerServiceException.getWorkerErrorDto(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/start")
    public @ResponseBody
    ResponseEntity<?> startContainer(@RequestBody String containerId) {
        try {
            workerService.startContainer(containerId);
            return new ResponseEntity<>("Started container " + containerId, HttpStatus.OK);
        } catch (WorkerServiceException e) {
            return new ResponseEntity<>(e.getWorkerErrorDto(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/stop")
    public @ResponseBody
    ResponseEntity<?> stopContainer(@RequestBody String containerId) {
        try {
            workerService.stopContainer(containerId);
            return new ResponseEntity<>("Stopped container " + containerId, HttpStatus.OK);
        } catch (WorkerServiceException e) {
            return new ResponseEntity<>(e.getWorkerErrorDto(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/containers/{containerId}/stats")
    public ResponseEntity<?> getWorkerStats(@PathVariable String containerId) {

        try {
            Statistics statistics = workerService.getNextStatistics(containerId);
            return new ResponseEntity<>(statistics, HttpStatus.OK);
        } catch (WorkerServiceException e) {
            return new ResponseEntity<>(e.getWorkerErrorDto(), HttpStatus.BAD_REQUEST);
        }
    }


}
