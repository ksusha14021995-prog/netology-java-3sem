package com.example.hellospring;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Стартовые данные после инициализации схемы — не в конструкторе сервиса, чтобы не зависеть от порядка создания бинов. */
@Component
public class SampleDataLoader implements ApplicationRunner {
    private final MessageService service;

    public SampleDataLoader(MessageService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) {
        service.resetWithSampleData();
    }
}
