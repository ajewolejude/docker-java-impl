package ai.openfabric.api.model;

import com.github.dockerjava.api.model.Link;
import lombok.EqualsAndHashCode;
import lombok.ToString;

public class Network {

    private static final long serialVersionUID = 1L;

    private Ipam ipamConfig;

    private Link[] links;




    @EqualsAndHashCode
    @ToString
    public static class Ipam {
        private static final long serialVersionUID = 1L;


        private String ipv4Address;

        private String ipv6Address;

        public String getIpv4Address() {
            return ipv4Address;
        }

        public String getIpv6Address() {
            return ipv6Address;
        }


    }

}
