package ai.openfabric.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity()
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Mount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "of-uuid")
    @GenericGenerator(name = "of-uuid", strategy = "ai.openfabric.api.model.IDGenerator")
    public String id;

    String name;

    String source;

    String destination;

    String driver;

    String mode;

    boolean rw;

    String propagation;

    @ManyToOne
    @JoinColumn(name="worker_id", nullable=false)
    private Worker worker;

    public Mount( String name, String source, String destination, String driver, String mode, boolean rw, String propagation) {
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.driver = driver;
        this.mode = mode;
        this.rw = rw;
        this.propagation = propagation;
    }
}
