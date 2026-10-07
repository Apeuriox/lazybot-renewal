package me.aloic.lazybot.controller;

import me.aloic.lazybot.component.SlashCommandProcessor;
import me.aloic.lazybot.entity.WebResult;
import me.aloic.lazybot.shiro.utils.MessageEventFactory;
import me.aloic.lazybot.util.ResultUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/test")
public class TestCaseController
{
    private final SlashCommandProcessor slashCommandProcessor;
    private final MessageEventFactory messageEventFactory;
    private final Long identity;
    private final Boolean testEnabled;

    public TestCaseController(SlashCommandProcessor slashCommandProcessor,
                              MessageEventFactory messageEventFactory,
                              @Value("${lazybot.test.identity}") Long identity,
                              @Value("${lazybot.test.enabled}") Boolean testEnabled)
    {
        this.slashCommandProcessor = slashCommandProcessor;
        this.messageEventFactory = messageEventFactory;
        this.identity = identity;
        this.testEnabled = testEnabled;
    }

    @GetMapping("/command")
    public WebResult testCommand(@RequestParam(value = "command", required = true) String command)
    {
        if (testEnabled){
            return ResultUtil.success(slashCommandProcessor.processTest(messageEventFactory.setupSlashCommandEvent(command)));
        }
        else {
            return ResultUtil.error("test not enabled");
        }
    }
}
