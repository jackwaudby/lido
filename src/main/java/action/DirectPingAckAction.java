package action;

import event.DirectPingAckEvent;
import org.apache.log4j.Logger;
import state.Cluster;
import state.MemberState;


public class DirectPingAckAction {
    private final static Logger LOGGER = Logger.getLogger(DirectPingAckAction.class.getName());

    public static void consume(DirectPingAckEvent event, Cluster cluster) {
        // check if the message is stale
        var thisMemberId = event.getReceiverId();
        var member = cluster.getMember(thisMemberId);
        var currSeqNum = member.getSeqNum();
        var expectedSeqNum = event.getInitiatorSeqNum();
        if (member.getState() == MemberState.FAULTY) {
            LOGGER.debug(String.format("member-%s -- ignore as failed", member.getId()));
        } else if (currSeqNum > expectedSeqNum) {
            // stale ACK, will already have marked as failed
        } else {
            // not failed, wait until the end of the protocol period
            member.setReceivedAck(true);
            LOGGER.debug(String.format("member-%s received DIRECT_PING_ACK for round %s from member-%s", thisMemberId, event.getInitiatorSeqNum(), event.getSenderId()));
            LOGGER.debug(String.format("member-%s knows member-%s is alive ", thisMemberId, event.getSenderId()));
        }
    }
}
