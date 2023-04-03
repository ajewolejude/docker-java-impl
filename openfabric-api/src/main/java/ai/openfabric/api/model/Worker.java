package ai.openfabric.api.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity()
@Table(name = "worker")
@Getter
@Setter
public class Worker extends Datable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "of-uuid")
    @GenericGenerator(name = "of-uuid", strategy = "ai.openfabric.api.model.IDGenerator")
    @Getter
    @Setter
    public String id;

    public String name;

    private String command;

    private Long created;

    private String image;

    private String imageId;

    @OneToMany(mappedBy="worker")
    public List<Port> ports;

    private String status;

    private String state;

    private Long sizeRw;

    private Long sizeRootFs;

    private String hostConfig;

    @OneToMany(mappedBy="worker")
    private List<Mount> mounts;


}
