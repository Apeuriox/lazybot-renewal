package me.aloic.lazybot.osu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.LazybotUserPO;
import me.aloic.lazybot.osu.dao.entity.po.OsuAccountPO;
import me.aloic.lazybot.osu.dao.entity.po.OsuOAuthCredentialPO;
import me.aloic.lazybot.osu.dao.entity.po.PlatformIdentityPO;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.dao.mapper.LazybotUserMapper;
import me.aloic.lazybot.osu.dao.mapper.OsuAccountMapper;
import me.aloic.lazybot.osu.dao.mapper.OsuOAuthCredentialMapper;
import me.aloic.lazybot.osu.dao.mapper.PlatformIdentityMapper;
import me.aloic.lazybot.osu.dao.mapper.UserBindingMapper;
import me.aloic.lazybot.osu.enums.AccountLinkMethod;
import me.aloic.lazybot.osu.enums.IdentityPlatform;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.enums.OsuServer;
import me.aloic.lazybot.osu.enums.OsuSubruleset;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserIdentityService
{
    @Resource
    private LazybotUserMapper lazybotUserMapper;
    @Resource
    private PlatformIdentityMapper platformIdentityMapper;
    @Resource
    private OsuAccountMapper osuAccountMapper;
    @Resource
    private OsuOAuthCredentialMapper oauthCredentialMapper;
    @Resource
    private UserBindingMapper userBindingMapper;

    public UserBindingPO findBinding(
            IdentityPlatform platform, String platformUserId, OsuServer server)
    {
        return userBindingMapper.selectByPlatform(
                platform.databaseValue(),
                platformUserId,
                server.databaseValue());
    }

    @Transactional
    public UserBindingPO bindManual(IdentityPlatform platform,
                                    String platformUserId,
                                    OsuServer server,
                                    Integer osuUserId,
                                    String username)
    {
        return bindManual(platform, platformUserId, server, osuUserId, username, null);
    }

    @Transactional
    public UserBindingPO bindManual(IdentityPlatform platform,
                                    String platformUserId,
                                    OsuServer server,
                                    Integer osuUserId,
                                    String username,
                                    OsuMode defaultMode)
    {
        OsuAccountPO existingAccount = osuAccountMapper.selectByServerIdentityForUpdate(
                server.databaseValue(), osuUserId);
        PlatformIdentityPO platformIdentity = platformIdentityMapper.selectByPlatformIdentity(
                platform.databaseValue(), platformUserId);
        if (platformIdentity != null) {
            platformIdentity = platformIdentityMapper.selectByIdForUpdate(platformIdentity.getId());
        }

        if (platformIdentity != null) {
            OsuAccountPO current = osuAccountMapper.selectByUserAndServer(
                    platformIdentity.getLazybot_user_id(), server.databaseValue());
            if (current != null) {
                if (osuUserId.equals(current.getOsu_user_id())) {
                    return findBinding(platform, platformUserId, server);
                }
                throw new LazybotRuntimeException(
                        "您已绑定用户: " + current.getUsername_cache());
            }
            if (existingAccount != null) {
                attachIdentityToAccountOwner(platformIdentity, existingAccount);
                synchronizeDefaultMode(existingAccount.getLazybot_user_id(), defaultMode);
                return findBinding(platform, platformUserId, server);
            }
            insertManualAccount(
                    platformIdentity.getLazybot_user_id(),
                    server,
                    osuUserId,
                    username);
            synchronizeDefaultMode(platformIdentity.getLazybot_user_id(), defaultMode);
            return findBinding(platform, platformUserId, server);
        }

        if (existingAccount != null) {
            insertPlatformIdentity(
                    existingAccount.getLazybot_user_id(), platform, platformUserId);
            synchronizeDefaultMode(existingAccount.getLazybot_user_id(), defaultMode);
            return findBinding(platform, platformUserId, server);
        }

        Integer lazybotUserId = createLazybotUser();
        insertPlatformIdentity(lazybotUserId, platform, platformUserId);
        insertManualAccount(lazybotUserId, server, osuUserId, username);
        synchronizeDefaultMode(lazybotUserId, defaultMode);
        return findBinding(platform, platformUserId, server);
    }

    @Transactional
    public PlatformIdentityPO ensurePlatformIdentity(
            IdentityPlatform platform, String platformUserId)
    {
        PlatformIdentityPO existing = platformIdentityMapper.selectByPlatformIdentity(
                platform.databaseValue(), platformUserId);
        if (existing != null)
            return existing;

        Integer userId = createLazybotUser();
        PlatformIdentityPO identity = new PlatformIdentityPO();
        identity.setLazybot_user_id(userId);
        identity.setPlatform(platform.databaseValue());
        identity.setPlatform_user_id(platformUserId);
        identity.setCreated_at(LocalDateTime.now());
        platformIdentityMapper.insert(identity);
        return identity;
    }

    /**
     * Completes a proven OAuth binding.
     *
     * <p>If the osu! account already belongs to another Lazybot user, this
     * platform identity is attached to that user so Discord / QQ / Tencent can
     * share one osu! binding. An OAuth-verified account on the current identity
     * is never overwritten implicitly.</p>
     */
    @Transactional
    public void bindOAuth(
            Long platformIdentityId,
            OsuServer server,
            Integer osuUserId,
            String username,
            OsuMode defaultMode,
            OsuOAuthCredentialPO credential)
    {
        PlatformIdentityPO identity =
                platformIdentityMapper.selectByIdForUpdate(platformIdentityId);
        if (identity == null)
            throw new LazybotRuntimeException("发起 OAuth 绑定的平台身份不存在");

        Integer userId = identity.getLazybot_user_id();
        OsuAccountPO current = osuAccountMapper.selectByUserAndServerForUpdate(
                userId, server.databaseValue());
        OsuAccountPO target = osuAccountMapper.selectByServerIdentityForUpdate(
                server.databaseValue(), osuUserId);

        if (target != null && !target.getLazybot_user_id().equals(userId))
        {
            if (current != null && !current.getId().equals(target.getId())) {
                rejectReplacingVerifiedAccount(current);
                deleteAccountExplicitly(current);
            }

            userId = target.getLazybot_user_id();
            attachIdentityToAccountOwner(identity, target);
        }
        else if (target == null)
        {
            if (current != null)
            {
                rejectReplacingVerifiedAccount(current);
                deleteAccountExplicitly(current);
            }

            target = new OsuAccountPO();
            target.setLazybot_user_id(userId);
            target.setServer(server.databaseValue());
            target.setOsu_user_id(osuUserId);
            target.setCreated_at(LocalDateTime.now());
        }

        target.setUsername_cache(username);
        target.setLink_method(AccountLinkMethod.OAUTH.databaseValue());
        target.setVerified_at(LocalDateTime.now());
        target.setUpdated_at(LocalDateTime.now());
        if (target.getId() == null)
            osuAccountMapper.insert(target);
        else
            osuAccountMapper.updateById(target);

        credential.setOsu_account_id(target.getId());
        OsuOAuthCredentialPO existingCredential =
                oauthCredentialMapper.selectByAccountIdForUpdate(target.getId());
        if (existingCredential == null) {
            oauthCredentialMapper.insert(credential);
        }
        else {
            credential.setCreated_at(existingCredential.getCreated_at());
            if (oauthCredentialMapper.updateRotatedCredential(
                    credential, existingCredential.getRow_version()) != 1) {
                throw new LazybotRuntimeException("OAuth 凭据并发更新失败，请重试");
            }
        }

        synchronizeDefaultMode(userId, defaultMode);
    }

    @Transactional
    public void unlink(IdentityPlatform platform, String platformUserId, OsuServer server)
    {
        PlatformIdentityPO identity = requirePlatformIdentity(platform, platformUserId);
        OsuAccountPO account = osuAccountMapper.selectByUserAndServerForUpdate(
                identity.getLazybot_user_id(), server.databaseValue());
        if (account == null) {
            throw new LazybotRuntimeException("您并未绑定");
        }

        int otherIdentities = platformIdentityMapper.countByUserId(identity.getLazybot_user_id()) - 1;
        if (otherIdentities > 0) {
            Integer detachedUserId = createLazybotUser();
            platformIdentityMapper.reassignToUser(identity.getId(), detachedUserId);
            return;
        }
        deleteAccountExplicitly(account);
    }

    public void updateDefaultMode(
            IdentityPlatform platform, String platformUserId, OsuMode mode)
    {
        PlatformIdentityPO identity = requirePlatformIdentity(platform, platformUserId);
        lazybotUserMapper.updateDefaultMode(
                identity.getLazybot_user_id(), mode.getDescribe());
    }

    public void updateDefaultSubset(
            IdentityPlatform platform, String platformUserId, String subset)
    {
        if (subset == null || subset.isBlank()) {
            throw new LazybotRuntimeException("默认子模式不能为空");
        }
        String normalizedSubset = OsuSubruleset.getRuleset(subset).getDescribe();
        PlatformIdentityPO identity = requirePlatformIdentity(platform, platformUserId);
        lazybotUserMapper.updateDefaultSubset(
                identity.getLazybot_user_id(), normalizedSubset);
    }

    public void updatePreferredPanel(
            IdentityPlatform platform, String platformUserId, Integer version)
    {
        PlatformIdentityPO identity = requirePlatformIdentity(platform, platformUserId);
        lazybotUserMapper.updatePreferredPanel(identity.getLazybot_user_id(), version);
    }

    public PlatformIdentityPO requirePlatformIdentity(
            IdentityPlatform platform, String platformUserId)
    {
        PlatformIdentityPO identity = platformIdentityMapper.selectByPlatformIdentity(
                platform.databaseValue(), platformUserId);
        if (identity == null) {
            throw new LazybotRuntimeException("您并未绑定");
        }
        return identity;
    }

    public boolean hasAnyOsuAccount(Integer lazybotUserId)
    {
        return osuAccountMapper.selectCount(
                new LambdaQueryWrapper<OsuAccountPO>()
                        .eq(OsuAccountPO::getLazybot_user_id, lazybotUserId)) > 0;
    }

    private PlatformIdentityPO insertPlatformIdentity(
            Integer lazybotUserId, IdentityPlatform platform, String platformUserId)
    {
        PlatformIdentityPO identity = new PlatformIdentityPO();
        identity.setLazybot_user_id(lazybotUserId);
        identity.setPlatform(platform.databaseValue());
        identity.setPlatform_user_id(platformUserId);
        identity.setCreated_at(LocalDateTime.now());
        platformIdentityMapper.insert(identity);
        return identity;
    }

    private void insertManualAccount(
            Integer lazybotUserId, OsuServer server, Integer osuUserId, String username)
    {
        OsuAccountPO account = new OsuAccountPO();
        account.setLazybot_user_id(lazybotUserId);
        account.setServer(server.databaseValue());
        account.setOsu_user_id(osuUserId);
        account.setUsername_cache(username);
        account.setLink_method(AccountLinkMethod.MANUAL.databaseValue());
        account.setCreated_at(LocalDateTime.now());
        account.setUpdated_at(LocalDateTime.now());
        try {
            osuAccountMapper.insert(account);
        }
        catch (DuplicateKeyException e) {
            throw new LazybotRuntimeException(
                    "该 " + server.databaseValue() + " 用户已被绑定", e);
        }
    }

    private void attachIdentityToAccountOwner(
            PlatformIdentityPO identity, OsuAccountPO existingAccount)
    {
        Integer oldUserId = identity.getLazybot_user_id();
        Integer ownerId = existingAccount.getLazybot_user_id();
        if (oldUserId.equals(ownerId)) {
            return;
        }
        platformIdentityMapper.reassignToUser(identity.getId(), ownerId);
        deleteOrphanUserIfEmpty(oldUserId);
    }

    private void deleteOrphanUserIfEmpty(Integer userId)
    {
        if (userId == null) {
            return;
        }
        if (platformIdentityMapper.countByUserId(userId) > 0) {
            return;
        }
        if (hasAnyOsuAccount(userId)) {
            return;
        }
        lazybotUserMapper.deleteById(userId);
    }

    private Integer createLazybotUser()
    {
        LazybotUserPO user = new LazybotUserPO();
        user.setDefault_mode(OsuMode.Osu.getDescribe());
        user.setDefault_subset("relax");
        user.setEnabled(true);
        user.setCreated_at(LocalDateTime.now());
        user.setUpdated_at(LocalDateTime.now());
        lazybotUserMapper.insert(user);
        return user.getId();
    }

    private void synchronizeDefaultMode(
            Integer lazybotUserId, OsuMode defaultMode)
    {
        if (defaultMode != null && defaultMode != OsuMode.Default) {
            lazybotUserMapper.updateDefaultMode(
                    lazybotUserId, defaultMode.getDescribe());
        }
    }

    private void rejectReplacingVerifiedAccount(OsuAccountPO account)
    {
        if (AccountLinkMethod.OAUTH.databaseValue().equals(account.getLink_method())) {
            throw new LazybotRuntimeException(
                    "当前平台身份已绑定另一个经过 OAuth 验证的 osu! 账号，请先解除绑定");
        }
    }

    private void deleteAccountExplicitly(OsuAccountPO account)
    {
        // No database cascade: the optional credential references osu_account.
        oauthCredentialMapper.deleteById(account.getId());
        osuAccountMapper.deleteById(account.getId());
    }
}
