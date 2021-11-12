create table answer (
    answer_id serial primary key,
    answer_value int,
    question_id int,
    person_id int,
    constraint fk_question
        foreign key (question_id) references question(question_id),
    constraint fk_person
        foreign key (person_id) references person(person_id)
);