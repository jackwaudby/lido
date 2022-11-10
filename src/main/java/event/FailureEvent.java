package event;

public class FailureEvent extends AbstractEvent
{
    private final int memberId;


    public FailureEvent(double eventTime, EventType eventTypeEnum, int memberId)
    {
        super( eventTime, eventTypeEnum );
        this.memberId = memberId;
    }

    public int getMemberId() {
        return memberId;
    }
}
