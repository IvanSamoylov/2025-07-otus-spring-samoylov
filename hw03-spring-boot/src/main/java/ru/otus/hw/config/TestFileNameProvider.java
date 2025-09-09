package ru.otus.hw.config;

import org.springframework.stereotype.Service;

@Service
public interface TestFileNameProvider {
    String getTestFileName();
}
