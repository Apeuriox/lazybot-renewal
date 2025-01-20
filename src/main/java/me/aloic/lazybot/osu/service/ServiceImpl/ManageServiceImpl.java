package me.aloic.lazybot.osu.service.ServiceImpl;

import me.aloic.lazybot.osu.dao.entity.dto.osuTrack.UserDifference;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.service.ManageService;
import me.aloic.lazybot.osu.utils.AssertDownloadUtil;
import me.aloic.lazybot.parameter.UpdateParameter;
import me.aloic.lazybot.util.ApiRequestStarter;
import me.aloic.lazybot.util.ContentUtil;
import me.aloic.lazybot.util.DataObjectExtractor;
import me.aloic.lazybot.util.URLBuildUtil;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;

@Service
public class ManageServiceImpl implements ManageService
{
    private static final Map<String, Function<UpdateParameter,String>> updateMap;
    static{
        updateMap = Map.of("avatar",ManageServiceImpl::updateAvatar,
                "track",ManageServiceImpl::updateOsuTrack);
    }

    @Override
    public String update(UpdateParameter params)
    {
        if(!updateMap.containsKey(params.getType())) return "Update avatar {username} or Update track {username}";
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

}
