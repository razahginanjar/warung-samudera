create database warung_samudera_api;

create table branch
(
	id varchar(150) not null,
    code varchar(150) not null,
    name varchar(150) not null,
    address varchar(150),
    phone varchar(150),
    primary key(id),
    unique(id)
)engine innodb;

create table product_price
(
	id varchar(150) not null,
    price bigint not null,
    primary key(id)
)engine innodb;

create table products
(
	id varchar(150) not null,
    name varchar(150) not null,
    code varchar(150) not null,
    product_price_id varchar(150) not null,
    branch_id varchar(150) not null,
    foreign key fk_product_productpriceId (product_price_id) references product_price(id),
    foreign key fk_product_branchId (branch_id) references branch(id),
	primary key(id)
)engine innodb;

create table transaction
(
	bill_id varchar(150) not null,
    receipt_number int not null,
    transaction_date datetime,
    transaction_type enum("EAT_IN", "TAKE_AWAY", "ONLINE"),
     primary key(bill_id)
)engine innodb;

create table detail_transaction
(
	bill_detail_id varchar(150) not null,
    total_sales bigint,
    quantity int,
    product_id varchar(150) not null,
    transaction_bill_id varchar(150) not null,
    foreign key fk_detailtransaction_product (product_id) references products(id),
    foreign key fk_detailtransaction_transaction (transaction_bill_id) references transaction(bill_id),
	primary key(bill_detail_id)
)engine innodb;

CREATE TABLE sequence_generator (
    name VARCHAR(255) PRIMARY KEY,
    id INT NOT NULL
);


