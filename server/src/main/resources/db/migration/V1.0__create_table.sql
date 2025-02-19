
create table Books (
  id bigint GENERATED BY DEFAULT AS IDENTITY
  , title VARCHAR not null
  , price bigint not null
  , status VARCHAR not null
  , update_at timestamp not null
  , create_at timestamp not null
  , constraint Books_PKC primary key (id)
) ;

create table Authors (
  id bigint GENERATED BY DEFAULT AS IDENTITY
  , name VARCHAR not null
  , birth date not null
  , update_at timestamp not null
  , create_at timestamp not null
  , constraint Authors_PKC primary key (id)
) ;


create table Books_Authors (
  id bigint GENERATED BY DEFAULT AS IDENTITY
  , book_id bigint not null
  , author_id bigint not null
  , update_at timestamp not null
  , create_at timestamp not null
  , constraint Books_Authors_PKC primary key (id)
) ;

alter table Books_Authors
  add constraint Books_Authors_FK1 foreign key (author_id) references Authors(id)
  on delete cascade;

alter table Books_Authors
  add constraint Books_Authors_FK2 foreign key (book_id) references Books(id)
  on delete cascade;

