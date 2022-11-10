package event;

public class TimeoutEvent extends AbstractEvent
{
    private final int memberId;


    public TimeoutEvent(double eventTime, EventType eventTypeEnum, int memberId)
    {
        super( eventTime, eventTypeEnum );
        this.memberId = memberId;
    }

    public int getMemberId() {
        return memberId;
    }
}
