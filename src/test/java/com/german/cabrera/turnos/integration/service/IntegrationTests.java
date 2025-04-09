package com.german.cabrera.turnos.integration.service;

import com.german.cabrera.turnos.TurnosApplicationTests;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
public abstract class IntegrationTests extends TurnosApplicationTests { }
