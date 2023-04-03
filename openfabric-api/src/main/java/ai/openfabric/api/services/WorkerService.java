package ai.openfabric.api.services;

import ai.openfabric.api.error.WorkerErrorDto;
import ai.openfabric.api.error.WorkerServiceException;
import ai.openfabric.api.model.Mount;
import ai.openfabric.api.model.Port;
import ai.openfabric.api.model.Worker;
import ai.openfabric.api.model.ContainerInfo;
import ai.openfabric.api.repository.WorkerRepository;
import ai.openfabric.api.util.UtilsService;
import ai.openfabric.api.util.WorkerListValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerMount;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.InvocationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkerService {

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private UtilsService utilsService;


    @Bean
    DockerClient dockerClient() {
        return DockerClientBuilder.getInstance().build();
    }


    public List<Worker> getContainerList() {
        List<Container> containerList = dockerClient()
                .listContainersCmd().withShowAll(true)
                .exec();
        List<Worker> workerList = new ArrayList<>();
        for (Container container : containerList) {
            Worker worker = new Worker();
            worker.setId(container.getId());
            worker.setName(container.getNames()[0]);
            worker.setCommand(container.getCommand());
            worker.setImage(container.getImage());
            worker.setImageId(container.getImageId());
            worker.setCreated(container.getCreated());
            List<Port> ports = new ArrayList<>();
            for (int i = 0; i < container.getPorts().length; i++) {
                ports.add(new Port(container.getPorts()[i].getIp(), container.getPorts()[i].getPrivatePort(),
                        container.getPorts()[i].getPublicPort(), container.getPorts()[i].getType()));
            }
            worker.setPorts(ports);
            worker.setStatus(container.getStatus());
            worker.setState(container.getState());
            worker.setSizeRw(container.getSizeRw());
            worker.setSizeRootFs(container.getSizeRootFs());
            worker.setSizeRw(container.getSizeRw());
            worker.setSizeRw(container.getSizeRw());
            worker.setSizeRw(container.getSizeRw());
            worker.setSizeRw(container.getSizeRw());
            List<ContainerMount> containerMountList = container.getMounts();
            List<Mount> mountList = new ArrayList<>();
            for (ContainerMount containerMount : containerMountList) {
                mountList.add(new Mount(containerMount.getName(), containerMount.getSource(),
                        containerMount.getDestination(), containerMount.getDriver(), containerMount.getMode(),
                        containerMount.getRw(), containerMount.getPropagation()));
            }
            worker.setMounts(mountList);
            worker.setHostConfig(container.getHostConfig().getNetworkMode());

            workerList.add(worker);
        }
        return workerList;
    }

    public void saveContainerList() {
        List<Worker> workerList = getContainerList();
        for (Worker worker : workerList) {
            workerRepository.save(worker);
        }
    }

    public Map<String, Object> getWorkerListPaginatedFromDatabase(PageRequest pageable) {
        Map<String, Object> workers = new HashMap<>();

        List<Map<String, Object>> workersList = new ArrayList<>();
        Page<Worker> workerPage = workerRepository
                .findAll(pageable);
        ObjectMapper oMapper = new ObjectMapper();
        workerPage.get().collect(Collectors.toList()).forEach(worker -> {
            oMapper.registerModule(new JavaTimeModule());
            Map<String, Object> workerMap = oMapper.convertValue(worker, Map.class);
            workersList.add(workerMap);
        });
        workers.put("workers", workersList);
        workers.put("total_element", workersList.size());

        return utilsService.paginationDetails(workers, workerPage);
    }

    public Page<Worker> getWorkerListPaginatedWithoutDatabase(int page, int size) {
        try {
            List<Worker> workerList = getContainerList();
            WorkerListValidator.validatePageAndSize(page, size, workerList);

            int start = page * size;
            int end = Math.min(start + size, workerList.size());
            Pageable pageable = PageRequest.of(page, size);

            return new PageImpl<>(workerList.subList(start, end), pageable, workerList.size());
        } catch (Exception ex) {
            // log the exception
            throw ex;
        }
    }


    public void startContainer(String containerId) throws WorkerServiceException {
        try {
            // Start Container
            dockerClient().startContainerCmd(containerId).exec();
        } catch (Exception e) {
            throw new WorkerServiceException(new WorkerErrorDto(LocalDateTime.now().toString(), Instant.now(), 500, "Error", "Failed to start container"));
        }
    }

    public void stopContainer(String containerId) throws WorkerServiceException {
        try {
            // Stop Container
            dockerClient().stopContainerCmd(containerId).exec();
        } catch (Exception e) {
            throw new WorkerServiceException(new WorkerErrorDto(LocalDateTime.now().toString(), Instant.now(), 500, "Error", "Failed to stop container"));
        }
    }


    public ContainerInfo getContainerInfo(String containerId) {
        try {
            InspectContainerResponse response = dockerClient().inspectContainerCmd(containerId).exec();
            ContainerInfo containerInfo = new ContainerInfo();
            containerInfo.setId(response.getId());
            containerInfo.setName(response.getName());
            containerInfo.setImageName(response.getConfig().getImage());
            containerInfo.setImageId(response.getImageId());
            containerInfo.setCommand(String.join(" ", response.getConfig().getCmd()));
            containerInfo.setArgs(response.getArgs());
            containerInfo.setState(response.getState().getStatus());
            containerInfo.setHostName(response.getHostnamePath());
            containerInfo.setIpAddress(response.getNetworkSettings().getIpAddress());
            return containerInfo;
        } catch (NotFoundException e) {
            throw new WorkerServiceException(new WorkerErrorDto(LocalDateTime.now().toString(), Instant.now(), 400, "Container not found", e.getMessage()));
        } catch (DockerException e) {
            throw new WorkerServiceException(new WorkerErrorDto(LocalDateTime.now().toString(), Instant.now(), 500, "Error getting container information", e.getMessage()));
        }
    }

    public Statistics getNextStatistics(String containerId) {
        InvocationBuilder.AsyncResultCallback<Statistics> callback = new InvocationBuilder.AsyncResultCallback<>();
        dockerClient().statsCmd(containerId).exec(callback);
        Statistics stats = new Statistics();
        try {
            stats = callback.awaitResult();
            callback.close();
        } catch (RuntimeException | IOException e) {
            // you may want to throw an exception here
        }
        return stats; // this may be null or invalid if the container has terminated
    }


}
