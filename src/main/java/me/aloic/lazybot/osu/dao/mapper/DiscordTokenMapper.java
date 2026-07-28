package me.aloic.lazybot.osu.dao.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * @deprecated Retained as an injection-compatible marker during command migration.
 * Identity reads now go through UserBindingMapper / CommandDatabaseProxy.
 */
@Deprecated
@Mapper
public interface DiscordTokenMapper
{
}
