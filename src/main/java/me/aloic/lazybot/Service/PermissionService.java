package me.aloic.lazybot.Service;

import me.aloic.lazybot.command.LazybotSlashCommand;

public interface PermissionService
{
    void setPermission(String type, long id, LazybotSlashCommand command, int version);

    Boolean checkPermission(String type, long id, LazybotSlashCommand command, int version);
}
