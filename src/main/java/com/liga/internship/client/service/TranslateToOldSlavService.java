package com.liga.internship.client.service;

public class TranslateToOldSlavService {

    public String translateTextToOldSlav(String srcTxt) {
        String[] arrStrParts = srcTxt.split(" ");
        int numOfStringParts = arrStrParts.length;
        int numOfSymbolsInStr;

        String tmpWord;
        StringBuilder tmpResult = new StringBuilder();

        int currStrPartNum = 0;

        while (currStrPartNum < numOfStringParts) {
            numOfSymbolsInStr = arrStrParts[currStrPartNum].length();

            if (numOfSymbolsInStr > 2) {
                tmpWord = translateWordToOldSlav(arrStrParts[currStrPartNum]);

            } else if (numOfSymbolsInStr == 1) {
                tmpWord = arrStrParts[currStrPartNum];

                if (arrStrParts[currStrPartNum].equals("В") ||
                        arrStrParts[currStrPartNum].equals("К") ||
                        arrStrParts[currStrPartNum].equals("С")) {
                    tmpWord += "ъ";
                } else if (arrStrParts[currStrPartNum].equals("в") ||
                        arrStrParts[currStrPartNum].equals("к") ||
                        arrStrParts[currStrPartNum].equals("с")) {
                    tmpWord += "ъ";
                }

            } else {
                tmpWord = arrStrParts[currStrPartNum];

            }
            tmpResult.append(tmpWord).append(" ");
            currStrPartNum++;
        }
        return tmpResult.toString();
    }

    public String translateWordToOldSlav(String input) {

        String curChar;
        String newInput = input;

        if (newInput.length() > 2) {
            newInput = newInput.replace("ого", "аго");
            newInput = newInput.replace("eго", "аго");
            newInput = newInput.replace("ая", "ыя");
            newInput = newInput.replace("ее", "ея");
            newInput = newInput.replace("её", "ея");
            newInput = newInput.replace("ие", "iя");
            newInput = newInput.replace("иё", "iя");

            newInput = newInput.replace("ии", "iи");
            newInput = newInput.replace("ий", "iи");

            newInput = newInput.replace("иу", "iу");
            newInput = newInput.replace("иы", "iы");
            newInput = newInput.replace("иа", "iа");
            newInput = newInput.replace("ио", "iо");
            newInput = newInput.replace("иэ", "iэ");
            newInput = newInput.replace("ию", "iю");

            newInput = newInput.replace("бе", "бѣ");
            newInput = newInput.replace("ве", "вѣ");
            newInput = newInput.replace("вё", "вѣ");
            newInput = newInput.replace("де", "дѣ");
            newInput = newInput.replace("же", "жѣ");
            newInput = newInput.replace("зе", "зѣ");
            newInput = newInput.replace("ке", "кѣ");
            newInput = newInput.replace("ле", "лѣ");
            newInput = newInput.replace("не", "нѣ");
            newInput = newInput.replace("ме", "мѣ");
            newInput = newInput.replace("пе", "пѣ");
            newInput = newInput.replace("ре", "рѣ");
            newInput = newInput.replace("рё", "рѣ");
            newInput = newInput.replace("тё", "тѣ");
            newInput = newInput.replace("те", "тѣ");
            newInput = newInput.replace("се", "сѣ");
            newInput = newInput.replace("це", "цѣ");

            newInput = newInput.replace("Бе", "Бѣ");
            newInput = newInput.replace("Ве", "Вѣ");
            newInput = newInput.replace("Вё", "Вѣ");
            newInput = newInput.replace("Де", "Дѣ");
            newInput = newInput.replace("Же", "Жѣ");
            newInput = newInput.replace("Зе", "Зѣ");
            newInput = newInput.replace("Ке", "Кѣ");
            newInput = newInput.replace("Ле", "Лѣ");
            newInput = newInput.replace("Не", "Нѣ");
            newInput = newInput.replace("Ме", "Мѣ");
            newInput = newInput.replace("Мё", "Мѣ");
            newInput = newInput.replace("Пе", "Пѣ");
            newInput = newInput.replace("Ре", "Рѣ");
            newInput = newInput.replace("Рё", "Рѣ");
            newInput = newInput.replace("Се", "Сѣ");
            newInput = newInput.replace("Те", "Тѣ");
            newInput = newInput.replace("Тё", "Тѣ");
            newInput = newInput.replace("Це", "Цѣ");

            newInput = newInput.replace("Ед", "ѣд");
            newInput = newInput.replace("ед", "ѣд");

            newInput = newInput.replace("ри", "рi");
            newInput = newInput.replace("ни", "нi");
        }

        String lastChar = String.valueOf(newInput.charAt(newInput.length() - 1));

        if (lastChar.equals(".") ||
                lastChar.equals(",") ||
                lastChar.equals(";") ||
                lastChar.equals(":") ||
                lastChar.equals("!") ||
                lastChar.equals("?") ||
                lastChar.equals(")") ||
                lastChar.equals("]") ||
                lastChar.equals("»") ||
                lastChar.equals("\"")) {

            newInput = newInput.substring(0, (newInput.length() - 1));
            curChar = String.valueOf(newInput.charAt(newInput.length() - 1));

        } else {
            curChar = lastChar;
            lastChar = "";
        }

        if (curChar.equals("б") ||
                curChar.equals("в") ||
                curChar.equals("г") ||
                curChar.equals("д") ||
                curChar.equals("ж") ||
                curChar.equals("з") ||
                curChar.equals("к") ||
                curChar.equals("л") ||
                curChar.equals("м") ||
                curChar.equals("н") ||
                curChar.equals("п") ||
                curChar.equals("р") ||
                curChar.equals("с") ||
                curChar.equals("т") ||
                curChar.equals("ф") ||
                curChar.equals("х") ||
                curChar.equals("ц") ||
                curChar.equals("ч") ||
                curChar.equals("ш") ||
                curChar.equals("щ")) {
            newInput += "ъ";
        } else if (curChar.equals("ы")) {
            newInput = newInput.substring(0, (newInput.length() - 1)) + "ъ";
        }
        newInput += lastChar;

        return newInput;
    }

    public static void main(String[] args) {
        TranslateToOldSlavService translateToOldSlavService = new TranslateToOldSlavService();
        String input = "Найду ли в прекрасной, чарующей улыбке, в тихом сиянии ее глаз оправдание, разгадку мучительного существования?";
        System.out.println(translateToOldSlavService.translateTextToOldSlav(translateToOldSlavService.translateWordToOldSlav(input)));
        System.out.println();
        System.out.println(input);
    }
}
