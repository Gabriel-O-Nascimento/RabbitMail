package com.rabbitmail;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RabbitMailApplicationTests {

    @Test
    void applicationClassExists() {
        assertThat(RabbitMailApplication.class).isNotNull();
    }
}
