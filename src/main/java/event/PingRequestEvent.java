package event;

/*
When a member Mi times out it asks another member to contact its target
A PingRequestEvent occurs on Mk when it receives the ping from Mi.
 */
public class PingRequestEvent extends AbstractEvent implements MessageEvent {

    private final int senderId;

    private final int receiverId;

    private final int initiatorSeqNum;

    private final int targetMemberId;

    public PingRequestEvent(double eventTime, EventType eventTypeEnum, int senderId, int receiverId, int initiatorSeqNum, int targetMemberId) {
        super(eventTime, eventTypeEnum);
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.initiatorSeqNum = initiatorSeqNum;
        this.targetMemberId = targetMemberId;
    }

    public int getSenderId()
    {
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
}
