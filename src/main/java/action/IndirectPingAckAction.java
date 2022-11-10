package action;

import event.*;
import org.apache.log4j.Logger;
import state.Cluster;
import state.MemberState;
import utils.EventList;
import utils.Rand;

public class IndirectPingAckAction {
    private final static Logger LOGGER = Logger.getLogger(IndirectPingAckAction.class.getName());

    public static void forward(IndirectPingAckEvent event, EventList eventList, Rand rand) {
        // forward on an indirect ping
        var thisEventTime = event.getEventTime();
        var thisMemberId = event.getReceiverId();

        var member = Cluster.getInstance().getMember(thisMemberId);
        if (member.getState() == MemberState.FAULTY) {
            LOGGER.debug(String.format("member-%s -- ignore as failed", member.getId()));
            return;
        }

        var indirectPingEventTime = thisEventTime + rand.generateMessageDelay();
        var indirectPingEvent = new PingRequestAckEvent(indirectPingEventTime, EventType.PING_REQUEST_ACK, thisMemberId, event.getInitiatorMemberId(), event.getInitiatorSeqNum(), event.getTargetMemberId());
        eventList.addEvent(indirectPingEvent);
        LOGGER.debug(String.format("member-%s received an INDIRECT_PING_ACK from to member-%s", event.getReceiverId(), event.getTargetMemberId()));
    }
}
