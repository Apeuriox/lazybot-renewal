create table badge_definition
(
    id               int auto_increment
        primary key,
    name             varchar(512)  null,
    description      varchar(512)  null,
    type             varchar(16)   null,
    alternative_name varchar(256)  null,
    create_time      datetime      null,
    remote_url       varchar(1024) null
);

create table badge_challenge_definition
(
    id          int auto_increment
        primary key,
    name        varchar(64)  null,
    description varchar(128) null,
    badge_id    int          null,
    create_time datetime     null,
    is_active   tinyint(1)   null,
    expire_time datetime     null,
    constraint badge_challenge_definition_badge_definition_id_fk
        foreign key (badge_id) references badge_definition (id)
);

create table badge_challenge_map
(
    id                 int auto_increment
        primary key,
    challenge_id       int           null,
    beatmap_id         int           null,
    required_acc       double        null,
    required_combo     int           null,
    max_accepted_miss  int           null,
    mods_allowed       varchar(512)  null,
    mode               tinyint       null,
    title_with_version varchar(1024) null,
    constraint badge_challenge_map_badge_challenge_definition_id_fk
        foreign key (challenge_id) references badge_challenge_definition (id)
);

create table badge_challenge_submission_details
(
    id             int auto_increment
        primary key,
    challenge_id   int          null,
    beatmap_id     int          null,
    achieved_acc   double       null,
    achieved_combo int          null,
    miss_count     int          null,
    mod_used       varchar(512) null,
    player_id      int          null,
    score_id       bigint       null,
    create_time    datetime     null,
    constraint badge_challenge_definition_id_fk
        foreign key (challenge_id) references badge_challenge_definition (id)
);

create table badge_key
(
    id         int auto_increment
        primary key,
    cdkey      varchar(256) null,
    badge_id   int          null,
    max_uses   int          null,
    used_count int          null,
    is_active  tinyint(1)   null,
    created_at datetime     null,
    expired_at datetime     null,
    constraint badge_key_badge_definition_id_fk
        foreign key (badge_id) references badge_definition (id)
);

create table badge_key_redeem_log
(
    id          int auto_increment
        primary key,
    key_id      int      null,
    user_id     int      null,
    redeemed_at datetime null,
    badge_id    int      null,
    constraint badge_key_redeem_log_badge_definition_id_fk
        foreign key (badge_id) references badge_definition (id),
    constraint badge_key_redeem_log_badge_key_id_fk
        foreign key (key_id) references badge_key (id)
);

create table badge_user_owned
(
    id                  int auto_increment
        primary key,
    user_id             int           null,
    badge_id            int           null,
    obtain_time         datetime      null,
    source_challenge_id int           null,
    source_text         varchar(1024) null,
    constraint badge_user_owned_badge_definition_id_fk
        foreign key (badge_id) references badge_definition (id)
);

create table badge_user_showcase
(
    id         int auto_increment
        primary key,
    badge_id   int null,
    lazybot_id int null
);

create table card_user_points
(
    user_id                int      null,
    points                 int      null,
    last_signin_time       datetime null,
    total_history_points   int      null,
    total_spent_points     int      null,
    accumulated_check_time int      null,
    continuous_check_time  int      null
);

create index card_user_points_token_id_fk
    on card_user_points (user_id);

create table card_user_points_log
(
    user_id       int         null,
    change_amount int         null,
    reason        varchar(32) null,
    create_time   datetime    null
);

create index card_user_points_log_token_id_fk
    on card_user_points_log (user_id);

create table command_usage
(
    id           int auto_increment
        primary key,
    total        int      null,
    distribution json     null,
    source       json     null,
    command      json     null,
    is_complete  tinyint  null,
    created_at   datetime null
);

create table lazybot_user
(
    id                      bigint unsigned auto_increment
        primary key,
    default_mode            varchar(16) default 'osu'                not null,
    default_subset          varchar(32)                              null,
    preferred_panel_version smallint                                 null,
    enabled                 tinyint(1)  default 1                    not null,
    created_at              datetime(3) default CURRENT_TIMESTAMP(3) not null,
    updated_at              datetime(3) default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3)
);

create table osu_account
(
    id                   bigint unsigned auto_increment
        primary key,
    lazybot_user_id      bigint unsigned                          not null,
    server               varchar(16)                              not null,
    osu_user_id          bigint unsigned                          not null,
    username_cache       varchar(128)                             not null,
    link_method          varchar(16)                              not null,
    verified_at          datetime(3)                              null,
    avatar_etag          varchar(512)                             null,
    avatar_last_checked  datetime(3)                              null,
    avatar_next_check_at datetime(3)                              null,
    created_at           datetime(3) default CURRENT_TIMESTAMP(3) not null,
    updated_at           datetime(3) default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3),
    constraint uk_osu_account_identity
        unique (server, osu_user_id),
    constraint uk_osu_account_user_server
        unique (lazybot_user_id, server),
    constraint fk_osu_account_user
        foreign key (lazybot_user_id) references lazybot_user (id)
);

