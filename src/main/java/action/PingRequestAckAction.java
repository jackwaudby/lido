package action;

import event.EventType;
import event.IndirectPingAckEvent;
import event.PingRequestAckEvent;
import org.apache.log4j.Logger;
import state.Cluster;
import state.MemberState;
import utils.EventList;
import utils.Rand;

public class PingRequestAckAction {

    private final static Logger LOGGER = Logger.getLogger(PingRequestAckAction.class.getName());

    public static void consume(PingRequestAckEvent event, Cluster cluster) {
        // check if the message is stale
        var thisMemberId = event.getReceiverId();
        var member = cluster.getMember(thisMemberId);
        var currSeqNum = member.getSeqNum();
        var expectedSeqNum = event.getInitiatorSeqNum();

        if (member.getState() == MemberState.FAULTY) {
            LOGGER.debug(String.format("member-%s -- ignore as failed", member.getId()));
            return;
        }

        if (currSeqNum > expectedSeqNum) {
            // stale ACK, will already have marked as failed
            return;
        } else {
            // not failed, wait until the end of the protocol period
            LOGGER.debug(String.format("member-%s received INDIRECT_PING_ACK from member-%s for target member-%s ", thisMemberId, event.getSenderId(), event.getTargetMemberId()));

            if (member.receivedAck()) {
                LOGGER.debug(String.format("member-%s already heard from member-%s ", thisMemberId, member.getTargetMember()));
            } else {
                member.setReceivedAck(true);
                LOGGER.debug(String.format("member-%s knows member-%s is alive ", thisMemberId, member.getTargetMember()));
            }
        }
    }
}
