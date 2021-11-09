create table answer (
    answer_id serial primary key,
    answer_value int,
    question_id int,
    constraint fk_question
        foreign key (question_id) references question(question_id)
);