package com.example.hellospring;

import java.time.LocalDateTime;

public record User(Long id, String name, LocalDateTime createdAt) {
}
