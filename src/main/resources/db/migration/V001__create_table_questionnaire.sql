create table questionnaire
(
    questionnaire_id serial primary key,
    questionnaire_title varchar(100) not null,
    questionnaire_text varchar(100)
)