package event;

/*
A IndirectPingEvent occurs on Mj when it receives the ping from Mk.
 */
public class IndirectPingEvent extends AbstractEvent implements MessageEvent {


    private final int senderId;
    private final int receiverId;
    private final int initiatorSeqNum;
    private final int targetMemberId;
    private final int initiatorMemberId;

    public IndirectPingEvent(double eventTime, EventType eventTypeEnum, int senderId, int receiverId, int initiatorSeqNum, int targetMemberId, int initiatorMemberId) {
        super(eventTime, eventTypeEnum);
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.initiatorSeqNum = initiatorSeqNum;
        this.targetMemberId = targetMemberId;
        this.initiatorMemberId = initiatorMemberId;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public int getInitiatorSeqNum() {
        return initiatorSeqNum;
    }

    public int getTargetMemberId() {
        return targetMemberId;
    }

    public int getInitiatorMemberId() {
        return initiatorMemberId;
    }
}
