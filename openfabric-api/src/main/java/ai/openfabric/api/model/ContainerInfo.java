package ai.openfabric.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity()
@Table(name = "information")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContainerInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "of-uuid")
    @GenericGenerator(name = "of-uuid", strategy = "ai.openfabric.api.model.IDGenerator")
    @Getter
    @Setter
    private String id;



    private String[] args;
    private String hostConfig;
    private String hostnamePath;
    private String imageId;
    private String name;
    private String imageName;
    private String command;
    private String state;
    private String hostName;
    private String ipAddress;
    private String path;

}