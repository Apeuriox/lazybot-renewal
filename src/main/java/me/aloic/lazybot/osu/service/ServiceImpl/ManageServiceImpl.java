package me.aloic.lazybot.osu.service.ServiceImpl;

import jakarta.annotation.Resource;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.dto.osuTrack.UserDifference;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.po.ProfileCustomizationPO;
import me.aloic.lazybot.osu.dao.mapper.CustomizationMapper;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.service.ManageService;
import me.aloic.lazybot.osu.utils.AssertDownloadUtil;
import me.aloic.lazybot.parameter.BeatmapParameter;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.parameter.UpdateParameter;
import me.aloic.lazybot.parameter.VerifyParameter;
import me.aloic.lazybot.util.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

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

    @Override
    public String update(UpdateParameter params)
    {
        if(params==null || params.getType()==null || !updateMap.containsKey(params.getType())) return "Update avatar {user_name} or Update track {user_name}";
        return updateMap.get(params.getType()).apply(params);
    }

    private static String updateAvatar(UpdateParameter params)
    {
        PlayerInfoDTO playerInfoDTO= DataObjectExtractor.extractPlayerInfo(params.getAccessToken(),params.getPlayerName(), params.getMode());
        playerInfoDTO.setAvatar_url((AssertDownloadUtil.avatarAbsolutePath(playerInfoDTO,true)));
        return "已更新用户"+playerInfoDTO.getUsername()+"的头像缓存";
    }
    private static String updateOsuTrack(UpdateParameter params)
    {
        PlayerInfoDTO playerInfoDTO= DataObjectExtractor.extractPlayerInfo(params.getAccessToken(),params.getPlayerName(), params.getMode());
        ApiRequestStarter trackApiRequest = new ApiRequestStarter(URLBuildUtil.buildURLOfOsuTrackUpdate(playerInfoDTO.getId(),params.getMode()));
        UserDifference userDifference = trackApiRequest.executeRequest(ContentUtil.HTTP_REQUEST_TYPE_POST, UserDifference.class);
        return "已更新用户"+playerInfoDTO.getUsername()+"的Osu Track数据";
    }

    @Override
    public String verifyBeatmap(BeatmapParameter params)
    {
        if(!adminMap.containsKey(params.getUserIdentity())) throw new RuntimeException("你没有权限");
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
        if(!adminMap.containsKey(params.getQqCode())) throw new RuntimeException("你没有权限");
        if(Objects.equals(params.getType(), "view")) {
            return showUnverifiedCustomization();
        }
        else if(Objects.equals(params.getType(), "profile")) {
            return verifyProfileCustomization(params);
        }
        return "未知二级命令";
    }

    public String verifyProfileCustomization(VerifyParameter params)
    {
        customizationMapper.updateVerified(2,params.getCustomizeId());
        return "成功设置";
    }

    public String showUnverifiedCustomization()
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
