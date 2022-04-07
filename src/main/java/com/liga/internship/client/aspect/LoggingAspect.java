package com.liga.internship.client.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;


@Aspect
@Slf4j
public class LoggingAspect {

    private static final String FILENAME = "G:\\GitHub\\prerevolutionary-tinder-tgbot-client\\slf4jlogbackexample.log";

    @After("execution(* *(com.liga.internship.client.bot.handler,..))")
    public static void infoLogAfter(JoinPoint joinPoint) {
        log.info("Entering in Method :  " + joinPoint.getSignature().getName());
        log.info("Class Name :  " + joinPoint.getSignature().getDeclaringTypeName());
        log.info("Arguments :  " + Arrays.toString(joinPoint.getArgs()));
        log.info("Target class : " + joinPoint.getTarget().getClass().getName());
        try {
            Files.readAllBytes(Paths.get(FILENAME));
        } catch (IOException ioex) {
            log.error("Failed to read file {}.", FILENAME, ioex);
        }
    }

    @After("execution(* *(com.liga.internship.client.service,..))")
    public static void debugLogAfter(JoinPoint joinPoint) {
        log.debug("Entering in Method :  " + joinPoint.getSignature().getName());
        log.debug("Class Name :  " + joinPoint.getSignature().getDeclaringTypeName());
        log.debug("Arguments :  " + Arrays.toString(joinPoint.getArgs()));
        log.debug("Target class : " + joinPoint.getTarget().getClass().getName());
        try {
            Files.readAllBytes(Paths.get(FILENAME));
        } catch (IOException ioex) {
            log.error("Failed to read file {}.", FILENAME, ioex);
        }

    }

}
