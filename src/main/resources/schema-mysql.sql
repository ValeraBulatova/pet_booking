create table rooms (id int primary key, name varchar(4) not null unique, occupied tinyint not null, book_start bigint, book_end bigint)