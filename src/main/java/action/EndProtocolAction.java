package action;

import event.EndProtocolPeriodEvent;
import event.EventType;
import event.StartProtocolPeriodEvent;
import org.apache.log4j.Logger;
import state.Cluster;
import state.MemberState;
import utils.Config;
import utils.EventList;
import utils.Metrics;
import utils.Rand;

public class EndProtocolAction {
    private final static Logger LOGGER = Logger.getLogger(EndProtocolAction.class.getName());

    public static void end(EndProtocolPeriodEvent event, Cluster cluster, EventList eventList, Metrics metrics) {
        {
            var thisMemberId = event.getMemberId();
            var member = cluster.getMember(thisMemberId);
            if (member.receivedAck()) {
                member.setReceivedAck(false);
                LOGGER.debug(String.format("member-%s detected no failure", thisMemberId));
            } else {
                var targetMember = member.getTargetMember();
                var entry = member.getLocalMembershipList().stream().filter(e -> e.getMemberId() == targetMember).findFirst().orElseThrow();
//                entry.setState(MemberState.FAULTY);
                metrics.incFalsePositives();
                LOGGER.debug(String.format("member-%s detected member-%s as failed", thisMemberId, targetMember));
            }

            metrics.incRounds();
            member.incSeqNum();
            var newStartProtocolPeriod = new StartProtocolPeriodEvent(event.getEventTime(), EventType.START_PROTOCOL_PERIOD, thisMemberId);
            eventList.addEvent(newStartProtocolPeriod);
        }
    }
}
