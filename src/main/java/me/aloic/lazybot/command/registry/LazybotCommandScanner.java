//package me.aloic.lazybot.discord.command.registry;
//
//import java.util.Set;
//
//import me.aloic.lazybot.annotation.LazybotCommandMapping;
//import me.aloic.lazybot.discord.command.LazybotSlashCommand;
//import me.aloic.lazybot.osu.service.PlayerService;
//import me.aloic.lazybot.osu.service.UserService;
//import org.reflections.Reflections;
//import org.springframework.stereotype.Component;
//
//@Component
//public class LazybotCommandScanner
//{
//    public static LazybotSlashCommandRegistry scanCommands(String packageName, UserService userService, PlayerService playerService) throws Exception {
//        LazybotSlashCommandRegistry registry = new LazybotSlashCommandRegistry();
//        Reflections reflections = new Reflections(packageName);
//        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(LazybotCommandMapping.class);
//
//        for (Class<?> clazz : classes) {
//            LazybotCommandMapping annotation = clazz.getAnnotation(LazybotCommandMapping.class);
//            LazybotSlashCommand command = (LazybotSlashCommand) clazz.getConstructor(UserService.class, PlayerService.class)
//                    .newInstance(userService, playerService);
//            registry.register(annotation.value(), command);
//        }
//
//        return registry;
//    }
//}
