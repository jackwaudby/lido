package action;

import event.*;
import org.apache.log4j.Logger;
import state.Cluster;
import state.MemberState;
import state.MembershipListEntry;
import utils.Config;
import utils.EventList;
import utils.Rand;

public class StartProtocolAction {
    private final static Logger LOGGER = Logger.getLogger(StartProtocolAction.class.getName());

    public static void start(StartProtocolPeriodEvent event, Cluster cluster, Config config, EventList eventList, Rand rand) {
        var thisEventTime = event.getEventTime();
        var thisMemberId = event.getMemberId();
        var member = cluster.getMember(thisMemberId);
        var seqNum = member.getSeqNum();
        LOGGER.debug(String.format("member-%s starting protocol round %s at %.2f(ms)", thisMemberId, seqNum, thisEventTime * 1000.0));

        // (1) get a random member from this member's local view of members
        var localMembershipList = member.getLocalMembershipList().stream().filter(e -> e.getState() == MemberState.ALIVE).map(MembershipListEntry::getMemberId).toList();
        var aliveLocalMembers = localMembershipList.size();
        if (aliveLocalMembers == 0) {
            LOGGER.debug(String.format("member-%s all members failed", thisMemberId));
            return;
        }
        var r = rand.getRandom(aliveLocalMembers);
        var targetMemberId = localMembershipList.get(r);
        member.setTargetMember(targetMemberId);
        LOGGER.debug(String.format("member-%s target member-%s", thisMemberId, targetMemberId));

        // (2) send a DIRECT_PING to random member
        var directPingReceivedTime = thisEventTime + rand.generateMessageDelay();
        var receivedDirectPingEvent = new DirectPingEvent(directPingReceivedTime, EventType.DIRECT_PING, thisMemberId, targetMemberId, seqNum);
        eventList.addEvent(receivedDirectPingEvent);
        LOGGER.debug(String.format("member-%s sent DIRECT_PING to member-%s (arrives at %.2fms)", thisMemberId, targetMemberId, directPingReceivedTime * 1000.0));

        // (3) create timeout event
        var timeoutTime = thisEventTime + (config.getTimeout() / 1000.0);
        var timeoutEvent = new TimeoutEvent(timeoutTime, EventType.TIMEOUT, thisMemberId);
        eventList.addEvent(timeoutEvent);
        LOGGER.debug(String.format("member-%s set TIMEOUT to %.2fms", thisMemberId, timeoutTime * 1000.0));

        // (4) create protocol period event
        var protocolPeriodEndTime = thisEventTime + (config.getProtocolPeriod() / 1000.0); // 3x normally
        var protocolPeriodEndEvent = new EndProtocolPeriodEvent(protocolPeriodEndTime, EventType.END_PROTOCOL_PERIOD, thisMemberId, seqNum);
        eventList.addEvent(protocolPeriodEndEvent);
        LOGGER.debug(String.format("member-%s set END_PROTOCOL to %.2fms", thisMemberId, protocolPeriodEndTime * 1000.0));
    }
}
