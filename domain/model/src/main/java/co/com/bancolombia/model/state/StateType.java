package co.com.bancolombia.model.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StateType {
    PENDING_REVIEW("Pending review"),
    MANUAL_REVIEW("Manual review"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    CANCELED("Canceled");

    private final String value;
}
