drop table users;
drop table departments;
drop table sales;

create table users (
    id int,
    name text,
    PRIMARY KEY(id)
);

create table departments (
    id int,
    name text,
    PRIMARY KEY(id)
);

create table sales  (
    id int,
    amount int,
    department_id int,
    user_id int,
    PRIMARY KEY(id),
    CONSTRAINT su_fk
        FOREIGN KEY(user_id)
            REFERENCES users(id),
    CONSTRAINT sd_fk
        FOREIGN KEY(department_id)
            REFERENCES users(id)
);

insert into users(id, name) values (1, 'David');
insert into users(id, name) values (2, 'Leo');
insert into users(id, name) values (3, 'Gerd');
insert into users(id, name) values (11, 'Jowee');

insert into departments(id, name) values (1, 'Sales Berlin');
insert into departments(id, name) values (2, 'Sales London');
insert into departments(id, name) values (3, 'Sales Barcelona');

insert into sales(id, amount, department_id, user_id) values (1, 100, 1, 1);
insert into sales(id, amount, department_id, user_id) values (2, 200, 1, 1);
insert into sales(id, amount, department_id, user_id) values (22, 100, 1, 11);
insert into sales(id, amount, department_id, user_id) values (23, 100, 1, 11);
insert into sales(id, amount, department_id, user_id) values (3, 100, 2, 2);
insert into sales(id, amount, department_id, user_id) values (4, 150, 3, 3);
