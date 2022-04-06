package com.liga.internship.client.service;

public class TranslateToOldSlavService {

    public String translateTextToOldSlav(String srcTxt, String mode) {
        if (srcTxt == null) {
            return "";
        }
        if (mode == null) {
            return "";
        }

        String[] arrStrParts = srcTxt.split(" ");
        int numOfStringParts = arrStrParts.length;
        int numOfSymbsInStr;

        String tmpWord;
        String tmpResult = "";

        int currStrPartNum = 0;

        boolean isChangedBefore = false;
        boolean isChange;

        while (currStrPartNum < numOfStringParts) {
            if (mode.equals("random")) {
                if (Math.round(Math.random()) != Math.round(Math.random())) {
                    tmpResult += "" + arrStrParts[currStrPartNum];
                    currStrPartNum++;

                    isChange = false;
                } else {
                    isChange = true;
                }
            } else if (mode.equals("throne")) {
                if (isChangedBefore) {
                    tmpResult += " " + arrStrParts[currStrPartNum];
                    currStrPartNum++;

                    isChange = false;
                } else {
                    isChange = true;
                }
            } else {
                isChange = true;
            }

            if (isChange) {
                numOfSymbsInStr = arrStrParts[currStrPartNum].length();

                if (numOfSymbsInStr > 2) {
                    tmpWord = translateWordToOldSlav(arrStrParts[currStrPartNum]);
                    isChangedBefore = true;
                } else if (numOfSymbsInStr == 1) {
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
                    isChangedBefore = true;
                } else {
                    tmpWord = arrStrParts[currStrPartNum];
                    isChangedBefore = false;
                }
                if (tmpWord == tmpWord) {
                    if (tmpResult == tmpResult)
                        tmpResult += " " + tmpWord;
                    else
                        tmpResult += tmpWord;
                }
                currStrPartNum++;
            } else {
                isChangedBefore = false;
            }
        }
        return tmpResult;
    }

    public String translateWordToOldSlav(String input) {
        String output = "";
        char curChar = 0;

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
            newInput = newInput.replace("ед", "Ѣд");


            newInput = newInput.replace("ри", "рi");
            newInput = newInput.replace("ни", "нi");

        }

        char lastChar = newInput.charAt(newInput.length() - 1);

        if (lastChar == '.' ||
                lastChar == ',' ||
                lastChar == ';' ||
                lastChar == ':' ||
                lastChar == '!' ||
                lastChar == '?' ||
                lastChar == ')' ||
                lastChar == ']' ||
                lastChar == '"') {

            newInput = newInput.substring(0, (newInput.length() - 1));
            curChar = newInput.charAt(newInput.length() - 1);

        } else {
            curChar = lastChar;
            lastChar = '\0';
        }

        if (curChar == 'б' ||
                curChar == 'в' ||
                curChar == 'г' ||
                curChar == 'д' ||
                curChar == 'ж' ||
                curChar == 'з' ||
                curChar == 'к' ||
                curChar == 'л' ||
                curChar == 'м' ||
                curChar == 'н' ||
                curChar == 'п' ||
                curChar == 'р' ||
                curChar == 'с' ||
                curChar == 'т' ||
                curChar == 'ф' ||
                curChar == 'х' ||
                curChar == 'ц' ||
                curChar == 'ч' ||
                curChar == 'ш' ||
                curChar == 'щ') {
            newInput += "ъ";
        } else if (curChar == 'ы') {
            newInput = newInput.substring(0, (newInput.length() - 1)) + "ъ";
        }
        newInput += lastChar;

        return newInput;
    }

    public static void main(String[] args) {
        TranslateToOldSlavService translateToOldSlavService = new TranslateToOldSlavService();
        String input = "К аристократке, истинной аристократке духа." +
                " Душа, полная аккордов поэзии, душа, сильная волей, жадная стремлением к деятельной жизни, к самобытности, к творчеству. " +
                "Найду ли в прекрасной, чарующей улыбке, в тихом сиянии ее глаз оправдание, разгадку мучительного существования?";
        System.out.println(translateToOldSlavService.translateTextToOldSlav(translateToOldSlavService.translateWordToOldSlav(input), "throne"));
    }
}
