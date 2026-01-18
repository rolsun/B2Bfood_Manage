create table after_sales_table
(
    id                  bigint auto_increment comment '主键ID'
        primary key,
    application_content text                                null comment '申请内容',
    complained_supplier varchar(255)                        null comment '投诉的供应商',
    complained_order    varchar(255)                        null comment '投诉的订单',
    status              tinyint   default 1                 not null comment '状态(1为待处理,2为已处理)',
    created_time        timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    after_sales_type    tinyint                             null comment '售后类型(1-退货退款, 2-仅退款, 3-换货)'
);

create table cart
(
    id          bigint auto_increment
        primary key,
    user_id     bigint                              not null comment '用户ID',
    product_id  bigint                              not null comment '商品ID',
    quantity    int       default 1                 not null comment '商品数量',
    note        text                                null comment '备注',
    create_time timestamp default CURRENT_TIMESTAMP null,
    update_time timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
);

create index idx_product_id
    on cart (product_id);

create index idx_user_id
    on cart (user_id);

create table categories
(
    id   int auto_increment
        primary key,
    name varchar(50) not null
);

create table orders
(
    id                      bigint auto_increment
        primary key,
    order_sn                varchar(64)                         not null comment '订单编号',
    buyer_id                bigint                              not null comment '采购商ID',
    supplier_id             bigint                              not null comment '供应商ID',
    total_amount            decimal(10, 2)                      not null comment '订单总金额',
    order_status            tinyint   default 1                 not null comment '订单状态: 1-待支付,2-待发货,3-已发货,4-已完成,5-已取消',
    payment_method          tinyint   default 1                 not null comment '支付方式: 1-在线支付',
    delivery_address_id     bigint                              null comment '收货地址ID',
    buyer_note              text                                null comment '采购商备注',
    cancel_reason           text                                null comment '取消原因',
    shipping_company        varchar(100)                        null comment '物流公司',
    tracking_number         varchar(100)                        null comment '运单号',
    estimated_delivery_time datetime                            null comment '预计送达时间',
    actual_delivery_time    datetime                            null comment '实际送达时间',
    create_time             timestamp default CURRENT_TIMESTAMP null,
    update_time             timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint order_sn
        unique (order_sn)
);

create table order_items
(
    id          bigint auto_increment
        primary key,
    order_id    bigint                              not null comment '订单ID',
    product_id  bigint                              not null comment '商品ID',
    quantity    int                                 not null comment '购买数量',
    price       decimal(10, 2)                      not null comment '单价',
    total_price decimal(10, 2)                      not null comment '小计金额',
    note        text                                null comment '商品备注',
    create_time timestamp default CURRENT_TIMESTAMP null,
    constraint order_items_ibfk_1
        foreign key (order_id) references orders (id)
            on delete cascade
);

create index idx_order_id
    on order_items (order_id);

create index idx_product_id
    on order_items (product_id);

create index idx_buyer_id
    on orders (buyer_id);

create index idx_create_time
    on orders (create_time);

create index idx_order_sn
    on orders (order_sn);

create index idx_order_status
    on orders (order_status);

create index idx_supplier_id
    on orders (supplier_id);

create table users
(
    userId   int auto_increment
        primary key,
    username varchar(20)       null,
    password varchar(255)      null,
    userType int               null,
    phone    varchar(20)       null,
    email    varchar(100)      null,
    address  text              null,
    status   tinyint default 1 null,
    constraint username
        unique (username)
);

create table products
(
    productId   int auto_increment
        primary key,
    name        varchar(20)    null,
    categoryId  int            null,
    unit        varchar(20)    null,
    price       decimal(10, 2) null,
    stock       int            null,
    description varchar(100)   null,
    image       varchar(500)   null,
    supplier_id int            null,
    constraint fk_products_category
        foreign key (categoryId) references categories (id),
    constraint products_ibfk_1
        foreign key (supplier_id) references users (userId)
);

create index supplier_id
    on products (supplier_id);

create table wallet_transactions
(
    id               bigint auto_increment
        primary key,
    user_id          bigint                              not null comment '用户ID',
    transaction_type tinyint                             not null comment '交易类型: 1-充值, 2-用户支付, 3-售后退款,4-提现',
    amount           decimal(10, 2)                      not null comment '交易金额',
    before_balance   decimal(10, 2)                      not null comment '交易前余额',
    after_balance    decimal(10, 2)                      not null comment '交易后余额',
    order_id         bigint                              null comment '关联订单ID（支付时）',
    description      varchar(255)                        null comment '交易描述',
    status           tinyint   default 1                 not null comment '状态: 1-成功, 2-失败, 3-处理中',
    create_time      timestamp default CURRENT_TIMESTAMP null
);

create index idx_create_time
    on wallet_transactions (create_time);

create index idx_transaction_type
    on wallet_transactions (transaction_type);

create index idx_user_id
    on wallet_transactions (user_id);

create table wallets
(
    id             bigint auto_increment
        primary key,
    user_id        bigint                                   not null comment '用户ID',
    balance        decimal(10, 2) default 0.00              not null comment '余额',
    total_recharge decimal(10, 2) default 0.00              not null comment '累计充值金额',
    total_spent    decimal(10, 2) default 0.00              not null comment '累计消费金额',
    create_time    timestamp      default CURRENT_TIMESTAMP null,
    update_time    timestamp      default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint user_id
        unique (user_id)
);

create index idx_user_id
    on wallets (user_id);

