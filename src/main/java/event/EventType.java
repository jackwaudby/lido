package event;

public enum EventType {
        FAILURE,
    START_PROTOCOL_PERIOD,
    END_PROTOCOL_PERIOD,
    TIMEOUT,

    // Messages
    DIRECT_PING,
    PING_REQUEST,
    INDIRECT_PING,
    DIRECT_PING_ACK,
    PING_REQUEST_ACK,
    INDIRECT_PING_ACK,
}
