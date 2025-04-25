package me.aloic.lazybot.controller;

import jakarta.annotation.Resource;
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
    @Resource
    private SlashCommandProcessor slashCommandProcessor;
    @Resource
    private MessageEventFactory messageEventFactory;
    @Value("${lazybot.test.identity}")
    private Long identity;
    @Value("${lazybot.test.enabled}")
    private Boolean testEnabled;

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
