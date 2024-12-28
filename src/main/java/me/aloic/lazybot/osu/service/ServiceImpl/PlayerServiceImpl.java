package me.aloic.lazybot.osu.service.ServiceImpl;

import jakarta.annotation.Resource;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.player.BeatmapUserScoreLazer;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.utils.OsuToolsUtil;
import me.aloic.lazybot.osu.utils.SVGRenderUtil;
import me.aloic.lazybot.util.DataObjectExtractor;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService
{
    @Resource
    private TokenMapper tokenMapper;

    @Override
    public void score(SlashCommandInteractionEvent event) throws Exception {
        event.deferReply().queue();
        UserTokenPO accessToken=tokenMapper.selectByDiscord(0L);
        UserTokenPO tokenPO = tokenMapper.selectByDiscord(event.getUser().getIdLong());
        if (tokenPO == null) {
            ErrorResultHandler.createNotBindOsuError(event);
            return;
        }
        tokenPO.setAccess_token(accessToken.getAccess_token());
        String modCombination = OptionMappingTool.getOptionOrDefault(event.getOption("mod"), "");
        String playerName = OptionMappingTool.getOptionOrDefault(event.getOption("user"), tokenPO.getPlayer_name());
        String beatmapId=Optional.ofNullable(event.getOption("bid")).orElseThrow(() -> new RuntimeException("bid为必选参数")).getAsString();
        String mode= OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe();
        Integer version = OptionMappingTool.getOptionOrDefault(event.getOption("version"), 1);
        Integer playerId= OsuToolsUtil.getUserIdByUsername(playerName,tokenPO);

        BeatmapUserScoreLazer beatmapUserScoreLazer = DataObjectExtractor.extractBeatmapUserScore(accessToken.getAccess_token(),
                beatmapId,playerId,mode,modCombination);
        ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(DataObjectExtractor.extractBeatmap(accessToken.getAccess_token(),beatmapId,mode),beatmapUserScoreLazer.getScore());
        ImageUploadUtil.uploadImage(event, SVGRenderUtil.renderScoreToByteArray(scoreVO,version));
    }


    @Override
    public void recent(SlashCommandInteractionEvent event, Integer type){
        event.deferReply().queue();
        UserTokenPO accessToken=tokenMapper.selectByDiscord(0L);
        UserTokenPO tokenPO = tokenMapper.selectByDiscord(event.getUser().getIdLong());
        if (tokenPO == null) {
            ErrorResultHandler.createNotBindOsuError(event);
            return;
        }
        tokenPO.setAccess_token(accessToken.getAccess_token());
        Integer index = OptionMappingTool.getOptionOrDefault(event.getOption("index"), 0);
        String playerName = OptionMappingTool.getOptionOrDefault(event.getOption("user"), tokenPO.getPlayer_name());
        String mode= OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe();
        Integer version = OptionMappingTool.getOptionOrDefault(event.getOption("version"), 1);
        Integer playerId= OsuToolsUtil.getUserIdByUsername(playerName,tokenPO);

        List<ScoreLazerDTO> scoreList = DataObjectExtractor.extractRecentScoreList(accessToken.getAccess_token(), playerId, type, mode);
        if(index>=scoreList.size()) {
            throw new RuntimeException("超出能索引的最大距离，当前为: "+index+", 最大为: " +(scoreList.size()-1));
        }
        ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(DataObjectExtractor.extractBeatmap(
                accessToken.getAccess_token(),
                String.valueOf(scoreList.get(index).getBeatmap_id()),mode),
                scoreList.get(index));
        ImageUploadUtil.uploadImage(event, SVGRenderUtil.renderScoreToByteArray(scoreVO,version));
    }

}
