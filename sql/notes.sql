DROP DATABASE IF EXISTS notes;
CREATE DATABASE `notes`;
USE `notes`;

CREATE TABLE user
(
    id            INT(11)     NOT NULL AUTO_INCREMENT,
    login         VARCHAR(50) NOT NULL,
    password      VARCHAR(50) NOT NULL,
    first_name    VARCHAR(50) NOT NULL,
    last_name     VARCHAR(50) NOT NULL,
    patronymic    VARCHAR(50) DEFAULT NULL,
    is_active     BOOLEAN     DEFAULT TRUE,
    is_super_user BOOLEAN     DEFAULT FALSE,
    UNIQUE KEY (login),
    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

CREATE TABLE session
(
    id          INT(11)     NOT NULL AUTO_INCREMENT,
    user_id     INT(11)     NOT NULL,
    token       VARCHAR(50) NOT NULL,
    last_action DATETIME    NOT NULL,
    PRIMARY KEY (id),
    foreign key (user_id) REFERENCES user (id) ON DELETE cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

CREATE TABLE section
(
    id      INT(11)     NOT NULL AUTO_INCREMENT,
    user_id INT(11)     NOT NULL,
    name    VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    foreign key (user_id) REFERENCES user (id) ON DELETE cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

CREATE TABLE note
(
    id         INT(11) NOT NULL AUTO_INCREMENT,
    user_id    INT(11) NOT NULL,
    section_id INT(11) NOT NULL,
    PRIMARY KEY (id),
    foreign key (user_id) REFERENCES user (id) ON DELETE cascade,
    foreign key (section_id) REFERENCES section (id) ON DELETE cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

CREATE TABLE rating
(
    id      INT(11) NOT NULL AUTO_INCREMENT,
    user_id INT(11) NOT NULL,
    note_id INT(11) NOT NULL,
    PRIMARY KEY (id),
    foreign key (user_id) REFERENCES user (id) ON DELETE cascade,
    foreign key (note_id) REFERENCES note (id) ON DELETE cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

CREATE TABLE note_text_version
(
    id              INT(11)      NOT NULL AUTO_INCREMENT,
    note_id         INT(11)      NOT NULL,
    text            VARCHAR(100) NOT NULL,
    publishing_date DATETIME     NOT NULL,
    PRIMARY KEY (id),
    foreign key (note_id) REFERENCES note (id) ON DELETE cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

CREATE TABLE comment
(
    id                   INT(11)      NOT NULL AUTO_INCREMENT,
    user_id              INT(11)      NOT NULL,
    note_text_version_id INT(11)      NOT NULL,
    publishing_date      DATETIME     NOT NULL,
    text                 VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    foreign key (user_id) REFERENCES user (id) ON DELETE cascade,
    foreign key (note_text_version_id) REFERENCES note_text_version (id) ON DELETE cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

CREATE TABLE following
(
    id           INT(11) NOT NULL AUTO_INCREMENT,
    follower_id  INT(11) NOT NULL,
    following_id INT(11) NOT NULL,
    PRIMARY KEY (id),
    foreign key (follower_id) REFERENCES user (id) ON DELETE cascade,
    foreign key (following_id) REFERENCES user (id) ON DELETE cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

CREATE TABLE ignoring
(
    id           INT(11) NOT NULL AUTO_INCREMENT,
    ignore_id    INT(11) NOT NULL,
    ignore_by_id INT(11) NOT NULL,
    PRIMARY KEY (id),
    foreign key (ignore_id) REFERENCES user (id) ON DELETE cascade,
    foreign key (ignore_by_id) REFERENCES user (id) ON DELETE cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;