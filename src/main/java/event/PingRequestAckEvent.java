package event;
/*
A PingRequestAckEvent occurs on Mi when it receives the ping from Mk.
 */
public class PingRequestAckEvent extends PingRequestEvent {
    public PingRequestAckEvent(double eventTime, EventType eventTypeEnum, int senderId, int receiverId, int initiatorSeqNum, int targetMemberId) {
        super(eventTime, eventTypeEnum, senderId, receiverId, initiatorSeqNum, targetMemberId);
    }
}
