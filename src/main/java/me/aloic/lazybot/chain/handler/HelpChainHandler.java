package me.aloic.lazybot.chain.handler;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.chain.model.CommandHandlerChain;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.graphics.service.RasterizationService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
public class HelpChainHandler implements CommandHandlerInterface {
    private static final Logger logger = LoggerFactory.getLogger(HelpChainHandler.class);

    private final TestOutputTool testOutputTool;
    private final RasterizationService rasterizationService;

    public HelpChainHandler(TestOutputTool testOutputTool,
                            RasterizationService rasterizationService)
    {
        this.testOutputTool = testOutputTool;
        this.rasterizationService = rasterizationService;
    }

    @Override
    public void handle(LazybotSlashCommandEvent event, LazybotSlashCommand command, CommandHandlerChain chain) throws Exception {
        if (!isHelpRequest(event.getCommandParameters())) {
            chain.doHandle(event, command);
            return;
        }
        byte[] image = helpImage(command, event.getCommandParameters());
        if (image == null) {
            testOutputTool.writeStringToFile(command.getHelp());
            return;
        }
        testOutputTool.saveImageToLocal(image);
    }

    @Override
    public void handle(Bot bot, LazybotSlashCommand command, LazybotSlashCommandEvent event, CommandHandlerChain chain) throws Exception
    {
        if (!isHelpRequest(event.getCommandParameters())) {
            chain.doHandle(bot, event, command);
            return;
        }
        byte[] image = helpImage(command, event.getCommandParameters());
        if (image == null) {
            bot.sendGroupMsg(event.getMessageEvent().getGroupId(), command.getHelp(), false);
            return;
        }
        CommandResultHandler.uploadImageToOnebot(bot, event, image);
    }

    private byte[] helpImage(LazybotSlashCommand command, List<String> parameters)
    {
        if (wantsText(parameters)) {
            return null;
        }
        CommandHelp help = command.commandHelp();
        if (help == null) {
            return null;
        }
        try {
            return rasterizationService.renderCommandHelp(help);
        }
        catch (RuntimeException exception) {
            logger.error("Failed to render help image for {}", help.getCommand(), exception);
            return null;
        }
    }

    private static boolean isHelpRequest(List<String> parameters)
    {
        if (parameters == null || parameters.isEmpty()) {
            return false;
        }
        String head = parameters.getFirst();
        return "*help".equalsIgnoreCase(head)
                || "*h".equalsIgnoreCase(head)
                || "*ht".equalsIgnoreCase(head);
    }

    private static boolean wantsText(List<String> parameters)
    {
        String head = parameters.getFirst();
        if ("*ht".equalsIgnoreCase(head)) {
            return true;
        }
        if (parameters.size() < 2) {
            return false;
        }
        String mode = parameters.get(1);
        return "text".equalsIgnoreCase(mode) || "txt".equalsIgnoreCase(mode);
    }
}
