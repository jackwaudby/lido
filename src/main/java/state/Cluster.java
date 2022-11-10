package state;

import org.apache.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import utils.Config;
import utils.Metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cluster {
    private final static Logger LOGGER = Logger.getLogger(Cluster.class.getName());
    private static final Cluster instance = new Cluster();
    private Map<Integer, Member> members;

    private Cluster() {
        this.members = new HashMap<>();

        var clusterSize = Config.getInstance().getInitialNbrOfMembers();
        for (int i = 0; i < clusterSize; i++) {
            var member = new Member(i, clusterSize);
            members.put(i, member);
        }
    }

    public static Cluster getInstance() {
        return instance;
    }

    public Member getMember(int memberId) {
        return members.get(memberId);
    }

    @Override
    public String toString() {
        return "Cluster{" +
                '}';
    }
}
