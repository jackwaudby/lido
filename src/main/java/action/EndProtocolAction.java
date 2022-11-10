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
            if (member.getState() == MemberState.FAULTY) {
                LOGGER.debug(String.format("member-%s -- ignore as failed", member.getId()));
                return;
            }

            if (member.receivedAck()) {
                member.setReceivedAck(false);
                LOGGER.debug(String.format("member-%s detected no failure", thisMemberId));
            } else {
                var targetMember = member.getTargetMember();
                LOGGER.debug(String.format("member-%s detected member-%s as failed", thisMemberId, targetMember));
                // if this happens terminate program
                metrics.setTimeToDetection(event.getEventTime());
                metrics.getSummary();
                System.exit(0);
            }

            metrics.incRounds();
            member.incSeqNum();
            var newStartProtocolPeriod = new StartProtocolPeriodEvent(event.getEventTime(), EventType.START_PROTOCOL_PERIOD, thisMemberId);
            eventList.addEvent(newStartProtocolPeriod);
        }
    }
}
