package event;

/*
A DirectPingAckEvent occurs on Mi when it receives the ping from Mj.
 */

public class DirectPingAckEvent extends DirectPingEvent {

    public DirectPingAckEvent(double eventTime, EventType eventTypeEnum, int senderId, int receiverId, int initiatorSeqNum) {
        super(eventTime, eventTypeEnum, senderId, receiverId, initiatorSeqNum);
    }
}
