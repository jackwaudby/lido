package state;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private final int id;
    private int seqNum;
    private List<MembershipListEntry> localMembershipList;
    private MemberState state;

    private boolean receivedAck;

    private int targetMember;

    public Member(int id, int clusterSize) {
        this.id = id;
        this.seqNum = 0;
        this.localMembershipList = new ArrayList<>();

        for (int i = 0; i < clusterSize; i++) {
            if (i != id) {
                this.localMembershipList.add(new MembershipListEntry(i));
            }
        }

        this.state = MemberState.ALIVE;
        this.receivedAck = false;
        this.targetMember = -1;
    }

    public int getId() {
        return id;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public void incSeqNum() {
        seqNum += 1;
    }

    public MemberState getState() {
        return state;
    }

    public List<MembershipListEntry> getLocalMembershipList() {
        return localMembershipList;
    }

    public boolean receivedAck() {
        return receivedAck;
    }

    public void setReceivedAck(boolean receivedAck) {
        this.receivedAck = receivedAck;
    }

    public int getTargetMember() {
        return targetMember;
    }

    public void setTargetMember(int targetMember) {
        this.targetMember = targetMember;
    }

    public void setState(MemberState state) {
        this.state = state;
    }
}
