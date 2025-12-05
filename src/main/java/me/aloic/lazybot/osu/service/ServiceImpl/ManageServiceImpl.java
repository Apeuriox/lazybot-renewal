package me.aloic.lazybot.osu.service.ServiceImpl;

import jakarta.annotation.Resource;
import me.aloic.lazybot.entity.CommandStat;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.CommandMonitor;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.dto.osuTrack.UserDifference;
import me.aloic.lazybot.osu.dao.entity.dto.player.BeatmapUserScoreLazer;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.po.*;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.dao.mapper.CustomizationMapper;
import me.aloic.lazybot.osu.dao.mapper.TipsMapper;
import me.aloic.lazybot.osu.service.ManageService;
import me.aloic.lazybot.osu.utils.AssetDownloadUtil;
import me.aloic.lazybot.osu.utils.OsuToolsUtil;
import me.aloic.lazybot.parameter.*;
import me.aloic.lazybot.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;


// onebot protocol can be forged so no sensitive functions here
@Service
public class ManageServiceImpl implements ManageService
{
//    private static final Map<String, Function<UpdateParameter,String>> updateMap;

    @Resource
    private CommandMonitor commandMonitor;
    @Resource
    private OsuToolsUtil osuToolsUtil;

    static{
//        updateMap = Map.of("avatar",ManageServiceImpl::updateAvatar,
//                "track",ManageServiceImpl::updateOsuTrack,
//                "banner",ManageServiceImpl::updateBanner);

    }

    @Resource
    private CustomizationMapper customizationMapper;
    @Resource
    private TipsMapper tipsMapper;
    private static final Logger logger = LoggerFactory.getLogger(ManageServiceImpl.class);

    @Resource
    private DataExtractor dataExtractor;

    @Override
    public String update(UpdateParameter params)
    {
        if(params==null || params.getType()==null)
            return "[Lazybot] 输入Update avatar 用户名 以更新头像\n输入 Update track 用户名  以更新ppmap数据\n输入Update banner 用户名 以更新用户横幅\n输入Update plus 用户名 以更新pp+数据";
        else if(params.getType().equals("avatar"))
            return updateAvatar(params);
        else if(params.getType().equals("track"))
            return updateOsuTrack(params);
        else if(params.getType().equals("banner"))
            return updateBanner(params);
        else if(params.getType().equals("plus"))
            return updatePlus(params);
        return "[Lazybot] 输入Update avatar 用户名 以更新头像\n输入 Update track 用户名  以更新ppmap数据\n输入Update banner 用户名 以更新用户横幅\n输入Update plus 用户名 以更新pp+数据";
    }
    private String updateAvatar(UpdateParameter params)
    {
        PlayerInfoDTO playerInfoDTO;
        if (params.getPlayerId()!=null) playerInfoDTO = dataExtractor.extractPlayerInfoDTO(params.getPlayerId(),params.getMode());
        else playerInfoDTO = dataExtractor.extractPlayerInfoDTO(params.getPlayerName(),params.getMode());

        AssetDownloadUtil.avatarAbsolutePath(playerInfoDTO,true);
        if (params.getStarMoonId()!=null) {
            AssetDownloadUtil.avatarAbsolutePathStarNoon(params.getStarMoonId(),true);
        }

        return "[Lazybot] 已更新用户"+playerInfoDTO.getUsername()+"的头像缓存";
    }
    private String updateBanner(UpdateParameter params)
    {
        PlayerInfoDTO playerInfoDTO;
        if (params.getPlayerId()!=null) playerInfoDTO = dataExtractor.extractPlayerInfoDTO(params.getPlayerId(),params.getMode());
        else playerInfoDTO = dataExtractor.extractPlayerInfoDTO(params.getPlayerName(),params.getMode());

        playerInfoDTO.setCover_url((AssetDownloadUtil.bannerAbsolutePath(playerInfoDTO,true)));
        return "[Lazybot] 已更新用户"+playerInfoDTO.getUsername()+"的横幅缓存";
    }
    private String updateOsuTrack(UpdateParameter params)
    {
        PlayerInfoDTO playerInfoDTO;
        if (params.getPlayerId()!=null) playerInfoDTO = dataExtractor.extractPlayerInfoDTO(params.getPlayerId(),params.getMode());
        else playerInfoDTO = dataExtractor.extractPlayerInfoDTO(params.getPlayerName(),params.getMode());
        ApiRequestStarter trackApiRequest = new ApiRequestStarter(URLBuildUtil.buildURLOfOsuTrackUpdate(playerInfoDTO.getId(),params.getMode()));
        try{
            UserDifference userDifference = trackApiRequest.executeRequest(ContentUtil.HTTP_REQUEST_TYPE_POST, UserDifference.class);
        }
        catch (Exception e) {
            throw new LazybotRuntimeException("更新Osu Track失败，是否是用户未初始化?");
        }
        return "[Lazybot] 已更新用户"+playerInfoDTO.getUsername()+"的Osu Track数据";
    }
    private String updatePlus(UpdateParameter params)
    {
        PlayerInfoDTO playerInfoDTO;
        if (params.getPlayerId()!=null) playerInfoDTO = dataExtractor.extractPlayerInfoDTO(params.getPlayerId(),params.getMode());
        else playerInfoDTO = dataExtractor.extractPlayerInfoDTO(params.getPlayerName(),params.getMode());
        dataExtractor.extractPerformancePlusPlayerUpdate(playerInfoDTO.getId());
        return "[Lazybot] 已更新用户"+playerInfoDTO.getUsername()+"的PP+数据，请注意此更新仅更新最近游玩，若想添加指定成绩请使用/add";
    }

