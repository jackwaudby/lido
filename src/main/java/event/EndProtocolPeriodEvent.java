package event;

public class EndProtocolPeriodEvent extends AbstractEvent
{

    private final int memberId;
    private final int seqNum;


    public EndProtocolPeriodEvent(double eventTime, EventType eventTypeEnum, int memberId, int seqNum)
    {
        super( eventTime, eventTypeEnum );
        this.memberId = memberId;
        this.seqNum = seqNum;
    }

    public int getMemberId() {
        return memberId;
    }

    public int getSeqNum() {
        return seqNum;
    }
}

