package me.aloic.lazybot.service.Impl;
import jakarta.annotation.Resource;
import me.aloic.lazybot.service.PermissionService;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.entity.po.PermissionPO;
import me.aloic.lazybot.osu.dao.mapper.PermissionMapper;
import org.springframework.stereotype.Service;


@Service
public class PermissionServiceImpl implements PermissionService
{
    @Resource
    private PermissionMapper permissionMapper;

    @Override
    public void setPermission(String type, long id, LazybotSlashCommand command, int version) {
        permissionMapper.insertOrUpdate(new PermissionPO(type,id,command.getClass().getSimpleName(),version));
    }
    @Override
    public Boolean checkPermission(String type, long id, LazybotSlashCommand command, int version)
    {
        return permissionMapper.selectByStats(type, id, command.getClass().getSimpleName(), version) == null;
    }

    private String buildKey(String type, long id, String command) {
        return type + ":" + id + ":" + command;
    }

}
