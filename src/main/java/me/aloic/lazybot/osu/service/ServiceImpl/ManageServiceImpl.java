package me.aloic.lazybot.osu.service.ServiceImpl;

import jakarta.annotation.Resource;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.dto.osuTrack.UserDifference;
import me.aloic.lazybot.osu.dao.entity.dto.player.BeatmapUserScoreLazer;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.po.ProfileCustomizationPO;
import me.aloic.lazybot.osu.dao.entity.po.TipsPO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.dao.mapper.CustomizationMapper;
import me.aloic.lazybot.osu.dao.mapper.TipsMapper;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.service.ManageService;
import me.aloic.lazybot.osu.utils.AssertDownloadUtil;
import me.aloic.lazybot.osu.utils.OsuToolsUtil;
import me.aloic.lazybot.osu.utils.SvgUtil;
import me.aloic.lazybot.parameter.*;
import me.aloic.lazybot.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;


// onebot protocol can be forged so no sensitive functions here
@Service
public class ManageServiceImpl implements ManageService
{
    private static final Map<String, Function<UpdateParameter,String>> updateMap;
    private static final Map<Long,Boolean> adminMap;  //Long ass numbers are discord ids
    static{
        updateMap = Map.of("avatar",ManageServiceImpl::updateAvatar,
                "track",ManageServiceImpl::updateOsuTrack);
        adminMap = Map.of( 1524185356L,true,
                412246007024451585L,true);
    }

    @Resource
    private CustomizationMapper customizationMapper;
    @Resource
    private TokenMapper tokenMapper;
    @Resource
    private TipsMapper tipsMapper;
    private static final Logger logger = LoggerFactory.getLogger(ManageServiceImpl.class);
    @Override
    public String update(UpdateParameter params)
    {
        if(params==null || params.getType()==null || !updateMap.containsKey(params.getType())) return "Update avatar {user_name} or Update track {user_name}";
        return updateMap.get(params.getType()).apply(params);
    }

    private static String updateAvatar(UpdateParameter params)
    {
        PlayerInfoDTO playerInfoDTO;
        if (params.getPlayerId()!=null) playerInfoDTO = DataObjectExtractor.extractPlayerInfo(params.getAccessToken(),params.getPlayerId(),params.getMode());
        else playerInfoDTO = DataObjectExtractor.extractPlayerInfo(params.getAccessToken(),params.getPlayerName(),params.getMode());

        playerInfoDTO.setAvatar_url((AssertDownloadUtil.avatarAbsolutePath(playerInfoDTO,true)));
        return "已更新用户"+playerInfoDTO.getUsername()+"的头像缓存";
    }
    private static String updateOsuTrack(UpdateParameter params)
    {
        PlayerInfoDTO playerInfoDTO;
        if (params.getPlayerId()!=null) playerInfoDTO = DataObjectExtractor.extractPlayerInfo(params.getAccessToken(),params.getPlayerId(),params.getMode());
        else playerInfoDTO = DataObjectExtractor.extractPlayerInfo(params.getAccessToken(),params.getPlayerName(),params.getMode());
        ApiRequestStarter trackApiRequest = new ApiRequestStarter(URLBuildUtil.buildURLOfOsuTrackUpdate(playerInfoDTO.getId(),params.getMode()));
        UserDifference userDifference = trackApiRequest.executeRequest(ContentUtil.HTTP_REQUEST_TYPE_POST, UserDifference.class);
        return "已更新用户"+playerInfoDTO.getUsername()+"的Osu Track数据";
    }

    @Override
    public String verifyBeatmap(BeatmapParameter params)
    {
        if(!adminMap.containsKey(params.getUserIdentity())) throw new LazybotRuntimeException("你没有权限");
        File beatmapFile;
        try{
            beatmapFile = new File(AssertDownloadUtil.beatmapPath(params.getBid(),false).toUri());
        }
        catch (Exception e) {
            return "未检索到本地缓存";
        }
        String checksum= CommonTool.calculateMD5(beatmapFile);
        BeatmapDTO beatmapDTO = DataObjectExtractor.extractBeatmap(params.getAccessToken(), String.valueOf(params.getBid()),params.getMode());
        if (!checksum.equals(beatmapDTO.getChecksum())) {
            AssertDownloadUtil.beatmapPath(params.getBid(), true);
            return "校验和不匹配: " + beatmapDTO.getChecksum() + " != " + checksum;
        }
        return "校验和正常: "+checksum;
    }

    @Override
    public String unlinkUser(GeneralParameter params)
    {
//        Optional.ofNullable(tokenMapper.selectByQq_code())
//                .ifPresentOrElse(
//                        token -> tokenMapper.deleteByQQ(event.getMessageEvent().getSender().getUserId()),
//                        this::createNotBindError);
        return "";
    }
    @Override
    public String verify(VerifyParameter params)
    {
        if(!adminMap.containsKey(params.getQqCode())) throw new LazybotRuntimeException("你没有权限");
        if(Objects.equals(params.getType(), "view")) {
            return showUnverifiedCustomization();
        }
        else if(Objects.equals(params.getType(), "profile")) {
            return verifyProfileCustomization(params);
        }
        return "未知二级命令";
    }

    @Override
    public String addTips(ContentParameter params)
    {
            if(!adminMap.containsKey(params.getUserIdentity())) throw new LazybotRuntimeException("你没有权限");
            try{
                TipsPO tipsPO=new TipsPO();
                tipsPO.setContent(params.getContent());
                tipsPO.setCreated_by(String.valueOf(params.getUserIdentity()));
                tipsPO.setUpdated_by(String.valueOf(params.getUserIdentity()));
                tipsPO.setLast_updated(LocalDateTime.now());
                try{
                    tipsMapper.insert(tipsPO);
                }
                catch (Exception e){
                    throw new RuntimeException("添加提示时失败" + e.getMessage());
                }
            }
            catch (Exception e) {
                logger.error("添加提示时失败",e);
                return "添加tips失败，详情请见log";
            }
            return "成功添加";
    }

    @Override
    public String ppTest(ScoreParameter params)
    {
        BeatmapUserScoreLazer beatmapUserScoreLazer = DataObjectExtractor.extractBeatmapUserScore(params.getAccessToken(),
                String.valueOf(params.getBeatmapId()), params.getPlayerId(), params.getMode(), params.getModCombination());
        ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(
                DataObjectExtractor.extractBeatmap(params.getAccessToken(), String.valueOf(params.getBeatmapId()), params.getMode()),
                beatmapUserScoreLazer.getScore(),
                false);
        return scoreVO.getPpDetailsLocal().getOriginal().toString();
    }

    private String verifyProfileCustomization(VerifyParameter params)
    {
        customizationMapper.updateVerified(2,params.getCustomizeId());
        return "成功设置";
    }

    private String showUnverifiedCustomization()
    {
        List<ProfileCustomizationPO> profiles=customizationMapper.selectUnverified();
        StringBuffer sb = new StringBuffer("所有客制化请求均已完成审核");
        if(profiles!=null && !profiles.isEmpty()) {
            sb.delete(0,sb.length());
            for(ProfileCustomizationPO profile:profiles) {
                sb.append("ID: ").append(profile.getId()).append("\n")
                        .append("PlayerID: ").append(profile.getPlayer_id()).append("\n")
                        .append("URL: ").append(profile.getOriginal_url()).append("\n").append("\n");
            }
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

}
