package state;

public class MembershipListEntry {
    private final int memberId;
    private MemberState state;

    public MembershipListEntry(int memberId) {
        this.memberId = memberId;
        this.state = MemberState.ALIVE;
    }

    public int getMemberId() {
        return memberId;
    }

    public MemberState getState() {
        return state;
    }

    public void setState(MemberState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "{" +
                "" + memberId +
                "," + state +
                '}';
    }
}