    @Override
    public String verifyBeatmap(BeatmapParameter params)
    {
        AuthorityVerifier.isAdmin(params.getUserIdentity());
        File beatmapFile;
        try{
            beatmapFile = new File(AssetDownloadUtil.beatmapPath(params.getBid(),false).toUri());
        }
        catch (Exception e) {
            return "[Lazybot] 未检索到本地缓存";
        }
        String checksum= CommonTool.calculateMD5(beatmapFile);
        BeatmapDTO beatmapDTO = dataExtractor.extractBeatmap(String.valueOf(params.getBid()),params.getMode());
        if (!checksum.equals(beatmapDTO.getChecksum())) {
            AssetDownloadUtil.beatmapPath(params.getBid(), true);
            return "[Lazybot] 校验和不匹配: " + beatmapDTO.getChecksum() + " != " + checksum;
        }
        return "[Lazybot] 校验和正常: "+checksum;
    }
    @Override
    public String updateBeatmapBackground(BeatmapParameter params)
    {
        AuthorityVerifier.isAdmin(params.getUserIdentity());
        BeatmapDTO beatmap = dataExtractor.extractBeatmap(String.valueOf(params.getBid()), params.getMode());
        try{
           AssetDownloadUtil.backgroundDownload(beatmap.getBeatmapset_id(),true);
        }
        catch (Exception e) {
            return "[Lazybot] 更新失败";
        }
        return "[Lazybot] 成功更新Set " + beatmap.getBeatmapset_id() + " 的背景";
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
        AuthorityVerifier.isAdmin(params.getQqCode());
        if(Objects.equals(params.getType(), "view")) {
            return showUnverifiedCustomization();
        }
        else if(Objects.equals(params.getType(), "profile")) {
            return verifyProfileCustomization(params);
        }
        return "[Lazybot] 未知二级命令";
    }

    @Override
    public String addTips(ContentParameter params)
    {
            AuthorityVerifier.isAdmin(params.getUserIdentity());
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
                    throw new LazybotRuntimeException("添加提示时失败" + e.getMessage());
                }
            }
            catch (Exception e) {
                logger.error("添加提示时失败",e);
                return "[Lazybot] 添加tips失败，详情请见log";
            }
            return "[Lazybot] 成功添加";
    }

    @Override
    public String ppTest(ScoreParameter params, Long userIdentity)
    {
        AuthorityVerifier.isAdmin(userIdentity);
        BeatmapUserScoreLazer beatmapUserScoreLazer = dataExtractor.extractBeatmapUserScore(
                String.valueOf(params.getBeatmapId()), params.getPlayerId(), params.getMode(), params.getModCombination());
        ScoreVO scoreVO = osuToolsUtil.setupScoreVO(
                dataExtractor.extractBeatmap(String.valueOf(params.getBeatmapId()), params.getMode()),
                beatmapUserScoreLazer.getScore(),
                false);
        return scoreVO.getPpDetailsLocal().getOriginal().toString();
    }

    private String verifyProfileCustomization(VerifyParameter params)
    {
        customizationMapper.updateVerified(2,params.getCustomizeId());
        return "[Lazybot] 成功设置";
    }

    private String showUnverifiedCustomization()
    {
        List<ProfileCustomizationPO> profiles=customizationMapper.selectUnverified();
        StringBuffer sb = new StringBuffer("[Lazybot] 所有客制化请求均已完成审核");
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
    @Override
    public CommandUsage commandUsage()
    {
        Map<String, CommandStat> commandStatMap = commandMonitor.getAllStats();
        LocalDateTime startTime = commandMonitor.getStartTime();
        return CommandMonitor.setupCommandUsage(commandStatMap,startTime);
    }

}
