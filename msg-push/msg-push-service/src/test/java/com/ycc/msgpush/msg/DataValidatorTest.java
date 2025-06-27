package com.ycc.msgpush.msg;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataValidatorTest {

    @Mock
    private MessageValidator mockNextValidator;

    private DataValidator dataValidatorUnderTest;

    @BeforeEach
    void setUp() {
        dataValidatorUnderTest = new DataValidator();
        dataValidatorUnderTest.setNextValidator(mockNextValidator);
    }

    @Test
    void testValidate() {
        // Setup
        final MessageRequest request = new MessageRequest("messageContent");
        when(mockNextValidator.validate(any(MessageRequest.class))).thenReturn(false);

        // Run the test
        final boolean result = dataValidatorUnderTest.validate(request);

        // Verify the results
        assertThat(result).isFalse();
    }

    @Test
    void testValidate_MessageValidatorReturnsTrue() {
        // Setup
        final MessageRequest request = new MessageRequest("messageContent");
        when(mockNextValidator.validate(any(MessageRequest.class))).thenReturn(true);

        // Run the test
        final boolean result = dataValidatorUnderTest.validate(request);

        // Verify the results
        assertThat(result).isTrue();
    }
}
