package com.app.extractor.core;

import java.io.File;
import java.io.IOException;

/**
 * Базовий інтерфейс для обробки документів.
 * Використовується для забезпечення принципу інверсії залежностей (DIP).
 */
public interface DocumentProcessor {
    String extractText(File file) throws IOException;
}