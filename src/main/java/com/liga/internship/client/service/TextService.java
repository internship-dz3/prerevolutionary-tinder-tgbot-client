package com.liga.internship.client.service;

public interface TextService {
    /**
     * Метод возвращающий переведенный текст на старославянский
     *
     * @param text - вводимый текст на русском языке
     * @return переведнный текст согласно условиям
     */
    String translateTextIntoSlavOld(String text);
}
