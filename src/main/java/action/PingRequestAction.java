package action;

import event.*;
import org.apache.log4j.Logger;
import state.Cluster;
import state.MemberState;
import utils.EventList;
import utils.Rand;

public class PingRequestAction {

    private final static Logger LOGGER = Logger.getLogger(PingRequestAction.class.getName());

    public static void forward(PingRequestEvent event, Cluster cluster, EventList eventList, Rand rand) {
        // send an indirect ping
        var thisEventTime = event.getEventTime();
        var pingSenderId = event.getSenderId();
        var thisMemberId = event.getReceiverId();

        var member = cluster.getMember(thisMemberId);
        if (member.getState() == MemberState.FAULTY) {
            LOGGER.debug(String.format("member-%s -- ignore as failed", member.getId()));
            return;
        }

        var targetMemberId = event.getTargetMemberId();
        LOGGER.debug(String.format("member-%s received a PING_REQUEST from member-%s for member-%s", thisMemberId, pingSenderId, targetMemberId));

        var indirectPingEventTime = thisEventTime + rand.generateMessageDelay();
        var indirectPingEvent = new IndirectPingEvent(indirectPingEventTime, EventType.INDIRECT_PING, thisMemberId, targetMemberId, event.getInitiatorSeqNum(), event.getTargetMemberId(), event.getSenderId());
        eventList.addEvent(indirectPingEvent);
        LOGGER.debug(String.format("member-%s sent INDIRECT_PING to member-%s (arrives at %.2f)", thisMemberId, targetMemberId, indirectPingEventTime * 1000.0));


    }
}
