package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;

/**
 * osu! 头像的本地缓存。
 *
 * <p>osu! 的头像 URL 现在被ppy改了，改了头像也不会变，所以不能再通过比较 URL 判断头像是否更新。
 * 本服务按 TTL 发起带 If-None-Match 的条件请求；TTL 内完全不访问头像服务器，
 * 304 时只刷新检查时间，200 时原子替换本地文件。</p>
 */
public interface AvatarCacheService
{
    String ensureAvatar(PlayerInfoDTO player, UserBindingPO binding);
}
