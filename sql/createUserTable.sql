create table `user-center`.user
(
    id            bigint auto_increment comment '用户id'
        primary key,
    username      varchar(256)                       null comment '用户名，用于登录',
    nick_name     varchar(256)                       null comment '用户昵称',
    avatar_url    varchar(1028)                      null comment '头像',
    gender        tinyint                            null comment '性别',
    user_password varchar(512)                       null comment '用户密码',
    user_status   int      default 0                 not null comment '用户状态，0正常',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null comment '修改时间',
    is_delete     tinyint  default 0                 not null comment '是否删除',
    user_role     int      default 0                 not null comment '用户角色',
    tags          varchar(1024)                      null comment '标签',
    introduction  varchar(255)                       null comment '简介'
);

create table if not exists `user-center`.team
(
    t_id           bigint auto_increment
        primary key,
    t_code         varchar(20)                        not null comment '队伍码，用于输入搜索队伍',
    t_name         varchar(50)                        not null comment '队伍名称',
    t_description  varchar(255)                       not null comment '队伍描述',
    t_max_num      int      default 1                 not null comment '最大人数',
    expireTime     datetime                           not null comment '过期时间',
    t_captain_u_id bigint                             not null comment '队伍队长用户id',
    t_is_public    int      default 0                 not null comment '是否公开，0公开，1私密',
    t_password     varchar(50)                        null comment '进入队伍密码',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    is_delete      int      default 0                 not null
)
    comment '队伍表';


create table `user-center`.user_index
(
    ui_id    bigint auto_increment comment '索引id'
        primary key,
    u_id     bigint        not null comment '用户id',
    ui_count int default 0 not null comment '计数器，到达一千次后减半',
    constraint user_index_u_id_uindex
        unique (u_id)
)
    comment '用户索引表，用于缓存预热';


create table `user-center`.user_team
(
    ut_id     bigint auto_increment
        primary key,
    u_id      bigint                             not null comment '用户id',
    t_id      bigint                             not null comment '队伍id',
    join_time datetime default CURRENT_TIMESTAMP not null comment '用户加入时间',
    is_delete int      default 0                 not null
)
    comment '用户队伍关系表';

