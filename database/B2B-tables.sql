create table categories
(
    id   int auto_increment
        primary key,
    name varchar(50) not null
);

create table users
(
    userId   int auto_increment
        primary key,
    username varchar(20) null,
    password varchar(20) null,
    userType int         null,
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
    image       varchar(100)   null,
    supplier_id int            null,
    constraint fk_products_category
        foreign key (categoryId) references categories (id),
    constraint products_ibfk_1
        foreign key (supplier_id) references users (userId)
);

create index supplier_id
    on products (supplier_id);