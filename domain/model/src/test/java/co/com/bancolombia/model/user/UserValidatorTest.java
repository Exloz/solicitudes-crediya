package co.com.bancolombia.model.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserValidator Interface Tests")
class UserValidatorTest {

    @Test
    @DisplayName("Should be a valid interface")
    void shouldBeAValidInterface() {
        // Verify that UserValidator is indeed an interface
        Class<?> userValidatorClass = UserValidator.class;

        assertTrue(userValidatorClass.isInterface());
        assertEquals("co.com.bancolombia.model.user.UserValidator", userValidatorClass.getName());
    }

    @Test
    @DisplayName("Should have expected method count")
    void shouldHaveExpectedMethodCount() {
        // Verify that the interface has the expected number of methods
        Class<?> userValidatorClass = UserValidator.class;

        // Should have 2 methods: validateUserInfo and validateUserIdMatch
        Method[] methods = userValidatorClass.getMethods();
        assertEquals(2, methods.length);

        // Verify method names
        List<String> methodNames = Arrays.stream(methods)
                .map(Method::getName)
                .collect(Collectors.toList());

        assertTrue(methodNames.contains("validateUserInfo"));
        assertTrue(methodNames.contains("validateUserIdMatch"));
    }

    @Test
    @DisplayName("Should have validateUserInfo method with correct signature")
    void shouldHaveValidateUserInfoMethodWithCorrectSignature() {
        // Arrange
        Class<?> userValidatorClass = UserValidator.class;

        // Act
        Method method = findMethodByName(userValidatorClass, "validateUserInfo");

        // Assert
        assertNotNull(method, "validateUserInfo method should exist");
        assertTrue(method.getReturnType().getName().contains("Mono"), "Should return Mono");

        Parameter[] parameters = method.getParameters();
        assertEquals(2, parameters.length, "Should have 2 parameters");

        // Check parameter types (String, String)
        assertEquals(String.class, parameters[0].getType());
        assertEquals(String.class, parameters[1].getType());
    }

    @Test
    @DisplayName("Should have validateUserIdMatch method with correct signature")
    void shouldHaveValidateUserIdMatchMethodWithCorrectSignature() {
        // Arrange
        Class<?> userValidatorClass = UserValidator.class;

        // Act
        Method method = findMethodByName(userValidatorClass, "validateUserIdMatch");

        // Assert
        assertNotNull(method, "validateUserIdMatch method should exist");
        assertTrue(method.getReturnType().getName().contains("Mono"), "Should return Mono");

        Parameter[] parameters = method.getParameters();
        assertEquals(2, parameters.length, "Should have 2 parameters");

        // Check parameter types (String, String)
        assertEquals(String.class, parameters[0].getType());
        assertEquals(String.class, parameters[1].getType());
    }

    @Test
    @DisplayName("Should have methods that return reactive types")
    void shouldHaveMethodsThatReturnReactiveTypes() {
        // Arrange
        Class<?> userValidatorClass = UserValidator.class;

        // Act
        Method[] methods = userValidatorClass.getMethods();

        // Assert
        for (Method method : methods) {
            String returnTypeName = method.getReturnType().getName();
            assertTrue(returnTypeName.contains("Mono") || returnTypeName.contains("Flux"),
                    "Method " + method.getName() + " should return a reactive type");
        }
    }

    @Test
    @DisplayName("Should have methods with String parameters")
    void shouldHaveMethodsWithStringParameters() {
        // Arrange
        Class<?> userValidatorClass = UserValidator.class;

        // Act
        Method[] methods = userValidatorClass.getMethods();

        // Assert
        for (Method method : methods) {
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                assertEquals(String.class, parameter.getType(),
                        "Parameter " + parameter.getName() + " in method " + method.getName() + " should be String");
            }
        }
    }

    @Test
    @DisplayName("Should be able to be implemented by classes")
    void shouldBeAbleToBeImplementedByClasses() {
        // Verify that the interface can be implemented
        Class<?> userValidatorClass = UserValidator.class;

        assertTrue(userValidatorClass.isInterface());
        assertFalse(userValidatorClass.isAnnotation());
        assertFalse(userValidatorClass.isEnum());
    }

    @Test
    @DisplayName("Should have proper interface modifiers")
    void shouldHaveProperInterfaceModifiers() {
        // Arrange
        Class<?> userValidatorClass = UserValidator.class;

        // Act
        int modifiers = userValidatorClass.getModifiers();

        // Assert
        assertTrue(java.lang.reflect.Modifier.isPublic(modifiers), "Interface should be public");
        assertTrue(java.lang.reflect.Modifier.isInterface(modifiers), "Should be an interface");
    }

    @Test
    @DisplayName("Should have methods with proper visibility")
    void shouldHaveMethodsWithProperVisibility() {
        // Arrange
        Class<?> userValidatorClass = UserValidator.class;

        // Act
        Method[] methods = userValidatorClass.getMethods();

        // Assert
        for (Method method : methods) {
            int modifiers = method.getModifiers();
            assertTrue(java.lang.reflect.Modifier.isPublic(modifiers) || java.lang.reflect.Modifier.isAbstract(modifiers),
                    "Method " + method.getName() + " should be public or abstract");
        }
    }

    @Test
    @DisplayName("Should not have default methods")
    void shouldNotHaveDefaultMethods() {
        // Arrange
        Class<?> userValidatorClass = UserValidator.class;

        // Act
        Method[] methods = userValidatorClass.getMethods();

        // Assert
        for (Method method : methods) {
            int modifiers = method.getModifiers();
            assertFalse(java.lang.reflect.Modifier.isDefault(modifiers),
                    "Method " + method.getName() + " should not be a default method");
        }
    }

    @Test
    @DisplayName("Should have methods that are abstract")
    void shouldHaveMethodsThatAreAbstract() {
        // Arrange
        Class<?> userValidatorClass = UserValidator.class;

        // Act
        Method[] methods = userValidatorClass.getMethods();

        // Assert
        for (Method method : methods) {
            int modifiers = method.getModifiers();
            assertTrue(java.lang.reflect.Modifier.isAbstract(modifiers),
                    "Method " + method.getName() + " should be abstract");
        }
    }

    @Test
    @DisplayName("Should have unique method names")
    void shouldHaveUniqueMethodNames() {
        // Arrange
        Class<?> userValidatorClass = UserValidator.class;

        // Act
        Method[] methods = userValidatorClass.getMethods();
        List<String> methodNames = Arrays.stream(methods)
                .map(Method::getName)
                .collect(Collectors.toList());

        // Assert
        assertEquals(methodNames.size(), methodNames.stream().distinct().count(),
                "All method names should be unique");
    }

    @Test
    @DisplayName("Should be in correct package")
    void shouldBeInCorrectPackage() {
        // Arrange
        Class<?> userValidatorClass = UserValidator.class;

        // Act
        Package pkg = userValidatorClass.getPackage();

        // Assert
        assertNotNull(pkg);
        assertEquals("co.com.bancolombia.model.user", pkg.getName());
    }

    // Helper method
    private Method findMethodByName(Class<?> clazz, String methodName) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
}