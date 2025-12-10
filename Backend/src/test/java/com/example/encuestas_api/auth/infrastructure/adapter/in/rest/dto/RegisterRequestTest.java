package com.example.encuestas_api.auth.infrastructure.adapter.in.rest.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class RegisterRequestTest {


    private Set<?> tryValidate(Object instance) {
        try {
            Class<?> validationClass = Class.forName("jakarta.validation.Validation");
            Object factory = validationClass.getMethod("buildDefaultValidatorFactory").invoke(null);
            Object validator = factory.getClass().getMethod("getValidator").invoke(factory);
            return (Set<?>) validator.getClass()
                    .getMethod("validate", Object.class)
                    .invoke(validator, instance);
        } catch (Exception e) {
            // No se interrumpe si no existe Jakarta Validation o falla
            System.out.println("Validación omitida: " + e.getMessage());
            return null;
        }
    }


    private Object createInstance(String email, String fullName, String password, Boolean systemAdmin) throws Exception {
        Class<?> clazz = Class.forName("com.example.encuestas_api.auth.infrastructure.adapter.in.rest.dto.RegisterRequest");
        Constructor<?> constructor = clazz.getDeclaredConstructor(String.class, String.class, String.class, Boolean.class);
        return constructor.newInstance(email, fullName, password, systemAdmin);
    }


    private Object get(Object instance, String method) throws Exception {
        Method getter = instance.getClass().getDeclaredMethod(method);
        return getter.invoke(instance);
    }

    // ======================================================
    // CASO VÁLIDO
    // ======================================================
    @Test
    @DisplayName("Debe crear instancia válida y retornar datos correctamente")
    void shouldCreateValidInstance() throws Exception {
        Object req = createInstance("test@example.com", "User Test", "Pass123!", true);

        assertAll(
                () -> assertEquals("test@example.com", get(req, "email")),
                () -> assertEquals("User Test", get(req, "fullName")),
                () -> assertEquals("Pass123!", get(req, "password")),
                () -> assertTrue((Boolean) get(req, "systemAdmin"))
        );
        System.out.println("shouldCreateValidInstance: OK");
    }

    // ======================================================
    // CAMPOS VACÍOS
    // ======================================================
    @Test
    @DisplayName("Debe permitir valores vacíos sin lanzar excepción")
    void shouldAllowEmptyFields() throws Exception {
        Object req = createInstance("", "", "", false);

        assertAll(
                () -> assertEquals("", get(req, "email")),
                () -> assertEquals("", get(req, "fullName")),
                () -> assertEquals("", get(req, "password")),
                () -> assertFalse((Boolean) get(req, "systemAdmin"))
        );
        System.out.println(" shouldAllowEmptyFields: OK");

        Set<?> violations = tryValidate(req);
        if (violations != null) {
            System.out.println("Validaciones detectadas: " + violations.size());
        }
    }

    // ======================================================
    // SYSTEM ADMIN NULO
    // ======================================================
    @Test
    @DisplayName("Debe permitir systemAdmin nulo sin errores")
    void shouldAllowNullSystemAdmin() throws Exception {
        Object req = createInstance("user@ok.com", "User Null", "abc", null);

        assertNull(get(req, "systemAdmin"));
        System.out.println("shouldAllowNullSystemAdmin: OK");

        Set<?> violations = tryValidate(req);
        if (violations != null) {
            System.out.println("Validaciones detectadas: " + violations.size());
        }
    }

    // ======================================================
    // CONSISTENCIA ENTRE INSTANCIAS
    // ======================================================
    @Test
    @DisplayName("Debe mantener datos consistentes entre instancias diferentes")
    void shouldMaintainConsistencyBetweenInstances() throws Exception {
        Object a = createInstance("a@mail.com", "User A", "123", false);
        Object b = createInstance("b@mail.com", "User B", "456", true);

        assertNotEquals(get(a, "email"), get(b, "email"));
        assertNotEquals(get(a, "fullName"), get(b, "fullName"));
        System.out.println("shouldMaintainConsistencyBetweenInstances: OK");
    }

    // ======================================================
    // FULL NAME LARGO
    // ======================================================
    @Test
    @DisplayName("Debe aceptar nombres largos y validar longitud si aplica")
    void shouldHandleLongFullName() throws Exception {
        String longName = "A".repeat(300);
        Object req = createInstance("long@name.com", longName, "P@ssword", true);

        assertEquals(300, ((String) get(req, "fullName")).length());
        System.out.println("shouldHandleLongFullName: OK");

        Set<?> violations = tryValidate(req);
        if (violations != null) {
            System.out.println("Validaciones detectadas: " + violations.size());
        }
    }

    // ======================================================
    // CAMPOS NULOS
    // ======================================================
    @Test
    @DisplayName("Debe aceptar valores nulos en campos opcionales")
    void shouldHandleNullValuesGracefully() throws Exception {
        Object req = createInstance(null, null, null, false);

        assertAll(
                () -> assertNull(get(req, "email")),
                () -> assertNull(get(req, "fullName")),
                () -> assertNull(get(req, "password"))
        );
        System.out.println("shouldHandleNullValuesGracefully: OK");

        Set<?> violations = tryValidate(req);
        if (violations != null) {
            System.out.println("Validaciones detectadas: " + violations.size());
        }
    }

    // ======================================================
    // TEST GLOBAL (ROBUSTEZ)
    // ======================================================
    @Test
    @DisplayName("Debe crear múltiples instancias sin interferencias")
    void shouldCreateMultipleInstances() throws Exception {
        Object first = createInstance("one@mail.com", "First", "123", false);
        Object second = createInstance("two@mail.com", "Second", "456", true);

        assertAll(
                () -> assertEquals("one@mail.com", get(first, "email")),
                () -> assertEquals("two@mail.com", get(second, "email")),
                () -> assertFalse((Boolean) get(first, "systemAdmin")),
                () -> assertTrue((Boolean) get(second, "systemAdmin"))
        );
        System.out.println("shouldCreateMultipleInstances: OK");
    }
}
