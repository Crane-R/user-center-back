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

