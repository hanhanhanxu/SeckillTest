create table voucher (
                         id bigint(20) unsigned not null auto_increment primary key comment '主键',
                         shop_id bigint(20) unsigned default null comment '商铺id',
                         title varchar(255) not null comment '券标题',
                         sub_title varchar(255) default null comment '副标题',
                         rules varchar(1024) default null comment '使用规则',
                         pay_value bigint(10) unsigned not null comment '支付金额，单位：分，例如：200，代表2元',
                         actual_value bigint(10) unsigned not null comment '抵扣金额，单位：分，例如：100，代表1元',
                         type tinyint(1) unsigned not null default '0' comment '券类型，0普通券，1秒杀券',
                         status tinyint(1) unsigned not null default '1' comment '状态，1上架，2下架，3过期',
                         create_time datetime not null default CURRENT_TIMESTAMP comment "创建时间",
                         update_time datetime not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment "更新时间"
) default charset = utf8mb4 comment '优惠券表';

create table seckill_vouscher (
                                  voucher_id bigint(20) unsigned not null primary key comment '主键，关联的优惠券的id',
                                  stock int(8) unsigned not null comment '库存',
                                  begin_time datetime not null default '0000-00-00 00:00:00' comment "生效时间",
                                  end_time datetime not null default '0000-00-00 00:00:00' comment "失效时间",
                                  create_time datetime not null default CURRENT_TIMESTAMP comment "创建时间",
                                  update_time datetime not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment "更新时间"
) default charset = utf8mb4 comment '秒杀券表，与优惠券是一对一关系';

create table voucher_order (
                               id bigint(20) not null primary key comment '主键',
                               user_id bigint(20) unsigned not null comment '下单的用户id',
                               voucher_id bigint(20) unsigned not null comment '购买的优惠券id',
                               pay_type tinyint(1) unsigned not null default '1' comment '支付方式，1余额支付，2支付宝，3微信',
                               status tinyint(1) unsigned not null default '1' comment '订单状态，1未支付，2已支付，3已核销，4已取消',
                               create_time datetime not null default CURRENT_TIMESTAMP comment "创建时间，也即下单时间",
                               pay_time datetime default null comment "支付时间",
                               use_time datetime default null comment "核销时间",
                               refund_time datetime default null comment "退款时间",
                               update_time datetime not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment "更新时间"
) default charset = utf8mb4 comment '优惠券订单表';