package action;

import event.*;
import org.apache.log4j.Logger;
import state.Cluster;
import state.MemberState;
import utils.EventList;
import utils.Rand;


public class DirectPingAction {

    private final static Logger LOGGER = Logger.getLogger(DirectPingAction.class.getName());

    public static void reply(DirectPingEvent event, Cluster cluster, EventList eventList, Rand rand) {


        // reply with an ACK
        var thisEventTime = event.getEventTime();
        var senderMemberId = event.getSenderId();
        var senderSeqNum = event.getInitiatorSeqNum();
        var thisMemberId = event.getReceiverId();

        var member = cluster.getMember(thisMemberId);
        if (member.getState() == MemberState.FAULTY) {
            return;
        }

        var directPingAckTime = thisEventTime + rand.generateMessageDelay();
        var directPingAckEvent = new DirectPingAckEvent(directPingAckTime, EventType.DIRECT_PING_ACK, thisMemberId, senderMemberId, senderSeqNum);
        eventList.addEvent(directPingAckEvent);

        LOGGER.debug(String.format("member-%s sent DIRECT_PING_ACK for round %s to member-%s (arrives at %.2f)", event.getReceiverId(), senderSeqNum, senderMemberId , directPingAckTime * 1000.0));

    }
}
