package co.com.bancolombia.model.state;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StateType Enum Tests")
class StateTypeTest {

    @Test
    @DisplayName("Should have correct enum values")
    void shouldHaveCorrectEnumValues() {
        // Assert
        assertEquals(5, StateType.values().length);
        assertNotNull(StateType.PENDING_REVIEW);
        assertNotNull(StateType.MANUAL_REVIEW);
        assertNotNull(StateType.APPROVED);
        assertNotNull(StateType.REJECTED);
        assertNotNull(StateType.CANCELED);
    }

    @Test
    @DisplayName("Should return correct string values")
    void shouldReturnCorrectStringValues() {
        // Assert
        assertEquals("Pending review", StateType.PENDING_REVIEW.getValue());
        assertEquals("Manual review", StateType.MANUAL_REVIEW.getValue());
        assertEquals("Approved", StateType.APPROVED.getValue());
        assertEquals("Rejected", StateType.REJECTED.getValue());
        assertEquals("Canceled", StateType.CANCELED.getValue());
    }

    @Test
    @DisplayName("Should find enum by value")
    void shouldFindEnumByValue() {
        // Act & Assert
        assertEquals(StateType.PENDING_REVIEW, findByValue("Pending review"));
        assertEquals(StateType.MANUAL_REVIEW, findByValue("Manual review"));
        assertEquals(StateType.APPROVED, findByValue("Approved"));
        assertEquals(StateType.REJECTED, findByValue("Rejected"));
        assertEquals(StateType.CANCELED, findByValue("Canceled"));
    }

    @Test
    @DisplayName("Should return null for unknown value")
    void shouldReturnNullForUnknownValue() {
        // Act & Assert
        assertNull(findByValue("Unknown status"));
        assertNull(findByValue(""));
        assertNull(findByValue(null));
    }

    @Test
    @DisplayName("Should have consistent toString")
    void shouldHaveConsistentToString() {
        // Assert
        assertEquals("PENDING_REVIEW", StateType.PENDING_REVIEW.toString());
        assertEquals("MANUAL_REVIEW", StateType.MANUAL_REVIEW.toString());
        assertEquals("APPROVED", StateType.APPROVED.toString());
        assertEquals("REJECTED", StateType.REJECTED.toString());
        assertEquals("CANCELED", StateType.CANCELED.toString());
    }

    @Test
    @DisplayName("Should have unique values")
    void shouldHaveUniqueValues() {
        // Arrange
        String[] values = {
            StateType.PENDING_REVIEW.getValue(),
            StateType.MANUAL_REVIEW.getValue(),
            StateType.APPROVED.getValue(),
            StateType.REJECTED.getValue(),
            StateType.CANCELED.getValue()
        };

        // Assert
        assertEquals(5, values.length);
        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                assertNotEquals(values[i], values[j], "Values should be unique");
            }
        }
    }

    @Test
    @DisplayName("Should handle case sensitivity in value comparison")
    void shouldHandleCaseSensitivity() {
        // Act & Assert
        assertNull(findByValue("pending review")); // lowercase
        assertNull(findByValue("PENDING REVIEW")); // uppercase
        assertNull(findByValue("Pending Review")); // mixed case
    }

    @Test
    @DisplayName("Should have ordinal values in logical order")
    void shouldHaveOrdinalValuesInLogicalOrder() {
        // Assert
        assertTrue(StateType.PENDING_REVIEW.ordinal() < StateType.MANUAL_REVIEW.ordinal());
        assertTrue(StateType.MANUAL_REVIEW.ordinal() < StateType.APPROVED.ordinal());
        assertTrue(StateType.APPROVED.ordinal() < StateType.REJECTED.ordinal());
        assertTrue(StateType.REJECTED.ordinal() < StateType.CANCELED.ordinal());
    }

    @Test
    @DisplayName("Should be able to use in switch statements")
    void shouldBeAbleToUseInSwitchStatements() {
        // Arrange
        StateType[] types = StateType.values();

        // Act & Assert
        for (StateType type : types) {
            String result = switch (type) {
                case PENDING_REVIEW -> "Initial status";
                case MANUAL_REVIEW -> "Requires manual intervention";
                case APPROVED -> "Successfully approved";
                case REJECTED -> "Application rejected";
                case CANCELED -> "Application canceled";
            };

            assertNotNull(result);
            assertFalse(result.isEmpty());
        }
    }

    @Test
    @DisplayName("Should handle enum name conversion")
    void shouldHandleEnumNameConversion() {
        // Assert
        assertEquals("PENDING_REVIEW", StateType.PENDING_REVIEW.name());
        assertEquals("MANUAL_REVIEW", StateType.MANUAL_REVIEW.name());
        assertEquals("APPROVED", StateType.APPROVED.name());
        assertEquals("REJECTED", StateType.REJECTED.name());
        assertEquals("CANCELED", StateType.CANCELED.name());
    }

    @Test
    @DisplayName("Should be serializable")
    void shouldBeSerializable() {
        // Assert
        assertTrue(StateType.class.isEnum());
        assertTrue(Enum.class.isAssignableFrom(StateType.class));
    }

    @Test
    @DisplayName("Should have proper equals and hashCode")
    void shouldHaveProperEqualsAndHashCode() {
        // Arrange
        StateType type1 = StateType.APPROVED;
        StateType type2 = StateType.APPROVED;
        StateType type3 = StateType.REJECTED;

        // Assert
        assertEquals(type1, type2);
        assertEquals(type1.hashCode(), type2.hashCode());
        assertNotEquals(type1, type3);
        assertNotEquals(type1.hashCode(), type3.hashCode());
    }

    @Test
    @DisplayName("Should handle valueOf method")
    void shouldHandleValueOfMethod() {
        // Act & Assert
        assertEquals(StateType.PENDING_REVIEW, StateType.valueOf("PENDING_REVIEW"));
        assertEquals(StateType.MANUAL_REVIEW, StateType.valueOf("MANUAL_REVIEW"));
        assertEquals(StateType.APPROVED, StateType.valueOf("APPROVED"));
        assertEquals(StateType.REJECTED, StateType.valueOf("REJECTED"));
        assertEquals(StateType.CANCELED, StateType.valueOf("CANCELED"));
    }

    @Test
    @DisplayName("Should throw exception for invalid valueOf")
    void shouldThrowExceptionForInvalidValueOf() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> StateType.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> StateType.valueOf(""));
        assertThrows(NullPointerException.class, () -> StateType.valueOf(null));
    }

    // Helper method to simulate finding enum by value
    private StateType findByValue(String value) {
        if (value == null) {
            return null;
        }
        for (StateType type : StateType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}