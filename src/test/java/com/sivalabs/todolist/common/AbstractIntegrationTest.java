package com.sivalabs.todolist.common;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {PostgreSQLContainerInitializer.class})
public abstract class AbstractIntegrationTest {
}
