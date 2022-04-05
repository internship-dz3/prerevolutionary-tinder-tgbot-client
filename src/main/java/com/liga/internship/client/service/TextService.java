package com.liga.internship.client.service;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TextService {

    public String translateTextIntoSlavOld(String text) {
        List<String> list = List.of(text.split(" "));
        List<String> modifiedList = list.stream().map(this::modifyEndOfTheWord).collect(Collectors.toList());
        StringBuilder result = new StringBuilder();
        modifiedList.forEach(result::append);
        return replaceEandI(new StringBuilder(result)).toString();
    }

    private String modifyEndOfTheWord(String word) {

        List<String> modifiableLetters = List.of("б", "в", "г", "д", "з", "к", "л", "м", "н", "п", "р", "с", "т", "ф", "х");
        String symbol = getLastSymbolIfExists(word);
        if (!symbol.equals("")) {
            word = removeLastChar(word);
        }
        if(word == null || word.isBlank()) {
            return "";
        }
        if (modifiableLetters.contains(String.valueOf(word.charAt(word.length() - 1)))) {
            return word + "ъ" + symbol + " ";
        } else
            return word + " ";
    }

    private String removeLastChar(String s) {
        return (s == null || s.length() == 0) ? null : (s.substring(0, s.length() - 1));
    }

    private String getLastSymbolIfExists(String word) {
        if(word.isEmpty()) {
            return "";
        }
        List<String> symbols = List.of("?", ":", ";", "-", " ");
        String lastChar = String.valueOf(word.charAt(word.length() - 1));
        if (symbols.contains(lastChar)) {
            return lastChar;
        } else
            return "";
    }

    private StringBuilder replaceEandI(StringBuilder text) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == 'е') {
                text.setCharAt(i, 'ѣ');
            }
            if (text.charAt(i) == 'Е') {
                text.setCharAt(i, 'Ѣ');
            }
            if (text.charAt(i) == 'и') {
                text.setCharAt(i, 'i');
            }
        }
        return text;
    }

}

