package action;

import event.EventType;
import event.PingRequestEvent;
import event.TimeoutEvent;
import org.apache.log4j.Logger;
import state.Cluster;
import state.MemberState;
import utils.Config;
import utils.EventList;
import utils.Rand;

import java.util.ArrayList;
import java.util.Collections;

public class TimeoutAction {
    private final static Logger LOGGER = Logger.getLogger(TimeoutAction.class.getName());

    public static void timeout(TimeoutEvent event, Cluster cluster, Config config, EventList eventList, Rand rand) {
        var thisMemberId = event.getMemberId();
        var member = cluster.getMember(thisMemberId);
        var targetMemberId = member.getTargetMember();

        if (member.getState() == MemberState.FAULTY) {
            LOGGER.debug(String.format("member-%s -- ignore as failed", member.getId()));
            return;
        }

        if (member.receivedAck()) {
            LOGGER.debug(String.format("member-%s knows it contacted is alive", thisMemberId));
            return;
        }

        // choose a subset of alive members from local membership
        var candidateMembers = new ArrayList<>(member.getLocalMembershipList().stream()
                .filter(e -> e.getState() == MemberState.ALIVE)
                .filter(e -> e.getMemberId() != member.getTargetMember())
                .filter(e -> e.getMemberId() != targetMemberId).toList());
        LOGGER.debug(String.format("member-%s candidates %s", thisMemberId, candidateMembers));


        var k = config.getSizeOfFailureDetectionSubgroup();
        var nbrOfCandidateMembers = candidateMembers.size();
        if (k > nbrOfCandidateMembers) {
            throw new IllegalStateException("not enough alive members to satisfy k");
        }
        Collections.shuffle(candidateMembers);
        var subset = candidateMembers.subList(0, k);
        LOGGER.debug(String.format("member-%s subset %s", thisMemberId, candidateMembers));

        // send each subset member a ping request
        var thisEventTime = event.getEventTime();
        var initiatorSeqNum = member.getSeqNum();
        for (var entry : subset) {
            var subsetMemberId = entry.getMemberId();
            var receivedPingReqTime = thisEventTime + rand.generateMessageDelay();
            var receivedPingReqEvent = new PingRequestEvent(receivedPingReqTime, EventType.PING_REQUEST, thisMemberId, subsetMemberId, initiatorSeqNum, targetMemberId);
            eventList.addEvent(receivedPingReqEvent);
            LOGGER.debug(String.format("member-%s sent PING_REQUEST to member-%s (arrives at %.2fms)", thisMemberId, subsetMemberId, receivedPingReqTime * 1000.0));
        }
    }
}
