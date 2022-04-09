package com.liga.internship.client.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TranslateToOldSlavServiceTest {

    @Test
    void translateTextToOldSlav() {
        TranslateToOldSlavService translateToOldSlavService = new TranslateToOldSlavService();
        String input = "Найду ли в прекрасной, чарующей улыбке, в тихом сиянии ее глаз оправдание, разгадку мучительного существования?";
        String actual = translateToOldSlavService.translateTextToOldSlav(input);
        String expected = "Найду ли въ прѣкрасной, чарующей улыбкѣ, въ тихомъ сиянiи ея глазъ оправданiя, разгадку мучитѣльнаго существованiя?";
        assertEquals(expected, actual);
    }

    @Test
    void translateWordToOldSlav() {
        TranslateToOldSlavService translateToOldSlavService = new TranslateToOldSlavService();
        String input = "аристократке";
        String actual = translateToOldSlavService.translateWordToOldSlav(input);
        String expected = "арiстократкѣ";
        assertEquals(expected, actual);
    }
}