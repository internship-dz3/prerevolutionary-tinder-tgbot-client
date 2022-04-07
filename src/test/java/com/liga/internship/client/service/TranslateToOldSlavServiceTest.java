package com.liga.internship.client.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TranslateToOldSlavServiceTest {

    @Test
    void translateTextToOldSlav() {
        TranslateToOldSlavService translateToOldSlavService = new TranslateToOldSlavService();
        String input = "К аристократке, истинной аристократке духа. " +
                "Душа, полная аккордов поэзии, душа, сильная волей, жадная стремлением к деятельной жизни, к самобытности, к творчеству." +
                " Найду ли в прекрасной, чарующей улыбке, в тихом сиянии ее глаз оправдание, разгадку мучительного существования?";
        String actual = translateToOldSlavService.translateTextToOldSlav(input, "");
        String expected = "Къ арiстократкѣ, истинной арiстократкѣ духа." +
                " Душа, полныя аккордовъ поэзiи, душа, сильныя волѣй, жадныя стрѣмлѣнiямъ къ дѣятѣльной жизнi, къ самобытности, къ творчеству." +
                " Найду ли въ прѣкрасной, чарующей улыбкѣ, въ тихомъ сиянiи ея глазъ оправданiя, разгадку мучитѣльнаго существованiя?";
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