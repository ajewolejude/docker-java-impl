package ai.openfabric.api.model;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity()
@Getter
@NoArgsConstructor
public class Port {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "of-uuid")
    @GenericGenerator(name = "of-uuid", strategy = "ai.openfabric.api.model.IDGenerator")
    public String id;

    private String ip;

    private Integer privatePort;

    private Integer publicPort;

    private String type;

    @ManyToOne
    @JoinColumn(name="worker_id", nullable=false)
    private Worker worker;

    public Port(String ip, Integer privatePort, Integer publicPort, String type) {
        this.ip = ip;
        this.privatePort = privatePort;
        this.publicPort = publicPort;
        this.type = type;
    }
}
