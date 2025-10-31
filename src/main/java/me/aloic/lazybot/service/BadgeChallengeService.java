package me.aloic.lazybot.service;

import me.aloic.lazybot.entity.message.LazybotMessageWithImage;
import me.aloic.lazybot.parameter.ChallengeSubmitParameter;

import java.io.IOException;
import java.util.List;

public interface BadgeChallengeService
{
    String checkUserSubmit(ChallengeSubmitParameter params);

    List<LazybotMessageWithImage> showAllActiveChallenges() throws IOException;
}
