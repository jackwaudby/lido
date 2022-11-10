package event;

/*
A IndirectPingAckEvent occurs on Mk when it receives the ping from Mj.
 */

public class IndirectPingAckEvent extends IndirectPingEvent {

    public IndirectPingAckEvent(double eventTime, EventType eventTypeEnum, int senderId, int receiverId, int initiatorSeqNum, int targetMemberId, int initiatorMemberId) {
        super(eventTime, eventTypeEnum, senderId, receiverId, initiatorSeqNum, targetMemberId, initiatorMemberId);
    }
}
