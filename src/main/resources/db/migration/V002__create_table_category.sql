create table category
(
    category_id serial primary key,
    category_title varchar(100) not null,
    category_text varchar(100),
    questionnaire_id int,
    constraint fk_questionnaire
        foreign key (questionnaire_id) references questionnaire(questionnaire_id)
)