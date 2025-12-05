package com.example.encuestas_api.forms.applicaction.usecase;

import com.example.encuestas_api.forms.application.port.out.DeleteFormPort;
import com.example.encuestas_api.forms.application.usecase.DeleteFormService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class DeleteFormServiceTest {

    @Mock
    private DeleteFormPort deleteFormPort;

    @InjectMocks
    private DeleteFormService service;

    @Test
    void testDeleteFormSuccessfully() {
        // Arrange
        Long formId = 1L;

        // Act
        service.handle(formId);

        // Assert
        verify(deleteFormPort).deleteById(formId);
    }

    @Test
    void testDeleteFormWithNullId() {
        // Arrange
        Long nullFormId = null;

        // Act
        service.handle(nullFormId);

        // Assert
        verify(deleteFormPort).deleteById(nullFormId);
    }

    @Test
    void testPropagateExceptionFromDeletePort() {
        // Arrange
        Long formId = 1L;
        RuntimeException expectedException = new RuntimeException("Delete failed");
        doThrow(expectedException).when(deleteFormPort).deleteById(formId);

        // Act & Assert
        try {
            service.handle(formId);
            throw new AssertionError("Expected exception was not thrown");
        } catch (RuntimeException e) {
            if (e != expectedException) {
                throw new AssertionError("Different exception was thrown");
            }
        }

        verify(deleteFormPort).deleteById(formId);
    }
}