create table osu_oauth_credential
(
    osu_account_id          bigint unsigned                              not null
        primary key,
    access_token_cipher     mediumblob                                   not null,
    refresh_token_cipher    mediumblob                                   not null,
    access_token_expires_at datetime(3)                                  not null,
    granted_scopes          varchar(512)                                 not null,
    encryption_key_version  smallint unsigned                            not null,
    row_version             bigint unsigned default '0'                  not null,
    created_at              datetime(3)     default CURRENT_TIMESTAMP(3) not null,
    updated_at              datetime(3)     default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3),
    constraint fk_oauth_credential_account
        foreign key (osu_account_id) references osu_account (id)
);

create table permission
(
    id          int auto_increment
        primary key,
    target_type varchar(8)  null,
    target_id   bigint      null,
    command     varchar(64) null,
    version     int         null,
    created_at  datetime    null
);

create table platform_identity
(
    id               bigint unsigned auto_increment
        primary key,
    lazybot_user_id  bigint unsigned                          not null,
    platform         varchar(16)                              not null,
    platform_user_id varchar(64)                              not null,
    created_at       datetime(3) default CURRENT_TIMESTAMP(3) not null,
    constraint uk_platform_identity
        unique (platform, platform_user_id),
    constraint fk_platform_identity_user
        foreign key (lazybot_user_id) references lazybot_user (id)
);

create table player_info
(
    id                int auto_increment
        primary key,
    player_id         int      null,
    performance_point float    null,
    created_time      datetime null,
    modified_time     datetime null
);

create table profile_customization
(
    player_name    varchar(200)  null,
    player_id      int           null,
    qq_code        bigint        null,
    verified       int           null,
    preferred_type int           null,
    hue            int           null,
    id             int auto_increment
        primary key,
    original_url   varchar(2000) null,
    last_updated   datetime      null
);

create definer = root@`183.230.242.186` trigger `update _timestamp`
    before update
    on profile_customization
    for each row
begin
    set NEW.last_updated=NOW();
end;

create table tips
(
    id           int auto_increment
        primary key,
    created_by   varchar(1000) null,
    content      mediumtext    null,
    last_updated datetime      null,
    updated_by   varchar(1000) null
);

create table token
(
    id                      int auto_increment
        primary key,
    qq_code                 bigint        null,
    player_id               int           null,
    access_token            varchar(2048) null,
    refresh_token           varchar(2048) null,
    expires_in              int           null,
    default_mode            varchar(16)   null,
    player_name             varchar(64)   null,
    valid                   tinyint       null,
    avatar_url              varchar(256)  null,
    preferred_panel_version int           null
);

create table token_star_moon
(
    id              int auto_increment
        primary key,
    star_moon_id    int          null,
    star_moon_name  varchar(128) null comment 'only fpr preview',
    qq_code         mediumtext   null,
    create_time     datetime     null,
    default_mode    varchar(16)  null,
    default_ruleset varchar(32)  null
);

create table user_token_discord
(
    id            int auto_increment
        primary key,
    player_name   varchar(500)  null,
    discord_code  bigint        null,
    access_token  varchar(5000) null,
    refresh_token varchar(5000) null,
    player_id     bigint        null,
    default_mode  int           null
);

create table player_stats_daily_template
(
    id                 int          not null,
    mode               tinyint      not null,
    subserver          tinyint      not null,
    record_date_time   datetime     not null,
    player_name        varchar(128) not null,
    performance_point  double       null,
    global_rank        int          null,
    country_rank       int          null,
    total_score        bigint       null,
    rank_total_score   bigint       null,
    accuracy           double       null,
    play_count         int          null,
    total_hit_count    bigint       null,
    total_play_time    bigint       null,
    grades             json         null,
    primary key (id, mode, subserver, record_date_time),
    key idx_record_date_time_mode (record_date_time, mode)
);

create table player_stats_table_meta
(
    year       smallint    not null
        primary key,
    table_name varchar(64) not null,
    created_at datetime    not null
);

create table player_stats_watch
(
    id         int         not null,
    mode       tinyint     not null,
    subserver  tinyint     not null,
    active     tinyint(1)  not null,
    updated_at datetime    not null,
    primary key (id, mode, subserver)
);

