//package me.aloic.lazybot;
//
//import jakarta.annotation.Resource;
//import me.aloic.lazybot.component.SlashCommandProcessor;
//import me.aloic.lazybot.shiro.utils.MessageEventFactory;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Scanner;
//
//@SpringBootTest
//class LazybotJdaApplicationTests
//{
//    @Resource
//    private SlashCommandProcessor slashCommandProcessor;
//    @Resource
//    private MessageEventFactory messageEventFactory;
//
//    @Test
//    void commandTest()
//    {
//        while (true)
//        {
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("Enter command: ");
//            String command = scanner.nextLine();
//            slashCommandProcessor.processTest(messageEventFactory.setupSlashCommandEvent(command));
//        }
//
//    }
//
//}
