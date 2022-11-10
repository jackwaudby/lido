package event;

public class StartProtocolPeriodEvent extends AbstractEvent
{

    private final int memberId;


    public StartProtocolPeriodEvent(double eventTime, EventType eventTypeEnum, int memberId)
    {
        super( eventTime, eventTypeEnum );
        this.memberId = memberId;
    }

    public int getMemberId() {
        return memberId;
    }
}

