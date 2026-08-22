create table products (
    id uuid primary key,
    sku varchar(64) not null unique,
    name varchar(200) not null
);

create table product_prices (
    product_id uuid not null references products(id),
    currency char(3) not null,
    unit_price numeric(19, 2) not null check (unit_price >= 0),
    minimum_order_quantity integer not null check (minimum_order_quantity > 0),
    primary key (product_id, currency)
);

create table purchase_orders (
    id uuid primary key,
    customer_id uuid not null,
    currency char(3) not null,
    status varchar(20) not null,
    created_at timestamptz not null,
    constraint purchase_orders_status_check
        check (status in ('DRAFT', 'SUBMITTED'))
);

create table order_lines (
    order_id uuid not null references purchase_orders(id) on delete cascade,
    line_number integer not null check (line_number > 0),
    product_id uuid not null references products(id),
    quantity integer not null check (quantity > 0),
    unit_price numeric(19, 2) not null check (unit_price >= 0),
    primary key (order_id, line_number)
);

create index order_lines_product_id_idx on order_lines(product_id);
create index purchase_orders_customer_id_idx on purchase_orders(customer_id);
