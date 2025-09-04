package co.com.bancolombia.model.state;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("State Domain Model Tests")
class StateTest {

    private static final Long TEST_ID = 1L;
    private static final String TEST_NAME = "Pending review";
    private static final String TEST_DESCRIPTION = "The application has been received but not yet reviewed";

    @Nested
    @DisplayName("Builder Pattern Tests")
    class BuilderPatternTest {

        @Test
        @DisplayName("Should build State with all fields using builder")
        void shouldBuildWithAllFields() {
            // Act
            State state = State.builder()
                    .id(TEST_ID)
                    .name(TEST_NAME)
                    .description(TEST_DESCRIPTION)
                    .build();

            // Assert
            assertEquals(TEST_ID, state.getId());
            assertEquals(TEST_NAME, state.getName());
            assertEquals(TEST_DESCRIPTION, state.getDescription());
        }

        @Test
        @DisplayName("Should build State with minimal fields")
        void shouldBuildWithMinimalFields() {
            // Act
            State state = State.builder()
                    .name(TEST_NAME)
                    .build();

            // Assert
            assertNull(state.getId());
            assertEquals(TEST_NAME, state.getName());
            assertNull(state.getDescription());
        }

        @Test
        @DisplayName("Should support toBuilder pattern")
        void shouldSupportToBuilder() {
            // Arrange
            State original = State.builder()
                    .id(TEST_ID)
                    .name(TEST_NAME)
                    .build();

            // Act
            State modified = original.toBuilder()
                    .description(TEST_DESCRIPTION)
                    .build();

            // Assert
            assertEquals(TEST_ID, modified.getId());
            assertEquals(TEST_NAME, modified.getName());
            assertEquals(TEST_DESCRIPTION, modified.getDescription());
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTest {

        @Test
        @DisplayName("Should create State with no-args constructor")
        void shouldCreateWithNoArgsConstructor() {
            // Act
            State state = new State();

            // Assert
            assertNull(state.getId());
            assertNull(state.getName());
            assertNull(state.getDescription());
        }

        @Test
        @DisplayName("Should create State with all-args constructor")
        void shouldCreateWithAllArgsConstructor() {
            // Act
            State state = new State(TEST_ID, TEST_NAME, TEST_DESCRIPTION);

            // Assert
            assertEquals(TEST_ID, state.getId());
            assertEquals(TEST_NAME, state.getName());
            assertEquals(TEST_DESCRIPTION, state.getDescription());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTest {

        @Test
        @DisplayName("Should set and get all fields correctly")
        void shouldSetAndGetAllFields() {
            // Arrange
            State state = new State();

            // Act
            state.setId(TEST_ID);
            state.setName(TEST_NAME);
            state.setDescription(TEST_DESCRIPTION);

            // Assert
            assertEquals(TEST_ID, state.getId());
            assertEquals(TEST_NAME, state.getName());
            assertEquals(TEST_DESCRIPTION, state.getDescription());
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void shouldHandleNullValues() {
            // Arrange
            State state = State.builder()
                    .id(TEST_ID)
                    .name(TEST_NAME)
                    .build();

            // Act
            state.setDescription(null);

            // Assert
            assertEquals(TEST_ID, state.getId());
            assertEquals(TEST_NAME, state.getName());
            assertNull(state.getDescription());
        }
    }

    @Nested
    @DisplayName("Object Behavior Tests")
    class ObjectBehaviorTest {

        @Test
        @DisplayName("Should generate meaningful toString")
        void shouldGenerateMeaningfulToString() {
            // Arrange
            State state = State.builder()
                    .id(TEST_ID)
                    .name(TEST_NAME)
                    .description(TEST_DESCRIPTION)
                    .build();

            // Act
            String toString = state.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("State"));
            assertTrue(toString.contains(TEST_NAME));
            assertTrue(toString.contains(TEST_DESCRIPTION));
        }

        @Test
        @DisplayName("Should have consistent equals and hashCode")
        void shouldHaveConsistentEqualsAndHashCode() {
            // Arrange
            State state1 = State.builder()
                    .id(TEST_ID)
                    .name(TEST_NAME)
                    .description(TEST_DESCRIPTION)
                    .build();

            State state2 = State.builder()
                    .id(TEST_ID)
                    .name(TEST_NAME)
                    .description(TEST_DESCRIPTION)
                    .build();

            State state3 = State.builder()
                    .id(2L)
                    .name(TEST_NAME)
                    .description(TEST_DESCRIPTION)
                    .build();

            // Assert
            assertEquals(state1, state2);
            assertEquals(state1.hashCode(), state2.hashCode());
            assertNotEquals(state1, state3);
            assertNotEquals(state1.hashCode(), state3.hashCode());
        }

        @Test
        @DisplayName("Should handle equals with null values")
        void shouldHandleEqualsWithNullValues() {
            // Arrange
            State state1 = new State();
            State state2 = new State();

            // Assert
            assertEquals(state1, state2);
            assertEquals(state1.hashCode(), state2.hashCode());
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTest {

        @Test
        @DisplayName("Should handle different state types")
        void shouldHandleDifferentStateTypes() {
            // Arrange & Act
            State pending = State.builder().name("Pending review").build();
            State approved = State.builder().name("Approved").build();
            State rejected = State.builder().name("Rejected").build();
            State cancelled = State.builder().name("Cancelled").build();

            // Assert
            assertEquals("Pending review", pending.getName());
            assertEquals("Approved", approved.getName());
            assertEquals("Rejected", rejected.getName());
            assertEquals("Cancelled", cancelled.getName());
        }

        @Test
        @DisplayName("Should handle state descriptions")
        void shouldHandleStateDescriptions() {
            // Arrange
            String longDescription = "This is a very detailed description of the state that explains what it means and when it should be used in the loan application process.";

            // Act
            State state = State.builder()
                    .name(TEST_NAME)
                    .description(longDescription)
                    .build();

            // Assert
            assertEquals(TEST_NAME, state.getName());
            assertEquals(longDescription, state.getDescription());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTest {

        @Test
        @DisplayName("Should handle zero and negative ID values")
        void shouldHandleZeroAndNegativeIdValues() {
            // Act
            State state = State.builder()
                    .id(0L)
                    .name(TEST_NAME)
                    .build();

            // Assert
            assertEquals(0L, state.getId());
            assertEquals(TEST_NAME, state.getName());
        }

        @Test
        @DisplayName("Should handle very large ID values")
        void shouldHandleVeryLargeIdValues() {
            // Act
            State state = State.builder()
                    .id(Long.MAX_VALUE)
                    .name(TEST_NAME)
                    .build();

            // Assert
            assertEquals(Long.MAX_VALUE, state.getId());
            assertEquals(TEST_NAME, state.getName());
        }

        @Test
        @DisplayName("Should handle empty and whitespace strings")
        void shouldHandleEmptyAndWhitespaceStrings() {
            // Act
            State state = State.builder()
                    .name("")
                    .description("   ")
                    .build();

            // Assert
            assertEquals("", state.getName());
            assertEquals("   ", state.getDescription());
        }

        @Test
        @DisplayName("Should handle null description")
        void shouldHandleNullDescription() {
            // Act
            State state = State.builder()
                    .name(TEST_NAME)
                    .description(null)
                    .build();

            // Assert
            assertEquals(TEST_NAME, state.getName());
            assertNull(state.getDescription());
        }

        @Test
        @DisplayName("Should handle very long names and descriptions")
        void shouldHandleVeryLongNamesAndDescriptions() {
            // Arrange
            String longName = "A".repeat(255); // Max VARCHAR length
            String longDescription = "A".repeat(1000);

            // Act
            State state = State.builder()
                    .name(longName)
                    .description(longDescription)
                    .build();

            // Assert
            assertEquals(longName, state.getName());
            assertEquals(longDescription, state.getDescription());
        }
    }
}