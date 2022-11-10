package event;

/*
A member Mi attempts to contact another member Mj in a round of the protocol.
A DirectPingEvent occurs on Mj when it receives the ping from Mi.
 */

public class DirectPingEvent extends AbstractEvent implements MessageEvent
{
    private final int senderId;
    private final int receiverId;
    private final int initiatorSeqNum;

    public DirectPingEvent(double eventTime, EventType eventTypeEnum, int senderId, int receiverId, int initiatorSeqNum)
    {
        super( eventTime, eventTypeEnum );
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.initiatorSeqNum = initiatorSeqNum;
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
}
