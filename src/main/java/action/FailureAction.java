package action;

import event.FailureEvent;
import org.apache.log4j.Logger;
import state.Cluster;
import state.MemberState;
import utils.Metrics;

public class FailureAction {
    private final static Logger LOGGER = Logger.getLogger(FailureAction.class.getName());

    public static void failed(FailureEvent event, Cluster cluster) {
        var thisMemberId = event.getMemberId();
        var member = cluster.getMember(thisMemberId);
        member.setState(MemberState.FAULTY);
        Metrics.getInstance().setFailureTime(event.getEventTime());
        LOGGER.debug(String.format("member-%s failed", thisMemberId));
    }
}
