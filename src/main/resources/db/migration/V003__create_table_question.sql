create table question
(
    question_id serial primary key,
    question_title varchar(100) not null,
    low_label varchar(100),
    high_label varchar(100),
    number_of_values int,
    category_id int,
    constraint fk_category
    foreign key (category_id) references category(category_id)
);