package action;

import event.EventType;
import event.IndirectPingAckEvent;
import event.IndirectPingEvent;
import event.PingRequestEvent;
import org.apache.log4j.Logger;
import state.Cluster;
import state.MemberState;
import utils.EventList;
import utils.Rand;

public class IndirectPingAction {
    private final static Logger LOGGER = Logger.getLogger(IndirectPingAction.class.getName());

    public static void reply(IndirectPingEvent event, Cluster cluster, EventList eventList, Rand rand) {
        // reply to indirect ping
        var thisMemberId = event.getReceiverId();
        var senderId = event.getSenderId();
        var member = cluster.getMember(thisMemberId);

        if (member.getState() == MemberState.FAULTY) {
            LOGGER.debug(String.format("member-%s -- ignore as failed", member.getId()));
            return;
        }


        LOGGER.debug(String.format("member-%s received an INDIRECT_PING from member-%s", thisMemberId, senderId));


        var thisEventTime = event.getEventTime();
        var indirectPingAckEventTime = thisEventTime + rand.generateMessageDelay();
        var indirectPingAckEvent = new IndirectPingAckEvent(indirectPingAckEventTime, EventType.INDIRECT_PING_ACK, thisMemberId, event.getSenderId(), event.getInitiatorSeqNum(), event.getTargetMemberId(), event.getInitiatorMemberId());
        eventList.addEvent(indirectPingAckEvent);
        LOGGER.debug(String.format("member-%s sent INDIRECT_PING_ACK to member-%s (arrives at %.2f)", event.getReceiverId(), event.getSenderId(), indirectPingAckEventTime * 1000.0));
    }
}
