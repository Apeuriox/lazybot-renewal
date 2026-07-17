package me.aloic.lazybot.service;

import me.aloic.lazybot.command.LazybotSlashCommand;

public interface PermissionService
{
    void setPermission(String type, long id, String commandKey, int version);

    Boolean checkPermission(String type, long id, String commandKey, int version);

    default void setPermission(String type, long id, LazybotSlashCommand command, int version) {
        setPermission(type, id, command.getClass().getSimpleName(), version);
    }

    default Boolean checkPermission(String type, long id, LazybotSlashCommand command, int version) {
        return checkPermission(type, id, command.getClass().getSimpleName(), version);
    }
}
